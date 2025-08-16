package org.minu.dnd13th3backend.screentime.service;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.user.entity.Profile;
import org.minu.dnd13th3backend.user.repository.ProfileRepository;
import org.minu.dnd13th3backend.screentime.dto.response.ScreenTimeGetDailyResponse;
import org.minu.dnd13th3backend.screentime.dto.response.ScreenTimeGetWeeklyResponse;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import org.minu.dnd13th3backend.screentime.repository.ScreenTimeRepository;
import org.minu.dnd13th3backend.user.entity.User;
import org.minu.dnd13th3backend.user.type.ScreenTimeGoalType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreenTimeService {

    private final ScreenTimeRepository screenTimeRepository;
    private final ProfileRepository profileRepository;
    private final Random random = new Random();

    @Transactional
    public ScreenTime generateAndSaveScreenTime(User user) {
        LocalDate today = LocalDate.now();
        Optional<ScreenTime> optionalScreenTime = screenTimeRepository.findByUser_IdAndDate(user.getId(), today);

        int currentMinutes = optionalScreenTime.map(ScreenTime::getScreentimeMinutes).orElse(0);

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("해당 사용자의 프로필을 찾을 수 없습니다: " + user.getId()));

        int goalMinutes = getGoalMinutes(profile);

        int maxMinutesForNow = calculateCurrentMaxMinutes(goalMinutes);

        int newMinimum = currentMinutes + 1;

        int randomMinutes;

        if (newMinimum < maxMinutesForNow) {
            randomMinutes = random.nextInt(maxMinutesForNow - newMinimum + 1) + newMinimum;
        } else {
            randomMinutes = currentMinutes;
        }

        ScreenTime screenTime;
        if (optionalScreenTime.isPresent()) {
            screenTime = optionalScreenTime.get();
            if (randomMinutes > currentMinutes) {
                screenTime.updateScreenTime(randomMinutes);
            }
        } else {
            screenTime = ScreenTime.builder()
                    .user(user)
                    .date(today)
                    .screentimeMinutes(randomMinutes)
                    .build();
        }

        return screenTimeRepository.save(screenTime);
    }

    private int getGoalMinutes(Profile profile) {
        if (profile.getScreenTimeGoalType() == ScreenTimeGoalType.CUSTOM) {
            try {
                return Integer.parseInt(profile.getScreenTimeGoalCustom());
            } catch (NumberFormatException e) {
                return 240;
            }
        }
        return profile.getScreenTimeGoalType().getMinutes();
    }

    private int calculateCurrentMaxMinutes(int goalMinutes) {
        double dayProgressRatio = (double) LocalTime.now().toSecondOfDay() / (24.0 * 3600.0);
        int maxMinutes = (int) (goalMinutes * dayProgressRatio);
        return Math.min(maxMinutes, goalMinutes);
    }

    @Transactional(readOnly = true)
    public Object getScreenTime(String period, LocalDate date, User user) {
        if ("day".equalsIgnoreCase(period)) {
            return getDailyScreenTime(date, user);
        } else if ("week".equalsIgnoreCase(period)) {
            return getWeeklyScreenTime(date, user);
        } else {
            throw new IllegalArgumentException("Invalid period value. It must be 'day' or 'week'.");
        }
    }

    private ScreenTimeGetDailyResponse getDailyScreenTime(LocalDate date, User user) {
        LocalDate targetDate = (date == null) ? LocalDate.now() : date;
        ScreenTime screenTime = screenTimeRepository.findByUser_IdAndDate(user.getId(), targetDate)
                .orElse(null);

        return ScreenTimeGetDailyResponse.from(screenTime);
    }

    private ScreenTimeGetWeeklyResponse getWeeklyScreenTime(LocalDate date, User user) {
        LocalDate today = (date == null) ? LocalDate.now() : date;
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        List<ScreenTime> recordedTimes = screenTimeRepository.findByUser_IdAndDateBetween(user.getId(), startOfWeek, endOfWeek);

        Map<LocalDate, Integer> recordedMap = recordedTimes.stream()
                .collect(Collectors.toMap(ScreenTime::getDate, ScreenTime::getScreentimeMinutes));

        List<ScreenTimeGetWeeklyResponse.DailyRecord> dailyRecords = new ArrayList<>();
        for (LocalDate d = startOfWeek; !d.isAfter(endOfWeek); d = d.plusDays(1)) {
            int minutes = recordedMap.getOrDefault(d, 0);
            dailyRecords.add(ScreenTimeGetWeeklyResponse.DailyRecord.builder()
                    .date(d)
                    .screentimeMinutes(minutes)
                    .build());
        }

        int totalMinutes = dailyRecords.stream().mapToInt(ScreenTimeGetWeeklyResponse.DailyRecord::getScreentimeMinutes).sum();
        double averageMinutes = (double) totalMinutes / 7.0;

        return ScreenTimeGetWeeklyResponse.builder()
                .period("week")
                .startDate(startOfWeek)
                .endDate(endOfWeek)
                .totalMinutes(totalMinutes)
                .averageMinutes(averageMinutes)
                .dailyRecords(dailyRecords)
                .build();
    }
}

package org.minu.dnd13th3backend.screentime.service;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.screentime.dto.response.ScreenTimeGetDailyResponse;
import org.minu.dnd13th3backend.screentime.dto.response.ScreenTimeGetWeeklyResponse;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import org.minu.dnd13th3backend.screentime.repository.ScreenTimeRepository;
import org.minu.dnd13th3backend.user.entity.Profile;
import org.minu.dnd13th3backend.user.entity.User;
import org.minu.dnd13th3backend.user.repository.ProfileRepository;
import org.minu.dnd13th3backend.user.type.ScreenTimeGoalType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
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

        int prevInsta = optionalScreenTime.map(ScreenTime::getInstagramMinutes).orElse(0);
        int prevYoutube = optionalScreenTime.map(ScreenTime::getYoutubeMinutes).orElse(0);
        int prevKakaotalk = optionalScreenTime.map(ScreenTime::getKakaotalkMinutes).orElse(0);
        int prevChrome = optionalScreenTime.map(ScreenTime::getChromeMinutes).orElse(0);
        int currentTotalMinutes = prevInsta + prevYoutube + prevKakaotalk + prevChrome;

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("해당 사용자의 프로필을 찾을 수 없습니다: " + user.getId()));
        int goalMinutes = getGoalMinutes(profile);
        int maxMinutesForNow = calculateCurrentMaxMinutes(goalMinutes);

        int remainingBudget = maxMinutesForNow - currentTotalMinutes;

        ScreenTime screenTime;
        if (optionalScreenTime.isPresent()) {
            screenTime = optionalScreenTime.get();
            if (remainingBudget > 0) {
                int totalIncrease = random.nextInt(Math.min(remainingBudget, 30)) + 1;

                int[] increases = distributeTotalTime(totalIncrease, 4);

                screenTime.updateScreenTime(
                        prevInsta + increases[0],
                        prevYoutube + increases[1],
                        prevKakaotalk + increases[2],
                        prevChrome + increases[3]
                );
            }
        } else {
            int initialTotalMinutes = 0;
            if (maxMinutesForNow > 0) {
                initialTotalMinutes = random.nextInt(Math.min(maxMinutesForNow, 30)) + 1;
            }

            int[] appMinutes = distributeTotalTime(initialTotalMinutes, 4);
            screenTime = ScreenTime.builder()
                    .user(user)
                    .date(today)
                    .instagramMinutes(appMinutes[0])
                    .youtubeMinutes(appMinutes[1])
                    .kakaotalkMinutes(appMinutes[2])
                    .chromeMinutes(appMinutes[3])
                    .build();
        }
        return screenTimeRepository.save(screenTime);
    }

    private int[] distributeTotalTime(int total, int parts) {
        int[] result = new int[parts];
        if (total <= 0) return result;
        int currentTotal = 0;
        for (int i = 0; i < parts - 1; i++) {
            if (currentTotal >= total) break;
            int value = random.nextInt(total - currentTotal + 1);
            result[i] = value;
            currentTotal += value;
        }
        result[parts - 1] = total - currentTotal;

        List<Integer> list = Arrays.stream(result).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        for(int i=0; i<parts; i++) {
            result[i] = list.get(i);
        }
        return result;
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
        ScreenTime screenTime = screenTimeRepository.findByUser_IdAndDate(user.getId(), targetDate).orElse(null);
        return ScreenTimeGetDailyResponse.from(screenTime);
    }

    private ScreenTimeGetWeeklyResponse getWeeklyScreenTime(LocalDate date, User user) {
        LocalDate today = (date == null) ? LocalDate.now() : date;
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        List<ScreenTime> recordedTimes = screenTimeRepository.findByUser_IdAndDateBetween(user.getId(), startOfWeek, endOfWeek);
        Map<LocalDate, ScreenTime> recordedMap = recordedTimes.stream().collect(Collectors.toMap(ScreenTime::getDate, st -> st));

        List<ScreenTimeGetWeeklyResponse.DailyRecord> dailyRecords = new ArrayList<>();
        int totalInsta = 0, totalYoutube = 0, totalKakaotalk = 0, totalChrome = 0;

        for (LocalDate d = startOfWeek; !d.isAfter(endOfWeek); d = d.plusDays(1)) {
            ScreenTime st = recordedMap.get(d);
            if (st != null) {
                totalInsta += st.getInstagramMinutes();
                totalYoutube += st.getYoutubeMinutes();
                totalKakaotalk += st.getKakaotalkMinutes();
                totalChrome += st.getChromeMinutes();

                dailyRecords.add(ScreenTimeGetWeeklyResponse.DailyRecord.builder()
                        .date(d)
                        .totalMinutes(st.getInstagramMinutes() + st.getYoutubeMinutes() + st.getKakaotalkMinutes() + st.getChromeMinutes())
                        .appTimes(ScreenTimeGetWeeklyResponse.AppTimeDetails.builder()
                                .instagram(st.getInstagramMinutes())
                                .youtube(st.getYoutubeMinutes())
                                .kakaotalk(st.getKakaotalkMinutes())
                                .chrome(st.getChromeMinutes())
                                .build())
                        .build());
            } else {
                dailyRecords.add(ScreenTimeGetWeeklyResponse.DailyRecord.builder()
                        .date(d)
                        .totalMinutes(0)
                        .appTimes(ScreenTimeGetWeeklyResponse.AppTimeDetails.builder().instagram(0).youtube(0).kakaotalk(0).chrome(0).build())
                        .build());
            }
        }

        int totalMinutes = totalInsta + totalYoutube + totalKakaotalk + totalChrome;
        double averageMinutes = (recordedTimes.isEmpty()) ? 0.0 : (double) totalMinutes / 7.0;

        return ScreenTimeGetWeeklyResponse.builder()
                .period("week")
                .startDate(startOfWeek)
                .endDate(endOfWeek)
                .totalMinutes(totalMinutes)
                .averageMinutes(averageMinutes)
                .weeklyAppTotals(ScreenTimeGetWeeklyResponse.AppTimeDetails.builder()
                        .instagram(totalInsta)
                        .youtube(totalYoutube)
                        .kakaotalk(totalKakaotalk)
                        .chrome(totalChrome)
                        .build())
                .dailyRecords(dailyRecords)
                .build();
    }
}
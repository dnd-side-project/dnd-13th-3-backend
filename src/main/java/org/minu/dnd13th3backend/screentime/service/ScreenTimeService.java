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

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreenTimeService {

    private final ScreenTimeRepository screenTimeRepository;
    private final ProfileRepository profileRepository;
    private final Random random = new Random();
    private final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Transactional
    public ScreenTime generateAndSaveScreenTime(User user) {
        LocalDate today = LocalDate.now(KST);
        Optional<ScreenTime> optionalScreenTime = screenTimeRepository.findByUser_IdAndDate(user.getId(), today);

        ScreenTime screenTime;

        if (optionalScreenTime.isPresent()) {
            screenTime = optionalScreenTime.get();

            LocalDateTime nowInKst = LocalDateTime.now(KST);

            LocalDateTime lastUpdateFromDb = screenTime.getUpdatedAt() != null ? screenTime.getUpdatedAt() : screenTime.getCreatedAt();

            if (lastUpdateFromDb == null) {
                lastUpdateFromDb = nowInKst.minusMinutes(5);
            }

            long minutesPassed = Duration.between(lastUpdateFromDb, nowInKst).toMinutes();

            if (minutesPassed > 0) {
                screenTime.updateScreenTime(
                        screenTime.getInstagramMinutes() + (int) minutesPassed,
                        screenTime.getYoutubeMinutes() + (int) minutesPassed,
                        screenTime.getKakaotalkMinutes() + (int) minutesPassed,
                        screenTime.getChromeMinutes() + (int) minutesPassed
                );
            }
        } else {
            Profile profile = profileRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalStateException("해당 사용자의 프로필을 찾을 수 없습니다: " + user.getId()));

            int goalMinutes = getGoalMinutes(profile);
            int maxMinutesForNow = calculateCurrentMaxMinutes(goalMinutes);

            int initialTotalMinutes = 0;
            if (maxMinutesForNow > 0) {
                if (maxMinutesForNow > 1) {
                    int minMinutes = maxMinutesForNow / 2;
                    initialTotalMinutes = random.nextInt(maxMinutesForNow - minMinutes + 1) + minMinutes;
                } else {
                    initialTotalMinutes = maxMinutesForNow;
                }
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

    private int calculateCurrentMaxMinutes(int goalMinutes) {
        LocalTime now = LocalTime.now(KST);
        double dayProgressRatio = (double) now.toSecondOfDay() / (24.0 * 3600.0);
        int maxMinutes = (int) (goalMinutes * dayProgressRatio);
        return Math.min(maxMinutes, goalMinutes);
    }

    private ScreenTimeGetDailyResponse getDailyScreenTime(LocalDate date, User user) {
        LocalDate targetDate = (date == null) ? LocalDate.now(KST) : date;
        ScreenTime screenTime = screenTimeRepository.findByUser_IdAndDate(user.getId(), targetDate).orElse(null);

        Optional<Profile> optionalProfile = profileRepository.findByUserId(user.getId());

        if (optionalProfile.isEmpty()) {
            return ScreenTimeGetDailyResponse.from(screenTime, "NO_DATA");
        }

        Profile profile = optionalProfile.get();
        int goalMinutes = getGoalMinutes(profile);

        String status = "NO_DATA";
        if (screenTime != null) {
            int totalMinutes = screenTime.getInstagramMinutes() + screenTime.getYoutubeMinutes() + screenTime.getKakaotalkMinutes() + screenTime.getChromeMinutes();
            status = (totalMinutes > goalMinutes) ? "OVER" : "UNDER";
        }

        return ScreenTimeGetDailyResponse.from(screenTime, status);
    }

    private ScreenTimeGetWeeklyResponse getWeeklyScreenTime(LocalDate date, User user) {
        LocalDate today = (date == null) ? LocalDate.now(KST) : date;
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        Optional<Profile> optionalProfile = profileRepository.findByUserId(user.getId());

        int goalMinutes = optionalProfile.map(this::getGoalMinutes).orElse(0);

        List<ScreenTime> recordedTimes = screenTimeRepository.findByUser_IdAndDateBetween(user.getId(), startOfWeek, endOfWeek);
        Map<LocalDate, ScreenTime> recordedMap = recordedTimes.stream().collect(Collectors.toMap(ScreenTime::getDate, st -> st));

        List<ScreenTimeGetWeeklyResponse.DailyRecord> dailyRecords = new ArrayList<>();
        int totalInsta = 0, totalYoutube = 0, totalKakaotalk = 0, totalChrome = 0;

        for (LocalDate d = startOfWeek; !d.isAfter(endOfWeek); d = d.plusDays(1)) {
            ScreenTime st = recordedMap.get(d);
            String status = "NO_DATA";
            int dailyTotal = 0;

            ScreenTimeGetWeeklyResponse.AppTimeDetails appTimes;

            if (st != null) {
                dailyTotal = st.getInstagramMinutes() + st.getYoutubeMinutes() + st.getKakaotalkMinutes() + st.getChromeMinutes();
                if (optionalProfile.isPresent()) {
                    status = (dailyTotal > goalMinutes) ? "OVER" : "UNDER";
                }
                totalInsta += st.getInstagramMinutes();
                totalYoutube += st.getYoutubeMinutes();
                totalKakaotalk += st.getKakaotalkMinutes();
                totalChrome += st.getChromeMinutes();
                appTimes = ScreenTimeGetWeeklyResponse.AppTimeDetails.builder()
                        .instagram(st.getInstagramMinutes())
                        .youtube(st.getYoutubeMinutes())
                        .kakaotalk(st.getKakaotalkMinutes())
                        .chrome(st.getChromeMinutes())
                        .build();
            } else {
                appTimes = ScreenTimeGetWeeklyResponse.AppTimeDetails.builder()
                        .instagram(0).youtube(0).kakaotalk(0).chrome(0).build();
            }

            dailyRecords.add(ScreenTimeGetWeeklyResponse.DailyRecord.builder()
                    .date(d)
                    .dayOfWeek(d.getDayOfWeek().name())
                    .totalMinutes(dailyTotal)
                    .status(status)
                    .appTimes(appTimes)
                    .build());
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

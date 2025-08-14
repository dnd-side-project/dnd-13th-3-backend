package org.minu.dnd13th3backend.screentime.service;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.screentime.dto.request.ScreenTimePostRequest;
import org.minu.dnd13th3backend.screentime.dto.response.ScreenTimeGetDailyResponse; // DTO import 이름 변경
import org.minu.dnd13th3backend.screentime.dto.response.ScreenTimeGetWeeklyResponse;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import org.minu.dnd13th3backend.screentime.repository.ScreenTimeRepository;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreenTimeService {

    private final ScreenTimeRepository screenTimeRepository;

    @Transactional
    public ScreenTime registerOrUpdateScreenTime(ScreenTimePostRequest requestDto, User user) {
        Optional<ScreenTime> optionalScreenTime = screenTimeRepository.findByUser_IdAndDate(user.getId(), requestDto.getDate());

        if (optionalScreenTime.isPresent()) {
            ScreenTime screenTime = optionalScreenTime.get();
            screenTime.updateScreenTime(requestDto.getScreentimeMinutes());
            return screenTime;
        } else {
            ScreenTime newScreenTime = ScreenTime.builder()
                    .user(user)
                    .date(requestDto.getDate())
                    .screentimeMinutes(requestDto.getScreentimeMinutes())
                    .build();
            return screenTimeRepository.save(newScreenTime);
        }
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

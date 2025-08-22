package org.minu.dnd13th3backend.timer.service;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.timer.dto.request.TimerPostRequest;
import org.minu.dnd13th3backend.timer.dto.response.TimerGetResponse;
import org.minu.dnd13th3backend.timer.entity.Timer;
import org.minu.dnd13th3backend.timer.repository.TimerRepository;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimerService {

    private final TimerRepository timerRepository;

    @Transactional
    public Timer createTimer(TimerPostRequest requestDto, User user) {
        LocalDateTime endedAt = requestDto.getStartedAt()
                .plusHours(requestDto.getDurationHours())
                .plusMinutes(requestDto.getDurationMinutes())
                .plusSeconds(requestDto.getDurationSeconds());

        Timer timer = Timer.builder()
                .user(user)
                .category(requestDto.getCategory())
                .durationHours(requestDto.getDurationHours())
                .durationMinutes(requestDto.getDurationMinutes())
                .durationSeconds(requestDto.getDurationSeconds())
                .startedAt(requestDto.getStartedAt())
                .endedAt(endedAt)
                .build();

        return timerRepository.save(timer);
    }

    @Transactional(readOnly = true)
    public TimerGetResponse getTimer(String period, LocalDate date, User user) {
        if ("day".equalsIgnoreCase(period)) {
            return getDailyTimerRecords(date, user);
        } else if ("week".equalsIgnoreCase(period)) {
            return getWeeklyTimerRecords(date, user);
        } else {
            throw new IllegalArgumentException("Invalid period value. It must be 'day' or 'week'.");
        }
    }

    private TimerGetResponse getDailyTimerRecords(LocalDate date, User user) {
        LocalDate targetDate = (date == null) ? LocalDate.now() : date;
        LocalDateTime startDateTime = targetDate.atStartOfDay();
        LocalDateTime endDateTime = targetDate.atTime(LocalTime.MAX);

        List<Timer> timers = timerRepository.findByUser_IdAndStartedAtBetween(user.getId(), startDateTime, endDateTime);

        List<TimerGetResponse.DailyRecord> dailyRecords = timers.stream()
                .map(timer -> TimerGetResponse.DailyRecord.builder()
                        .category(timer.getCategory())
                        .durationHours(timer.getDurationHours())
                        .durationMinutes(timer.getDurationMinutes())
                        .durationSeconds(timer.getDurationSeconds())
                        .startedAt(timer.getStartedAt())
                        .endedAt(timer.getEndedAt())
                        .build())
                .collect(Collectors.toList());

        return TimerGetResponse.builder()
                .period("day")
                .startDate(targetDate)
                .endDate(targetDate)
                .records(dailyRecords)
                .build();
    }

    private TimerGetResponse getWeeklyTimerRecords(LocalDate date, User user) {
        LocalDate targetDate = (date == null) ? LocalDate.now() : date;
        LocalDate startOfWeek = targetDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = targetDate.with(DayOfWeek.SUNDAY);
        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);

        List<Timer> timers = timerRepository.findByUser_IdAndStartedAtBetween(user.getId(), startDateTime, endDateTime);

        Map<String, Long> categoryDurationMap = timers.stream()
                .collect(Collectors.groupingBy(
                        Timer::getCategory,
                        Collectors.summingLong(timer ->
                                (long) timer.getDurationHours() * 3600 +
                                        (long) timer.getDurationMinutes() * 60 +
                                        timer.getDurationSeconds()
                        )
                ));

        List<TimerGetResponse.CategoryRecord> categoryRecords = categoryDurationMap.entrySet().stream()
                .map(entry -> TimerGetResponse.CategoryRecord.builder()
                        .category(entry.getKey())
                        .totalDuration(secondsToDuration(entry.getValue()))
                        .build())
                .collect(Collectors.toList());

        long totalSeconds = categoryRecords.stream()
                .mapToLong(record -> record.getTotalDuration().getHours() * 3600 + record.getTotalDuration().getMinutes() * 60 + record.getTotalDuration().getSeconds())
                .sum();

        double averageSeconds = (double) totalSeconds / 7.0;

        return TimerGetResponse.builder()
                .period("week")
                .startDate(startOfWeek)
                .endDate(endOfWeek)
                .totalDuration(secondsToDuration(totalSeconds))
                .averageDuration(secondsToDuration((long) averageSeconds))
                .records(categoryRecords)
                .build();
    }

    private TimerGetResponse.Duration secondsToDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return TimerGetResponse.Duration.builder()
                .hours(hours)
                .minutes(minutes)
                .seconds(seconds)
                .build();
    }
}

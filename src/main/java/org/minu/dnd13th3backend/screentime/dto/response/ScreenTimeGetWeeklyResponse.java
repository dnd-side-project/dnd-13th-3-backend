package org.minu.dnd13th3backend.screentime.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ScreenTimeGetWeeklyResponse {
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalMinutes;
    private double averageMinutes;
    private AppTimeDetails weeklyAppTotals;
    private List<DailyRecord> dailyRecords;

    @Getter
    @Builder
    public static class AppTimeDetails {
        private int instagram;
        private int youtube;
        private int kakaotalk;
        private int chrome;
    }

    @Getter
    @Builder
    public static class DailyRecord {
        private LocalDate date;
        private int totalMinutes;
        private AppTimeDetails appTimes;
    }
}

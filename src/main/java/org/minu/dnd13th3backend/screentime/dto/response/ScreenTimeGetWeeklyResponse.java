package org.minu.dnd13th3backend.screentime.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ScreenTimeGetWeeklyResponse {

    private String period;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("total_minutes")
    private int totalMinutes;

    @JsonProperty("average_minutes")
    private double averageMinutes;

    @JsonProperty("daily_records")
    private List<DailyRecord> dailyRecords;

    @Getter
    @Builder
    public static class DailyRecord {
        private LocalDate date;

        @JsonProperty("screentime_minutes")
        private int screentimeMinutes;
    }
}

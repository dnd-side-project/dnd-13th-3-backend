package org.minu.dnd13th3backend.timer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimerGetResponse {

    private String period;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("total_duration")
    private Duration totalDuration;

    @JsonProperty("average_duration")
    private Duration averageDuration;

    private List<?> records;

    @Getter
    @Builder
    public static class Duration {
        private long hours;
        private long minutes;
        private long seconds;
    }

    @Getter
    @Builder
    public static class DailyRecord {
        private String category;
        @JsonProperty("duration_hours")
        private int durationHours;
        @JsonProperty("duration_minutes")
        private int durationMinutes;
        @JsonProperty("duration_seconds")
        private int durationSeconds;
        @JsonProperty("started_at")
        private LocalDateTime startedAt;
        @JsonProperty("ended_at")
        private LocalDateTime endedAt;
    }

    @Getter
    @Builder
    public static class CategoryRecord {
        private String category;
        @JsonProperty("total_duration")
        private Duration totalDuration;
    }
}

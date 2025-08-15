package org.minu.dnd13th3backend.timer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TimerPostRequest {

    private String category;

    @JsonProperty("duration_hours")
    private int durationHours;

    @JsonProperty("duration_minutes")
    private int durationMinutes;

    @JsonProperty("duration_seconds")
    private int durationSeconds;

    @JsonProperty("started_at")
    private LocalDateTime startedAt;
}

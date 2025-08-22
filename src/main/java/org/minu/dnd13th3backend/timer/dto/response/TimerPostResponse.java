package org.minu.dnd13th3backend.timer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.minu.dnd13th3backend.timer.entity.Timer;

import java.time.LocalDateTime;

@Getter
public class TimerPostResponse {

    private Long id;
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

    public TimerPostResponse(Timer timer) {
        this.id = timer.getId();
        this.category = timer.getCategory();
        this.durationHours = timer.getDurationHours();
        this.durationMinutes = timer.getDurationMinutes();
        this.durationSeconds = timer.getDurationSeconds();
        this.startedAt = timer.getStartedAt();
        this.endedAt = timer.getEndedAt();
    }
}

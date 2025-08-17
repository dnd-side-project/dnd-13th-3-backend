package org.minu.dnd13th3backend.challenge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.minu.dnd13th3backend.challenge.type.ChallengeType;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ChallengeCreateRequest {

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("goal_time_minutes")
    private int goalTimeMinutes;

    private ChallengeType type;

    private String title;
}
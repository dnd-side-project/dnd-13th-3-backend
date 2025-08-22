package org.minu.dnd13th3backend.challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.minu.dnd13th3backend.challenge.entity.Challenge;

import java.time.LocalDate;

@Getter
public class InviteJoinResponse {

    private final String message;

    @JsonProperty("challenge_id")
    private final Long challengeId;

    @JsonProperty("start_date")
    private final LocalDate startDate;

    @JsonProperty("end_date")
    private final LocalDate endDate;

    private final String title;

    public InviteJoinResponse(Challenge challenge, String message) {
        this.message = message;
        this.challengeId = challenge.getId();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.title = challenge.getTitle();
    }
}

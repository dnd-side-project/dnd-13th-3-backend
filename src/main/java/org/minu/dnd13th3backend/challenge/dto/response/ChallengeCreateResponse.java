package org.minu.dnd13th3backend.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ChallengeCreateResponse {

    @JsonProperty("challenge_id")
    private final Long challengeId;

    public ChallengeCreateResponse(Long challengeId) {
        this.challengeId = challengeId;
    }
}

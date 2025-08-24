package org.minu.dnd13th3backend.challenge.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChallengeListResponse {
    private List<ChallengeGetResponse> challenges;
}

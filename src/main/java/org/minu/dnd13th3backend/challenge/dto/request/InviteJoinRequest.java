package org.minu.dnd13th3backend.challenge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InviteJoinRequest {

    @JsonProperty("invite_code")
    private String inviteCode;
}
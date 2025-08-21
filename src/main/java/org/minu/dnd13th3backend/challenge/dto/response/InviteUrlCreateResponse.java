package org.minu.dnd13th3backend.challenge.dto.response;

import lombok.Getter;

@Getter
public class InviteUrlCreateResponse {

    private final String url;

    public InviteUrlCreateResponse(String url) {
        this.url = url;
    }
}

package org.minu.dnd13th3backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "JWT 토큰 응답")
public class TokenResponse {
    
    @Schema(
            description = "JWT Access Token (API 요청 시 사용)",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzU0Mjg3NTQ1LCJleHAiOjE3NTQyODkzNDV9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String accessToken;
    
    @Schema(
            description = "JWT Refresh Token (토큰 갱신 시 사용)",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzU0Mjg3NTQ1LCJleHAiOjE3NTQ4OTIzNDV9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String refreshToken;
    
    @Schema(
            description = "캐릭터 인덱스 (1-6)",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final Integer characterIndex;
    
    public static TokenResponse of(String accessToken, String refreshToken, Integer characterIndex) {
        return new TokenResponse(accessToken, refreshToken, characterIndex);
    }
}
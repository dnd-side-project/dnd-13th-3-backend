package org.minu.dnd13th3backend.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "프로필 응답")
public class ProfileResponse {

    @Schema(description = "응답 메시지", example = "프로필이 성공적으로 등록되었습니다.")
    private String message;
    
    @Schema(description = "캐릭터 인덱스", example = "3")
    private Integer characterIndex;

    public static ProfileResponse success(String message) {
        return new ProfileResponse(message, null);
    }
    
    public static ProfileResponse success(String message, Integer characterIndex) {
        return new ProfileResponse(message, characterIndex);
    }
}
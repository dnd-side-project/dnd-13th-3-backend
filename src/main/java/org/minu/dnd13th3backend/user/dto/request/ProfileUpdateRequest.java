package org.minu.dnd13th3backend.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.minu.dnd13th3backend.user.type.GoalType;
import org.minu.dnd13th3backend.user.type.ScreenTimeGoalType;

@Getter
@NoArgsConstructor
@Schema(description = "프로필 수정 요청")
public class ProfileUpdateRequest {

    @Valid
    @NotNull(message = "목표는 필수입니다")
    @Schema(description = "목표 정보")
    private Goal goal;

    @Valid
    @NotNull(message = "스크린 타임 목표는 필수입니다")
    @Schema(description = "스크린 타임 목표 정보")
    private ScreenTimeGoal screenTimeGoal;

    @Getter
    @NoArgsConstructor
    @Schema(description = "목표 정보")
    public static class Goal {
        @NotNull(message = "목표 타입은 필수입니다")
        @Schema(description = "목표 타입", example = "CUSTOM")
        private GoalType type;

        @Size(max = 100, message = "커스텀 목표는 100자 이하여야 합니다")
        @Schema(description = "커스텀 목표 (type이 CUSTOM일 때 필수)", example = "디지털 디톡스 챌린지 성공하기", maxLength = 100)
        private String custom;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "스크린 타임 목표 정보")
    public static class ScreenTimeGoal {
        @NotNull(message = "스크린 타임 목표 타입은 필수입니다")
        @Schema(description = "스크린 타임 목표 타입", example = "2HOURS", allowableValues = {"2HOURS", "4HOURS", "6HOURS", "8HOURS", "CUSTOM"})
        private String type;

        @Size(max = 100, message = "커스텀 스크린 타임 목표는 100자 이하여야 합니다")
        @Schema(description = "커스텀 스크린 타임 목표 (type이 CUSTOM일 때 필수)", example = "3시간", maxLength = 100)
        private String custom;
    }

    public GoalType getGoalType() {
        return goal.getType();
    }

    public String getGoalCustom() {
        return goal.getCustom();
    }

    public ScreenTimeGoalType getScreenTimeGoalType() {
        String type = screenTimeGoal.getType();
        switch (type) {
            case "2HOURS": return ScreenTimeGoalType.TWO_HOURS;
            case "4HOURS": return ScreenTimeGoalType.FOUR_HOURS;
            case "6HOURS": return ScreenTimeGoalType.SIX_HOURS;
            case "8HOURS": return ScreenTimeGoalType.EIGHT_HOURS;
            case "CUSTOM": return ScreenTimeGoalType.CUSTOM;
            default: throw new IllegalArgumentException("Invalid screen time goal type: " + type);
        }
    }

    public String getScreenTimeGoalCustom() {
        return screenTimeGoal.getCustom();
    }
}
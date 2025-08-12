package org.minu.dnd13th3backend.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.minu.dnd13th3backend.user.entity.Profile;
import org.minu.dnd13th3backend.user.type.GoalType;

@Getter
@Builder
@Schema(description = "프로필 상세 조회 응답")
public class ProfileDetailResponse {

    @Schema(description = "사용자 ID", example = "1")
    private String id;

    @Schema(description = "사용자 닉네임", example = "종훈")
    private String nickname;

    @Schema(description = "목표 정보")
    private Goal goal;

    @Schema(description = "스크린 타임 목표 정보")
    private ScreenTimeGoal screenTimeGoal;

    @Getter
    @Builder
    @Schema(description = "목표 정보")
    public static class Goal {
        @Schema(description = "목표 타입", example = "FOCUS_IMPROVEMENT")
        private GoalType type;

        @Schema(description = "커스텀 목표", example = "디지털 디톡스 챌린지")
        private String custom;
    }

    @Getter
    @Builder
    @Schema(description = "스크린 타임 목표 정보")
    public static class ScreenTimeGoal {
        @Schema(description = "스크린 타임 목표 타입", example = "2HOURS")
        private String type;

        @Schema(description = "커스텀 스크린 타임 목표", example = "3시간")
        private String custom;
    }

    public static ProfileDetailResponse from(Profile profile) {
        return ProfileDetailResponse.builder()
                .id(profile.getUserId().toString())
                .nickname(profile.getNickname())
                .goal(Goal.builder()
                        .type(profile.getGoalType())
                        .custom(profile.getGoalCustom())
                        .build())
                .screenTimeGoal(ScreenTimeGoal.builder()
                        .type(profile.getScreenTimeGoalType().getValue())
                        .custom(profile.getScreenTimeGoalCustom())
                        .build())
                .build();
    }
}
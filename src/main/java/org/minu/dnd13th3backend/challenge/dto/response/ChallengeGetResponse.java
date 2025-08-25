package org.minu.dnd13th3backend.challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ChallengeGetResponse {

    private Long challengeId;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    private String title;

    @JsonProperty("goal_time_minutes")
    private int goalTimeMinutes;

    private List<ParticipantRecord> participants;

    // 참가자 한 명의 정보를 담는 내부 클래스
    @Getter
    @Builder
    public static class ParticipantRecord {
        private Long userId;
        private String nickname;

        private Integer characterIndex;

        @JsonProperty("current_time_minutes")
        private long currentTimeMinutes;

        @JsonProperty("instagram_minutes")
        private long instagramMinutes;

        @JsonProperty("youtube_minutes")
        private long youtubeMinutes;

        @JsonProperty("kakaotalk_minutes")
        private long kakaotalkMinutes;

        @JsonProperty("chrome_minutes")
        private long chromeMinutes;

        @JsonProperty("achievement_rate")
        private double achievementRate;

        private String status;
    }
}

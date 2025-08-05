package org.minu.dnd13th3backend.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.minu.dnd13th3backend.user.type.GoalType;
import org.minu.dnd13th3backend.user.type.ScreenTimeGoalType;

@Entity
@Table(name = "profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "nickname", length = 30)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type")
    private GoalType goalType;

    @Column(name = "goal_custom", length = 100)
    private String goalCustom;

    @Enumerated(EnumType.STRING)
    @Column(name = "screen_time_goal_type")
    private ScreenTimeGoalType screenTimeGoalType;

    @Column(name = "screen_time_goal_custom", length = 100)
    private String screenTimeGoalCustom;


    @Builder
    private Profile(User user, String nickname, GoalType goalType, String goalCustom,
                   ScreenTimeGoalType screenTimeGoalType, String screenTimeGoalCustom) {
        this.user = user;
        this.nickname = nickname;
        this.goalType = goalType;
        this.goalCustom = goalCustom;
        this.screenTimeGoalType = screenTimeGoalType;
        this.screenTimeGoalCustom = screenTimeGoalCustom;
    }

    public void updateProfile(String nickname, GoalType goalType, String goalCustom,
                             ScreenTimeGoalType screenTimeGoalType, String screenTimeGoalCustom) {
        this.nickname = nickname;
        this.goalType = goalType;
        this.goalCustom = goalCustom;
        this.screenTimeGoalType = screenTimeGoalType;
        this.screenTimeGoalCustom = screenTimeGoalCustom;
    }
}
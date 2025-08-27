package org.minu.dnd13th3backend.challenge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.minu.dnd13th3backend.challenge.type.ChallengeStatus;
import org.minu.dnd13th3backend.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(length = 100)
    private String title;

    private int goalTimeMinutes;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Challenge(User creator, String title, int goalTimeMinutes, LocalDate startDate, LocalDate endDate, ChallengeStatus status) {
        this.creator = creator;
        this.title = title;
        this.goalTimeMinutes = goalTimeMinutes;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
}

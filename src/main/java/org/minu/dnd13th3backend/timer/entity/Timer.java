package org.minu.dnd13th3backend.timer.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.minu.dnd13th3backend.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "timer")
public class Timer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 50)
    private String category;

    private int durationHours;
    private int durationMinutes;
    private int durationSeconds;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @Builder
    public Timer(User user, String category, int durationHours, int durationMinutes, int durationSeconds, LocalDateTime startedAt, LocalDateTime endedAt) {
        this.user = user;
        this.category = category;
        this.durationHours = durationHours;
        this.durationMinutes = durationMinutes;
        this.durationSeconds = durationSeconds;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }
}

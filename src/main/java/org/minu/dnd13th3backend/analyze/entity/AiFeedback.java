package org.minu.dnd13th3backend.analyze.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.minu.dnd13th3backend.analyze.type.FeedbackType;
import org.minu.dnd13th3backend.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_feedback")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    private AiFeedback(User user, FeedbackType type, String content, LocalDate referenceDate, LocalDateTime expiresAt) {
        this.user = user;
        this.type = type;
        this.content = content;
        this.referenceDate = referenceDate;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
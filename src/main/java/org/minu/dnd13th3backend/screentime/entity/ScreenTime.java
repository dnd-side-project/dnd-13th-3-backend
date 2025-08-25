package org.minu.dnd13th3backend.screentime.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ScreenTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate date;

    private int instagramMinutes;
    private int youtubeMinutes;
    private int kakaotalkMinutes;
    private int chromeMinutes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public ScreenTime(User user, LocalDate date, int instagramMinutes, int youtubeMinutes, int kakaotalkMinutes, int chromeMinutes) {
        this.user = user;
        this.date = date;
        this.instagramMinutes = instagramMinutes;
        this.youtubeMinutes = youtubeMinutes;
        this.kakaotalkMinutes = kakaotalkMinutes;
        this.chromeMinutes = chromeMinutes;
    }

    public void updateScreenTime(int instagramMinutes, int youtubeMinutes, int kakaotalkMinutes, int chromeMinutes) {
        this.instagramMinutes = instagramMinutes;
        this.youtubeMinutes = youtubeMinutes;
        this.kakaotalkMinutes = kakaotalkMinutes;
        this.chromeMinutes = chromeMinutes;
    }
}

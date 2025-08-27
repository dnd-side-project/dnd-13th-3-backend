package org.minu.dnd13th3backend.screentime.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.minu.dnd13th3backend.user.entity.User;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Column(columnDefinition = "TIMESTAMP")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Instant updatedAt;

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

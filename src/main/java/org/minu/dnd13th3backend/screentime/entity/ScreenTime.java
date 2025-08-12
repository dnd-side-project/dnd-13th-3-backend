package org.minu.dnd13th3backend.screentime.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.minu.dnd13th3backend.user.entity.User;

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
    private Integer screentimeMinutes;

    @Builder
    public ScreenTime(User user, LocalDate date, Integer screentimeMinutes) {
        this.user = user;
        this.date = date;
        this.screentimeMinutes = screentimeMinutes;
    }

    public void updateScreenTime(Integer minutes) {
        this.screentimeMinutes = minutes;
    }
}

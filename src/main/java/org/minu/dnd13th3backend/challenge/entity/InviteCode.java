package org.minu.dnd13th3backend.challenge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder; // import 추가
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InviteCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    private String code;

    private LocalDate expiresAt;

    @Builder
    public InviteCode(Challenge challenge, String code, LocalDate expiresAt) {
        this.challenge = challenge;
        this.code = code;
        this.expiresAt = expiresAt;
    }
}

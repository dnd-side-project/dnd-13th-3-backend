package org.minu.dnd13th3backend.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthInfo {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "oauth_id", length = 100)
    private String oauthId;

    @Column(name = "provider", length = 50)
    private String provider;

    @Builder
    private OauthInfo(User user, String oauthId, String provider) {
        this.user = user;
        this.oauthId = oauthId;
        this.provider = provider;
    }
}

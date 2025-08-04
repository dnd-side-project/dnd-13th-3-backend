package org.minu.dnd13th3backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.auth.dto.TokenResponse;
import org.minu.dnd13th3backend.common.exception.BusinessException;
import org.minu.dnd13th3backend.user.entity.OauthInfo;
import org.minu.dnd13th3backend.user.entity.User;
import org.minu.dnd13th3backend.user.repository.OauthInfoRepository;
import org.minu.dnd13th3backend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final OauthInfoRepository oauthInfoRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponse processOAuth2Login(OAuth2User oauth2User) {
        if (oauth2User == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "OAuth2 인증 정보가 없습니다.");
        }
        
        String googleId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        if (googleId == null || email == null || name == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "필수 OAuth2 정보가 누락되었습니다.");
        }

        OauthInfo oauthInfo = oauthInfoRepository.findByOauthIdAndProvider(googleId, "google")
                .orElseGet(() -> createNewUser(googleId, email, name));

        User user = oauthInfo.getUser();
        String accessToken = jwtTokenProvider.createAccessToken(user.getId().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId().toString());

        return TokenResponse.of(accessToken, refreshToken);
    }

    private OauthInfo createNewUser(String googleId, String email, String name) {
        User newUser = User.builder()
                .name(name)
                .email(email)
                .build();
        User savedUser = userRepository.save(newUser);
        
        OauthInfo oauthInfo = OauthInfo.builder()
                .user(savedUser)
                .oauthId(googleId)
                .provider("google")
                .build();
        return oauthInfoRepository.save(oauthInfo);
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");
        }

        String userId = jwtTokenProvider.getSubject(refreshToken);
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId().toString());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId().toString());

        return TokenResponse.of(newAccessToken, newRefreshToken);
    }
}
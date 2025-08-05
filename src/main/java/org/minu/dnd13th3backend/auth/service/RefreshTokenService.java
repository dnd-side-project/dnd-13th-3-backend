package org.minu.dnd13th3backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    public void storeRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(
            "refresh_token:" + userId, 
            refreshToken, 
            refreshTokenValidityInSeconds, 
            TimeUnit.SECONDS
        );
    }

    public boolean isValidRefreshToken(String userId, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get("refresh_token:" + userId);
        return refreshToken.equals(storedToken);
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("refresh_token:" + userId);
    }
}
package com.ticketPing.auth.infrastructure.service;

import caching.repository.RedisRepository;
import com.ticketPing.auth.application.service.CacheService;
import com.ticketPing.auth.common.exception.AuthErrorCase;
import exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static com.ticketPing.auth.common.constants.AuthConstants.REFRESH_TOKEN_EXPIRATION;

@Service
@RequiredArgsConstructor
public class RedisCacheService implements CacheService {

    private final RedisRepository redisRepository;

    public void saveRefreshToken(UUID userId, String refreshToken) {
        String key = generateKey(userId);
        redisRepository.setValueWithTTL(key, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRATION));
    }

    public String getRefreshToken(UUID userId) {
        String key = generateKey(userId);
        return Optional.ofNullable(redisRepository.getValueAsClass(key, String.class))
                .orElseThrow(() -> new ApplicationException(AuthErrorCase.REFRESH_TOKEN_NOT_FOUND));
    }

    public void deleteRefreshToken(UUID userId) {
        String key = generateKey(userId);
        redisRepository.deleteKey(key);
    }

    private String generateKey(UUID userId) {
        return String.format("refreshToken:%s", userId);
    }

}

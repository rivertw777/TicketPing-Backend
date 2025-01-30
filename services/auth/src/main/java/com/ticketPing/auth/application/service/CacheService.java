package com.ticketPing.auth.application.service;

import java.util.UUID;

public interface CacheService {
    public void saveRefreshToken(UUID userId, String refreshToken);

    public String getRefreshToken(UUID userId);

    public void deleteRefreshToken(UUID userId);
}

package com.ticketPing.auth.common.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class AuthConstants {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String REFRESH_COOKIE = "refreshToken";

    public static long REFRESH_TOKEN_EXPIRATION;
    public static long ACCESS_TOKEN_EXPIRATION;

    private AuthConstants(@Value("${jwt.refreshToken.expiration}") long refreshTokenExpiration,
                          @Value("${jwt.accessToken.expiration}") long accessTokenExpiration) {
        REFRESH_TOKEN_EXPIRATION = refreshTokenExpiration;
        ACCESS_TOKEN_EXPIRATION = accessTokenExpiration;
    }
}
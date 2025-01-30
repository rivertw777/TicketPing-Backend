package com.ticketPing.gateway.common.utils;

import static caching.enums.RedisKeyPrefix.TOKEN_VALUE;
import static com.ticketPing.gateway.common.utils.ConfigHolder.tokenValueSecretKey;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TokenValueGenerator {

    public static String generateTokenValue(String userId, String performanceId) {
        try {
            String input = userId + tokenValueSecretKey();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return TOKEN_VALUE.getValue() + performanceId + ":" + Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

}

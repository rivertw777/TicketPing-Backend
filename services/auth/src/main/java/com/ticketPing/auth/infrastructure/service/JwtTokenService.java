package com.ticketPing.auth.infrastructure.service;

import com.ticketPing.auth.application.service.TokenService;
import com.ticketPing.auth.common.exception.AuthErrorCase;
import com.ticketPing.auth.common.enums.Role;
import exception.ApplicationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static com.ticketPing.auth.common.constants.AuthConstants.*;

@Component
public class JwtTokenService implements TokenService {

    private final Key secretKey;

    public JwtTokenService(@Value("${jwt.secret}") String secret) {
        byte[] bytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(UUID userId, Role role) {
        Date now = new Date();
        return Jwts.builder()
                        .setSubject(userId.toString())
                        .claim("role", role)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION))
                        .signWith(this.secretKey, SignatureAlgorithm.HS256)
                        .compact();
    }

    public String createRefreshToken(UUID userId, Role role) {
        Date now = new Date();
        return Jwts.builder()
                        .setSubject(userId.toString())
                        .claim("role", role)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION))
                        .signWith(this.secretKey, SignatureAlgorithm.HS256)
                        .compact();
    }

    public String parseToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new ApplicationException(AuthErrorCase.INVALID_TOKEN);
        }
        return authHeader.substring(7);
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ApplicationException(AuthErrorCase.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new ApplicationException(AuthErrorCase.INVALID_TOKEN);
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    public UUID getUserId(Claims claims) {
        try {
            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            throw new ApplicationException(AuthErrorCase.INVALID_TOKEN);
        }
    }

    public Role getUserRole(Claims claims) {
        try {
            return Role.getRole(claims.get("role", String.class));
        } catch (NullPointerException e) {
            throw new ApplicationException(AuthErrorCase.INVALID_TOKEN);
        }
    }
}

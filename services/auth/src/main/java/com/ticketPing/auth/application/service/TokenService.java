package com.ticketPing.auth.application.service;

import com.ticketPing.auth.common.enums.Role;
import io.jsonwebtoken.Claims;
import java.util.UUID;

public interface TokenService {

    public String createAccessToken(UUID userId, Role role);

    public String createRefreshToken(UUID userId, Role role);

    public String parseToken(String authHeader);

    public void validateToken(String token);

    public Claims getClaimsFromToken(String token);

    public UUID getUserId(Claims claims);

    public Role getUserRole(Claims claims);
}

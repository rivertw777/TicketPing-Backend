package com.ticketPing.auth.application.service;

import auth.UserCacheDto;
import com.ticketPing.auth.application.client.UserClient;
import com.ticketPing.auth.application.dto.TokenResponse;
import com.ticketPing.auth.common.enums.Role;
import com.ticketPing.auth.common.exception.AuthErrorCase;
import com.ticketPing.auth.presentation.request.LoginRequest;
import exception.ApplicationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.UserLookupRequest;
import user.UserResponse;

import java.util.UUID;

import static com.ticketPing.auth.common.constants.AuthConstants.BEARER_PREFIX;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenService tokenService;
    private final CacheService cacheService;
    private final CookieService cookieService;
    private final UserClient userClient;

    public TokenResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        UUID userId = authenticateUser(loginRequest);
        createAndSaveRefreshToken(userId, Role.USER, response);
        String accessToken = createAccessToken(userId, Role.USER);
        return TokenResponse.of(accessToken);
    }

    public UserCacheDto validateToken(String authHeader) {
        String accessToken = tokenService.parseToken(authHeader);
        tokenService.validateToken(accessToken);
        Claims claims = tokenService.getClaimsFromToken(accessToken);
        return extractUserFromClaims(claims);
    }

    public TokenResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String cookieRefreshToken = getValidatedRefreshToken(request);

        Claims claims = tokenService.getClaimsFromToken(cookieRefreshToken);
        UUID userId = tokenService.getUserId(claims);
        Role userRole = tokenService.getUserRole(claims);

        validateStoredRefreshToken(cookieRefreshToken, userId, response);

        createAndSaveRefreshToken(userId, userRole, response);
        String newAccessToken = createAccessToken(userId, userRole);

        return TokenResponse.of(newAccessToken);
    }

    public void logout(UUID userId, HttpServletResponse response) {
        cacheService.deleteRefreshToken(userId);
        cookieService.deleteRefreshToken(response);
    }

    private UUID authenticateUser(LoginRequest loginRequest) {
        UserLookupRequest request = new UserLookupRequest(loginRequest.email(), loginRequest.password());
        UserResponse userResponse = userClient.getUserByEmailAndPassword(request).getData();
        return userResponse.userId();
    }

    private String createAccessToken(UUID userId, Role role) {
        return BEARER_PREFIX + tokenService.createAccessToken(userId, role);
    }

    private void createAndSaveRefreshToken(UUID userId, Role role, HttpServletResponse response) {
        String refreshToken = tokenService.createRefreshToken(userId, role);
        cacheService.saveRefreshToken(userId, refreshToken);
        cookieService.setRefreshToken(response, refreshToken);
    }

    private UserCacheDto extractUserFromClaims(Claims claims) {
        UUID userId = tokenService.getUserId(claims);
        Role role = tokenService.getUserRole(claims);
        return new UserCacheDto(userId, role.getValue());
    }

    private String getValidatedRefreshToken(HttpServletRequest request) {
        String refreshToken = cookieService.getRefreshToken(request);
        tokenService.validateToken(refreshToken);
        return refreshToken;
    }

    private void validateStoredRefreshToken(String cookieRefreshToken, UUID userId, HttpServletResponse response) {
        String storedRefreshToken = cacheService.getRefreshToken(userId);
        if (!cookieRefreshToken.equals(storedRefreshToken)) {
            logout(userId, response);
            throw new ApplicationException(AuthErrorCase.INVALID_REFRESH_TOKEN);
        }
    }
}
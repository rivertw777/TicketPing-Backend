package com.ticketPing.auth.application.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    public void setRefreshToken(HttpServletResponse response, String refreshToken);

    public String getRefreshToken(HttpServletRequest request);

    public void deleteRefreshToken(HttpServletResponse response);
}

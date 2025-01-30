package com.ticketPing.performance.infrastructure.service;

import com.ticketPing.performance.application.service.NotificationService;
import com.ticketPing.performance.common.exception.MessageExceptionCase;
import exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationService implements NotificationService {

    @Value("${discord.webhook-url}")
    private String discordWebhookUrl;

    private final RestTemplate restTemplate;

    @Override
    public void sendErrorNotification(String errorMessage) {
        try {
            String message = String.format("**Error occurred in SeatCacheScheduler**: %s", errorMessage);
            String payload = String.format("{\"content\": \"%s\"}", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            restTemplate.exchange(discordWebhookUrl, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            log.error("Error occurred during execution: {}", e.getMessage(), e);
            throw new ApplicationException(MessageExceptionCase.MESSAGE_SEND_FAIL);
        }
    }
}

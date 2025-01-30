package com.ticketPing.performance.infrastructure.config;

import com.ticketPing.performance.infrastructure.listener.RedisKeyExpiredListener;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
public class RedissonMessageListenerConfig {

    private final RedissonClient redissonClient;
    private final RedisKeyExpiredListener redisKeyExpiredListener;

    @EventListener(ApplicationReadyEvent.class)
    public void addMessageListener() {
        RTopic topic = redissonClient.getTopic("__keyevent@0__:expired");
        topic.addListener(String.class, redisKeyExpiredListener);
    }

}

package com.ticketPing.queue_manage.domain.command.waitingQueue;

import static caching.enums.RedisKeyPrefix.WAITING_QUEUE;
import static caching.enums.RedisKeyPrefix.WORKING_QUEUE;
import static com.ticketPing.queue_manage.common.utils.ConfigHolder.initialWorkingQueueTokenTTL;
import static com.ticketPing.queue_manage.common.utils.TokenValueGenerator.generateTokenValue;
import static com.ticketPing.queue_manage.common.utils.ConfigHolder.workingQueueMaxSize;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * tokenValue: 사용자 토큰 값 (대기열 Sorted Set 멤버 or 작업열 토큰 키)
 * waitingQueueName: 대기열 이름 (대기열 Sorted Set 키)
 * enterTime: 대기열 진입 시간 (Sorted Set 스코어)
 * workingQueueName: 작업열 이름 (작업 인원 카운터 키)
 * cacheValue: 작업열 토큰 value
 * ttl: 작업열 토큰 TTL
 * workingQueueMaxSize: 작업열 최대 크기
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class InsertWaitingQueueTokenCommand {

    private String userId;
    private String performanceId;
    private String tokenValue;
    private String waitingQueueName;
    private double enterTime;
    private String workingQueueName;
    private String cacheValue;
    private long ttlInMinutes;
    private int workingQueueMaxSlots;

    public static InsertWaitingQueueTokenCommand create(String userId, String performanceId) {
        return InsertWaitingQueueTokenCommand.builder()
                .userId(userId)
                .performanceId(performanceId)
                .tokenValue(generateTokenValue(userId, performanceId))
                .waitingQueueName(WAITING_QUEUE.getValue() + performanceId)
                .enterTime(System.currentTimeMillis() / 1000.0)
                .workingQueueName(WORKING_QUEUE.getValue() + performanceId)
                .cacheValue("NA")
                .ttlInMinutes(initialWorkingQueueTokenTTL())
                .workingQueueMaxSlots(workingQueueMaxSize())
                .build();
    }

}

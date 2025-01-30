package com.ticketPing.queue_manage.domain.command.workingQueue;

import static com.ticketPing.queue_manage.common.utils.ConfigHolder.extendedWorkingQueueTokenTTL;
import static com.ticketPing.queue_manage.common.utils.TokenValueGenerator.generateTokenValue;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ExtendWorkingQueueTokenTTLCommand {

    private String userId;
    private String performanceId;
    private String tokenValue;
    private String cacheValue;
    private long ttlInMinutes;

    public static ExtendWorkingQueueTokenTTLCommand create(String userId, String performanceId) {
        return ExtendWorkingQueueTokenTTLCommand.builder()
                .userId(userId)
                .performanceId(performanceId)
                .tokenValue(generateTokenValue(userId, performanceId))
                .ttlInMinutes(extendedWorkingQueueTokenTTL())
                .build();
    }

}

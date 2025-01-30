package com.ticketPing.performance.infrastructure.listener;

import com.ticketPing.performance.application.service.EventApplicationService;
import com.ticketPing.performance.infrastructure.service.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import messaging.events.SeatPreReserveExpiredEvent;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.ticketPing.performance.common.constants.SeatConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisKeyExpiredListener implements MessageListener<String> {

    private final DistributedLockService lockService;
    private final EventApplicationService eventApplicationService;

    private static final int LOCK_TIMEOUT = 60;

    @Override
    public void onMessage(CharSequence channel, String expiredKey) {
        if(expiredKey.startsWith(PRE_RESERVE_SEAT_KEY) && expiredKey.split(":").length == 3) {
            log.info("Seat ttl key has expired: {}", expiredKey);

            String scheduleId = expiredKey.split(":")[1].replaceAll("[{}]", "");
            String seatId = expiredKey.split(":")[2];
            String lockKey = PRE_RESERVE_EXPIRE_LOCK_KEY + ":" + seatId;

            try {
                boolean executed = lockService.executeWithLock(lockKey, 0, LOCK_TIMEOUT, () -> {
                    publishPreReserveExpire(UUID.fromString(scheduleId), UUID.fromString(seatId));
                });

                if (!executed) {
                    log.warn("Another server is running");
                }

                log.info("Successfully handle expired seat TTL key: {}", expiredKey);
            } catch (Exception e) {
                log.error("Error occurred while handling expired seat TTL key [{}]: {}", expiredKey, e.getMessage(), e);
            }

        }
    }

    private void publishPreReserveExpire(UUID scheduleId, UUID seatId) {
        val event = SeatPreReserveExpiredEvent.create(scheduleId, seatId);
        eventApplicationService.publishSeatPreReserveExpiredEvent(event);
    }

}

package com.ticketPing.performance.application.scheduler;

import com.ticketPing.performance.application.service.NotificationService;
import com.ticketPing.performance.application.service.PerformanceService;
import com.ticketPing.performance.domain.model.entity.Performance;
import com.ticketPing.performance.infrastructure.service.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.ticketPing.performance.common.constants.SeatConstants.CACHE_SCHEDULER_LOCK_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatCacheScheduler {

    private final PerformanceService performanceService;
    private final DistributedLockService lockService;
    private final NotificationService notificationService;

    private static final int LOCK_TIMEOUT = 300;

    @Scheduled(cron = "0 0/10 * * * *")
    public void runScheduler() {
        log.info("Scheduler triggered");
        try {
            boolean executed = lockService.executeWithLock(CACHE_SCHEDULER_LOCK_KEY, 0, LOCK_TIMEOUT, this::cacheSeatsForUpcomingPerformance);
            if (!executed) {
                log.warn("Another server is running the scheduler");
            }
        } catch (Exception e) {
            log.error("Unexpected error in scheduler: {}", e.getMessage(), e);
            notificationService.sendErrorNotification(
                    String.format("Error in SeatCacheScheduler: %s", e.getMessage())
            );
        }
    }

    private void cacheSeatsForUpcomingPerformance() {
        Performance performance = performanceService.getUpcomingPerformance();
        if (performance != null) {
            performanceService.cacheAllSeatsForPerformance(performance.getId());
            log.info("Caching completed for performance ID: {}", performance.getId());
        } else {
            log.info("No upcoming performance found");
        }
    }
}


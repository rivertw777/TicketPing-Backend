package com.ticketPing.performance.common.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SeatConstants {

    public static final String SEAT_CACHE_KEY = "seat";
    public static final String PRE_RESERVE_SEAT_KEY = "seat-ttl";
    public static final String CACHE_SCHEDULER_LOCK_KEY = "SchedulerLock";
    public static final String PRE_RESERVE_EXPIRE_LOCK_KEY = "PreReserveLock:";


    public static int PRE_RESERVE_TTL;

    private SeatConstants(@Value("${seat.pre-reserve-ttl}") int preReserveTtl) {
        PRE_RESERVE_TTL = preReserveTtl;
    }
}

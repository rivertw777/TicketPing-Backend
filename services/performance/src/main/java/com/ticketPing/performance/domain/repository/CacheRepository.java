package com.ticketPing.performance.domain.repository;

import com.ticketPing.performance.domain.model.entity.SeatCache;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public interface CacheRepository {
    void cacheSeats(UUID scheduleId, Map<String, SeatCache> seatMap, Duration ttl);

    Map<String, SeatCache> getSeatCaches(UUID scheduleId);

    SeatCache getSeatCache(UUID scheduleId, UUID seatId);

    void putSeatCache(SeatCache seatCache, UUID scheduleId, UUID seatId);

    void preReserveSeatCache(UUID scheduleId, UUID seatId, UUID userId);

    String getPreReserveUserId(UUID scheduleId, UUID seatId);

    void extendPreReserveTTL(UUID scheduleId, UUID seatId, Duration ttl);

    void deletePreReserveTTL(UUID scheduleId, UUID seatId);

    void cacheAvailableSeats(UUID performanceId, long availableSeats);
}

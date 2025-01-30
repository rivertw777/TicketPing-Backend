package com.ticketPing.performance.application.service;

import com.ticketPing.performance.application.dtos.OrderSeatResponse;
import com.ticketPing.performance.common.exception.SeatExceptionCase;
import com.ticketPing.performance.domain.model.entity.Schedule;
import com.ticketPing.performance.domain.model.entity.Seat;
import com.ticketPing.performance.domain.model.entity.SeatCache;
import com.ticketPing.performance.domain.model.enums.SeatStatus;
import com.ticketPing.performance.domain.repository.CacheRepository;
import com.ticketPing.performance.domain.repository.SeatRepository;
import exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ticketPing.performance.common.constants.SeatConstants.PRE_RESERVE_TTL;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final CacheRepository cacheRepository;

    public void preReserveSeat(UUID scheduleId, UUID seatId, UUID userId) {
        cacheRepository.preReserveSeatCache(scheduleId, seatId, userId);
    }

    public void cancelPreReserveSeat(UUID scheduleId, UUID seatId, UUID userId) {
        validatePreserve(scheduleId, seatId, userId);
        cacheRepository.deletePreReserveTTL(scheduleId, seatId);
        cancelPreReserveSeatInCache(scheduleId, seatId);
    }

    public void cancelPreReserveSeatInCache(UUID scheduleId, UUID seatId) {
        SeatCache seatCache = cacheRepository.getSeatCache(scheduleId, seatId);
        seatCache.cancelPreReserveSeat();
        cacheRepository.putSeatCache(seatCache, scheduleId, seatId);
    }

    @Transactional
    public void reserveSeat(String scheduleId, String seatId) {
        reserveSeatInDB(seatId);
        reserveSeatInCache(UUID.fromString(scheduleId), UUID.fromString(seatId));
    }

    public OrderSeatResponse getOrderSeatInfo(UUID scheduleId, UUID seatId, UUID userId) {
        validatePreserve(scheduleId, seatId, userId);
        Seat seat = getSeatWithDetails(seatId);
        extendPreReserveTTL(scheduleId, seatId);
        return OrderSeatResponse.of(seat);
    }

    public void extendPreReserveTTL(UUID scheduleId, UUID seatId) {
        cacheRepository.extendPreReserveTTL(scheduleId, seatId, Duration.ofSeconds(PRE_RESERVE_TTL));
    }

    public long cacheSeatsForSchedule(Schedule schedule) {
        List<Seat> seats = seatRepository.findByScheduleWithSeatCost(schedule);

        Map<String, SeatCache> seatMap = seats.stream().collect(Collectors.toMap(seat -> seat.getId().toString(), SeatCache::from));

        LocalDateTime expiration = schedule.getStartDate().atTime(23, 59, 59);
        Duration ttl = Duration.between(LocalDateTime.now(), expiration);

        cacheRepository.cacheSeats(schedule.getId(), seatMap, ttl);

        return seats.stream().filter(seat -> seat.getSeatStatus() == SeatStatus.AVAILABLE).count();
    }

    private Seat getSeatWithDetails(UUID seatId) {
        return seatRepository.findByIdWithAll(seatId)
                .orElseThrow(() -> new ApplicationException(SeatExceptionCase.SEAT_NOT_FOUND));
    }

    private void reserveSeatInDB(String seatId) {
        Seat seat = seatRepository.findById(UUID.fromString(seatId))
                .orElseThrow(() ->  new ApplicationException(SeatExceptionCase.SEAT_NOT_FOUND));
        seat.reserveSeat();
    }

    private void validatePreserve(UUID scheduleId, UUID seatId, UUID userId) {
        String preReserveUserId = cacheRepository.getPreReserveUserId(scheduleId, seatId);
        if(!preReserveUserId.equals(userId.toString()))
            throw new ApplicationException(SeatExceptionCase.USER_NOT_MATCH);
    }

    private void reserveSeatInCache(UUID scheduleId, UUID seatId) {
        cacheRepository.deletePreReserveTTL(scheduleId, seatId);
        SeatCache seatCache = cacheRepository.getSeatCache(scheduleId, seatId);
        seatCache.reserveSeat();
        cacheRepository.putSeatCache(seatCache, scheduleId, seatId);
    }
}



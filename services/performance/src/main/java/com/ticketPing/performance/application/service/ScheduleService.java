package com.ticketPing.performance.application.service;

import com.ticketPing.performance.application.dtos.SeatResponse;
import com.ticketPing.performance.domain.model.entity.SeatCache;
import com.ticketPing.performance.infrastructure.repository.CacheRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final CacheRepositoryImpl cacheRepositoryImpl;

    public List<SeatResponse> getAllScheduleSeats(UUID scheduleId) {
        Map<String, SeatCache> seatMap = cacheRepositoryImpl.getSeatCaches(scheduleId);
        return seatMap.values().stream().map(SeatResponse::of).toList();
    }
}

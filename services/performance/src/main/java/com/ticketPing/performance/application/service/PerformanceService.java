package com.ticketPing.performance.application.service;

import com.ticketPing.performance.application.dtos.PerformanceListResponse;
import com.ticketPing.performance.application.dtos.PerformanceResponse;
import com.ticketPing.performance.application.dtos.ScheduleResponse;
import com.ticketPing.performance.common.exception.PerformanceExceptionCase;
import com.ticketPing.performance.domain.model.entity.Performance;
import com.ticketPing.performance.domain.model.entity.Schedule;
import com.ticketPing.performance.domain.repository.PerformanceRepository;
import com.ticketPing.performance.infrastructure.repository.CacheRepositoryImpl;
import exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final CacheRepositoryImpl cacheRepositoryImpl;
    private final SeatService seatService;

    public PerformanceResponse getPerformance(UUID performanceId) {
        Performance performance = findPerformanceWithDetails(performanceId);
        return PerformanceResponse.of(performance);
    }

    public Slice<PerformanceListResponse> getAllPerformances(Pageable pageable) {
        return performanceRepository.findAllWithPerformanceHall(pageable)
                .map(PerformanceListResponse::of);
    }

    public List<ScheduleResponse> getPerformanceSchedules(UUID performanceId) {
        Performance performance = findPerformanceWithSchedules(performanceId);
        return performance.getSchedules().stream()
                .map(ScheduleResponse::of)
                .toList();
    }

    public Performance getUpcomingPerformance() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesLater = now.plusMinutes(10);

        return performanceRepository.findUpcomingPerformance(now, tenMinutesLater);
    }

    public void cacheAllSeatsForPerformance(UUID performanceId) {
        Performance performance = findPerformanceWithSchedules(performanceId);
        List<Schedule> schedules = performance.getSchedules();

        long totalAvailableSeats = 0;
        for (Schedule schedule : schedules) {
            long availableSeats = seatService.cacheSeatsForSchedule(schedule);
            totalAvailableSeats += availableSeats;
        }
        cacheRepositoryImpl.cacheAvailableSeats(performanceId, totalAvailableSeats);
    }

    private Performance findPerformanceWithSchedules(UUID id) {
        return performanceRepository.findByIdWithSchedules(id)
                .orElseThrow(() -> new ApplicationException(PerformanceExceptionCase.PERFORMANCE_NOT_FOUND));
    }

    private Performance findPerformanceWithDetails(UUID id) {
        return performanceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ApplicationException(PerformanceExceptionCase.PERFORMANCE_NOT_FOUND));
    }
}


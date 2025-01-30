package com.ticketPing.performance.domain.repository;

import com.ticketPing.performance.domain.model.entity.Performance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PerformanceRepository {
    Performance save(Performance performance);

    Slice<Performance> findAllWithPerformanceHall(Pageable pageable);

    Performance findByName(String name);

    Optional<Performance> findByIdWithSchedules(UUID id);

    Optional<Performance> findByIdWithDetails(UUID id);

    Performance findUpcomingPerformance(LocalDateTime start, LocalDateTime end);
}

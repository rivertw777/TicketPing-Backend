package com.ticketPing.performance.domain.repository;

import com.ticketPing.performance.domain.model.entity.Schedule;
import com.ticketPing.performance.domain.model.entity.Seat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository {
    Seat save(Seat seat);

    Optional<Seat> findById(UUID uuid);

    Optional<Seat> findByIdWithAll(UUID seatId);

    List<Seat> findByScheduleWithSeatCost(Schedule schedule);
}

package com.ticketPing.performance.application.dtos;

import com.ticketPing.performance.domain.model.entity.Performance;
import com.ticketPing.performance.domain.model.entity.PerformanceHall;
import com.ticketPing.performance.domain.model.entity.Schedule;
import com.ticketPing.performance.domain.model.entity.Seat;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record OrderSeatResponse(
        UUID performanceId,
        String performanceName,
        UUID scheduleId,
        LocalDate startDate,
        UUID performanceHallId,
        String performanceHallName,
        UUID companyId,
        UUID seatId,
        Integer row,
        Integer col,
        String seatGrade,
        Integer cost
) {
    public static OrderSeatResponse of(Seat seat) {
        Schedule schedule = seat.getSchedule();
        Performance performance = schedule.getPerformance();
        PerformanceHall performanceHall = performance.getPerformanceHall();

        return OrderSeatResponse.builder()
                .performanceId(performance.getId())
                .performanceName(performance.getName())
                .scheduleId(schedule.getId())
                .startDate(schedule.getStartDate())
                .performanceHallId(performanceHall.getId())
                .performanceHallName(performanceHall.getName())
                .companyId(performance.getCompanyId()).seatId(seat.getId())
                .row(seat.getRow())
                .col(seat.getCol())
                .seatGrade(seat.getSeatCost().getSeatGrade())
                .cost(seat.getSeatCost().getCost())
                .build();
    }
}

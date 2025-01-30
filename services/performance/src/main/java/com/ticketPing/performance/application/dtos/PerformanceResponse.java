package com.ticketPing.performance.application.dtos;

import com.ticketPing.performance.domain.model.entity.Performance;
import com.ticketPing.performance.domain.model.entity.SeatCost;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder(access = AccessLevel.PRIVATE)
public record PerformanceResponse(
        UUID id,
        String name,
        String posterUrl,
        int runTime,
        LocalDateTime reservationStartDate,
        LocalDateTime reservationEndDate,
        LocalDate startDate,
        LocalDate endDate,
        int grade,
        UUID companyId,
        String performanceHallName,
        int rows,
        int columns,
        List<SeatCostResponse> seatCostResponses
){
    public static PerformanceResponse of(Performance performance) {
        return PerformanceResponse.builder()
                .id(performance.getId())
                .name(performance.getName())
                .posterUrl(performance.getPosterUrl())
                .runTime(performance.getRunTime())
                .reservationStartDate(performance.getReservationStartDate())
                .reservationEndDate(performance.getReservationEndDate())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .grade(performance.getGrade())
                .companyId(performance.getCompanyId())
                .performanceHallName(performance.getPerformanceHall().getName())
                .rows(performance.getPerformanceHall().getRows())
                .columns(performance.getPerformanceHall().getColumns())
                .seatCostResponses(performance.getSeatCosts().stream()
                        .map(SeatCostResponse::of)
                        .collect(Collectors.toList())
                )
                .build();
    }
}

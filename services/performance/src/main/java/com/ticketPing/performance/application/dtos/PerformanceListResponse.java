package com.ticketPing.performance.application.dtos;

import com.ticketPing.performance.domain.model.entity.Performance;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PerformanceListResponse(
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
        String performanceHallName
){
    public static PerformanceListResponse of(Performance performance) {
        return PerformanceListResponse.builder()
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
                .build();
    }
}

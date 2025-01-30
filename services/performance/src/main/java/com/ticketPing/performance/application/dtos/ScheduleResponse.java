package com.ticketPing.performance.application.dtos;

import com.ticketPing.performance.domain.model.entity.Schedule;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record ScheduleResponse (
        UUID id,
        UUID performanceId,
        LocalDate startDate
){
    public static ScheduleResponse of(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .performanceId(schedule.getPerformance().getId())
                .startDate(schedule.getStartDate())
                .build();
    }
}



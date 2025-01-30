package com.ticketPing.performance.application.dtos;

import com.ticketPing.performance.domain.model.entity.SeatCost;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SeatCostResponse(
        String seatGrade,
        int cost
) {
    public static SeatCostResponse of(SeatCost seatCost) {
        return SeatCostResponse.builder()
                .seatGrade(seatCost.getSeatGrade())
                .cost(seatCost.getCost())
                .build();
    }
}
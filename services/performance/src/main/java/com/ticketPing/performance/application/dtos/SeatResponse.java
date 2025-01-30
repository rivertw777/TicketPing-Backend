package com.ticketPing.performance.application.dtos;

import com.ticketPing.performance.domain.model.entity.Seat;
import com.ticketPing.performance.domain.model.entity.SeatCache;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;


@Builder(access = AccessLevel.PRIVATE)
public record SeatResponse (
        UUID seatId,
        Integer row,
        Integer col,
        String seatStatus,
        String seatGrade
) {
    public static SeatResponse of(Seat seat) {
        return SeatResponse.builder()
                .seatId(seat.getId())
                .row(seat.getRow())
                .col(seat.getCol())
                .seatStatus(seat.getSeatStatus().getValue())
                .seatGrade(seat.getSeatCost().getSeatGrade())
                .build();
    }

    public static SeatResponse of(SeatCache seatCache) {
        return SeatResponse.builder()
                .seatId(seatCache.getId())
                .row(seatCache.getRow())
                .col(seatCache.getCol())
                .seatStatus(seatCache.getSeatStatus())
                .seatGrade(seatCache.getSeatGrade())
                .build();
    }
}

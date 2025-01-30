package com.ticketPing.performance.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ticketPing.performance.domain.model.enums.SeatStatus;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class SeatCache {
    private UUID id;
    private Integer row;
    private Integer col;
    private String seatStatus;
    private String seatGrade;

    public static SeatCache from(Seat seat) {
        return SeatCache.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .col(seat.getCol())
                .seatStatus(seat.getSeatStatus().getValue())
                .seatGrade(seat.getSeatCost().getSeatGrade())
                .build();
    }

    public void cancelPreReserveSeat() {
        seatStatus = SeatStatus.AVAILABLE.getValue();
    }

    public void reserveSeat() {
        seatStatus = SeatStatus.RESERVED.getValue();
    }
}

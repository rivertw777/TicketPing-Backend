package com.ticketPing.performance.domain.model.enums;

import com.ticketPing.performance.common.exception.SeatExceptionCase;
import exception.ApplicationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SeatStatus {
    AVAILABLE("AVAILABLE"),
    HELD("HELD"),
    RESERVED("RESERVED");

    private final String value;

    public static SeatStatus getSeatStatus(final String value) {
        return Arrays.stream(SeatStatus.values())
        .filter(t -> t.getValue().equals(value))
        .findAny().orElseThrow(() -> new ApplicationException(SeatExceptionCase.INVALID_SEAT_STATUS));
        }
}

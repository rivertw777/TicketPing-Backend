package com.ticketPing.performance.common.exception;

import exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SeatExceptionCase implements ErrorCase {
    SEAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "좌석 정보를 찾을 수 없습니다."),
    INVALID_SEAT_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 좌석 상태입니다."),
    SEAT_CACHE_NOT_FOUND(HttpStatus.BAD_REQUEST, "좌석 캐싱 정보를 찾을 수 없습니다."),
    SEAT_ALREADY_TAKEN(HttpStatus.BAD_REQUEST, "좌석이 이미 점유되어 있습니다."),
    PRE_RESERVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "좌석 선점 과정에서 오류가 발생했습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    USER_NOT_MATCH(HttpStatus.BAD_REQUEST, "본인이 선점한 좌석이 아닙니다."),
    SEAT_NOT_PRE_RESERVED(HttpStatus.BAD_REQUEST, "좌석이 선점 상태가 아닙니다."),
    TTL_NOT_EXIST(HttpStatus.BAD_REQUEST, "좌석 선점 상태가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public static SeatExceptionCase getByValue(String value) {
        try {
            return SeatExceptionCase.valueOf(value);
        } catch (IllegalArgumentException e) {
            return SeatExceptionCase.SERVER_ERROR;
        }
    }
}

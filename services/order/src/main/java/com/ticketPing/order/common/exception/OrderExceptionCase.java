package com.ticketPing.order.common.exception;

import exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionCase implements ErrorCase {

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "예매정보를 찾을 수 없습니다."),
    INVALID_TTL_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 TTL 명입니다."),
    NOT_FOUND_ORDER_ID_IN_TTL(HttpStatus.NOT_FOUND,"TTL 정보에서 order_id를 찾을 수 없습니다."),
    DUPLICATED_ORDER(HttpStatus.BAD_REQUEST, "중복된 주문입니다."),
    INVALID_ORDER(HttpStatus.BAD_REQUEST, "유효하지 않은 주문입니다."),
    SEAT_CACHE_NOT_FOUND(HttpStatus.NOT_FOUND, "레디스에 공연관련 정보가 캐싱되어 있지 않습니다."),
    ORDER_STATUS_UNKNOWN(HttpStatus.CONFLICT,"저장된 enum 상태값을 사용해야 합니다."),
    INVALID_SEAT_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 좌석 상태를 사용했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

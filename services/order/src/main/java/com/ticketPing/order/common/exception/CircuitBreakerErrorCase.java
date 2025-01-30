package com.ticketPing.order.common.exception;

import exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CircuitBreakerErrorCase implements ErrorCase {
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스가 연결 불가능합니다. 잠시 후 다시 시도해주세요."),
    SERVICE_IS_OPEN(HttpStatus.SERVICE_UNAVAILABLE, "서비스가 연결 불가능합니다. 관리자에게 문의해주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}

package com.ticketPing.performance.common.exception;

import exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MessageExceptionCase implements ErrorCase {
    MESSAGE_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "알람을 보낼 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

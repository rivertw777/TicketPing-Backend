package com.ticketPing.gateway.common.exception;

import exception.ErrorCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SecurityErrorCase implements ErrorCase {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}

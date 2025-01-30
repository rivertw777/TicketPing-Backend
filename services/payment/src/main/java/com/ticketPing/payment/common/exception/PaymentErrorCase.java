package com.ticketPing.payment.common.exception;

import exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCase implements ErrorCase {

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보가 존재하지 않습니다."),
    TOSS_PAYMENT_CONFIRM_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "토스 서버 요청에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}

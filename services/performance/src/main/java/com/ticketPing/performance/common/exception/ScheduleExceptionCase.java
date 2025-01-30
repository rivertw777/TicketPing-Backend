package com.ticketPing.performance.common.exception;

import exception.ErrorCase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleExceptionCase implements ErrorCase {
    SCHEDULE_NOT_FOUND(HttpStatus.BAD_REQUEST, "공연 스케줄을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

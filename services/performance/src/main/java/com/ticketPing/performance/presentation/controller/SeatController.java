package com.ticketPing.performance.presentation.controller;

import com.ticketPing.performance.application.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.CommonResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @Operation(summary = "좌석 선점")
    @PostMapping("/{seatId}/pre-reserve")
    public ResponseEntity<CommonResponse<Object>> preReserveSeat(@RequestHeader("X_USER_ID") UUID userId,
                                                                 @PathVariable("seatId") UUID seatId,
                                                                 @RequestParam("performanceId") UUID performanceId,
                                                                 @RequestParam("scheduleId") UUID scheduleId) {
        seatService.preReserveSeat(scheduleId, seatId, userId);
        return ResponseEntity
                .status(200)
                .body(CommonResponse.success());
    }

    @Operation(summary = "좌석 선점 취소")
    @PostMapping("/{seatId}/cancel-reserve")
    public ResponseEntity<CommonResponse<Object>> cancelPreReserveSeat(@RequestHeader("X_USER_ID") UUID userId,
                                                                       @PathVariable("seatId") UUID seatId,
                                                                       @RequestParam("performanceId") UUID performanceId,
                                                                       @RequestParam("scheduleId") UUID scheduleId) {
        seatService.cancelPreReserveSeat(scheduleId, seatId, userId);
        return ResponseEntity
                .status(200)
                .body(CommonResponse.success());
    }
}

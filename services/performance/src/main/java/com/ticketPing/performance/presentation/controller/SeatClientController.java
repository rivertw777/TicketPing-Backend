package com.ticketPing.performance.presentation.controller;

import com.ticketPing.performance.application.dtos.OrderSeatResponse;
import com.ticketPing.performance.application.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.CommonResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/client/seats")
@RequiredArgsConstructor
public class SeatClientController {

    private final SeatService seatService;

    @Operation(summary = "좌석 주문 정보 조회 (order 서비스에서 호출용)")
    @GetMapping("/{seatId}/order-info")
    public ResponseEntity<CommonResponse<OrderSeatResponse>> getOrderSeatInfo(@RequestHeader("X_USER_ID") UUID userId,
                                                                              @PathVariable("seatId") UUID seatId,
                                                                              @RequestParam("scheduleId") UUID scheduleId) {
        OrderSeatResponse orderSeatResponse = seatService.getOrderSeatInfo(scheduleId, seatId, userId);
        return ResponseEntity
                .status(200)
                .body(CommonResponse.success(orderSeatResponse));
    }

    @Operation(summary = "좌석 선점 ttl 연장 (order 서비스 호출용)")
    @PostMapping("/{seatId}/extend-ttl")
    ResponseEntity<CommonResponse<Object>> extendPreReserveTTL (@PathVariable("seatId") UUID seatId,
                                                                @RequestParam("scheduleId") UUID scheduleId) {
        seatService.extendPreReserveTTL(scheduleId, seatId);
        return ResponseEntity
                .status(200)
                .body(CommonResponse.success());
    }
}

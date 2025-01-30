package com.ticketPing.order.application.client;

import org.springframework.http.ResponseEntity;
import performance.OrderSeatResponse;
import response.CommonResponse;

import java.util.UUID;

public interface  PerformanceClient {
    ResponseEntity<CommonResponse<OrderSeatResponse>> getOrderInfo(UUID userId, UUID scheduleId, UUID seatId);

    ResponseEntity<CommonResponse<Object>> extendPreReserveTTL(UUID scheduleId, UUID seatId);
}

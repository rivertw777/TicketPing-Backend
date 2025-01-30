package com.ticketPing.order.presentation.controller;

import com.ticketPing.order.application.client.PaymentClient;
import com.ticketPing.order.application.client.PerformanceClient;
import com.ticketPing.order.application.dtos.OrderResponse;
import com.ticketPing.order.application.service.OrderService;
import com.ticketPing.order.presentation.request.CreateOrderRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static response.CommonResponse.success;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final PaymentClient performanceClient;

    @PostMapping("/test")
    public ResponseEntity<CommonResponse<Object>> test() {
        performanceClient.getCompletedPaymentByOrderId(UUID.randomUUID());
        return ResponseEntity
                .status(200)
                .body(success());
    }

    @Operation(summary = "예매 좌석 생성")
    @PostMapping
    public ResponseEntity<CommonResponse<OrderResponse>> createOrder(@RequestHeader("X_USER_ID") UUID userId,
                                                                     @RequestParam("performanceId") UUID performanceId,
                                                                     @RequestBody CreateOrderRequest createOrderRequest) {
        OrderResponse orderResponse = orderService.createOrder(createOrderRequest, userId);
        return ResponseEntity
                .status(201)
                .body(success(orderResponse));
    }

    @Operation(summary = "사용자 예매 목록 전체 조회")
    @GetMapping("/user-orders")
    public ResponseEntity<CommonResponse<Slice<OrderResponse>>> getUserReservation(@RequestHeader("X_USER_ID") UUID userId,
                                                                                  Pageable pageable) {
        Slice<OrderResponse> userReservationDto = orderService.getUserOrders(userId, pageable);
        return ResponseEntity
                .status(200)
                .body(success(userReservationDto));
    }

    @Operation(summary = "주문 정보 검증")
    @PostMapping("/{orderId}/validate")
    public ResponseEntity<CommonResponse<Object>> validateOrder(@RequestHeader("X_USER_ID") UUID userId,
                                                                @RequestParam("performanceId") UUID performanceId,
                                                                @PathVariable("orderId") UUID orderId) {
        orderService.validateOrderAndExtendTTL(orderId, userId);
        return ResponseEntity
                .status(200)
                .body(success());
    }

}

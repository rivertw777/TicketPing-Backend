package com.ticketPing.order.application.dtos;

import com.ticketPing.order.domain.model.entity.Order;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record OrderResponse(
    UUID id,
    UUID performanceId,
    String performanceName,
    UUID scheduleId,
    LocalDate startDate,
    UUID performanceHallId,
    String performanceHallName,
    int amount,
    String orderStatus,
    LocalDateTime reservationDate,
    UUID paymentId,
    int row,
    int col,
    String seatGrade,
    UUID userId
) {
    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .performanceId(order.getPerformanceId())
                .performanceName(order.getPerformanceName())
                .scheduleId(order.getScheduleId())
                .startDate(order.getStartDate())
                .performanceHallId(order.getPerformanceHallId())
                .performanceHallName(order.getPerformanceHallName())
                .amount(order.getAmount())
                .orderStatus(order.getOrderStatus().getValue())
                .reservationDate(order.getReservationDate())
                .paymentId(order.getPaymentId())
                .row(order.getOrderSeat().getRow())
                .col(order.getOrderSeat().getCol())
                .seatGrade(order.getOrderSeat().getSeatGrade())
                .userId(order.getUserId())
                .build();
    }
}
package com.ticketPing.order.domain.model.entity;


import audit.BaseEntity;
import com.ticketPing.order.domain.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import performance.OrderSeatResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "p_orders")
@Entity
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID id;
    private UUID performanceId;
    private String performanceName;
    private UUID scheduleId;
    private LocalDate startDate;
    private UUID performanceHallId;
    private String performanceHallName;
    private int amount;
    private OrderStatus orderStatus;
    private LocalDateTime reservationDate;
    private UUID userId;
    private UUID companyId;
    private UUID paymentId;

    @OneToOne(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.LAZY)
    private OrderSeat orderSeat;

    public static Order from(UUID userId, OrderSeatResponse orderData) {
        return Order.builder()
                .performanceId(orderData.performanceId())
                .performanceName(orderData.performanceName())
                .performanceHallId(orderData.performanceHallId())
                .performanceHallName(orderData.performanceHallName())
                .scheduleId(orderData.scheduleId())
                .startDate(orderData.startDate())
                .amount(orderData.cost())
                .orderStatus(OrderStatus.PENDING)
                .reservationDate(LocalDateTime.now())
                .userId(userId)
                .companyId(orderData.companyId())
                .build();
    }

    public void updateOrderSeat(OrderSeat orderSeat) {
        this.orderSeat = orderSeat;
    }

    public void complete(UUID paymentId){
        this.orderStatus = OrderStatus.COMPLETED;
        this.paymentId =  paymentId;
    }

    public void fail() {
        this.orderStatus = OrderStatus.FAIL;
    }
}

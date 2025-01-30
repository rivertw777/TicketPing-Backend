package com.ticketPing.order.domain.model.entity;

import audit.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import performance.OrderSeatResponse;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "p_order_seats")
@Entity
public class OrderSeat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_seat_id")
    private UUID id;
    private UUID seatId;
    private int row;
    private int col;
    private String seatGrade;
    private int cost;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Order order;

    public static OrderSeat from(OrderSeatResponse orderData, Order order) {
        return OrderSeat.builder()
                .seatId(orderData.seatId())
                .row(orderData.row())
                .col(orderData.col())
                .seatGrade(orderData.seatGrade())
                .cost(orderData.cost())
                .order(order)
                .build();
    }

}

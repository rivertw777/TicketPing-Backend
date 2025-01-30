package com.ticketPing.order.domain.repository;

import com.ticketPing.order.domain.model.entity.Order;
import com.ticketPing.order.domain.model.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    Optional<Order> findByOrderSeatSeatIdAndOrderStatus(UUID seatId, OrderStatus orderStatus);

    Optional<Order> findByIdAndOrderStatus(UUID orderId, OrderStatus orderStatus);

    boolean existsByOrderSeatSeatIdAndOrderStatusIn(UUID seatId, List<OrderStatus> statuses);

    Slice<Order> findUserOrdersExcludingStatus(UUID userId, List<OrderStatus> statuses, Pageable pageable);
}

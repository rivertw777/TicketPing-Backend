package com.ticketPing.order.application.service;

import com.ticketPing.order.application.client.PaymentClient;
import com.ticketPing.order.application.client.PerformanceClient;
import com.ticketPing.order.application.dtos.OrderResponse;
import com.ticketPing.order.common.exception.OrderExceptionCase;
import com.ticketPing.order.domain.model.entity.Order;
import com.ticketPing.order.domain.model.entity.OrderSeat;
import com.ticketPing.order.domain.model.enums.OrderStatus;
import com.ticketPing.order.domain.repository.OrderRepository;
import com.ticketPing.order.presentation.request.CreateOrderRequest;
import exception.ApplicationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import messaging.events.OrderCompletedForQueueTokenRemovalEvent;
import messaging.events.OrderCompletedForSeatReservationEvent;
import messaging.events.OrderFailedEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import performance.OrderSeatResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ticketPing.order.common.exception.OrderExceptionCase.DUPLICATED_ORDER;
import static com.ticketPing.order.common.exception.OrderExceptionCase.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final EventApplicationService eventApplicationService;
    private final PerformanceClient performanceClient;
    private final PaymentClient paymentClient;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest, UUID userId) {
        UUID scheduleId = createOrderRequest.scheduleId();
        UUID seatId = createOrderRequest.seatId();

        validateDuplicateOrder(seatId);
        OrderSeatResponse orderData = performanceClient.getOrderInfo(userId, scheduleId, seatId).getBody().getData();
        Order order = saveOrderWithOrderSeat(userId, orderData);
        return OrderResponse.from(order);
    }

    public Slice<OrderResponse> getUserOrders(UUID userId, Pageable pageable) {
        Slice<Order> orders = orderRepository.findUserOrdersExcludingStatus(
                userId, List.of(OrderStatus.PENDING, OrderStatus.FAIL), pageable);
        return orders.map(OrderResponse::from);
    }

    public void validateOrderAndExtendTTL(UUID orderId, UUID userId) {
        Order order = validateAndGetOrder(orderId, userId);
        performanceClient.extendPreReserveTTL(order.getScheduleId(), order.getOrderSeat().getSeatId());
    }

    @Transactional
    public void failOrder(UUID scheduleId, UUID seatId) {
        Optional<Order> optionalOrder = orderRepository.findByOrderSeatSeatIdAndOrderStatus(seatId, OrderStatus.PENDING);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            try {
                paymentClient.getCompletedPaymentByOrderId(order.getId());
            } catch (FeignException.NotFound e) {
                order.fail();
                publishOrderFailed(scheduleId, seatId);
            }
        } else {
            publishOrderFailed(scheduleId, seatId);
        }
    }

    @Transactional
    public void completeOrder(UUID orderId, UUID paymentId) {
        Order order = findOrderById(orderId);
        order.complete(paymentId);

        publishForSeatReservation(order.getScheduleId(), order.getOrderSeat().getSeatId());
        publishForQueueTokenRemoval(order.getUserId(), order.getPerformanceId());
    }

    @Transactional
    private Order saveOrderWithOrderSeat(UUID userId, OrderSeatResponse orderData) {
        Order order = Order.from(userId, orderData);
        Order savedOrder = orderRepository.save(order);

        OrderSeat orderSeat = OrderSeat.from(orderData, savedOrder);
        savedOrder.updateOrderSeat(orderSeat);

        return savedOrder;
    }

    private void validateDuplicateOrder(UUID seatId) {
        boolean hasDuplicate = orderRepository.existsByOrderSeatSeatIdAndOrderStatusIn(
                seatId, List.of(OrderStatus.PENDING, OrderStatus.COMPLETED));

        if (hasDuplicate) {
            throw new ApplicationException(DUPLICATED_ORDER);
        }
    }

    private Order validateAndGetOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdAndOrderStatus(orderId, OrderStatus.PENDING)
                .orElseThrow(() -> new ApplicationException(OrderExceptionCase.INVALID_ORDER));

        if (!order.getUserId().equals(userId)) {
            throw new ApplicationException(OrderExceptionCase.INVALID_ORDER);
        }

        return order;
    }

    private Order findOrderById(UUID orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ApplicationException(ORDER_NOT_FOUND));
    }

    private void publishForSeatReservation(UUID scheduleId, UUID seatId) {
        val event = OrderCompletedForSeatReservationEvent.create(scheduleId, seatId);
        eventApplicationService.publishForSeatReservation(event);
    }

    private void publishForQueueTokenRemoval(UUID userId, UUID performanceId) {
        val event = OrderCompletedForQueueTokenRemovalEvent.create(userId, performanceId);
        eventApplicationService.publishForQueueTokenRemoval(event);
    }

    private void publishOrderFailed(UUID scheduleId, UUID seatId) {
        val event = OrderFailedEvent.create(scheduleId, seatId);
        eventApplicationService.publishOrderFailed(event);
    }
}

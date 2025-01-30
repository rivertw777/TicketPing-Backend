package com.ticketPing.order.infrastructure.listener;

import com.ticketPing.order.application.service.OrderService;
import messaging.events.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import messaging.events.SeatPreReserveExpiredEvent;
import messaging.utils.EventLogger;
import messaging.utils.EventSerializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "payment-completed", groupId = "order-group")
    public void handlePaymentCompletedEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        EventLogger.logReceivedMessage(record);
        PaymentCompletedEvent event = EventSerializer.deserialize(record.value(), PaymentCompletedEvent.class);
        orderService.completeOrder(event.orderId(), event.paymentId());
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "pre-reserve-expired", groupId = "order-group")
    public void handleSeatPreReserveExpiredEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        EventLogger.logReceivedMessage(record);
        SeatPreReserveExpiredEvent event = EventSerializer.deserialize(record.value(), SeatPreReserveExpiredEvent.class);
        orderService.failOrder(event.scheduleId(), event.seatId());
        acknowledgment.acknowledge();
    }

}
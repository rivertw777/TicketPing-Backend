package com.ticketPing.performance.infrastructure.listener;

import com.ticketPing.performance.application.service.SeatService;
import lombok.RequiredArgsConstructor;
import messaging.events.OrderCompletedForSeatReservationEvent;
import messaging.events.OrderFailedEvent;
import messaging.utils.EventLogger;
import messaging.utils.EventSerializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final SeatService seatService;

    @KafkaListener(topics = "order-completed-for-seat-reservation", groupId = "performance-group")
    public void handleOrderCompletedForSeatReservationEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        EventLogger.logReceivedMessage(record);
        OrderCompletedForSeatReservationEvent event = EventSerializer.deserialize(record.value(), OrderCompletedForSeatReservationEvent.class);
        seatService.reserveSeat(event.scheduleId(), event.seatId());
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "order-failed", groupId = "performance-group")
    public void handleOrderFailedEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        EventLogger.logReceivedMessage(record);
        OrderFailedEvent event = EventSerializer.deserialize(record.value(), OrderFailedEvent.class);
        seatService.cancelPreReserveSeatInCache(event.scheduleId(), event.seatId());
        acknowledgment.acknowledge();
    }

}

package com.ticketPing.order.application.service;

import messaging.events.OrderCompletedForQueueTokenRemovalEvent;
import messaging.events.OrderCompletedForSeatReservationEvent;
import messaging.events.OrderFailedEvent;
import messaging.utils.EventSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import messaging.topics.OrderTopic;

@Service
@RequiredArgsConstructor
public class EventApplicationService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishForSeatReservation(OrderCompletedForSeatReservationEvent event) {
        String message = EventSerializer.serialize(event);
        kafkaTemplate.send(OrderTopic.COMPLETED_FOR_SEAT_RESERVATION.getTopic(), message);
    }

    public void publishForQueueTokenRemoval(OrderCompletedForQueueTokenRemovalEvent event) {
        String message = EventSerializer.serialize(event);
        kafkaTemplate.send(OrderTopic.COMPLETED_FOR_QUEUE_TOKEN_REMOVAL.getTopic(), message);
    }

    public void publishOrderFailed(OrderFailedEvent event) {
        String message = EventSerializer.serialize(event);
        kafkaTemplate.send(OrderTopic.FAILED.getTopic(), message);
    }

}

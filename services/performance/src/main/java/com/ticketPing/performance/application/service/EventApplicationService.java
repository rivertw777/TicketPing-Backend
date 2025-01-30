package com.ticketPing.performance.application.service;

import lombok.RequiredArgsConstructor;
import messaging.events.SeatPreReserveExpiredEvent;
import messaging.topics.SeatTopic;
import messaging.utils.EventSerializer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventApplicationService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishSeatPreReserveExpiredEvent(SeatPreReserveExpiredEvent event) {
        String message = EventSerializer.serialize(event);
        kafkaTemplate.send(SeatTopic.PRE_RESERVE_EXPIRED.getTopic(), message);
    }
}

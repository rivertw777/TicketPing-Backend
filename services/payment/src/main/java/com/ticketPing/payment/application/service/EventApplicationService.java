package com.ticketPing.payment.application.service;

import messaging.events.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import messaging.utils.EventSerializer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import messaging.topics.PaymentTopic;

@Service
@RequiredArgsConstructor
public class EventApplicationService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishPaymentCompletedEvent(PaymentCompletedEvent event) {
        String message = EventSerializer.serialize(event);
        kafkaTemplate.send(PaymentTopic.COMPLETED.getTopic(), message);
    }

}
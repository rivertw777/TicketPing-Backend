package com.ticketPing.order.infrastructure.config;

import messaging.topics.OrderTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic seatReservationTopic() {
        return TopicBuilder.name(OrderTopic.COMPLETED_FOR_SEAT_RESERVATION.getTopic())
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic queueTokenRemovalTopic() {
        return TopicBuilder.name(OrderTopic.COMPLETED_FOR_QUEUE_TOKEN_REMOVAL.getTopic())
                .partitions(3)
                .replicas(3)
                .build();
    }

}
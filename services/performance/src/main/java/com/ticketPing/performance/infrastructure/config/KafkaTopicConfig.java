package com.ticketPing.performance.infrastructure.config;

import messaging.topics.SeatTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic preReserveExpiredTopic() {
        return TopicBuilder.name(SeatTopic.PRE_RESERVE_EXPIRED.getTopic())
                .partitions(3)
                .replicas(3)
                .build();
    }

}
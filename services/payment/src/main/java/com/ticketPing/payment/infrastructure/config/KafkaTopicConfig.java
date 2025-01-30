package com.ticketPing.payment.infrastructure.config;

import messaging.topics.PaymentTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(PaymentTopic.COMPLETED.getTopic())
                .partitions(3)
                .replicas(3)
                .build();
    }

}
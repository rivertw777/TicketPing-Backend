package com.ticketPing.queue_manage.infrastructure.listener;

import static com.ticketPing.queue_manage.domain.model.enums.WorkingQueueTokenDeleteCase.ORDER_COMPLETED;
import static com.ticketPing.queue_manage.common.utils.TokenValueGenerator.generateTokenValue;

import com.ticketPing.queue_manage.application.service.WorkingQueueService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import messaging.events.OrderCompletedForQueueTokenRemovalEvent;
import messaging.utils.EventLogger;
import messaging.utils.EventSerializer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;
import messaging.topics.OrderTopic;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

    private final ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;
    private final WorkingQueueService workingQueueService;

    @EventListener(ApplicationReadyEvent.class)
    public void consumeMessage() {
        reactiveKafkaConsumerTemplate
                .receive()
                .flatMap(this::handleMessage)
                .doOnError(throwable -> log.error("Error occurred while consuming message:", throwable))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .subscribe();
    }

    private Mono<Void> handleMessage(ReceiverRecord<String, String> record) {
        if (record.topic().equals(OrderTopic.COMPLETED_FOR_QUEUE_TOKEN_REMOVAL.getTopic())) {
            return handleOrderCompletedEvent(record);
        }
        return Mono.empty();
    }

    private Mono<Void> handleOrderCompletedEvent(ReceiverRecord<String, String> record) {
        EventLogger.logReceivedMessage(record);
        OrderCompletedForQueueTokenRemovalEvent event = EventSerializer.deserialize(record.value(), OrderCompletedForQueueTokenRemovalEvent.class);
        String tokenValue = generateTokenValue(event.userId(), event.performanceId());

        return Mono.fromRunnable(() -> workingQueueService.transferToken(ORDER_COMPLETED, tokenValue))
                .doOnTerminate(() -> record.receiverOffset().acknowledge())
                .then();
    }

}
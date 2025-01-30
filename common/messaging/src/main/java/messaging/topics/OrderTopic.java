package messaging.topics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderTopic {

    COMPLETED_FOR_SEAT_RESERVATION("order-completed-for-seat-reservation"),
    COMPLETED_FOR_QUEUE_TOKEN_REMOVAL("order-completed-for-queue-token-removal"),
    FAILED("order-failed");

    private final String topic;

}

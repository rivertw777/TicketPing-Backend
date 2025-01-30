package messaging.topics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatTopic {

    PRE_RESERVE_EXPIRED("pre-reserve-expired");

    private final String topic;
}

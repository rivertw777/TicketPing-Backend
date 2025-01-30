package messaging.events;

import java.util.UUID;

public record OrderFailedEvent(
        UUID scheduleId,
        UUID seatId
) {
    public static OrderFailedEvent create(UUID scheduleId, UUID seatId) {
        return new OrderFailedEvent(
                scheduleId,
                seatId
        );
    }
}

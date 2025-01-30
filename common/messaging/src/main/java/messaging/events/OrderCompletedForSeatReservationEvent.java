package messaging.events;

import java.util.UUID;

public record OrderCompletedForSeatReservationEvent(
        String scheduleId,
        String seatId
) {
    public static OrderCompletedForSeatReservationEvent create(UUID scheduleId, UUID seatId) {
        return new OrderCompletedForSeatReservationEvent(
                scheduleId.toString(),
                seatId.toString()
        );
    }
}

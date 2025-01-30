package messaging.events;

import java.util.UUID;

public record SeatPreReserveExpiredEvent(
        UUID scheduleId,
        UUID seatId
) {

    public static SeatPreReserveExpiredEvent create(UUID scheduleId, UUID seatId) {
        return new SeatPreReserveExpiredEvent(
                scheduleId,
                seatId
        );
    }

}

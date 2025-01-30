package messaging.events;

import java.util.UUID;

public record OrderCompletedForQueueTokenRemovalEvent(
        String userId,
        String performanceId
) {
    public static OrderCompletedForQueueTokenRemovalEvent create(UUID userId, UUID performanceId) {
        return new OrderCompletedForQueueTokenRemovalEvent(
                userId.toString(),
                performanceId.toString()
        );
    }
}

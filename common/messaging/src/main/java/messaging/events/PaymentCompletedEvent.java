package messaging.events;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID orderId,
        UUID paymentId
) {
    public static PaymentCompletedEvent create(UUID orderId, UUID paymentId) {
        return new PaymentCompletedEvent(
                orderId,
                paymentId
        );
    }
}

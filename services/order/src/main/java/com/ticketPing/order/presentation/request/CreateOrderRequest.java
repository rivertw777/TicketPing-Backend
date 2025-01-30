package com.ticketPing.order.presentation.request;

import java.util.UUID;

public record CreateOrderRequest (
        UUID scheduleId,
        UUID seatId
) {
}

package com.ticketPing.payment.application.dto;

import com.ticketPing.payment.domain.model.entity.Payment;
import mapper.ObjectMapperBasedVoMapper;
import java.util.UUID;

public record PaymentResponse(
        UUID Id,
        UUID userId,
        String status,
        UUID orderId,
        Long amount
) {
    public static PaymentResponse from(Payment payment) {
        return ObjectMapperBasedVoMapper.convert(payment, PaymentResponse.class);
    }
}

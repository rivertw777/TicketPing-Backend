package com.ticketPing.payment.domain.repository;

import com.ticketPing.payment.domain.model.entity.Payment;
import com.ticketPing.payment.domain.model.enums.PaymentStatus;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(UUID paymentId);
     Optional<Payment> findByOrderIdAndStatus(UUID orderId, PaymentStatus status);
}

package com.ticketPing.payment.domain.service;

import static com.ticketPing.payment.common.exception.PaymentErrorCase.PAYMENT_NOT_FOUND;

import com.ticketPing.payment.domain.model.entity.Payment;
import com.ticketPing.payment.domain.model.enums.PaymentStatus;
import com.ticketPing.payment.domain.repository.PaymentRepository;
import exception.ApplicationException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentDomainService {

    private final PaymentRepository paymentRepository;

    public Payment createPayment(UUID userId, JSONObject responseData) {
        Payment payment = Payment.create(userId, responseData);
        paymentRepository.save(payment);
        return payment;
    }

    public Payment findPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApplicationException(PAYMENT_NOT_FOUND));
    }

    public Payment getCompletedPaymentByOrderId(UUID orderId) {
        return paymentRepository.findByOrderIdAndStatus(orderId, PaymentStatus.COMPLETED)
                .orElseThrow(() -> new ApplicationException(PAYMENT_NOT_FOUND));
    }

}
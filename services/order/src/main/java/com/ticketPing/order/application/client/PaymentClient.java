package com.ticketPing.order.application.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import payment.PaymentResponse;
import response.CommonResponse;

import java.util.UUID;

public interface PaymentClient {
    ResponseEntity<CommonResponse<PaymentResponse>> getCompletedPaymentByOrderId(@RequestParam("orderId") UUID orderId);
}

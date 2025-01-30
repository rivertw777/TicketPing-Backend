package com.ticketPing.order.infrastructure.client;

import com.ticketPing.order.application.client.PaymentClient;
import com.ticketPing.order.infrastructure.config.CustomFeignConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import payment.PaymentResponse;
import response.CommonResponse;

import java.util.UUID;

import static com.ticketPing.order.common.utils.FeignFallbackUtils.handleFallback;

@FeignClient(name = "payment", configuration = CustomFeignConfig.class)
public interface PaymentFeignClient extends PaymentClient {
    @GetMapping("/api/v1/payments/completed")
    @Retry(name = "paymentServiceRetry")
    @CircuitBreaker(name = "paymentServiceCircuitBreaker", fallbackMethod = "fallbackForPaymentService")
    ResponseEntity<CommonResponse<PaymentResponse>> getCompletedPaymentByOrderId(@RequestParam("orderId") UUID orderId);

    default ResponseEntity<CommonResponse<PaymentResponse>> fallbackForPaymentService(UUID orderId, Throwable cause) {
        handleFallback(cause);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }
}

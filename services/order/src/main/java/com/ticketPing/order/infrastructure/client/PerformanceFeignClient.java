package com.ticketPing.order.infrastructure.client;

import static circuitbreaker.utils.FeignFallbackUtils.handleFallback;

import circuitbreaker.config.CustomFeignConfig;
import com.ticketPing.order.application.client.PerformanceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import performance.OrderSeatResponse;
import response.CommonResponse;
import java.util.UUID;

@FeignClient(name = "performance", configuration = CustomFeignConfig.class)
public interface PerformanceFeignClient extends PerformanceClient {

    @GetMapping("/api/v1/client/seats/{seatId}/order-info")
    @Retry(name = "performanceServiceRetry")
    @CircuitBreaker(name = "performanceServiceCircuitBreaker", fallbackMethod = "fallbackForGetOrderInfo")
    ResponseEntity<CommonResponse<OrderSeatResponse>> getOrderInfo(@RequestHeader("X_USER_ID") UUID userId,
                                                                   @RequestParam("scheduleId") UUID scheduleId,
                                                                   @PathVariable("seatId") UUID seatId);

    @PostMapping("/api/v1/client/seats/{seatId}/extend-ttl")
    @Retry(name = "performanceServiceRetry")
    @CircuitBreaker(name = "performanceServiceCircuitBreaker", fallbackMethod = "fallbackForExtendPreReserveTTL")
    ResponseEntity<CommonResponse<Object>> extendPreReserveTTL(@RequestParam("scheduleId") UUID scheduleId,
                                                               @PathVariable("seatId") UUID seatId);

    default ResponseEntity<CommonResponse<OrderSeatResponse>> fallbackForGetOrderInfo(UUID userId, UUID scheduleId, UUID seatId, Throwable cause) {
        handleFallback(cause);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    default ResponseEntity<CommonResponse<Object>> fallbackForExtendPreReserveTTL(UUID scheduleId, UUID seatId, Throwable cause) {
        handleFallback(cause);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

}



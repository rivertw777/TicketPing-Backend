package com.ticketPing.auth.infrastructure.client;

import static circuitbreaker.utils.FeignFallbackUtils.handleFallback;

import circuitbreaker.config.CustomFeignConfig;
import com.ticketPing.auth.application.client.UserClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import response.CommonResponse;
import user.UserLookupRequest;
import user.UserResponse;

@FeignClient(name = "user", configuration = CustomFeignConfig.class)
public interface UserFeignClient extends UserClient {

    @GetMapping("/api/v1/users/login")
    @Retry(name = "userServiceRetry")
    @CircuitBreaker(name = "userServiceCircuitBreaker", fallbackMethod = "fallbackForGetUserByEmailAndPassword")
    ResponseEntity<CommonResponse<UserResponse>> getUserByEmailAndPassword(@RequestBody UserLookupRequest userLookupRequest);

    default ResponseEntity<CommonResponse<UserResponse>> fallbackForGetUserByEmailAndPassword(UserLookupRequest userLookupRequest, Throwable cause) {
        handleFallback(cause);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

}

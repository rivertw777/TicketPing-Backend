package com.ticketPing.auth.infrastructure.client;

import com.ticketPing.auth.application.client.UserClient;
import com.ticketPing.auth.common.exception.CircuitBreakerErrorCase;
import com.ticketPing.auth.infrastructure.config.CustomFeignConfig;
import exception.ApplicationException;
import feign.FeignException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import response.CommonResponse;
import user.UserLookupRequest;
import user.UserResponse;

@FeignClient(name = "user", configuration = CustomFeignConfig.class)
public interface UserFeignClient extends UserClient {
    @GetMapping("/api/v1/users/login")
    @CircuitBreaker(name = "userServiceCircuitBreaker", fallbackMethod = "fallbackForUserService")
    CommonResponse<UserResponse> getUserByEmailAndPassword(@RequestBody UserLookupRequest userLookupRequest);

    default CommonResponse<UserResponse> fallbackForUserService(UserLookupRequest userLookupRequest, Throwable cause) {
        if (cause instanceof CallNotPermittedException) {
            throw new ApplicationException(CircuitBreakerErrorCase.SERVICE_IS_OPEN);
        }
        else if (
                cause instanceof FeignException.GatewayTimeout ||
                cause instanceof FeignException.ServiceUnavailable ||
                cause instanceof FeignException.BadGateway ||
                cause instanceof FeignException.TooManyRequests ||
                cause instanceof RetryableException
        ) {
            throw new ApplicationException(CircuitBreakerErrorCase.SERVICE_UNAVAILABLE);
        }
        else if (cause instanceof FeignException) {
            throw (FeignException) cause;
        } else {
            throw new ApplicationException(CircuitBreakerErrorCase.SERVICE_UNAVAILABLE);
        }
    }
}

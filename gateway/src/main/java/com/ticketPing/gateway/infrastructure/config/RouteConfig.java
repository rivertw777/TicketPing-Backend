package com.ticketPing.gateway.infrastructure.config;

import com.ticketPing.gateway.infrastructure.filter.QueueCheckFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    private final QueueCheckFilter queueCheckFilter;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // API Routing with Circuit Breaker
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .filters(f -> addCircuitBreaker(f, "authServiceCircuitBreaker", "default"))
                        .uri("lb://auth"))
                .route("user-service", r -> r.path("/api/v1/users/**")
                        .filters(f -> addCircuitBreaker(f, "userServiceCircuitBreaker", "default"))
                        .uri("lb://user"))
                .route("performance-service", r -> r.path("/api/v1/performances/**", "/api/v1/schedules/**", "/api/v1/seats/**")
                        .filters(f -> f.filter(queueCheckFilter::filter)
                                .circuitBreaker(c -> c.setName("performanceServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/default")))
                        .uri("lb://performance"))
                .route("order-service", r -> r.path("/api/v1/orders/**")
                        .filters(f -> f.filter(queueCheckFilter::filter)
                                .circuitBreaker(c -> c.setName("orderServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/default")))
                        .uri("lb://order"))
                .route("payment-service", r -> r.path("/api/v1/payments/**")
                        .filters(f -> addCircuitBreaker(f, "paymentServiceCircuitBreaker", "default"))
                        .uri("lb://payment"))
                .route("queue-manage-service", r -> r.path("/api/v1/waiting-queue/**", "/api/v1/working-queue/**")
                        .filters(f -> f.filter(queueCheckFilter::filter)
                                .circuitBreaker(c -> c.setName("queueManageServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/default")))
                        .uri("lb://queue-manage"))

                // Swagger Routing
                .route("auth-docs", r -> r.path("/v3/api-docs/auth-service")
                        .filters(f -> f.rewritePath("/v3/api-docs/auth-service", "/v3/api-docs"))
                        .uri("lb://auth"))
                .route("user-docs", r -> r.path("/v3/api-docs/user-service")
                        .filters(f -> f.rewritePath("/v3/api-docs/user-service", "/v3/api-docs"))
                        .uri("lb://user"))
                .route("performance-docs", r -> r.path("/v3/api-docs/performance-service")
                        .filters(f -> f.rewritePath("/v3/api-docs/performance-service", "/v3/api-docs"))
                        .uri("lb://performance"))
                .route("order-docs", r -> r.path("/v3/api-docs/order-service")
                        .filters(f -> f.rewritePath("/v3/api-docs/order-service", "/v3/api-docs"))
                        .uri("lb://order"))
                .route("payment-docs", r -> r.path("/v3/api-docs/payment-service")
                        .filters(f -> f.rewritePath("/v3/api-docs/payment-service", "/v3/api-docs"))
                        .uri("lb://payment"))
                .route("queue-manage-docs", r -> r.path("/v3/api-docs/queue-manage-service")
                        .filters(f -> f.rewritePath("/v3/api-docs/queue-manage-service", "/v3/api-docs"))
                        .uri("lb://queue-manage"))

                .build();
    }

    private GatewayFilterSpec addCircuitBreaker(
            GatewayFilterSpec filterSpec, String circuitBreakerName, String fallbackMethod) {
        return filterSpec.circuitBreaker(c -> c.setName(circuitBreakerName)
                .setFallbackUri("forward:/fallback/" + fallbackMethod));
    }

}

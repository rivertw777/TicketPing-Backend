package com.ticketPing.gateway.infrastructure.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.util.AntPathMatcher;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum ApiType {

    ENTER_WAITING_QUEUE("/api/v1/waiting-queue", HttpMethod.POST),
    GET_QUEUE_INFO("/api/v1/waiting-queue", HttpMethod.GET),
    PRE_RESERVE_SEAT("/api/v1/seats/**/pre-reserve", HttpMethod.POST),
    CREATE_ORDER("/api/v1/orders", HttpMethod.POST),
    VALIDATE_ORDER("/api/v1/orders/**/validate", HttpMethod.POST);

    private final String path;
    private final HttpMethod method;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public static Mono<ApiType> findByRequest(String requestPath, String httpMethod) {
        return Flux.fromArray(ApiType.values())
                .filter(api -> api.matches(requestPath, httpMethod))
                .next()
                .switchIfEmpty(Mono.empty());
    }

    private boolean matches(String requestPath, String httpMethod) {
        return pathMatcher.match(this.path, requestPath) && this.method.name().equals(httpMethod);
    }

}
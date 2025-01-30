package com.ticketPing.gateway.infrastructure.filter;

import static com.ticketPing.gateway.common.exception.FilterErrorCase.PERFORMANCE_ID_NOT_FOUND;
import static com.ticketPing.gateway.common.exception.FilterErrorCase.USER_ID_NOT_FOUND;

import com.ticketPing.gateway.application.service.QueueCheckService;
import exception.ApplicationException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueCheckFilter {

    private static final String PERFORMANCE_ID_PARAM = "performanceId";

    private final QueueCheckService queueCheckService;

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        return ApiType.findByRequest(path, method)
                .doOnSuccess(api -> log.info("API 타입: {}", api))
                .flatMap(api ->
                        switch (api) {
                            case ENTER_WAITING_QUEUE -> handleEnterWaitingQueueApi(exchange, chain);
                            case GET_QUEUE_INFO -> handleGetQueueInfoApi(exchange, chain);
                            case PRE_RESERVE_SEAT, CREATE_ORDER, VALIDATE_ORDER -> handleReservationApi(exchange, chain);
                        })
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Void> handleEnterWaitingQueueApi(ServerWebExchange exchange, GatewayFilterChain chain) {
        return getPerformanceIdFromQueryParams(exchange)
                .flatMap(performanceId ->
                        queueCheckService.checkIsPerformanceSoldOut(performanceId)
                                .then(queueCheckService.checkHasTooManyWaitingUsers(performanceId))
                                .then(chain.filter(exchange))
                );
    }

    private Mono<Void> handleGetQueueInfoApi(ServerWebExchange exchange, GatewayFilterChain chain) {
        return getPerformanceIdFromQueryParams(exchange)
                .flatMap(queueCheckService::checkIsPerformanceSoldOut)
                .then(chain.filter(exchange));
    }

    private Mono<Void> handleReservationApi(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.zip(
                        getPerformanceIdFromQueryParams(exchange),
                        getUserIdFromAuthentication()
                )
                .flatMap(tuple ->
                        queueCheckService.checkIsPerformanceSoldOut(tuple.getT1())
                                .then(queueCheckService.checkIsUserAvailable(tuple.getT2(), tuple.getT1()))
                                .then(chain.filter(exchange))
                );
    }

    private Mono<String> getPerformanceIdFromQueryParams(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getQueryParams().getFirst(PERFORMANCE_ID_PARAM))
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new ApplicationException(PERFORMANCE_ID_NOT_FOUND)));
    }

    private Mono<String> getUserIdFromAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication().getName())
                .switchIfEmpty(Mono.error(new ApplicationException(USER_ID_NOT_FOUND)));
    }

}

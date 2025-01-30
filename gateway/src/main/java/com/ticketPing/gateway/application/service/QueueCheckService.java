package com.ticketPing.gateway.application.service;

import static caching.enums.RedisKeyPrefix.AVAILABLE_SEATS;
import static caching.enums.RedisKeyPrefix.WAITING_QUEUE;
import static com.ticketPing.gateway.common.exception.FilterErrorCase.PERFORMANCE_SOLD_OUT;
import static com.ticketPing.gateway.common.exception.FilterErrorCase.TOO_MANY_WAITING_USERS;
import static com.ticketPing.gateway.common.exception.FilterErrorCase.WORKING_QUEUE_TOKEN_NOT_FOUND;
import static com.ticketPing.gateway.common.utils.TokenValueGenerator.generateTokenValue;

import caching.repository.ReactiveRedisRepository;
import exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueCheckService {

    private final ReactiveRedisRepository redisRepository;

    // 공연 매진 여부
    public Mono<Void> checkIsPerformanceSoldOut(String performanceId) {
        return getAvailableSeatsCount(performanceId)
                .doOnSuccess(availableSeatsCount -> log.info("공연 잔여석: {}", availableSeatsCount))
                .map(count -> count <= 0)
                .filter(soldOut -> !soldOut)
                .switchIfEmpty(Mono.error(new ApplicationException(PERFORMANCE_SOLD_OUT)))
                .then();
    }

    // 공연 잔여석 * 2 <= 대기열 인원 수
    public Mono<Void> checkHasTooManyWaitingUsers(String performanceId) {
        return Mono.zip(
                        getAvailableSeatsCount(performanceId),
                        getWaitingUsersCount(performanceId)
                )
                .doOnSuccess(tuple -> log.info("공연 잔여석: {}, 대기 인원: {}", tuple.getT1(), tuple.getT2()))
                .map(tuple -> tuple.getT1() * 2 <= tuple.getT2())
                .filter(tooMany -> !tooMany)
                .switchIfEmpty(Mono.error(new ApplicationException(TOO_MANY_WAITING_USERS)))
                .then();
    }

    // 작업열 토큰 조회
    public Mono<Void> checkIsUserAvailable(String userId, String performanceId) {
        return checkIsTokenExists(userId, performanceId)
                .doOnSuccess(isTokenExists -> log.info("작업열 토큰 존재 유무: {}", isTokenExists))
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new ApplicationException(WORKING_QUEUE_TOKEN_NOT_FOUND)))
                .then();
    }

    private Mono<Long> getAvailableSeatsCount(String performanceId) {
        String key = AVAILABLE_SEATS.getValue() + performanceId;
        return redisRepository.getValueAsClass(key, Long.class);
    }

    private Mono<Long> getWaitingUsersCount(String performanceId) {
        String key = WAITING_QUEUE.getValue() + performanceId;
        return redisRepository.getSortedSetSize(key);
    }

    private Mono<Boolean> checkIsTokenExists(String userId, String performanceId) {
        String tokenValue = generateTokenValue(userId, performanceId);
        return redisRepository.hasKey(tokenValue);
    }

}
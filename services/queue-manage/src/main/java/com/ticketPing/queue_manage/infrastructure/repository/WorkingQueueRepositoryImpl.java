package com.ticketPing.queue_manage.infrastructure.repository;

import static com.ticketPing.queue_manage.common.utils.TTLConverter.toLocalDateTime;

import com.ticketPing.queue_manage.domain.command.workingQueue.DeleteWorkingQueueTokenCommand;
import com.ticketPing.queue_manage.domain.command.workingQueue.ExtendWorkingQueueTokenTTLCommand;
import com.ticketPing.queue_manage.domain.command.workingQueue.InsertWorkingQueueTokenCommand;
import com.ticketPing.queue_manage.domain.command.workingQueue.FindWorkingQueueTokenCommand;
import com.ticketPing.queue_manage.domain.model.WorkingQueueToken;
import com.ticketPing.queue_manage.domain.repository.WorkingQueueRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucketReactive;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class WorkingQueueRepositoryImpl implements WorkingQueueRepository {

    private final RedissonRepository redissonRepository;

    @Override
    public Mono<Boolean> insertWorkingQueueToken(InsertWorkingQueueTokenCommand command) {
        RBucketReactive<String> bucket = redissonRepository.getBucket(command.getTokenValue());

        return handleIfTokenNotExists(command, bucket)
                .defaultIfEmpty(false);
    }

    private Mono<Boolean> handleIfTokenNotExists(InsertWorkingQueueTokenCommand command, RBucketReactive<String> bucket) {
        return bucket.get()
                .hasElement()
                .filter(hasValue -> !hasValue)
                .flatMap(__ -> saveToken(command, bucket));
    }

    private Mono<Boolean> saveToken(InsertWorkingQueueTokenCommand command, RBucketReactive<String> bucket) {
        return bucket.set(command.getCacheValue(), command.getTtlInMinutes(), TimeUnit.MINUTES)
                .then(incrementQueueCounter(command.getQueueName()))
                .thenReturn(true);
    }

    private Mono<Long> incrementQueueCounter(String queueName) {
        return redissonRepository.getCounter(queueName).incrementAndGet();
    }

    @Override
    public Mono<WorkingQueueToken> findWorkingQueueToken(FindWorkingQueueTokenCommand command) {
        RBucketReactive<String> bucket = redissonRepository.getBucket(command.getTokenValue());

        return bucket.remainTimeToLive()
                .filter(ttl -> ttl != null && ttl > 0)
                .flatMap(ttl -> WorkingQueueToken.withValidUntil(
                        command.getUserId(),
                        command.getPerformanceId(),
                        command.getTokenValue(),
                        toLocalDateTime(ttl)
                ));
    }

    @Override
    public Mono<Boolean> deleteWorkingQueueToken(DeleteWorkingQueueTokenCommand command) {
        return switch (command.getDeleteCase()) {
            case TOKEN_EXPIRED -> handleTokenExpired(command.getQueueName());
            case ORDER_COMPLETED -> handleOrderCompleted(command.getQueueName(), command.getTokenValue());
        };
    }

    private Mono<Boolean> handleTokenExpired(String queueName) {
        return decrementQueueCounter(queueName)
                .thenReturn(true);
    }

    private Mono<Boolean> handleOrderCompleted(String queueName, String tokenValue) {
        RBucketReactive<String> bucket = redissonRepository.getBucket(tokenValue);

        return handleIfTokenExists(queueName, bucket)
                .defaultIfEmpty(false);
    }

    private Mono<Boolean> handleIfTokenExists(String queueName, RBucketReactive<String> bucket) {
        return bucket.get()
                .hasElement()
                .filter(hasValue -> hasValue)
                .flatMap(__ -> deleteToken(queueName, bucket));
    }

    private Mono<Boolean> deleteToken(String queueName, RBucketReactive<String> bucket) {
        return bucket.delete()
                .then(decrementQueueCounter(queueName))
                .thenReturn(true);
    }

    private Mono<Long> decrementQueueCounter(String queueName) {
        return redissonRepository.getCounter(queueName).decrementAndGet();
    }

    @Override
    public Mono<WorkingQueueToken> extendWorkingQueueTokenTTL(ExtendWorkingQueueTokenTTLCommand command) {
        RBucketReactive<String> bucket = redissonRepository.getBucket(command.getTokenValue());

        return bucket.expire(command.getTtlInMinutes(), TimeUnit.MINUTES)
                .then(
                        bucket.remainTimeToLive()
                                .flatMap(ttl -> WorkingQueueToken.withValidUntil(
                                        command.getUserId(),
                                        command.getPerformanceId(),
                                        command.getTokenValue(),
                                        toLocalDateTime(ttl)
                                ))
                );
    }

}
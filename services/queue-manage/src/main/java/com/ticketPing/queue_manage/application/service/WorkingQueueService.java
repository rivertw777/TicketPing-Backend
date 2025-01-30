package com.ticketPing.queue_manage.application.service;

import static com.ticketPing.queue_manage.common.exception.QueueErrorCase.WORKING_QUEUE_TOKEN_NOT_FOUND;

import com.ticketPing.queue_manage.application.dto.GeneralQueueTokenResponse;
import com.ticketPing.queue_manage.domain.command.waitingQueue.DeleteFirstWaitingQueueTokenCommand;
import com.ticketPing.queue_manage.domain.command.workingQueue.DeleteWorkingQueueTokenCommand;
import com.ticketPing.queue_manage.domain.command.workingQueue.ExtendWorkingQueueTokenTTLCommand;
import com.ticketPing.queue_manage.domain.command.workingQueue.FindWorkingQueueTokenCommand;
import com.ticketPing.queue_manage.domain.command.workingQueue.InsertWorkingQueueTokenCommand;
import com.ticketPing.queue_manage.domain.model.WaitingQueueToken;
import com.ticketPing.queue_manage.domain.model.enums.WorkingQueueTokenDeleteCase;
import com.ticketPing.queue_manage.domain.repository.WaitingQueueRepository;
import com.ticketPing.queue_manage.domain.repository.WorkingQueueRepository;
import exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkingQueueService {

    private final WorkingQueueRepository workingQueueRepository;
    private final WaitingQueueRepository waitingQueueRepository;

    public Mono<GeneralQueueTokenResponse> getWorkingQueueToken(String userId, String performanceId) {
        val command = FindWorkingQueueTokenCommand.create(userId, performanceId);

        return workingQueueRepository.findWorkingQueueToken(command)
                .doOnSuccess(token -> log.info("작업열 토큰 조회 완료 {}", token))
                .map(GeneralQueueTokenResponse::from)
                .switchIfEmpty(Mono.error(new ApplicationException(WORKING_QUEUE_TOKEN_NOT_FOUND)));
    }

    public void transferToken(WorkingQueueTokenDeleteCase deleteCase, String tokenValue) {
        val command = DeleteWorkingQueueTokenCommand.create(deleteCase, tokenValue);

        workingQueueRepository.deleteWorkingQueueToken(command)
                .doOnSuccess(isTokenDeleted -> log.info("작업열 토큰 삭제 완료 {}", isTokenDeleted))
                .filter(Boolean::booleanValue)
                .flatMap(deleted -> transferFirstWaitingQueueToken(tokenValue))
                .subscribe();
    }

    private Mono<Boolean> transferFirstWaitingQueueToken(String tokenValue) {
        val command = DeleteFirstWaitingQueueTokenCommand.create(tokenValue);

        return waitingQueueRepository.deleteFirstWaitingQueueToken(command)
                .doOnSuccess(deletedWaitingToken -> log.info("대기열 첫 번째 토큰 삭제 완료 {}", deletedWaitingToken))
                .flatMap(this::enterWorkingQueue);
    }

    private Mono<Boolean> enterWorkingQueue(WaitingQueueToken deletedWaitingToken) {
        val command = InsertWorkingQueueTokenCommand.create(deletedWaitingToken.toWorkingQueueToken());

        return workingQueueRepository.insertWorkingQueueToken(command)
                .doOnSuccess(isWorkingQueueTokenSaved -> log.info("작업열 토큰 저장 완료 {}", isWorkingQueueTokenSaved));
    }

    public Mono<GeneralQueueTokenResponse> extendWorkingQueueTokenTTL(String userId, String performanceId) {
        val command = ExtendWorkingQueueTokenTTLCommand.create(userId, performanceId);

        return workingQueueRepository.extendWorkingQueueTokenTTL(command)
                .doOnSuccess(token -> log.info("작업열 토큰 TTL 연장 완료 {}", token))
                .map(GeneralQueueTokenResponse::from)
                .switchIfEmpty(Mono.error(new ApplicationException(WORKING_QUEUE_TOKEN_NOT_FOUND)));
    }

}
package com.ticketPing.queue_manage.presentaion.controller;

import com.ticketPing.queue_manage.application.dto.GeneralQueueTokenResponse;
import com.ticketPing.queue_manage.application.service.WorkingQueueService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import response.CommonResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/working-queue")
public class WorkingQueueController {

    private final WorkingQueueService workingQueueService;

    @Operation(summary = "작업열 토큰 조회")
    @GetMapping
    public Mono<ResponseEntity<CommonResponse<GeneralQueueTokenResponse>>> getWorkingQueueToken(
            @Valid @RequestHeader("X_USER_ID") UUID userId,
            @Valid @RequestParam("performanceId") String performanceId) {
        return workingQueueService.getWorkingQueueToken(userId.toString(), performanceId)
                .map(CommonResponse::success)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "작업열 토큰 TTL 연장")
    @PostMapping("/extend-ttl")
    public Mono<ResponseEntity<CommonResponse<GeneralQueueTokenResponse>>> extendWorkingQueueTokenTTL(
            @Valid @RequestHeader("X_USER_ID") UUID userId,
            @Valid @RequestParam("performanceId") String performanceId) {
        return workingQueueService.extendWorkingQueueTokenTTL(userId.toString(), performanceId)
                .map(CommonResponse::success)
                .map(ResponseEntity::ok);
    }

}

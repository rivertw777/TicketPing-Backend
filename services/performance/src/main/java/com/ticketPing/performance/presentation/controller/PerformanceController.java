package com.ticketPing.performance.presentation.controller;

import com.ticketPing.performance.application.dtos.PerformanceListResponse;
import com.ticketPing.performance.application.dtos.PerformanceResponse;
import com.ticketPing.performance.application.dtos.ScheduleResponse;
import com.ticketPing.performance.application.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.CommonResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/performances")
@RequiredArgsConstructor
public class PerformanceController {
    private final PerformanceService performanceService;

    @Operation(summary = "공연 조회")
    @GetMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<PerformanceResponse>> getPerformance(@PathVariable("performanceId") UUID performanceId) {
        PerformanceResponse performanceResponse = performanceService.getPerformance(performanceId);
        return ResponseEntity
                .status(200)
                .body(CommonResponse.success(performanceResponse));
    }

    @Operation(summary = "공연 목록 조회")
    @GetMapping
    public ResponseEntity<CommonResponse<Slice<PerformanceListResponse>>> getAllPerformances(Pageable pageable) {
        Slice<PerformanceListResponse> response = performanceService.getAllPerformances(pageable);
        return ResponseEntity
                .status(200)
                .body(CommonResponse.success(response));
    }

    @Operation(summary = "공연 스케줄 목록 조회")
    @GetMapping("/{performanceId}/schedules")
    public ResponseEntity<CommonResponse<List<ScheduleResponse>>> getSchedulesByPerformance(@PathVariable("performanceId") UUID performanceId) {
        List<ScheduleResponse> scheduleResponses = performanceService.getPerformanceSchedules(performanceId);
        return ResponseEntity
                .status(200)
                .body(CommonResponse.success(scheduleResponses));
    }

    @Operation(summary = "공연 전체 좌석 캐싱 생성")
    @PostMapping("/{performanceId}/seats-cache")
    public ResponseEntity<CommonResponse<Object>> createSeatsCache(@PathVariable("performanceId") UUID performanceId) {
        performanceService.cacheAllSeatsForPerformance(performanceId);
        return ResponseEntity
                .status(201)
                .body(CommonResponse.success());
    }
}


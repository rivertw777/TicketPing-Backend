package com.ticketPing.performance.presentation.controller;

import com.ticketPing.performance.application.dtos.SeatResponse;
import com.ticketPing.performance.application.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.CommonResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Operation(summary = "스케줄 전체 좌석 조회")
    @GetMapping("/{scheduleId}/seats")
    public ResponseEntity<CommonResponse<List<SeatResponse>>> getAllScheduleSeats(@PathVariable("scheduleId") UUID scheduleId) {
        List<SeatResponse> seatResponses = scheduleService.getAllScheduleSeats(scheduleId);
        return ResponseEntity
                .status(200)
                .body(CommonResponse.success(seatResponses));
    }
}

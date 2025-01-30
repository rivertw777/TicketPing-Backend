package com.ticketPing.performance.domain;

import com.ticketPing.performance.application.service.PerformanceService;
import com.ticketPing.performance.domain.model.entity.*;
import com.ticketPing.performance.domain.model.enums.SeatStatus;
import com.ticketPing.performance.domain.repository.PerformanceHallRepository;
import com.ticketPing.performance.domain.repository.PerformanceRepository;
import com.ticketPing.performance.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PerformanceService performanceService;
    private final PerformanceRepository performanceRepository;
    private final PerformanceHallRepository performanceHallRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (performanceHallRepository.count() > 0) {
            Performance performance = performanceRepository.findByName("햄릿");
            if(performance != null) {
                performanceService.cacheAllSeatsForPerformance(performance.getId());
            }
            return;
        }

        // 공연장 더미 데이터 생성
        PerformanceHall hall1 = PerformanceHall.createTestData("국립극장", "서울특별시 남산동 1-1", 50, 10, 5);
        performanceHallRepository.save(hall1);

        PerformanceHall hall2 = PerformanceHall.createTestData("세종문화회관", "서울특별시 세종로 81", 1200, 40, 30);
        performanceHallRepository.save(hall2);

        // 공연 더미 데이터 생성
        Performance performance1 = Performance.createTestData(
                "햄릿",
                "https://image.kmib.co.kr/online_image/2017/0622/201706222053_61170011561894_1.jpg",
                120,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(10),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                19,
                UUID.randomUUID(),
                hall1);
        performance1 = performanceRepository.save(performance1);

        Performance performance2 = Performance.createTestData(
                "라이온 킹",
                "https://image.yes24.com/themusical/upFiles/Themusical/Play/post_%EB%9D%BC%EC%9D%B4%EC%98%A8%ED%82%B9-.JPG",
                120,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(15),
                LocalDate.now().plusDays(15),
                LocalDate.now().plusDays(15),
                12,
                UUID.randomUUID(),
                hall2);
        performance2 = performanceRepository.save(performance2);

        Performance performance3 = Performance.createTestData(
                "데스노트",
                "https://image.yes24.com/themusical/fileStorage/ThemusicalAdmin/Play/Image/20230207843266905c36ce1ad354a823e902457bc904d112.jpg",
                120,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now().plusDays(13),
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(13),
                19,
                UUID.randomUUID(),
                hall1);
        performance3 = performanceRepository.save(performance3);

        // 공연 일정 더미 데이터 생성
        Schedule schedule1 = Schedule.createTestData(LocalDate.now().plusDays(5), performance1);
        performance1.addSchedule(schedule1);

        Schedule schedule2 = Schedule.createTestData(LocalDate.now().plusDays(10), performance1);
        performance1.addSchedule(schedule2);

        Schedule schedule3 = Schedule.createTestData(LocalDate.now().plusDays(15), performance2);
        performance2.addSchedule(schedule3);

        Schedule schedule4 = Schedule.createTestData(LocalDate.now().plusDays(5), performance2);
        performance3.addSchedule(schedule4);

        // 좌석 가격 더미 데이터 생성
        SeatCost seatCost1 = SeatCost.createTestData("S", 120000, performance1);
        performance1.addSeatCost(seatCost1);

        SeatCost seatCost2 = SeatCost.createTestData("A", 90000, performance1);
        performance1.addSeatCost(seatCost2);

        SeatCost seatCost3 = SeatCost.createTestData("B", 60000, performance1);
        performance1.addSeatCost(seatCost3);

        SeatCost seatCost4 = SeatCost.createTestData("S", 150000, performance2);
        performance2.addSeatCost(seatCost4);

        SeatCost seatCost5 = SeatCost.createTestData("A", 110000, performance2);
        performance2.addSeatCost(seatCost5);

        SeatCost seatCost6 = SeatCost.createTestData("B", 80000, performance2);
        performance2.addSeatCost(seatCost6);

        SeatCost seatCost7 = SeatCost.createTestData("S", 150000, performance3);
        performance3.addSeatCost(seatCost7);

        SeatCost seatCost8 = SeatCost.createTestData("A", 110000, performance3);
        performance3.addSeatCost(seatCost8);

        SeatCost seatCost9 = SeatCost.createTestData("B", 80000, performance3);
        performance3.addSeatCost(seatCost9);

        // 좌석 더미 데이터 생성
        createSeats(schedule1);
        createSeats(schedule2);
        createSeats(schedule3);
        createSeats(schedule4);

        // 공연 좌석 캐싱
        performanceService.cacheAllSeatsForPerformance(performance1.getId());
        performanceService.cacheAllSeatsForPerformance(performance3.getId());
    }

    private void createSeats(Schedule schedule) {
        Performance performance = schedule.getPerformance();
        PerformanceHall performanceHall = performance.getPerformanceHall();

        for (int row = 1; row <= performanceHall.getRows(); row++) {
            for (int column = 1; column <= performanceHall.getColumns(); column++) {
                SeatCost seatCost = determineSeatCost(performance, row, performanceHall.getRows());
                Seat seat = Seat.createTestData(row, column, SeatStatus.AVAILABLE, seatCost, schedule);
                seatRepository.save(seat);
            }
        }
    }

    // 좌석 등급을 결정하는 로직
    private SeatCost determineSeatCost(Performance performance, int row, int max) {
        if (row <= ((max*2)/10)) {
            return performance.getSeatCosts().get(0);
        } else if (row <= ((max*4)/10)) {
            return performance.getSeatCosts().get(1);
        } else {
            return performance.getSeatCosts().get(2);
        }
    }
}

package com.ticketPing.performance.domain.model.entity;

import audit.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_performances")
public class Performance extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "performance_id")
    private UUID id;
    private String name;
    private String posterUrl;
    private int runTime;
    private LocalDateTime reservationStartDate;
    private LocalDateTime reservationEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private int grade;
    private UUID companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_hall_id")
    private PerformanceHall performanceHall;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SeatCost> seatCosts;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    public void addSeatCost(SeatCost seatCost) {
        seatCosts.add(seatCost);
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }

    public static Performance createTestData(String name, String posterUrl, int runTime, LocalDateTime reservationStartDate, LocalDateTime reservationEndDate,
                                             LocalDate startDate, LocalDate endDate, int grade, UUID companyId, PerformanceHall performanceHall) {
        return Performance.builder()
                .name(name)
                .posterUrl(posterUrl)
                .runTime(runTime)
                .reservationStartDate(reservationStartDate)
                .reservationEndDate(reservationEndDate)
                .startDate(startDate)
                .endDate(endDate)
                .grade(grade)
                .companyId(companyId)
                .performanceHall(performanceHall)
                .schedules(new ArrayList<>())
                .seatCosts(new ArrayList<>())
                .build();
    }
}

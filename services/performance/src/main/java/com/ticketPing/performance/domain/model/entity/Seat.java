package com.ticketPing.performance.domain.model.entity;

import com.ticketPing.performance.domain.model.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seat_id")
    private UUID id;
    private Integer row;
    private Integer col;
    private SeatStatus seatStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_cost_id")
    private SeatCost seatCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public static Seat createTestData(Integer row, Integer col, SeatStatus seatStatus, SeatCost seatCosts, Schedule schedule) {
        return Seat.builder()
                .row(row)
                .col(col)
                .seatStatus(seatStatus)
                .seatCost(seatCosts)
                .schedule(schedule)
                .build();
    }

    public void reserveSeat() {
        this.seatStatus = SeatStatus.RESERVED;
    }
}

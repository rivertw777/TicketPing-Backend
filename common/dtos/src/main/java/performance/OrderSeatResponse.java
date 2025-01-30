package performance;

import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record OrderSeatResponse(
        UUID performanceId,
        String performanceName,
        UUID scheduleId,
        LocalDate startDate,
        UUID performanceHallId,
        String performanceHallName,
        UUID companyId,
        UUID seatId,
        Integer row,
        Integer col,
        String seatGrade,
        Integer cost
) { }

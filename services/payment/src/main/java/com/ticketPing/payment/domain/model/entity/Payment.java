package com.ticketPing.payment.domain.model.entity;

import audit.BaseEntity;
import com.ticketPing.payment.domain.model.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;
import org.json.simple.JSONObject;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_payments")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID id;

    @NotNull
    private UUID userId;

    @NotNull
    private PaymentStatus status;

    @NotNull
    private UUID orderId;

    @NotNull
    private String orderName;

    @NotNull
    private String method;

    @NotNull
    private Long amount;

    @NotNull
    ZonedDateTime requestedAt;

    @NotNull
    ZonedDateTime approvedAt;

    public static Payment create(UUID userId, JSONObject responseData) {
        return Payment.builder()
                .userId(userId)
                .status(PaymentStatus.COMPLETED)
                .orderId(UUID.fromString((String) responseData.get("orderId")))
                .orderName((String) responseData.get("orderName"))
                .method((String) responseData.get("method"))
                .amount(((Number) responseData.get("totalAmount")).longValue())
                .requestedAt(ZonedDateTime.parse((String) responseData.get("requestedAt")))
                .approvedAt(ZonedDateTime.parse((String) responseData.get("approvedAt")))
                .build();
    }

}

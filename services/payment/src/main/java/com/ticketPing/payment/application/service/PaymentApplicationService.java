package com.ticketPing.payment.application.service;

import static com.ticketPing.payment.common.exception.PaymentErrorCase.TOSS_PAYMENT_CONFIRM_REQUEST_FAILED;

import com.ticketPing.payment.application.constants.TossPaymentConstants;
import com.ticketPing.payment.common.exception.PaymentException;
import exception.ApplicationException;
import org.json.simple.JSONObject;
import com.ticketPing.payment.application.dto.PaymentResponse;
import com.ticketPing.payment.domain.model.entity.Payment;
import com.ticketPing.payment.domain.service.PaymentDomainService;
import com.ticketPing.payment.presentation.request.PaymentConfirmRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import messaging.events.PaymentCompletedEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentApplicationService {

    private final PaymentDomainService paymentDomainService;
    private final RestTemplate restTemplate;
    private final EventApplicationService eventApplicationService;

    @Transactional
    public JSONObject confirmPayment(UUID userId, PaymentConfirmRequest request) {
        ResponseEntity<JSONObject> response = confirmTossPayment(request);

        if (response.getStatusCode().is2xxSuccessful()) {
            Payment payment = paymentDomainService.createPayment(userId, response.getBody());
            publishPaymentCompletedEvent(payment);
            return response.getBody();
        }
        throw new PaymentException((HttpStatus) response.getStatusCode(), response.getBody().toJSONString());
    }

    private ResponseEntity<JSONObject> confirmTossPayment(PaymentConfirmRequest request) {
        try {
            // Basic 인증 정보 설정
            String auth = "Basic " + Base64.getEncoder()
                    .encodeToString((TossPaymentConstants.widgetSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, auth);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // TOSS 결제 확인 요청
            return restTemplate.exchange(
                    TossPaymentConstants.paymentConfirmUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(request.toJson(), headers),
                    JSONObject.class
            );
        } catch (Exception e) {
            throw new ApplicationException(TOSS_PAYMENT_CONFIRM_REQUEST_FAILED);
        }
    }

    private void publishPaymentCompletedEvent(Payment payment) {
        val event = PaymentCompletedEvent.create(payment.getOrderId(), payment.getId());
        eventApplicationService.publishPaymentCompletedEvent(event);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = paymentDomainService.findPayment(paymentId);
        return PaymentResponse.from(payment);
    }

    public PaymentResponse getCompletedPaymentByOrderId(UUID orderId) {
        Payment payment = paymentDomainService.getCompletedPaymentByOrderId(orderId);
        return PaymentResponse.from(payment);
    }

}


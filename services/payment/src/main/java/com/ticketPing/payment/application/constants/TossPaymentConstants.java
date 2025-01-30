package com.ticketPing.payment.application.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TossPaymentConstants {

    private static String widgetSecretKey;
    private static String paymentConfirmUrl;

    @Value("${toss.payment.widget-secret-key}")
    public void setWidgetSecretKey(String widgetSecretKeyValue) {
        widgetSecretKey = widgetSecretKeyValue;
    }

    @Value("${toss.payment.payment-confirm-url}")
    public void setPaymentConfirmUrl(String paymentConfirmUrlValue) {
        paymentConfirmUrl = paymentConfirmUrlValue;
    }

    public static String widgetSecretKey() {
        return widgetSecretKey;
    }

    public static String paymentConfirmUrl() {
        return paymentConfirmUrl;
    }
}

package com.ticketPing.payment.presentation.request;

import java.util.UUID;
import org.json.simple.JSONObject;

public record PaymentConfirmRequest(
        UUID orderId,
        long amount,
        String paymentKey
) {
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("orderId", orderId);
        json.put("amount", amount);
        json.put("paymentKey", paymentKey);
        return json;
    }
}

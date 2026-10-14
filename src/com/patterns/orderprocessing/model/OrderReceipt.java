package com.patterns.orderprocessing.model;

import java.time.LocalDateTime;

public record OrderReceipt(
        String orderId,
        String customerName,
        double totalAmount,
        String trackingNumber,
        LocalDateTime timestamp,
        boolean isSuccess,
        String statusMessage
) {
    public static OrderReceipt failure(String orderId, String customerName, String message) {
        return new OrderReceipt(orderId, customerName, 0.0, "N/A", LocalDateTime.now(), false, message);
    }
}
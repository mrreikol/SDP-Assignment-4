package com.patterns.orderprocessing.model;

public record OrderItem(String productId, String productName, int quantity, double unitPrice) {
    public double getTotalPrice() {
        return quantity * unitPrice;
    }
}
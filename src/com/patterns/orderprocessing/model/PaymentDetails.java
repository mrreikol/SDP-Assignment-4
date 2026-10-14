package com.patterns.orderprocessing.model;

public record PaymentDetails(String cardNumber, String cardHolder, String cvv, double balance) {
}
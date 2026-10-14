package com.patterns.orderprocessing.subsystem;
import com.patterns.orderprocessing.model.PaymentDetails;
public class PaymentProcessor {

    public boolean processPayment(String orderId, PaymentDetails payment, double amount) {
        System.out.printf("[PaymentProcessor] Charging $%.2f to card %s...%n",
                amount, maskCardNumber(payment.cardNumber()));

        if (payment.balance() < amount) {
            System.out.println("[PaymentProcessor] Payment failed: Insufficient funds.");
            return false;
        }

        System.out.printf("[PaymentProcessor] Payment of $%.2f for order %s successfully processed.%n",
                amount, orderId);
        return true;
    }

    public void refundPayment(String orderId, PaymentDetails payment, double amount) {
        System.out.printf("[PaymentProcessor] ROLLBACK: Refunding $%.2f to card %s for order %s%n",
                amount, maskCardNumber(payment.cardNumber()), orderId);
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}
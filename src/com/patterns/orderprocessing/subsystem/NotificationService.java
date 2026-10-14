package com.patterns.orderprocessing.subsystem;
import com.patterns.orderprocessing.model.Customer;
import com.patterns.orderprocessing.model.OrderReceipt;

public class NotificationService {

    public void sendOrderConfirmation(Customer customer, OrderReceipt receipt) {
        System.out.printf("[NotificationService] Email sent to %s: 'Dear %s, your order %s has been confirmed! Tracking: %s'%n",
                customer.email(), customer.name(), receipt.orderId(), receipt.trackingNumber());
    }

    public void sendOrderFailureNotification(Customer customer, String orderId, String reason) {
        System.out.printf("[NotificationService] Email sent to %s: 'Order %s could not be processed. Reason: %s'%n",
                customer.email(), orderId, reason);
    }
}
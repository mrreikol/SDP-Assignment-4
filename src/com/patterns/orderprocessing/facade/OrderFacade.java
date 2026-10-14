package com.patterns.orderprocessing.facade;

import com.patterns.orderprocessing.model.*;
import com.patterns.orderprocessing.subsystem.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderFacade {
    private final InventoryService inventoryService;
    private final PaymentProcessor paymentProcessor;
    private final ShippingService shippingService;
    private final NotificationService notificationService;

    public OrderFacade() {
        this(new InventoryService(), new PaymentProcessor(), new ShippingService(), new NotificationService());
    }

    public OrderFacade(InventoryService inventoryService,
                       PaymentProcessor paymentProcessor,
                       ShippingService shippingService,
                       NotificationService notificationService) {
        this.inventoryService = Objects.requireNonNull(inventoryService);
        this.paymentProcessor = Objects.requireNonNull(paymentProcessor);
        this.shippingService = Objects.requireNonNull(shippingService);
        this.notificationService = Objects.requireNonNull(notificationService);
    }

    public OrderReceipt placeOrder(Customer customer, List<OrderItem> items, PaymentDetails payment) {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        double totalAmount = items.stream().mapToDouble(OrderItem::getTotalPrice).sum();

        System.out.println("\n========== INITIATING ORDER PROCESSING [" + orderId + "] ==========");

        boolean itemsReserved = inventoryService.reserveItems(orderId, items);
        if (!itemsReserved) {
            String errorMsg = "Requested items are currently out of stock.";
            notificationService.sendOrderFailureNotification(customer, orderId, errorMsg);
            return OrderReceipt.failure(orderId, customer.name(), errorMsg);
        }

        boolean paymentSuccess = paymentProcessor.processPayment(orderId, payment, totalAmount);
        if (!paymentSuccess) {
            inventoryService.releaseReservedItems(orderId, items);
            String errorMsg = "Transaction declined: Insufficient funds or payment gateway error.";
            notificationService.sendOrderFailureNotification(customer, orderId, errorMsg);
            return OrderReceipt.failure(orderId, customer.name(), errorMsg);
        }

        String trackingNumber = shippingService.arrangeShipping(orderId, customer);
        if (trackingNumber == null) {
            paymentProcessor.refundPayment(orderId, payment, totalAmount);
            inventoryService.releaseReservedItems(orderId, items);
            String errorMsg = "Unable to schedule shipping for the provided address.";
            notificationService.sendOrderFailureNotification(customer, orderId, errorMsg);
            return OrderReceipt.failure(orderId, customer.name(), errorMsg);
        }

        OrderReceipt receipt = new OrderReceipt(
                orderId,
                customer.name(),
                totalAmount,
                trackingNumber,
                LocalDateTime.now(),
                true,
                "Order placed successfully and dispatched to delivery service."
        );

        notificationService.sendOrderConfirmation(customer, receipt);
        System.out.println("========== ORDER [" + orderId + "] COMPLETED SUCCESSFULLY ==========\n");
        return receipt;
    }

    public void cancelOrder(String orderId, Customer customer, List<OrderItem> items, PaymentDetails payment, double amount, String trackingNumber) {
        System.out.println("\n>> Cancelling order [" + orderId + "]...");
        shippingService.cancelShipping(trackingNumber);
        paymentProcessor.refundPayment(orderId, payment, amount);
        inventoryService.releaseReservedItems(orderId, items);
        notificationService.sendOrderFailureNotification(customer, orderId, "Order was cancelled by the user.");
    }
}
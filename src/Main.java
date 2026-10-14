package com.patterns.orderprocessing;
import com.patterns.orderprocessing.facade.OrderFacade;
import com.patterns.orderprocessing.model.*;
import com.patterns.orderprocessing.subsystem.InventoryService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        OrderFacade orderFacade = new OrderFacade();

        Customer customer = new Customer(
                "CUST-101",
                "Rayazov Mustafa",
                "mail@example.com",
                "Kabanbay Batyr 53, Astana"
        );

        List<OrderItem> cart = List.of(
                new OrderItem("PROD-001", "Mechanical Keyboard", 1, 120.0),
                new OrderItem("PROD-002", "Wireless Mouse", 2, 45.0)
        );

        System.out.println(">>> SCENARIO 1: Successful Order Processing <<<");
        PaymentDetails validPayment = new PaymentDetails("4400123456789012", "Rayazov Mustafa", "777", 500.0);
        OrderReceipt receipt1 = orderFacade.placeOrder(customer, cart, validPayment);
        System.out.println("Client Status: " + receipt1.statusMessage() + " (Tracking: " + receipt1.trackingNumber() + ")");

        System.out.println("\n>>> SCENARIO 2: Payment Failure with Subsystem Rollback <<<");
        PaymentDetails lowBalancePayment = new PaymentDetails("4400123456789012", "Rayazov Mustafa", "777", 20.0);
        OrderReceipt receipt2 = orderFacade.placeOrder(customer, cart, lowBalancePayment);
        System.out.println("Client Status: " + receipt2.statusMessage() + " (Success: " + receipt2.isSuccess() + ")");

        System.out.println("\n>>> SCENARIO 3: Direct Subsystem Access (Warehouse Staff) <<<");
        InventoryService directInventory = new InventoryService();
        boolean hasStock = directInventory.checkStock("PROD-003", 1);
        System.out.println("[Warehouse Operator] Direct check for PROD-003: " + (hasStock ? "Available" : "Out of stock"));
    }
}
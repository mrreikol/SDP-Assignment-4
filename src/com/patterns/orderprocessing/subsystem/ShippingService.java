package com.patterns.orderprocessing.subsystem;
import com.patterns.orderprocessing.model.Customer;
import java.util.UUID;

public class ShippingService {

    public String arrangeShipping(String orderId, Customer customer) {
        if (customer.address() == null || customer.address().isBlank()) {
            System.out.println("[ShippingService] Error: Shipping address is missing.");
            return null;
        }

        String trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.printf("[ShippingService] Shipment created. Tracking Number: %s. Destination: %s%n",
                trackingNumber, customer.address());
        return trackingNumber;
    }

    public void cancelShipping(String trackingNumber) {
        System.out.println("[ShippingService] ROLLBACK: Cancelling shipment with tracking number: " + trackingNumber);
    }
}
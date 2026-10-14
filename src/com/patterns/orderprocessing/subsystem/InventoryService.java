package com.patterns.orderprocessing.subsystem;
import com.patterns.orderprocessing.model.OrderItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryService {
    private final Map<String, Integer> stock = new HashMap<>();

    public InventoryService() {
        stock.put("PROD-001", 10);
        stock.put("PROD-002", 5);
        stock.put("PROD-003", 2);
    }

    public synchronized boolean checkStock(String productId, int quantity) {
        int currentStock = stock.getOrDefault(productId, 0);
        return currentStock >= quantity;
    }

    public synchronized boolean reserveItems(String orderId, List<OrderItem> items) {
        for (OrderItem item : items) {
            if (!checkStock(item.productId(), item.quantity())) {
                System.out.println("[InventoryService] Out of stock for product: " + item.productName());
                return false;
            }
        }

        for (OrderItem item : items) {
            int current = stock.get(item.productId());
            stock.put(item.productId(), current - item.quantity());
            System.out.printf("[InventoryService] Reserved: %s (x%d) for order %s%n",
                    item.productName(), item.quantity(), orderId);
        }
        return true;
    }

    public synchronized void releaseReservedItems(String orderId, List<OrderItem> items) {
        System.out.println("[InventoryService] ROLLBACK: Releasing reserved items for order: " + orderId);
        for (OrderItem item : items) {
            int current = stock.getOrDefault(item.productId(), 0);
            stock.put(item.productId(), current + item.quantity());
            System.out.printf("[InventoryService] Restocked: %s (x%d)%n",
                    item.productName(), item.quantity());
        }
    }
}
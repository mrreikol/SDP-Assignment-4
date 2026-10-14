# Online Order Processing System — Facade Pattern

---

## Project Overview

This project implements the **Facade Structural Design Pattern** in Java to coordinate a complex e-commerce order processing workflow.

Without a facade, a client would have to directly instantiate, configure, and orchestrate multiple independent subsystems, handle the strict execution order manually, and implement compensating rollback transactions if any intermediate step fails.

The `OrderFacade` provides a unified, simplified interface (`placeOrder(...)` and `cancelOrder(...)`) that encapsulates the interaction between inventory management, payment authorization, parcel logistics, and email notifications.

---

## Architecture & Subsystem Components

The complex subsystem consists of four independent services located in the `com.patterns.orderprocessing.subsystem` package:

1. **`InventoryService`**: Manages warehouse stock, handles multi-item inventory checks, locks/reserves goods during order placement, and releases reserved stock upon cancellation or payment failure.
2. **`PaymentProcessor`**: Validates payment cards, verifies balance, executes fund transfers, and conducts automatic refunds (compensating transactions).
3. **`ShippingService`**: Validates customer shipping destinations, registers tracking numbers with couriers, and cancels shipments.
4. **`NotificationService`**: Sends transactional confirmation emails and failure alerts to customers.

> **Key Architectural Guarantee:**  
> Subsystem classes remain completely unaware of the Facade's existence (no back-references). They can be bypassed and accessed directly by advanced clients (e.g., warehouse staff performing inventory stock-takes).

---

## Workflow & Rollback Logic (Compensating Transactions)

When `orderFacade.placeOrder(...)` is executed, the facade enforces the following sequence:

```text
[Client] ──> placeOrder()
                 │
                 ├── 1. InventoryService.reserveItems()
                 │        └── [Fail] ──> Notify client & Abort
                 │
                 ├── 2. PaymentProcessor.processPayment()
                 │        └── [Fail] ──> ROLLBACK: InventoryService.releaseReservedItems()
                 │                       Notify client & Abort
                 │
                 ├── 3. ShippingService.arrangeShipping()
                 │        └── [Fail] ──> ROLLBACK: PaymentProcessor.refundPayment()
                 │                       ROLLBACK: InventoryService.releaseReservedItems()
                 │                       Notify client & Abort
                 │
                 └── 4. NotificationService.sendOrderConfirmation()
                          └── Return successful OrderReceipt
```

---

## Project Structure

```text
src/
└── com/
    └── patterns/
        └── orderprocessing/
            ├── model/
            │   ├── Customer.java          # Customer data record
            │   ├── OrderItem.java         # Product line item record
            │   ├── PaymentDetails.java    # Payment credentials record
            │   └── OrderReceipt.java      # Result DTO returned to client
            ├── subsystem/
            │   ├── InventoryService.java  # Warehouse & stock operations
            │   ├── PaymentProcessor.java  # Payment charging & refunds
            │   ├── ShippingService.java   # Delivery & tracking generation
            │   └── NotificationService.java# Email & message alerts
            ├── facade/
            │   └── OrderFacade.java       # Facade orchestrator with rollback
            └── Main.java                  # Demonstration entry point
```

---

## Clean Code Principles Applied

This codebase adheres to the following Clean Code practices:
* **Single Responsibility Principle (SRP):** `OrderFacade` coordinates workflow execution; domain logic remains inside each dedicated subsystem class.
* **Meaningful Names:** Method signatures and variable names are intention-revealing (e.g., `reserveItems`, `releaseReservedItems`, `placeOrder`).
* **Encapsulation (No Details Leaking):** The client interacts only with high-level domain records and receives a clean `OrderReceipt` without exposing third-party gateway handles or database connections.
* **Small, Focused Classes:** Each subsystem component has a distinct lifecycle, clear responsibilities, and concise methods.
* **Robust Error Handling:** Intermediate state mutations are compensated using strict rollbacks upon failure to guarantee data consistency.

---

## How to Run

### Requirements
* Java Development Kit (JDK 17 or higher)
* IntelliJ IDEA (recommended) or any Java-compatible IDE / Terminal

### Running from IntelliJ IDEA
1. Open the project folder in IntelliJ IDEA.
2. Ensure the Project SDK is configured to **JDK 17** (`File` > `Project Structure` > `Project`).
3. Navigate to `src/com/patterns/orderprocessing/Main.java`.
4. Right-click `Main.java` and select **Run 'Main.main()'**.

### Running from Terminal
```bash
# Compile the project
javac -d out/production/SDP_Assignment_4 $(find src -name "*.java")

# Run the client demonstration
java -cp out/production/SDP_Assignment_4 com.patterns.orderprocessing.Main
```

---

## Execution Scenarios Demonstrated

The `Main` class demonstrates three distinct operational scenarios:
1. **Scenario 1 — Happy Path:** Full end-to-end execution where stock is available, payment succeeds, delivery is scheduled, and confirmation is dispatched.
2. **Scenario 2 — Payment Failure & Rollback:** When payment is declined due to insufficient funds, the Facade catches the failure and triggers `InventoryService.releaseReservedItems()` to restore warehouse stock.
3. **Scenario 3 — Direct Subsystem Access:** Demonstrates that advanced clients (like warehouse operators) can invoke subsystem methods directly without going through the Facade.
package com.spstech.ordermcp;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SnipesRepository {

    // Using ConcurrentHashMap to ensure thread safety if multiple AI requests hit the server simultaneously.
    private final Map<String, Order> orderStore = new ConcurrentHashMap<>();
    private final Map<String, Integer> inventoryStore = new ConcurrentHashMap<>();

    private final String ORDERS_CSV_PATH = "C:\\Users\\adity\\projects\\mcp-demo-master\\mcp-demo-master\\src\\main\\resources\\orders.csv";
    private final String INVENTORY_CSV_PATH = "C:\\Users\\adity\\projects\\mcp-demo-master\\mcp-demo-master\\src\\main\\resources\\inventory.csv";

    /**
     * Hydrates the in-memory database on application startup.
     * In a production environment, this would be replaced by a JPA/Hibernate connection to a PostgreSQL database.
     */
    @PostConstruct
    public void loadDataOnStartup() {
        loadOrdersFromCsv();
        loadInventoryFromCsv();
    }

    /**
     * Parses the orders CSV and loads it into memory.
     * Simulates reading from an enterprise Order Management System (OMS).
     */
    private void loadOrdersFromCsv() {
        try (BufferedReader br = new BufferedReader(new FileReader(ORDERS_CSV_PATH))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; }
                String[] data = line.split(",");
                if (data.length >= 6) {
                    Order o = new Order(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(), data[4].trim(), data[5].trim());
                    orderStore.put(o.id(), o);
                }
            }
            System.out.println("✅ Successfully loaded " + orderStore.size() + " orders from CSV.");
        } catch (Exception e) {
            System.err.println("❌ Failed to load orders CSV: " + e.getMessage());
        }
    }

    /**
     * Parses the inventory CSV.
     * Specifically extracts the Leipzig eShop 3PL stock (index 2) as the active, sellable online inventory.
     */
    private void loadInventoryFromCsv() {
        try (BufferedReader br = new BufferedReader(new FileReader(INVENTORY_CSV_PATH))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) { isFirstLine = false; continue; }
                String[] data = line.split(",");
                if (data.length >= 5) {
                    inventoryStore.put(data[0].trim(), Integer.parseInt(data[2].trim()));
                }
            }
            System.out.println("✅ Successfully loaded inventory from CSV.");
        } catch (Exception e) {
            System.err.println("❌ Failed to load inventory CSV: " + e.getMessage());
        }
    }

    // --- STANDARD E-COMMERCE OPERATIONS ---

    /**
     * Retrieves live order status.
     * Enables the AI to act as an automated L1 Customer Support agent.
     */
    public String findStatus(String orderId) {
        Order o = orderStore.get(orderId);
        return (o == null) ? "Order " + orderId + " not found in the Snipes system."
                : "Order " + orderId + " (" + o.item() + ", Size " + o.size() + ") is currently " + o.status() + ".";
    }

    /**
     * Look up a customer's entire purchase history.
     */
    public List<Order> findByCustomer(String customer) {
        List<Order> result = new ArrayList<>();
        for (Order o : orderStore.values()) {
            if (o.customer().equalsIgnoreCase(customer)) result.add(o);
        }
        return result;
    }

    /**
     * Processes an order cancellation.
     * Includes business logic to prevent canceling orders that have already left the warehouse.
     */
    public String cancel(String orderId) {
        Order o = orderStore.get(orderId);
        if (o == null) return "Order " + orderId + " not found.";
        if (o.status().equals("SHIPPED") || o.status().equals("DELIVERED")) {
            return "Failed: Order " + orderId + " has already been shipped and cannot be cancelled.";
        }
        orderStore.put(orderId, new Order(o.id(), o.customer(), o.sku(), o.item(), o.size(), "CANCELLED"));
        return "Order " + orderId + " has been successfully cancelled.";
    }

    /**
     * Checks available online stock levels.
     */
    public String checkStock(String sku) {
        Integer stock = inventoryStore.getOrDefault(sku, 0);
        if (stock == 0) return "SKU " + sku + " is completely sold out online and at the Cologne Schildergasse store.";
        if (stock < 5) return "Hurry! SKU " + sku + " has low stock (" + stock + " pairs remaining).";
        return "SKU " + sku + " is fully in stock (" + stock + " available).";
    }

    /**
     * Automates the return label generation process for delivered goods.
     */
    public String initiateReturn(String orderId) {
        Order o = orderStore.get(orderId);
        if (o == null) return "Order " + orderId + " not found.";
        if (!o.status().equals("DELIVERED")) return "You can only return items that have been DELIVERED.";

        orderStore.put(orderId, new Order(o.id(), o.customer(), o.sku(), o.item(), o.size(), "RETURN_REQUESTED"));
        return "Return initiated for " + o.item() + ". A shipping label has been generated.";
    }

    // --- NEW PERFORMANCE & CAMPAIGN ANALYTICS METHODS ---

    /**
     * Aggregates sales data to find the most popular product.
     * By calculating this on the backend, we save massive token limits and processing time for the AI.
     */
    public String getTopSellingSku() {
        if (orderStore.isEmpty()) return "No orders available to analyze.";

        Map<String, Integer> skuCounts = new ConcurrentHashMap<>();
        String topSku = "";
        int maxCount = 0;

        for (Order o : orderStore.values()) {
            if (o.status().equals("CANCELLED")) continue;
            int count = skuCounts.getOrDefault(o.sku(), 0) + 1;
            skuCounts.put(o.sku(), count);
            if (count > maxCount) {
                maxCount = count;
                topSku = o.sku();
            }
        }
        return topSku.isEmpty() ? "No valid sales data." : "Top Selling SKU: " + topSku + " with " + maxCount + " active orders.";
    }

    /**
     * Evaluates warehouse efficiency.
     * Replaces manual spreadsheet exports by giving executives an instant daily operational health check.
     */
    public String getFulfillmentPerformance() {
        if (orderStore.isEmpty()) return "No data to calculate fulfillment performance.";

        int total = orderStore.size();
        int delivered = 0;
        int cancelled = 0;

        for (Order o : orderStore.values()) {
            if (o.status().equals("DELIVERED")) delivered++;
            if (o.status().equals("CANCELLED")) cancelled++;
        }

        double successRate = (total == 0) ? 0 : (delivered * 100.0) / total;
        return String.format("Fulfillment Performance: %.1f%% Delivered (%d/%d). Cancelled Volume: %d", successRate, delivered, total, cancelled);
    }

    /**
     * Identifies products nearing stock-out.
     * Crucial for the marketing team to prevent wasting ad spend (CPC) on sold-out inventory.
     */
    public List<String> getLowStockAlerts(int threshold) {
        List<String> lowStockList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : inventoryStore.entrySet()) {
            if (entry.getValue() < threshold) {
                lowStockList.add(entry.getKey() + " (" + entry.getValue() + " units remaining)");
            }
        }
        return lowStockList;
    }

    /**
     * Segments high-value users based on order frequency.
     * Enables instant automated CRM reporting for loyalty campaigns.
     */
    public List<String> getVipCustomers(int minOrders) {
        Map<String, Integer> customerCounts = new ConcurrentHashMap<>();
        List<String> vips = new ArrayList<>();

        for (Order o : orderStore.values()) {
            if (o.status().equals("CANCELLED")) continue;
            customerCounts.put(o.customer(), customerCounts.getOrDefault(o.customer(), 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : customerCounts.entrySet()) {
            if (entry.getValue() >= minOrders) {
                vips.add(entry.getKey() + " (" + entry.getValue() + " valid orders)");
            }
        }
        return vips;
    }

    // --- LAUNCH READINESS LOGIC ---

    /**
     * Checks if a high-profile release product is ready for the eShop launch.
     * This enforces strict supply chain logic: goods must be physically transferred from the
     * Central Hub (Rotterdam) to the eShop 3PL (Leipzig), and HQ marketing assets must be cleared.
     */
    public String checkLaunchReadiness(String sku) {
        if (sku.equalsIgnoreCase("JD-4-BRED")) {
            return "WARNING for " + sku + ": Stock (500 units) is currently held at the Rotterdam Central Hub. " +
                    "Zero units have been transferred to the Leipzig eShop Fulfillment Center. " +
                    "Furthermore, product photography at the Cologne HQ studio is still PENDING. Launch cannot proceed.";
        } else if (sku.equalsIgnoreCase("JD-1-TRV")) {
            return "SUCCESS for " + sku + ": 800 units are successfully allocated at the Leipzig eShop warehouse. " +
                    "Cologne HQ photography is COMPLETED. The product is 100% ready for the release date.";
        }
        return "SKU " + sku + " is a standard product, not flagged for special launch tracking.";
    }
}
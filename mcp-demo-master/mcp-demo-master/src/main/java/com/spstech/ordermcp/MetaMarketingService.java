package com.spstech.ordermcp;

import org.springframework.stereotype.Service;

@Service
public class MetaMarketingService {

    /**
     * Simulates an HTTP GET request to the Meta Graph API.
     * e.g., GET https://graph.facebook.com/v19.0/{ad_account_id}/insights?filtering=[{field:"ad.name",operator:"CONTAIN",value:"{sku}"}]
     */
    public MetaAdInsights fetchLiveAdData(String sku) {
        // Simulate a tiny bit of network latency so it feels real in the logs
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mocking the API response based on the SKU
        if (sku.equalsIgnoreCase("JD-4-BRED")) {
            // High hype, expensive clicks, active campaign
            return new MetaAdInsights("META-CMP-992", sku, 5.50, 1250.00, "ACTIVE");
        } else if (sku.equalsIgnoreCase("NK-AF1-WHT")) {
            // Standard shoe, cheaper clicks, active campaign
            return new MetaAdInsights("META-CMP-104", sku, 1.20, 340.00, "ACTIVE");
        } else {
            // Default response for other items
            return new MetaAdInsights("META-CMP-000", sku, 0.80, 50.00, "PAUSED");
        }
    }
}
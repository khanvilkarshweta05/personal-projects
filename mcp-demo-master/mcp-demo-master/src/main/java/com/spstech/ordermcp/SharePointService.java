package com.spstech.ordermcp;

import org.springframework.stereotype.Service;

@Service
public class SharePointService {

    /**
     * Simulates an HTTP GET request to the Microsoft Graph API.
     * e.g., GET https://graph.microsoft.com/v1.0/sites/{site-id}/drive/root:/Marketing/Campaigns/{sku}:/children
     */
    public String searchCampaignAssets(String sku) {
        // Simulate network latency for the Microsoft Graph API call
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mocking the Graph API response for our test SKUs
        if (sku.equalsIgnoreCase("JD-4-BRED")) {
            return """
                   [SharePoint Document Library: Snipes HQ Marketing]
                   Folder: /Shared Documents/Campaigns/Q3_Jordan_Bred/
                   Files Found:
                   1. hero_banner_16x9.psd (Approved - 45MB)
                   2. ig_reel_story.mp4 (Approved - 120MB)
                   3. product_shots_raw.zip (Pending Retouching - 1.2GB)
                   SharePoint Direct Link: https://snipes.sharepoint.com/sites/marketing/JD4BRED
                   """;
        } else if (sku.equalsIgnoreCase("NK-AF1-WHT")) {
            return """
                   [SharePoint Document Library: Snipes HQ Marketing]
                   Folder: /Shared Documents/Evergreen/Nike_AF1/
                   Files Found:
                   1. af1_white_studio.jpg (Approved - 5MB)
                   2. af1_lifestyle_cologne.jpg (Approved - 8MB)
                   SharePoint Direct Link: https://snipes.sharepoint.com/sites/marketing/NKAF1
                   """;
        } else {
            return "[SharePoint Document Library] No specific campaign folder found for SKU: " + sku + ". Searching general archive...";
        }
    }
}
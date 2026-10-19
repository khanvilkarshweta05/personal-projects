package com.spstech.ordermcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderTools {

    private final SnipesRepository snipes;
    private final MetaMarketingService metaApi;
    private final SharePointService sharePoint;
    private final AssetManagementService assetService; // <-- NEW: Injecting Cloudinary Service

    public OrderTools(SnipesRepository snipes, MetaMarketingService metaApi, SharePointService sharePoint, AssetManagementService assetService) {
        this.snipes = snipes;
        this.metaApi = metaApi;
        this.sharePoint = sharePoint;
        this.assetService = assetService; // <-- NEW: Constructor assignment
    }

    // --- EXISTING SNIPES REPOSITORY TOOLS ---

    @Tool(name = "getOrderStatus", description = "Get the current status of a Snipes order by its ID")
    public String getOrderStatus(@ToolParam(description = "The order ID (e.g., SNP-1042)", required = true) String orderId) {
        return snipes.findStatus(orderId);
    }

    @Tool(name = "searchOrders", description = "Search for a customer's Snipes order history by their full name")
    public List<Order> searchOrders(@ToolParam(description = "Customer full name", required = true) String customer) {
        return snipes.findByCustomer(customer);
    }

    @Tool(name = "cancelOrder", description = "Cancel an order by its ID. Cannot cancel shipped items.")
    public String cancelOrder(@ToolParam(description = "The order ID", required = true) String orderId) {
        return snipes.cancel(orderId);
    }

    @Tool(name = "checkSneakerStock", description = "Check the live inventory stock level for a specific sneaker or item SKU")
    public String checkSneakerStock(@ToolParam(description = "The product SKU (e.g., JD-4-BRED)", required = true) String sku) {
        return snipes.checkStock(sku);
    }

    @Tool(name = "initiateReturn", description = "Start a return process for a delivered order")
    public String initiateReturn(@ToolParam(description = "The order ID to return", required = true) String orderId) {
        return snipes.initiateReturn(orderId);
    }

    @Tool(name = "getTopSellingSku", description = "Identifies the most popular product SKU based on current order volume.")
    public String getTopSellingSku() {
        return snipes.getTopSellingSku();
    }

    @Tool(name = "getFulfillmentPerformance", description = "Analyzes the warehouse delivery success rate.")
    public String getFulfillmentPerformance() {
        return snipes.getFulfillmentPerformance();
    }

    @Tool(name = "getLowStockAlerts", description = "Retrieves a list of SKUs that have dropped below a specified stock threshold.")
    public List<String> getLowStockAlerts(@ToolParam(description = "The stock threshold limit (e.g., 5 or 10)", required = true) int threshold) {
        return snipes.getLowStockAlerts(threshold);
    }

    @Tool(name = "getVipCustomers", description = "Identifies VIP customers based on a minimum number of valid orders.")
    public List<String> getVipCustomers(@ToolParam(description = "Minimum orders required", required = true) int minOrders) {
        return snipes.getVipCustomers(minOrders);
    }

    // --- EXISTING PLATFORM API INTEGRATION TOOLS ---

    @Tool(name = "fetchMetaAdInsights", description = "Calls the Meta Graph API to fetch live ad spend, Cost Per Click (CPC), and campaign status for a specific SKU.")
    public String fetchMetaAdInsights(@ToolParam(description = "The product SKU to check against Facebook/Instagram ads", required = true) String sku) {
        MetaAdInsights insights = metaApi.fetchLiveAdData(sku);
        return String.format("Meta API Graph Data -> Campaign: %s | SKU: %s | CPC: €%.2f | Total Spend: €%.2f | Ad Status: %s",
                insights.campaignId(), insights.sku(), insights.costPerClick(), insights.totalSpend(), insights.status());
    }

    @Tool(name = "checkLaunchReadiness", description = "Checks logistics and marketing asset readiness for a high-profile sneaker launch.")
    public String checkLaunchReadiness(
            @ToolParam(description = "The product SKU to check (e.g., JD-4-BRED)", required = true) String sku) {
        return snipes.checkLaunchReadiness(sku);
    }

    @Tool(name = "fetchSharePointAssets", description = "Connects to the corporate SharePoint Document Library via Microsoft Graph API to retrieve raw image and video assets for performance campaigns.")
    public String fetchSharePointAssets(
            @ToolParam(description = "The product SKU to find assets for", required = true) String sku) {
        return sharePoint.searchCampaignAssets(sku);
    }

    // --- NEW: CLOUDINARY DAM INTEGRATION TOOL ---

    @Tool(name = "searchInternalCampaignAssets", description = "Searches the internal Java backend DAM (Cloudinary) for marketing campaign assets by tag.")
    public String searchInternalCampaignAssets(
            @ToolParam(description = "The campaign tag to search for (e.g., META-CMP-992)", required = true) String campaignTag) {
        try {
            return assetService.searchCampaignAssets(campaignTag).toString();
        } catch (Exception e) {
            return "Error fetching assets from Java backend: " + e.getMessage();
        }
    }


    @Tool(name = "applySoldOutWatermark", description = "Applies a 'SOLD OUT' text watermark to a Cloudinary asset site-wide to prevent wasted ad spend.")
    public String applySoldOutWatermark(
            @ToolParam(description = "The exact Cloudinary public_id of the asset", required = true) String publicId) {
        try {
            return assetService.generateSoldOutWatermark(publicId);
        } catch (Exception e) {
            return "Error applying watermark: " + e.getMessage();
        }
    }

    @Tool(name = "bulkDownloadCloudinaryAssets", description = "Downloads Cloudinary assets by tag to a local Windows folder.")
    public String bulkDownloadCloudinaryAssets(
            @ToolParam(description = "The campaign tag to search for", required = true) String tag,
            @ToolParam(description = "The Windows directory path, e.g., C:\\Snipes\\Campaigns", required = true) String windowsDirectory) {
        return assetService.downloadCampaignAssets(tag, windowsDirectory);
    }
}
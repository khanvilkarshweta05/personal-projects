package com.spstech.ordermcp;

public record MetaAdInsights(
        String campaignId,
        String sku,
        double costPerClick,
        double totalSpend,
        String status
) {}
package com.spstech.ordermcp;

/** Simple order record returned to the AI client for Snipes inventory. */
public record Order(
        String id,
        String customer,
        String sku,
        String item,
        String size,
        String status
) {}
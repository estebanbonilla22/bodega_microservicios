package com.bodega.inventory.web.dto;

import java.util.UUID;

public record StockResponse(UUID productId, long quantity) {}

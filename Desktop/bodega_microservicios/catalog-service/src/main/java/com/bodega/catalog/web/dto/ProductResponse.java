package com.bodega.catalog.web.dto;

import com.bodega.catalog.domain.ProductType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String sku,
    String name,
    ProductType type,
    String unit,
    boolean active,
    UUID categoryId,
    OffsetDateTime createdAt) {}

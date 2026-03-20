package com.bodega.catalog.web.dto;

import com.bodega.catalog.domain.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateProductRequest(
    @NotBlank String sku,
    @NotBlank String name,
    @NotNull ProductType type,
    @NotBlank String unit,
    boolean active,
    UUID categoryId) {}

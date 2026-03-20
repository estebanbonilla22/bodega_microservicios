package com.bodega.inventory.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MovementRequest(
    @NotNull UUID productId, @NotNull @Min(1) Long quantity, String note) {}

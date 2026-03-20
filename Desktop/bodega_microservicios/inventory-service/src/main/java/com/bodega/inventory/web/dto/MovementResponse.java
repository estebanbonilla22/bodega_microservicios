package com.bodega.inventory.web.dto;

import com.bodega.inventory.domain.MovementType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MovementResponse(
    UUID id,
    UUID productId,
    MovementType type,
    long quantity,
    OffsetDateTime occurredAt,
    String note,
    String createdBy) {}

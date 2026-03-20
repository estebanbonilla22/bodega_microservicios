package com.bodega.dispatch.web.dto;

import com.bodega.dispatch.domain.DispatchStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DispatchResponse(
    UUID id,
    UUID movementId,
    String address,
    DispatchStatus status,
    OffsetDateTime deliveredAt,
    String notes,
    OffsetDateTime createdAt) {}

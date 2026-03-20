package com.bodega.dispatch.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InventoryMovementSnapshot(UUID id, UUID productId, String type, long quantity) {}

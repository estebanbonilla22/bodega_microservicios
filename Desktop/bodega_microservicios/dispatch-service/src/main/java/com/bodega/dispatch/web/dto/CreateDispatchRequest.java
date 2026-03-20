package com.bodega.dispatch.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateDispatchRequest(
    @NotNull UUID movementId, @NotBlank String address, String notes) {}

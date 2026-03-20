package com.bodega.auth.web.dto;

import com.bodega.auth.domain.UserRole;
import java.util.UUID;

public record MeResponse(UUID id, String username, UserRole role) {}


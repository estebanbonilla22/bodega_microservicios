package com.bodega.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(@NotBlank String name) {}

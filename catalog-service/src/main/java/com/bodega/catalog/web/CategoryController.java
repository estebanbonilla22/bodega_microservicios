package com.bodega.catalog.web;

import com.bodega.catalog.service.CategoryService;
import com.bodega.catalog.web.dto.CategoryResponse;
import com.bodega.catalog.web.dto.CreateCategoryRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping("/categories")
  public List<CategoryResponse> list() {
    return categoryService.list();
  }

  @PostMapping("/categories")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public CategoryResponse create(@Valid @RequestBody CreateCategoryRequest body) {
    return categoryService.create(body);
  }
}

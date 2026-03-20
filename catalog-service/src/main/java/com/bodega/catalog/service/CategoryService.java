package com.bodega.catalog.service;

import com.bodega.catalog.domain.Category;
import com.bodega.catalog.repo.CategoryRepository;
import com.bodega.catalog.web.dto.CategoryResponse;
import com.bodega.catalog.web.dto.CreateCategoryRequest;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Transactional(readOnly = true)
  public List<CategoryResponse> list() {
    return categoryRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional
  public CategoryResponse create(CreateCategoryRequest req) {
    categoryRepository
        .findByName(req.name().trim())
        .ifPresent(
            c -> {
              throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
            });
    Category c = new Category();
    c.setName(req.name().trim());
    c.setCreatedAt(OffsetDateTime.now());
    return toResponse(categoryRepository.save(c));
  }

  private CategoryResponse toResponse(Category c) {
    return new CategoryResponse(c.getId(), c.getName(), c.getCreatedAt());
  }
}

package com.bodega.catalog.service;

import com.bodega.catalog.domain.Category;
import com.bodega.catalog.domain.Product;
import com.bodega.catalog.repo.CategoryRepository;
import com.bodega.catalog.repo.ProductRepository;
import com.bodega.catalog.web.dto.CreateProductRequest;
import com.bodega.catalog.web.dto.ProductResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
  }

  @Transactional(readOnly = true)
  public List<ProductResponse> list() {
    return productRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public ProductResponse get(UUID id) {
    Product p =
        productRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    if (!p.isActive()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product inactive");
    }
    return toResponse(p);
  }

  /** Used by other services: exists, active, returns minimal view. */
  @Transactional(readOnly = true)
  public ProductResponse getForValidation(UUID id) {
    return get(id);
  }

  @Transactional
  public ProductResponse create(CreateProductRequest req) {
    productRepository
        .findBySku(req.sku())
        .ifPresent(
            x -> {
              throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
            });
    Product p = new Product();
    p.setSku(req.sku().trim());
    p.setName(req.name().trim());
    p.setType(req.type());
    p.setUnit(req.unit().trim());
    p.setActive(req.active());
    p.setCreatedAt(OffsetDateTime.now());
    if (req.categoryId() != null) {
      Category cat =
          categoryRepository
              .findById(req.categoryId())
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category"));
      p.setCategory(cat);
    }
    return toResponse(productRepository.save(p));
  }

  private ProductResponse toResponse(Product p) {
    UUID catId = p.getCategory() != null ? p.getCategory().getId() : null;
    return new ProductResponse(
        p.getId(),
        p.getSku(),
        p.getName(),
        p.getType(),
        p.getUnit(),
        p.isActive(),
        catId,
        p.getCreatedAt());
  }
}

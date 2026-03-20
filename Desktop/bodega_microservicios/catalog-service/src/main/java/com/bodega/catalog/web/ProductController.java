package com.bodega.catalog.web;

import com.bodega.catalog.service.ProductService;
import com.bodega.catalog.web.dto.CreateProductRequest;
import com.bodega.catalog.web.dto.ProductResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/products")
  public List<ProductResponse> list() {
    return productService.list();
  }

  @GetMapping("/products/{id}")
  public ProductResponse get(@PathVariable UUID id) {
    return productService.get(id);
  }

  @PostMapping("/products")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public ProductResponse create(@Valid @RequestBody CreateProductRequest body) {
    return productService.create(body);
  }
}

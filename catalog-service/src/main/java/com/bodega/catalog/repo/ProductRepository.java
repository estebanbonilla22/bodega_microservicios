package com.bodega.catalog.repo;

import com.bodega.catalog.domain.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  Optional<Product> findBySku(String sku);
}


package com.bodega.inventory.repo;

import com.bodega.inventory.domain.StockItem;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockItemRepository extends JpaRepository<StockItem, UUID> {
  Optional<StockItem> findByProductId(UUID productId);
}


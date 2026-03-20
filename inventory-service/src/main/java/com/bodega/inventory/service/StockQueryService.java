package com.bodega.inventory.service;

import com.bodega.inventory.repo.StockItemRepository;
import com.bodega.inventory.web.dto.StockResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StockQueryService {

  private final StockItemRepository stockItemRepository;

  public StockQueryService(StockItemRepository stockItemRepository) {
    this.stockItemRepository = stockItemRepository;
  }

  @Transactional(readOnly = true)
  public StockResponse get(UUID productId) {
    return stockItemRepository
        .findByProductId(productId)
        .map(s -> new StockResponse(s.getProductId(), s.getQuantity()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No stock for product"));
  }
}

package com.bodega.inventory.web;

import com.bodega.inventory.service.StockQueryService;
import com.bodega.inventory.web.dto.StockResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {

  private final StockQueryService stockQueryService;

  public StockController(StockQueryService stockQueryService) {
    this.stockQueryService = stockQueryService;
  }

  @GetMapping("/stock")
  public StockResponse stock(@RequestParam("productId") UUID productId) {
    return stockQueryService.get(productId);
  }
}

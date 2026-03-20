package com.bodega.inventory.service;

import com.bodega.inventory.client.CatalogProductClient;
import com.bodega.inventory.domain.Movement;
import com.bodega.inventory.domain.MovementType;
import com.bodega.inventory.domain.StockItem;
import com.bodega.inventory.repo.MovementRepository;
import com.bodega.inventory.repo.StockItemRepository;
import com.bodega.inventory.web.dto.MovementRequest;
import com.bodega.inventory.web.dto.MovementResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MovementService {

  private final MovementRepository movementRepository;
  private final StockItemRepository stockItemRepository;
  private final CatalogProductClient catalogProductClient;

  public MovementService(
      MovementRepository movementRepository,
      StockItemRepository stockItemRepository,
      CatalogProductClient catalogProductClient) {
    this.movementRepository = movementRepository;
    this.stockItemRepository = stockItemRepository;
    this.catalogProductClient = catalogProductClient;
  }

  @Transactional(readOnly = true)
  public Optional<MovementResponse> findById(UUID id) {
    return movementRepository.findById(id).map(this::toResponse);
  }

  @Transactional(readOnly = true)
  public List<MovementResponse> list(Optional<MovementType> type) {
    List<Movement> rows =
        type.map(movementRepository::findTop100ByTypeOrderByOccurredAtDesc)
            .orElseGet(movementRepository::findTop100ByOrderByOccurredAtDesc);
    return rows.stream().map(this::toResponse).toList();
  }

  @Transactional
  public MovementResponse entrada(MovementRequest req, String createdBy) {
    catalogProductClient.assertProductExistsAndActive(req.productId());
    StockItem stock =
        stockItemRepository
            .findByProductId(req.productId())
            .orElseGet(
                () -> {
                  StockItem s = new StockItem();
                  s.setProductId(req.productId());
                  s.setQuantity(0);
                  s.setUpdatedAt(OffsetDateTime.now());
                  return s;
                });
    stock.setQuantity(stock.getQuantity() + req.quantity());
    stock.setUpdatedAt(OffsetDateTime.now());
    stockItemRepository.save(stock);
    Movement m = new Movement();
    m.setProductId(req.productId());
    m.setType(MovementType.ENTRADA);
    m.setQuantity(req.quantity());
    m.setOccurredAt(OffsetDateTime.now());
    m.setNote(req.note());
    m.setCreatedBy(createdBy);
    return toResponse(movementRepository.save(m));
  }

  @Transactional
  public MovementResponse salida(MovementRequest req, String createdBy) {
    catalogProductClient.assertProductExistsAndActive(req.productId());
    StockItem stock =
        stockItemRepository
            .findByProductId(req.productId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No stock row for product"));
    if (stock.getQuantity() < req.quantity()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock");
    }
    stock.setQuantity(stock.getQuantity() - req.quantity());
    stock.setUpdatedAt(OffsetDateTime.now());
    stockItemRepository.save(stock);
    Movement m = new Movement();
    m.setProductId(req.productId());
    m.setType(MovementType.SALIDA);
    m.setQuantity(req.quantity());
    m.setOccurredAt(OffsetDateTime.now());
    m.setNote(req.note());
    m.setCreatedBy(createdBy);
    return toResponse(movementRepository.save(m));
  }

  private MovementResponse toResponse(Movement m) {
    return new MovementResponse(
        m.getId(),
        m.getProductId(),
        m.getType(),
        m.getQuantity(),
        m.getOccurredAt(),
        m.getNote(),
        m.getCreatedBy());
  }
}

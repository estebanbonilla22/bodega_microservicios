package com.bodega.dispatch.service;

import com.bodega.dispatch.client.InventoryMovementClient;
import com.bodega.dispatch.domain.Dispatch;
import com.bodega.dispatch.domain.DispatchStatus;
import com.bodega.dispatch.repo.DispatchRepository;
import com.bodega.dispatch.web.dto.CreateDispatchRequest;
import com.bodega.dispatch.web.dto.DispatchResponse;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DispatchService {

  private final DispatchRepository dispatchRepository;
  private final InventoryMovementClient inventoryMovementClient;

  public DispatchService(
      DispatchRepository dispatchRepository, InventoryMovementClient inventoryMovementClient) {
    this.dispatchRepository = dispatchRepository;
    this.inventoryMovementClient = inventoryMovementClient;
  }

  @Transactional(readOnly = true)
  public List<DispatchResponse> list() {
    return dispatchRepository.findTop100ByOrderByCreatedAtDesc().stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  public DispatchResponse create(CreateDispatchRequest req) {
    inventoryMovementClient.requireSalidaMovement(req.movementId());
    Dispatch d = new Dispatch();
    d.setMovementId(req.movementId());
    d.setAddress(req.address().trim());
    d.setStatus(DispatchStatus.CREATED);
    d.setNotes(req.notes());
    d.setCreatedAt(OffsetDateTime.now());
    return toResponse(dispatchRepository.save(d));
  }

  private DispatchResponse toResponse(Dispatch d) {
    return new DispatchResponse(
        d.getId(),
        d.getMovementId(),
        d.getAddress(),
        d.getStatus(),
        d.getDeliveredAt(),
        d.getNotes(),
        d.getCreatedAt());
  }
}

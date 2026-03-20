package com.bodega.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "movements")
public class Movement {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MovementType type;

  @Column(nullable = false)
  private long quantity;

  @Column(name = "occurred_at", nullable = false)
  private OffsetDateTime occurredAt;

  private String note;

  @Column(name = "created_by")
  private String createdBy;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getProductId() {
    return productId;
  }

  public void setProductId(UUID productId) {
    this.productId = productId;
  }

  public MovementType getType() {
    return type;
  }

  public void setType(MovementType type) {
    this.type = type;
  }

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }

  public OffsetDateTime getOccurredAt() {
    return occurredAt;
  }

  public void setOccurredAt(OffsetDateTime occurredAt) {
    this.occurredAt = occurredAt;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }
}


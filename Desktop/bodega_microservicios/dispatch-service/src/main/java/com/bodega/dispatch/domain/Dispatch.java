package com.bodega.dispatch.domain;

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
@Table(name = "dispatches")
public class Dispatch {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "movement_id", nullable = false)
  private UUID movementId;

  @Column(nullable = false)
  private String address;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DispatchStatus status;

  @Column(name = "delivered_at")
  private OffsetDateTime deliveredAt;

  private String notes;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getMovementId() {
    return movementId;
  }

  public void setMovementId(UUID movementId) {
    this.movementId = movementId;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public DispatchStatus getStatus() {
    return status;
  }

  public void setStatus(DispatchStatus status) {
    this.status = status;
  }

  public OffsetDateTime getDeliveredAt() {
    return deliveredAt;
  }

  public void setDeliveredAt(OffsetDateTime deliveredAt) {
    this.deliveredAt = deliveredAt;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}


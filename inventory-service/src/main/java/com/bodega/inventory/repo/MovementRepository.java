package com.bodega.inventory.repo;

import com.bodega.inventory.domain.Movement;
import com.bodega.inventory.domain.MovementType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<Movement, UUID> {
  List<Movement> findTop100ByTypeOrderByOccurredAtDesc(MovementType type);

  List<Movement> findTop100ByOrderByOccurredAtDesc();
}


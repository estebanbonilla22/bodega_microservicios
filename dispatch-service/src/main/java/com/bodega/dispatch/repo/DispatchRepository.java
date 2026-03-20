package com.bodega.dispatch.repo;

import com.bodega.dispatch.domain.Dispatch;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchRepository extends JpaRepository<Dispatch, UUID> {
  List<Dispatch> findTop100ByOrderByCreatedAtDesc();
}


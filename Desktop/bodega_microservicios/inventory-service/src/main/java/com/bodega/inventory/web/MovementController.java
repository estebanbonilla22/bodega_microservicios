package com.bodega.inventory.web;

import com.bodega.inventory.domain.MovementType;
import com.bodega.inventory.service.MovementService;
import com.bodega.inventory.web.dto.MovementRequest;
import com.bodega.inventory.web.dto.MovementResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MovementController {

  private final MovementService movementService;

  public MovementController(MovementService movementService) {
    this.movementService = movementService;
  }

  @GetMapping("/movements")
  public List<MovementResponse> list(@RequestParam Optional<MovementType> type) {
    return movementService.list(type);
  }

  @GetMapping("/movements/{id}")
  public MovementResponse get(@PathVariable UUID id) {
    return movementService
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movement not found"));
  }

  @PostMapping("/movements/entrada")
  @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public MovementResponse entrada(
      @Valid @RequestBody MovementRequest body, @AuthenticationPrincipal Jwt jwt) {
    return movementService.entrada(body, jwt.getClaimAsString("username"));
  }

  @PostMapping("/movements/salida")
  @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public MovementResponse salida(
      @Valid @RequestBody MovementRequest body, @AuthenticationPrincipal Jwt jwt) {
    return movementService.salida(body, jwt.getClaimAsString("username"));
  }
}

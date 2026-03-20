package com.bodega.dispatch.client;

import java.util.UUID;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class InventoryMovementClient {

  private static final String SALIDA = "SALIDA";

  private final RestClient restClient;

  public InventoryMovementClient(@LoadBalanced RestClient.Builder loadBalancedRestClientBuilder) {
    this.restClient = loadBalancedRestClientBuilder.baseUrl("http://inventory-service").build();
  }

  /** Loads movement from inventory-service; ensures it is a SALIDA (outbound) record. */
  public InventoryMovementSnapshot requireSalidaMovement(UUID movementId) {
    InventoryMovementSnapshot body;
    try {
      body =
          restClient
              .get()
              .uri("/movements/{id}", movementId)
              .retrieve()
              .body(InventoryMovementSnapshot.class);
    } catch (RestClientResponseException e) {
      if (HttpStatus.NOT_FOUND.isSameCodeAs(e.getStatusCode())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movement not found in inventory");
      }
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Inventory service error", e);
    }
    if (body == null || body.id() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid response from inventory");
    }
    if (!SALIDA.equals(body.type())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Dispatch can only reference a SALIDA movement");
    }
    return body;
  }
}

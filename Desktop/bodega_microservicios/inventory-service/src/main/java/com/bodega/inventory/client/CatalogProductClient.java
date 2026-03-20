package com.bodega.inventory.client;

import java.util.UUID;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CatalogProductClient {

  private final RestClient restClient;

  public CatalogProductClient(@LoadBalanced RestClient.Builder loadBalancedRestClientBuilder) {
    this.restClient = loadBalancedRestClientBuilder.baseUrl("http://catalog-service").build();
  }

  /**
   * Synchronous validation against catalog-service: product must exist and be active (404 otherwise
   * from catalog).
   */
  public void assertProductExistsAndActive(UUID productId) {
    try {
      restClient.get().uri("/products/{id}", productId).retrieve().toBodilessEntity();
    } catch (RestClientResponseException e) {
      if (HttpStatus.NOT_FOUND.isSameCodeAs(e.getStatusCode())) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Product not found or inactive in catalog");
      }
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Catalog service error", e);
    }
  }
}

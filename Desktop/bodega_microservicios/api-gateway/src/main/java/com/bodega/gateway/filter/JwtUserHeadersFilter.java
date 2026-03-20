package com.bodega.gateway.filter;

import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Component
public class JwtUserHeadersFilter implements GlobalFilter, Ordered {

  @Override
  public int getOrder() {
    // Must run after Spring Security has authenticated the request.
    return Ordered.LOWEST_PRECEDENCE;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .cast(JwtAuthenticationToken.class)
        .flatMap(
            auth -> {
              Jwt jwt = auth.getToken();
              String userId = jwt.getSubject();
              List<String> roles = jwt.getClaimAsStringList("roles");
              String rolesHeader = roles != null ? String.join(",", roles) : "";

              ServerHttpRequest mutated =
                  exchange.getRequest()
                      .mutate()
                      .header("X-User-Id", userId)
                      .header("X-Roles", rolesHeader)
                      .build();
              return chain.filter(exchange.mutate().request(mutated).build());
            })
        .switchIfEmpty(chain.filter(exchange));
  }
}


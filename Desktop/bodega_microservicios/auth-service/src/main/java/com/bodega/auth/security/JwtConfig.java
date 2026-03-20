package com.bodega.auth.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtConfig {

  @Value("${security.jwt.secret:dev-bodega-jwt-secret-dev-bodega-jwt-secret}")
  private String secret;

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withSecretKey(secretKey()).build();
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(secretKey());
  }

  private SecretKey secretKey() {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    // HS256 requires a reasonably strong key; for dev we rely on a sufficiently long default.
    return new SecretKeySpec(keyBytes, "HmacSHA256");
  }
}


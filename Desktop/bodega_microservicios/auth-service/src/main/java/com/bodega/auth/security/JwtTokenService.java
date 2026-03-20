package com.bodega.auth.security;

import com.bodega.auth.domain.UserRole;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

  private final JwtEncoder jwtEncoder;
  private final Duration ttl;
  private final String issuer;

  public JwtTokenService(
      JwtEncoder jwtEncoder,
      @Value("${security.jwt.ttl-seconds:3600}") long ttlSeconds,
      @Value("${security.jwt.issuer:bodega}") String issuer) {
    this.jwtEncoder = jwtEncoder;
    this.ttl = Duration.ofSeconds(ttlSeconds);
    this.issuer = issuer;
  }

  public String createAccessToken(UUID userId, String username, UserRole role) {
    Instant now = Instant.now();
    Instant exp = now.plus(ttl);

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(exp)
            .subject(userId.toString())
            .claim("username", username)
            .claim("role", role.name())
            .claim("roles", List.of(role.name()))
            .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }
}


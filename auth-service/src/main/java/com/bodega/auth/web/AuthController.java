package com.bodega.auth.web;

import com.bodega.auth.domain.UserAccount;
import com.bodega.auth.domain.UserRole;
import com.bodega.auth.repo.UserAccountRepository;
import com.bodega.auth.security.JwtTokenService;
import com.bodega.auth.web.dto.LoginRequest;
import com.bodega.auth.web.dto.LoginResponse;
import com.bodega.auth.web.dto.MeResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenService jwtTokenService;

  public AuthController(
      UserAccountRepository userAccountRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenService jwtTokenService) {
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenService = jwtTokenService;
  }

  @PostMapping("/auth/login")
  @ResponseStatus(HttpStatus.OK)
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    UserAccount user =
        userAccountRepository
            .findByUsername(request.username())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

    boolean ok = passwordEncoder.matches(request.password(), user.getPasswordHash());
    if (!ok) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    String token = jwtTokenService.createAccessToken(user.getId(), user.getUsername(), user.getRole());
    return new LoginResponse(token, "Bearer");
  }

  @GetMapping("/users/me")
  public MeResponse me(@AuthenticationPrincipal Jwt jwt) {
    UUID id = UUID.fromString(jwt.getSubject());
    String username = jwt.getClaimAsString("username");
    String roleRaw = jwt.getClaimAsString("role");
    UserRole role = roleRaw != null ? UserRole.valueOf(roleRaw) : UserRole.OPERATOR;
    return new MeResponse(id, username, role);
  }
}


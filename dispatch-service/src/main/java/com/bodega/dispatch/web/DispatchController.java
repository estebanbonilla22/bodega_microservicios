package com.bodega.dispatch.web;

import com.bodega.dispatch.service.DispatchService;
import com.bodega.dispatch.web.dto.CreateDispatchRequest;
import com.bodega.dispatch.web.dto.DispatchResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DispatchController {

  private final DispatchService dispatchService;

  public DispatchController(DispatchService dispatchService) {
    this.dispatchService = dispatchService;
  }

  @GetMapping("/dispatches")
  public List<DispatchResponse> list() {
    return dispatchService.list();
  }

  @PostMapping("/dispatches")
  @ResponseStatus(HttpStatus.CREATED)
  public DispatchResponse create(@Valid @RequestBody CreateDispatchRequest body) {
    return dispatchService.create(body);
  }
}

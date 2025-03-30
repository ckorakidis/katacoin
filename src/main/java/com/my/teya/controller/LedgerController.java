package com.my.teya.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/ledger")
@Validated
public class LedgerController {


  @Operation(summary = "Get current balance")
  @ApiResponse(responseCode = "200", description = "Returns current balance")
  @GetMapping("/balance")
  public ResponseEntity<BigDecimal> balance() {
    return ResponseEntity.ok(BigDecimal.valueOf(10));
  }

}

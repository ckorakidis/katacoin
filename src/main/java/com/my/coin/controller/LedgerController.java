package com.my.coin.controller;

import com.my.coin.controller.dto.AmountRequest;
import com.my.coin.controller.dto.BalanceResponse;
import com.my.coin.controller.dto.ValidationError;
import com.my.coin.domain.Transaction;
import com.my.coin.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@Validated
public class LedgerController {

  private final LedgerService service;

  public LedgerController(LedgerService service) {
    this.service = service;
  }

  @Operation(summary = "Deposit money to the ledger")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Deposit successful"),
          @ApiResponse(
                  responseCode = "400",
                  description = "Validation or deserialization error (e.g. missing, null, or negative amount)",
                  content = @Content(
                          schema = @Schema(implementation = ValidationError.class),
                          examples = @ExampleObject(
                                  value = """
                                          {
                                            "error": "Validation failed",
                                            "fieldErrors": [
                                              {
                                                "field": "amount",
                                                "messages": ["Amount is required", "Amount must be >= 0.01"]
                                              }
                                            ]
                                          }
                                          """
                          ))
          )

  })
  @PostMapping("/deposit")
  public ResponseEntity<Void> deposit(@Valid @RequestBody AmountRequest request) {
    service.deposit(request.amount());
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Withdraw money from the ledger")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Withdraw successful"),
          @ApiResponse(
                  responseCode = "400",
                  description = "Validation or deserialization error (e.g. missing, null, or negative amount)",
                  content = @Content(
                          schema = @Schema(implementation = ValidationError.class),
                          examples = @ExampleObject(
                                  value = """
                                          {
                                            "error": "Validation failed",
                                            "fieldErrors": [
                                              {
                                                "field": "amount",
                                                "messages": ["Amount is required", "Amount must be >= 0.01"]
                                              }
                                            ]
                                          }
                                          """
                          ))
          )
  })
  @PostMapping("/withdraw")
  public ResponseEntity<Void> withdraw(@Valid @RequestBody AmountRequest request) {
    service.withdraw(request.amount());
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Get current balance")
  @ApiResponse(responseCode = "200", description = "Returns current balance")
  @GetMapping("/balance")
  public ResponseEntity<BalanceResponse> balance() {
    return ResponseEntity.ok(new BalanceResponse(service.getBalance()));
  }

  @Operation(summary = "Get transaction history")
  @ApiResponse(responseCode = "200", description = "Returns transaction list in reverse order")
  @GetMapping("/transactions")
  public ResponseEntity<List<Transaction>> transactions() {
    return ResponseEntity.ok(service.getTransactions());
  }

}

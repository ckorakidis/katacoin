package com.my.coin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.coin.controller.dto.AmountRequest;
import com.my.coin.domain.Transaction;
import com.my.coin.domain.TransactionType;
import com.my.coin.exception.InsufficientBalanceException;
import com.my.coin.service.LedgerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(LedgerControllerTest.TestConfig.class)
@WebMvcTest(controllers = LedgerController.class)
class LedgerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private LedgerService ledgerService;

  @TestConfiguration
  static class TestConfig {
    @Bean
    public LedgerService ledgerService() {
      return Mockito.mock(LedgerService.class);
    }
  }

  private <T> MockHttpServletRequestBuilder postFor(String path, T request) throws Exception {
    return post(String.format("/api/ledger%s", path))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));
  }

  @Test
  @DisplayName("POST /deposit returns 200 OK for valid request")
  void depositShouldSucceed() throws Exception {
    AmountRequest request = new AmountRequest(BigDecimal.valueOf(100));

    mockMvc.perform(postFor("/deposit", request))
            .andExpect(status().isOk());

    verify(ledgerService).deposit(BigDecimal.valueOf(100));
  }

  @Test
  @DisplayName("POST /deposit returns 400 for invalid (negative) amount")
  void depositShouldFailWithInvalidAmount() throws Exception {
    AmountRequest request = new AmountRequest(BigDecimal.valueOf(-50));

    mockMvc.perform(postFor("/deposit", request))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation failed"))
            .andExpect(jsonPath("$.fieldErrors[*].field").value(hasItem("amount")))
            .andExpect(jsonPath("$.fieldErrors[?(@.field == 'amount')].messages[0]").value("Amount must be >= 0.01"));

  }

  @DisplayName("POST /deposit returns 400 if 'amount' field is missing")
  @Test
  void depositShouldFailWhenFieldMissing() throws Exception {
    String invalidJson = "{}";

    mockMvc.perform(postFor("/deposit", invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation failed"))
            .andExpect(jsonPath("$.fieldErrors[*].field").value(hasItem("amount")))
            .andExpect(jsonPath("$.fieldErrors[?(@.field == 'amount')].messages[0]").value("Amount is required"));

  }

  @DisplayName("POST /withdraw returns 200 OK for valid request")
  @Test
  void withdrawShouldSucceed() throws Exception {
    AmountRequest request = new AmountRequest(BigDecimal.valueOf(50));

    mockMvc.perform(postFor("/withdraw", request))
            .andExpect(status().isOk());

    verify(ledgerService).withdraw(BigDecimal.valueOf(50));
  }

  @Test
  @DisplayName("POST /withdraw returns 400 when balance is insufficient")
  void withdrawFailsIfInsufficientBalance() throws Exception {
    AmountRequest request = new AmountRequest(BigDecimal.valueOf(200));

    doThrow(new InsufficientBalanceException("Insufficient balance"))
            .when(ledgerService).withdraw(BigDecimal.valueOf(200));

    mockMvc.perform(postFor("/withdraw", request))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Insufficient balance"));
  }

  @DisplayName("POST /withdraw returns 400 for zero amount")
  @Test
  void withdrawShouldFailForZeroAmount() throws Exception {
    AmountRequest request = new AmountRequest(BigDecimal.ZERO);

    mockMvc.perform(postFor("/withdraw", request))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors[*].field").value(hasItem("amount")))
            .andExpect(jsonPath("$.fieldErrors[?(@.field == 'amount')].messages[0]").value("Amount must be >= 0.01"));

  }

  @DisplayName("GET /balance returns current balance")
  @Test
  void getBalanceReturnsValue() throws Exception {
    Mockito.when(ledgerService.getBalance()).thenReturn(BigDecimal.valueOf(123.45));

    mockMvc.perform(get("/api/ledger/balance"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance").value(123.45));
  }

  @DisplayName("GET /transactions returns list of transactions")
  @Test
  void getTransactionsReturnsList() throws Exception {
    List<Transaction> txs = List.of(
            new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, BigDecimal.valueOf(50), LocalDateTime.now()),
            new Transaction(UUID.randomUUID(), TransactionType.WITHDRAWAL, BigDecimal.valueOf(20), LocalDateTime.now())
    );

    Mockito.when(ledgerService.getTransactions()).thenReturn(txs);

    mockMvc.perform(get("/api/ledger/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
            .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"));
  }

}

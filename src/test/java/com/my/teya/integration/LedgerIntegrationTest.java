package com.my.teya.integration;

import com.my.teya.controller.dto.AmountRequest;
import com.my.teya.controller.dto.BalanceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LedgerIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @LocalServerPort
  private int port;

  private String baseUrl(String path) {
    return "http://localhost:" + port + "/api/ledger" + path;
  }

  @SuppressWarnings("unchecked")
  private List<String> getFieldMessages(Map<String, Object> body, String fieldName) {
    List<Map<String, Object>> fieldErrors = (List<Map<String, Object>>) body.get("fieldErrors");
    return fieldErrors.stream()
            .filter(e -> fieldName.equals(e.get("field")))
            .findFirst()
            .map(e -> (List<String>) e.get("messages"))
            .orElse(List.of());
  }


  @Test
  @DisplayName("Deposit money and verify balance reflects it")
  void depositAndCheckBalance() {
    AmountRequest deposit = new AmountRequest(BigDecimal.valueOf(150));
    HttpEntity<AmountRequest> entity = new HttpEntity<>(deposit);

    ResponseEntity<Void> depositResponse = restTemplate.postForEntity(baseUrl("/deposit"), entity, Void.class);
    assertEquals(HttpStatus.OK, depositResponse.getStatusCode());

    BalanceResponse balance = restTemplate.getForObject(baseUrl("/balance"), BalanceResponse.class);
    assertNotNull(balance);
    assertEquals(new BigDecimal("150"), balance.balance());
  }

  @Test
  @DisplayName("Withdraw more than balance returns 400 with plain text error")
  void withdrawMoreThanBalance() {
    AmountRequest withdraw = new AmountRequest(BigDecimal.valueOf(999));
    HttpEntity<AmountRequest> entity = new HttpEntity<>(withdraw);

    ResponseEntity<String> response = restTemplate.postForEntity(baseUrl("/withdraw"), entity, String.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Insufficient balance to withdraw 999", response.getBody());
  }

  @Test
  @DisplayName("Deposit with negative amount returns structured validation error")
  void depositWithInvalidAmount() {
    AmountRequest invalid = new AmountRequest(BigDecimal.valueOf(-5));
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<AmountRequest> entity = new HttpEntity<>(invalid, headers);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            baseUrl("/deposit"), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
            });


    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());


    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    List<String> messages = getFieldMessages(body, "amount");
    assertTrue(messages.contains("Amount must be >= 0.01"));
  }

  @Test
  @DisplayName("Deposit with missing field (empty JSON) returns deserialization error")
  void depositWithMissingAmountField() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>("{}", headers);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            baseUrl("/deposit"), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
            });

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals("Validation failed", body.get("error"));
    List<String> messages = getFieldMessages(body, "amount");
    assertTrue(messages.contains("Amount is required"));
  }
}

package com.my.coin.controller.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmountRequestValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setupValidator() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator(); // factory (autoclosable) should ideally be closed to release any internal resources.
    }
  }

  @Test
  @DisplayName("Fails validation when amount is null")
  void failsWhenAmountIsNull() {
    AmountRequest request = new AmountRequest(null);
    Set<ConstraintViolation<AmountRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
  }

  @Test
  @DisplayName("Fails validation when amount is zero")
  void failsWhenAmountIsZero() {
    AmountRequest request = new AmountRequest(BigDecimal.ZERO);
    Set<ConstraintViolation<AmountRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Amount must be >= 0.01")));
  }

  @Test
  @DisplayName("Fails validation when amount is negative")
  void failsWhenAmountIsNegative() {
    AmountRequest request = new AmountRequest(BigDecimal.valueOf(-5));
    Set<ConstraintViolation<AmountRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Amount must be >= 0.01")));
  }

  @Test
  @DisplayName("Passes validation when amount is valid")
  void passesWhenAmountIsValid() {
    AmountRequest request = new AmountRequest(BigDecimal.valueOf(50));
    Set<ConstraintViolation<AmountRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
  }
}
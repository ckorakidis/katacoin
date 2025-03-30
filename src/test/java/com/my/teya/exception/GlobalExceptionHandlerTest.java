package com.my.teya.exception;

import com.my.teya.controller.dto.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  @DisplayName("Handles IllegalArgumentException with 400 and message")
  void handlesIllegalArgumentException() {
    IllegalArgumentException ex = new IllegalArgumentException("Invalid amount");

    ResponseEntity<String> response = handler.handleIllegalArgument(ex);

    assertEquals(BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid amount", response.getBody());
  }

  @Test
  @DisplayName("Handles InsufficientBalanceException with 400 and message")
  void handlesInsufficientBalanceException() {
    InsufficientBalanceException ex = new InsufficientBalanceException("Insufficient funds");

    ResponseEntity<String> response = handler.handleInsufficientBalance(ex);

    assertEquals(BAD_REQUEST, response.getStatusCode());
    assertEquals("Insufficient funds", response.getBody());
  }

  @Test
  @DisplayName("Handles MethodArgumentNotValidException and returns field errors")
  void handlesValidationErrors() throws NoSuchMethodException {
    // Setup a dummy MethodArgumentNotValidException
    BindingResult bindingResult = new BindException(new Object(), "amountRequest");
    bindingResult.addError(new FieldError("amountRequest", "amount", "-10", false, null, null, "Amount must be >= 0.01"));

    Method method = this.getClass().getDeclaredMethod("dummyMethod", Object.class);
    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

    ResponseEntity<ValidationError> response = handler.handleValidationException(ex);

    assertEquals(BAD_REQUEST, response.getStatusCode());

    ValidationError body = response.getBody();
    assertNotNull(body);
    assertEquals("Validation failed", body.error());
    assertEquals(1, body.fieldErrors().size());
    assertEquals("amount", body.fieldErrors().getFirst().field());
    assertTrue(body.fieldErrors().getFirst().messages().contains("Amount must be >= 0.01"));
  }

  // Dummy method needed to mock MethodArgumentNotValidException
  void dummyMethod(Object o) {
  }

  @SuppressWarnings("deprecation") //new HttpMessageNotReadableException(..)
  @Test
  @DisplayName("Handles HttpMessageNotReadableException and returns validation error response")
  void handlesDeserializationError() {
    HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Cannot deserialize");

    ResponseEntity<ValidationError> response = handler.handleDeserializationError(ex);

    assertEquals(BAD_REQUEST, response.getStatusCode());

    ValidationError body = response.getBody();
    assertNotNull(body);
    assertEquals("Validation failed", body.error());
    assertEquals(1, body.fieldErrors().size());
    assertEquals("amount", body.fieldErrors().getFirst().field());
    assertTrue(body.fieldErrors().getFirst().messages().contains("Amount is required"));
  }

}

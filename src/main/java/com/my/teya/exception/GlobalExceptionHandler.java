package com.my.teya.exception;

import com.my.teya.controller.dto.ValidationError;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static java.util.stream.Collectors.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InsufficientBalanceException.class)
  public ResponseEntity<String> handleInsufficientBalance(InsufficientBalanceException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationError> handleValidationException(MethodArgumentNotValidException ex) {
    var groupedErrors = ex.getBindingResult().getFieldErrors().stream()
            .collect(groupingBy(
                    FieldError::getField,
                    mapping(FieldError::getDefaultMessage, toList())
            ));

    var fieldErrors = groupedErrors.entrySet().stream()
            .map(entry -> new ValidationError.FieldValidationError(entry.getKey(), entry.getValue()))
            .toList();

    return ResponseEntity.badRequest().body(new ValidationError(fieldErrors));
  }


  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ValidationError> handleDeserializationError(HttpMessageNotReadableException ex) {
    var fieldErrors = List.of(
            new ValidationError.FieldValidationError("amount", List.of("Amount is required"))
    );
    return ResponseEntity.badRequest().body(new ValidationError(fieldErrors));
  }

}

package com.my.teya.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Validation error response")
public record ValidationError(@Schema(description = "Short error message") String error,
                              @Schema(description = "List of field-specific validation errors")
                              List<FieldValidationError> fieldErrors) {

  public ValidationError(List<FieldValidationError> fieldErrors) {
    this("Validation failed", fieldErrors);
  }

  public record FieldValidationError(
          @Schema(description = "Name of the field with the error") String field,
          @Schema(description = "Validation messages for the field") List<String> messages
  ) {
  }
}

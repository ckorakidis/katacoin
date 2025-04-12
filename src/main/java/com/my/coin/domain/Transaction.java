package com.my.coin.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        LocalDateTime timestamp
) {
  public static Transaction transactionFor(TransactionType type, BigDecimal amount) {
    return new Transaction(
            UUID.randomUUID(),
            type,
            amount,
            LocalDateTime.now());
  }
}

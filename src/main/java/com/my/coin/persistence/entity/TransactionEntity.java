package com.my.coin.persistence.entity;

import com.my.coin.domain.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

  @Id
  private UUID id;

  @Enumerated(EnumType.STRING)
  private TransactionType type;

  private BigDecimal amount;

  private LocalDateTime timestamp;

  public TransactionEntity() {
  }

  public TransactionEntity(TransactionType type, BigDecimal amount) {
    this.type = type;
    this.amount = amount;
    this.timestamp = LocalDateTime.now();
  }

  public TransactionEntity(UUID id, TransactionType type, BigDecimal amount, LocalDateTime timestamp) {
    this.id = id;
    this.type = type;
    this.amount = amount;
    this.timestamp = timestamp;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}

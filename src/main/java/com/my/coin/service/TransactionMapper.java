package com.my.coin.service;

import com.my.coin.domain.Transaction;
import com.my.coin.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

  public Transaction toDomain(TransactionEntity entity) {
    return new Transaction(
            entity.getId(),
            entity.getType(),
            entity.getAmount(),
            entity.getTimestamp()
    );
  }

  public TransactionEntity toEntity(Transaction tx) {
    return new TransactionEntity(
            tx.id(),
            tx.type(),
            tx.amount(),
            tx.timestamp()
    );
  }
}

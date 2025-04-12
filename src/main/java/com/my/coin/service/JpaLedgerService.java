package com.my.coin.service;

import com.my.coin.domain.Transaction;
import com.my.coin.domain.TransactionType;
import com.my.coin.exception.InsufficientBalanceException;
import com.my.coin.persistence.entity.TransactionEntity;
import com.my.coin.persistence.repository.TransactionRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.my.coin.domain.TransactionType.DEPOSIT;

@Primary
@Service("jpaLedger")
public class JpaLedgerService implements LedgerService {

  private final TransactionRepository repository;

  private final TransactionMapper mapper;

  public JpaLedgerService(TransactionRepository repository, TransactionMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public void deposit(BigDecimal amount) {
    repository.save(mapper.toEntity(Transaction.transactionFor(TransactionType.DEPOSIT, amount)));
  }

  @Override
  public void deposit(Transaction transaction) {
    repository.save(mapper.toEntity(transaction));
  }

  @Override
  public void withdraw(BigDecimal amount) {
    BigDecimal balance = getBalance(Optional.empty());
    if (balance.compareTo(amount) < 0) {
      throw new InsufficientBalanceException("Insufficient balance to withdraw " + amount);
    }
    repository.save(new TransactionEntity(TransactionType.WITHDRAWAL, amount));
  }

  @Override
  public BigDecimal getBalance(Optional<LocalDateTime> dateOption) {

    return repository.findAll().stream()
            .filter(transaction -> dateOption
                    .map(date -> !transaction.getTimestamp().isBefore(date))
                    .orElse(true))
            .map(transaction ->
                    transaction.getType() == DEPOSIT ? transaction.getAmount() : transaction.getAmount().negate())
            .reduce(new BigDecimal(0), (amount, acc) -> acc.add(amount));
  }

  @Override
  public List<Transaction> getTransactions() {
    return repository.findAll().stream()
            .sorted(Comparator.comparing(TransactionEntity::getTimestamp).reversed())
            .map(tx -> new Transaction(
                    tx.getId(),
                    tx.getType(),
                    tx.getAmount(),
                    tx.getTimestamp()
            ))
            .toList();
  }
}

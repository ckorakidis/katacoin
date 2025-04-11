package com.my.coin.service;

import com.my.coin.domain.Transaction;
import com.my.coin.exception.InsufficientBalanceException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.my.coin.domain.Transaction.transactionFor;
import static com.my.coin.domain.TransactionType.DEPOSIT;
import static com.my.coin.domain.TransactionType.WITHDRAWAL;

@Service
public class DefaultLedgerService implements LedgerService {

  private final List<Transaction> transactions = new ArrayList<>();
  private BigDecimal balance = BigDecimal.ZERO;


  @Override
  public void deposit(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    transactions.add(transactionFor(DEPOSIT, amount));

    balance = balance.add(amount);
  }

  @Override
  public void deposit(Transaction transaction) {

    transactions.add(transaction);

    balance = balance.add(transaction.amount());
  }


  @Override
  public void withdraw(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (balance.compareTo(amount) < 0) {
      throw new InsufficientBalanceException("Insufficient balance to withdraw " + amount);
    }

    transactions.add(transactionFor(WITHDRAWAL, amount));

    balance = balance.subtract(amount);
  }

  @Override
  public BigDecimal getBalance(Optional<LocalDateTime> dateOption) {

    return transactions.stream()
            .filter(transaction -> dateOption
                    .map(date -> !transaction.timestamp().isBefore(date))
                    .orElse(true))
            .map(transaction ->
                    transaction.type() == DEPOSIT ? transaction.amount() : transaction.amount().negate())
            .reduce(new BigDecimal(0), (amount, acc) -> acc.add(amount));
  }

  @Override
  public List<Transaction> getTransactions() {
    return List.copyOf(transactions).reversed();
  }
}

package com.my.coin.service;

import com.my.coin.domain.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LedgerService {

  void deposit(BigDecimal amount);

  void deposit(Transaction transaction);

  void withdraw(BigDecimal amount);

  BigDecimal getBalance(Optional<LocalDateTime> dateOption);

  List<Transaction> getTransactions();
}

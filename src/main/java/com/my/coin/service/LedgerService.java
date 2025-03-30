package com.my.coin.service;

import com.my.coin.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface LedgerService {

  void deposit(BigDecimal amount);

  void withdraw(BigDecimal amount);

  BigDecimal getBalance();

  List<Transaction> getTransactions();
}

package com.my.teya.service;

import com.my.teya.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface LedgerService {

  void deposit(BigDecimal amount);

  void withdraw(BigDecimal amount);

  BigDecimal getBalance();

  List<Transaction> getTransactions();
}

package com.my.teya.service;

import com.my.teya.domain.TransactionType;
import com.my.teya.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultLedgerServiceTest {

  private LedgerService service;

  @BeforeEach
  void setUp() {
    service = new DefaultLedgerService();
  }

  @DisplayName("When deposit an amount, balance will be increased by that amount")
  @Test
  void depositIncreasesBalance() {
    service.deposit(BigDecimal.valueOf(100));
    assertEquals(BigDecimal.valueOf(100), service.getBalance());
  }

  @DisplayName("When deposit a negative amount, IllegalArgumentException is thrown")
  @Test
  void depositNegativeAmountThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> service.deposit(BigDecimal.valueOf(-100)));
  }

  @DisplayName("When withdraw an amount, balance will be decreased by that amount")
  @Test
  void withdrawDecreasesBalance() {
    service.deposit(BigDecimal.valueOf(100));
    service.withdraw(BigDecimal.valueOf(40));
    assertEquals(BigDecimal.valueOf(60), service.getBalance());
  }

  @DisplayName("When withdraw a negative amount, IllegalArgumentException is thrown")
  @Test
  void withdrawNegativeAmountThrowsException() {
    service.deposit(BigDecimal.valueOf(150));
    assertThrows(IllegalArgumentException.class, () -> service.withdraw(BigDecimal.valueOf(-100)));
  }

  @DisplayName("When withdraw an amount bigger than balance, InsufficientBalanceException is thrown")
  @Test
  void withdrawMoreThanBalanceThrowsException() {
    service.deposit(BigDecimal.valueOf(100));
    assertThrows(InsufficientBalanceException.class, () -> service.withdraw(BigDecimal.valueOf(150)));
  }

  @DisplayName("Transactions can be viewed in reversed order (most recent first)")
  @Test
  void recordsAndProvidesOrderedTransactions() {
    service.deposit(BigDecimal.valueOf(1));
    service.deposit(BigDecimal.valueOf(2));
    service.deposit(BigDecimal.valueOf(3));
    service.withdraw(BigDecimal.valueOf(4));

    List<Map.Entry<TransactionType, BigDecimal>> expected = List.of(
            Map.entry(TransactionType.WITHDRAWAL, BigDecimal.valueOf(4)),
            Map.entry(TransactionType.DEPOSIT, BigDecimal.valueOf(3)),
            Map.entry(TransactionType.DEPOSIT, BigDecimal.valueOf(2)),
            Map.entry(TransactionType.DEPOSIT, BigDecimal.valueOf(1))
    );

    List<Map.Entry<TransactionType, BigDecimal>> actual = service.getTransactions().stream()
            .map(t -> Map.entry(t.type(), t.amount()))
            .toList();

    assertEquals(expected, actual);
  }

}
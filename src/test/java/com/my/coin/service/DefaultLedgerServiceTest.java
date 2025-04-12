package com.my.coin.service;

import com.my.coin.domain.Transaction;
import com.my.coin.domain.TransactionType;
import com.my.coin.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultLedgerServiceTest {

  private final String DATE_FORMAT = "yyyy-MM-dd";

  private LedgerService service;

  @BeforeEach
  void setUp() {
    service = new DefaultLedgerService();
  }

  @DisplayName("When deposit an amount, balance will be increased by that amount")
  @Test
  void depositIncreasesBalance() {
    service.deposit(BigDecimal.valueOf(100));
    assertEquals(BigDecimal.valueOf(100), service.getBalance(Optional.empty()));
  }

  @DisplayName("When deposit requesting the balance after a date, returns sum after that date")
  @Test
  void getBalanceFilteredByDate() {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    service.deposit(new Transaction(
            UUID.randomUUID(),
            TransactionType.DEPOSIT,
            new BigDecimal("30.0"),
            LocalDate.parse("2020-01-10", formatter).atStartOfDay()));
    service.deposit(new Transaction(
            UUID.randomUUID(),
            TransactionType.DEPOSIT,
            new BigDecimal("100.0"),
            LocalDate.parse("2020-01-30", formatter).atStartOfDay()));
    service.deposit(new Transaction(
            UUID.randomUUID(),
            TransactionType.WITHDRAWAL,
            new BigDecimal("10.0"),
            LocalDate.parse("2020-01-31", formatter).atStartOfDay()));
    service.deposit(new Transaction(
            UUID.randomUUID(),
            TransactionType.DEPOSIT,
            new BigDecimal("150.0"),
            LocalDate.parse("2020-01-31", formatter).atStartOfDay()));
    assertEquals(BigDecimal.valueOf(240.0),
            service.getBalance(Optional.of(LocalDate.parse("2020-01-20", formatter).atStartOfDay())));
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
    assertEquals(BigDecimal.valueOf(60), service.getBalance(Optional.empty()));
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
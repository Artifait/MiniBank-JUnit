package com.top.bank;

/**
 * Класс банковского счета (упрощённо).
 * Не хранит транзакции, только баланс и взаимодействует с AuditService.
 */
public class BankAccount {
    private final String accountId;
    private double balance;
    private final AuditService auditService;

    public BankAccount(String accountId, double initialBalance, AuditService auditService) {
        if (accountId == null || accountId.isBlank()) throw new IllegalArgumentException("accountId required");
        if (auditService == null) throw new IllegalArgumentException("auditService required");
        this.accountId = accountId;
        this.balance = initialBalance;
        this.auditService = auditService;
    }

    /**
     * Пополнение счета.
     *
     * @param amount положительная сумма
     */
    public synchronized void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be > 0");
        }
        balance += amount;
        auditService.recordOperation(accountId, "DEPOSIT", amount);
    }

    /**
     * Снятие со счета.
     *
     * @param amount положительная сумма, не превышающая баланс
     */
    public synchronized void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be > 0");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance -= amount;
        auditService.recordOperation(accountId, "WITHDRAW", amount);
    }

    public synchronized double getBalance() {
        return balance;
    }

    public String getAccountId() {
        return accountId;
    }
}

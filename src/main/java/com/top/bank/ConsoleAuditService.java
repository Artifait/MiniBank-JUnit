package com.top.bank;

public class ConsoleAuditService implements AuditService {
    @Override
    public void recordOperation(String accountId, String operationType, double amount) {
        System.out.printf("AUDIT: account=%s, op=%s, amount=%.2f%n", accountId, operationType, amount);
    }
}

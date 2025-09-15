package com.top.bank;

/**
 * Интерфейс для аудита операций.
 */
public interface AuditService {
    /**
     * Регистрирует операцию для указанного счета.
     *
     * @param accountId     идентификатор счета
     * @param operationType тип операции, например "DEPOSIT" или "WITHDRAW"
     * @param amount        сумма операции (положительная)
     */
    void recordOperation(String accountId, String operationType, double amount);
}

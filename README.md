# Bank Account — мини-проект (JUnit5 + Mockito)

Коротко: простой банковский счёт с аудитом. Демонстрирует бизнес-логику (депозит/снятие), внедрение зависимости `AuditService` и тестирование через JUnit5 + Mockito.

## Что внутри

- `com.top.bank.BankAccount` — баланс, `deposit`, `withdraw`, `getBalance`.
- `com.top.bank.AuditService` — интерфейс для аудита.
- `com.top.bank.ConsoleAuditService` — простая заглушка.
- Тесты: `src/test/java/com/top/bank/BankAccountTest.java` — JUnit5 (включая параметризованные тесты), Mockito для моков и `verify`.

## Ключевые сценарии, покрытые тестами

- Успешный `deposit` / `withdraw` (проверка баланса + вызова аудита).
- Ошибки при неположительных суммах (`IllegalArgumentException`).
- Попытка снять больше баланса (`IllegalStateException`) — баланс не меняется и аудит не вызывается.
- Последовательные операции и параметризованные тесты.

## Требования

- JDK 21
- Maven

## Сборка и тесты

Запустить тесты:

```bash
mvn test
```
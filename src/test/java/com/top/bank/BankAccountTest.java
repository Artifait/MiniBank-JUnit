package com.top.bank;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountTest {

    private static final double EPS = 1e-9;

    @Nested
    @DisplayName("Deposit tests")
    class DepositTests {
        static Stream<org.junit.jupiter.params.provider.Arguments> depositProvider() {
            return Stream.of(
                    org.junit.jupiter.params.provider.Arguments.of(0.0, 50.0, 50.0),
                    org.junit.jupiter.params.provider.Arguments.of(100.0, 25.5, 125.5),
                    org.junit.jupiter.params.provider.Arguments.of(10.0, 0.01, 10.01)
            );
        }

        @ParameterizedTest
        @MethodSource("depositProvider")
        void deposit_success_changesBalanceAndRecordsAudit(double initial, double depositAmount, double expected) {
            AuditService audit = Mockito.mock(AuditService.class);
            BankAccount acct = new BankAccount("acct-1", initial, audit);

            acct.deposit(depositAmount);

            assertEquals(expected, acct.getBalance(), EPS);
            verify(audit, times(1)).recordOperation("acct-1", "DEPOSIT", depositAmount);
        }

        @Test
        void deposit_nonPositive_throwsAndNoAudit() {
            AuditService audit = Mockito.mock(AuditService.class);
            BankAccount acct = new BankAccount("acct-2", 10.0, audit);

            assertThrows(IllegalArgumentException.class, () -> acct.deposit(0.0));
            assertThrows(IllegalArgumentException.class, () -> acct.deposit(-5.0));

            verify(audit, never()).recordOperation(anyString(), anyString(), anyDouble());

            assertEquals(10.0, acct.getBalance(), EPS);
        }
    }

    @Nested
    @DisplayName("Withdraw tests")
    class WithdrawTests {
        static Stream<org.junit.jupiter.params.provider.Arguments> withdrawProvider() {
            return Stream.of(
                    org.junit.jupiter.params.provider.Arguments.of(100.0, 50.0, 50.0),
                    org.junit.jupiter.params.provider.Arguments.of(50.0, 50.0, 0.0),
                    org.junit.jupiter.params.provider.Arguments.of(10.0, 0.5, 9.5)
            );
        }

        @ParameterizedTest
        @MethodSource("withdrawProvider")
        void withdraw_success_changesBalanceAndRecordsAudit(double initial, double withdrawAmount, double expected) {
            AuditService audit = Mockito.mock(AuditService.class);
            BankAccount acct = new BankAccount("acct-3", initial, audit);

            acct.withdraw(withdrawAmount);

            assertEquals(expected, acct.getBalance(), EPS);
            verify(audit, times(1)).recordOperation("acct-3", "WITHDRAW", withdrawAmount);
        }

        @Test
        void withdraw_nonPositive_throwsAndNoAudit() {
            AuditService audit = Mockito.mock(AuditService.class);
            BankAccount acct = new BankAccount("acct-4", 20.0, audit);

            assertThrows(IllegalArgumentException.class, () -> acct.withdraw(0.0));
            assertThrows(IllegalArgumentException.class, () -> acct.withdraw(-1.0));

            verify(audit, never()).recordOperation(anyString(), anyString(), anyDouble());
            assertEquals(20.0, acct.getBalance(), EPS);
        }

        @Test
        void withdraw_moreThanBalance_throwsAndBalanceUnchangedAndNoAudit() {
            AuditService audit = Mockito.mock(AuditService.class);
            BankAccount acct = new BankAccount("acct-5", 30.0, audit);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> acct.withdraw(50.0));
            assertTrue(ex.getMessage().toLowerCase().contains("insufficient"));
            assertEquals(30.0, acct.getBalance(), EPS);
            verify(audit, never()).recordOperation(anyString(), anyString(), anyDouble());
        }

        @Test
        void multipleSequentialWithdrawals_accumulateCorrectly() {
            AuditService audit = Mockito.mock(AuditService.class);
            BankAccount acct = new BankAccount("acct-6", 100.0, audit);

            acct.withdraw(30.0); // 70
            acct.withdraw(20.0); // 50
            acct.withdraw(50.0); // 0

            assertEquals(0.0, acct.getBalance(), EPS);
            verify(audit, times(3)).recordOperation(eq("acct-6"), eq("WITHDRAW"), anyDouble());
            verify(audit).recordOperation("acct-6", "WITHDRAW", 30.0);
            verify(audit).recordOperation("acct-6", "WITHDRAW", 20.0);
            verify(audit).recordOperation("acct-6", "WITHDRAW", 50.0);
        }
    }
}

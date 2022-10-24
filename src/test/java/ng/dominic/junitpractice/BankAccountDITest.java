package ng.dominic.junitpractice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(BankAccountParameterResolver.class)
class BankAccountDITest {

    @Test
    @DisplayName("Deposit 500 successfully")
    void testDeposit(BankAccount bankAccount) {
        bankAccount.deposit(500);
        assertEquals(500, bankAccount.getBalance());
    }

    @Test
    @WithBalance("250")
    @DisplayName("Initialized bankaccount with balance 250 successfully with annotation")
    void testDepositInjected(BankAccount bankAccount) {
        assertEquals(250, bankAccount.getBalance());
    }

    @Nested
    class whenBalanceEqualsZero {

        @Test
        @WithBalance("0")
        void testWithdrawMinimumBalanceZero(BankAccount bankAccount) {
            assertThrows(RuntimeException.class, () -> bankAccount.withdraw(500));
        }

        @Test
        void testWithdrawMinimumBalanceNegativeThousand(BankAccount bankAccount) {
            bankAccount.setMinimumBalance(-1000);
            bankAccount.withdraw(500);
            assertEquals(-500, bankAccount.getBalance());
        }
    }
}

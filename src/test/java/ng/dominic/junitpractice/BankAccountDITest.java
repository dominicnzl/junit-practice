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
    @DisplayName("Deposit 500 successully")
    void testDeposit(BankAccount bankAccount) {
        bankAccount.deposit(500);
        assertEquals(500, bankAccount.getBalance());
    }

    @Nested
    class whenBalanceEqualsZero {

        @Test
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

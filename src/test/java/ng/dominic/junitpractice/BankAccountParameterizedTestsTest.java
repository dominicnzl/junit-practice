package ng.dominic.junitpractice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(BankAccountParameterResolver.class)
class BankAccountParameterizedTestsTest {

    @ParameterizedTest
    @ValueSource(ints = {100, 400, 800, 1000})
    @DisplayName("Depositing succesfully")
    void testDeposit(int amount, BankAccount bankAccount) {
        bankAccount.deposit(amount);
        assertEquals(amount, bankAccount.getBalance());
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"TUESDAY", "THURSDAY"})
    void testDayOfWeek(DayOfWeek day) {
        assertTrue(day.toString().startsWith("T"));
    }

    @ParameterizedTest
    @CsvSource({"100, Mary", "200, Henk", "150, Tom"})
    void depositAndNameTest(double amount, String name, BankAccount bankAccount) {
        bankAccount.deposit(amount);
        bankAccount.setHolderName(name);
        assertEquals(amount, bankAccount.getBalance());
        assertEquals(name, bankAccount.getHolderName());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/details.csv", numLinesToSkip = 1)
    void depositAndNameTest_withCsvFile(double amount, String name, BankAccount bankAccount) {
        bankAccount.deposit(amount);
        bankAccount.setHolderName(name);
        assertEquals(amount, bankAccount.getBalance());
        assertEquals(name, bankAccount.getHolderName());
    }

    @ParameterizedTest
    @MethodSource("hello")
    void depositAndNameTest_withMethodSource(double amount, String name, BankAccount bankAccount) {
        bankAccount.deposit(amount);
        bankAccount.setHolderName(name);
        assertAll(
                () -> assertEquals(0, bankAccount.getMinimumBalance()),
                () -> assertEquals(amount, bankAccount.getBalance()),
                () -> assertEquals(name, bankAccount.getHolderName())
        );
    }

    // this will be found by the MethodSource by matching the method name to its argument
    static Stream<Arguments> hello() {
        return Stream.of(
                Arguments.of(20, "Henk"),
                Arguments.of(30.4, "Miep"),
                Arguments.of(33, "Joop")
        );
    }

    // my colleague Hans Zuidervaart mentioned this:
    @TestFactory
    Stream<DynamicTest> testIsEven() {
        return IntStream.iterate(0, i -> i + 2)
                .limit(1000)
                .mapToObj(this::testNrIsEven);
    }

    private DynamicTest testNrIsEven(int i) {
        return DynamicTest.dynamicTest(i + " should be even", () -> assertEquals(0, i % 2));
    }
}

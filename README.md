# Java: Testing with JUnit
### a [course](https://www.linkedin.com/learning/java-testing-with-junit-14267963) by Maaike van Putten

After seeing this course on my Linkedin many many times I caved in and tried it too. Things that I learned:

---
#### Nested tests are a way to group scenarios within a broader test class
To do this create an inner class and annotate it with `@Nested`

---
#### ParameterResolver interface can be extended to provide dependency injection:
```java
    public class BankAccountParameterResolver implements ParameterResolver
``` 
Override `supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)` to call the hook
  like so: 
```java
    return parameterContext.getParameter().getType() == BankAccount.class;
```
Override `resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)` to return a default
object like so:
```java
    return new BankAccount(0, 0);
```
Then use `@ExtendWith(BankAccountParameterResolver.class)` on the test class and call the injected object via the test
method parameter, like below:
```java
    @Test
    @DisplayName("Deposit 500 successully")
    void testDeposit(BankAccount bankAccount) {
        bankAccount.deposit(500);
        assertEquals(500, bankAccount.getBalance());
    }
```
Note that if there are multiple parameters, the injected instance must be the last argument:
```java
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
```
---
#### Custom annotations
Something fun: it's possible to pass values in the ParameterResolver with a custom annotation!
```java
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface WithBalance {
        String value();
    }
```
When this holds a value we can set our return object like so:
```java
    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        final double balance = Optional.of(parameterContext.getDeclaringExecutable())
                .map(method -> method.getAnnotation(WithBalance.class))
                .map(WithBalance::value)
                .map(Double::parseDouble)
                .orElse(0.0);
        return new BankAccount(balance, 0);
    }
```
and this will pass!
```java
    @Test
    @WithBalance("250")
    @DisplayName("Initialized bankaccount with balance 250 successfully with annotation")
    void testDepositInjected(BankAccount bankAccount) {
        assertEquals(250, bankAccount.getBalance());
    }
```
---
#### ParameterizedTest and ValueSources
There is variant of `@ValueSource` called `@EnumSource`. Specify it like this and filter it by the `String[]` if 
desired. In this case only the mentioned `DayOfWeek` enums will be passed to the parameter.
```java
    @EnumSource(value = DayOfWeek.class, names = {"TUESDAY", "THURSDAY"})
```
The `@CsvFileSource` has a `numLinesToSkip` property that can be used to prevent parsing columnheaders 

---
#### Parallel tests
To enable parallel tests, set these properties in `src/test/resources/junit-platform.properties`:
```properties
    junit.jupiter.execution.parallel.enabled=true
    junit.jupiter.execution.parallel.strategy=dynamic
```
Then annotate the testclass with `@Execution(ExecutionMode.CONCURRENT)`

---
#### Conditional test execution
It's possible to execute tests depending on OS, JRE version and/or System properties, e.g.:
```java
    @DisabledIfSystemProperty(named = "os.version", matches = "x")
    @EnabledIfEnvironmentVariable(named = "USERNAME", matches = "henk")
```

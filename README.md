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

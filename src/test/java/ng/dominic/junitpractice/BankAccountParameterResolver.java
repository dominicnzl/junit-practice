package ng.dominic.junitpractice;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Optional;

public class BankAccountParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == BankAccount.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        final double balance = Optional.of(parameterContext.getDeclaringExecutable())
                .map(method -> method.getAnnotation(WithBalance.class))
                .map(WithBalance::value)
                .map(Double::parseDouble)
                .orElse(0.0);
        return new BankAccount(balance, 0);
    }
}

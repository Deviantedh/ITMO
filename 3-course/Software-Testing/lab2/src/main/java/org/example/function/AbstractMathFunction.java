package org.example.function;

import java.math.BigDecimal;

public abstract class AbstractMathFunction implements MathFunction {

    /** Проверяет входные данные и делегирует вычисление конкретной реализации. */
    @Override
    public final BigDecimal doCalculate(BigDecimal x, BigDecimal eps) {

        if (x == null) {
            throw new NullPointerException("x is null");
        }

        if (eps == null) {
            throw new NullPointerException("eps is null");
        }

        if (eps.compareTo(BigDecimal.ZERO) <= 0 ||
                eps.compareTo(BigDecimal.ONE) > 0) {
            throw new ArithmeticException("eps must be in (0, 1]");
        }

        return calculate(x, eps);
    }

    /** Вычисляет значение функции для аргумента x с точностью eps. */
    public abstract BigDecimal calculate(BigDecimal x, BigDecimal eps);
}

package org.example.function;

import java.math.BigDecimal;

public interface MathFunction {
    /** Возвращает значение функции для аргумента x с точностью eps. */
    BigDecimal doCalculate(BigDecimal x, BigDecimal eps);
}

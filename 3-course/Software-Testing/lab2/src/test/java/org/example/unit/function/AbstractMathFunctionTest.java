package org.example.unit.function;

import org.junit.jupiter.api.Test;
import org.example.function.AbstractMathFunction;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AbstractMathFunctionTest {

    private static final BigDecimal VALID_EPS = new BigDecimal("0.5");

    private final AbstractMathFunction function = new AbstractMathFunction() {
        @Override
        public BigDecimal calculate(BigDecimal x, BigDecimal eps) {
            return BigDecimal.ONE;
        }
    };

    // Проверяет валидацию входных данных: null-аргумент x недопустим.
    @Test
    void nullX() {
        assertThrows(NullPointerException.class,
                () -> function.doCalculate(null, VALID_EPS));
    }

    // Проверяет валидацию входных данных: null-точность eps недопустима.
    @Test
    void nullEps() {
        assertThrows(NullPointerException.class,
                () -> function.doCalculate(BigDecimal.ONE, null));
    }

    // Проверяет ограничение на точность: eps должен быть больше нуля.
    @Test
    void epsLessOrEqualZero() {
        assertThrows(ArithmeticException.class,
                () -> function.doCalculate(BigDecimal.ONE, BigDecimal.ZERO));

        assertThrows(ArithmeticException.class,
                () -> function.doCalculate(BigDecimal.ONE, new BigDecimal("-1")));
    }

    // Проверяет ограничение на точность: eps не может быть больше единицы.
    @Test
    void epsGreaterThanOne() {
        assertThrows(ArithmeticException.class,
                () -> function.doCalculate(BigDecimal.ONE, new BigDecimal("2")));
    }

    // Проверяет позитивный сценарий с корректными входными данными.
    @Test
    void validInput() {
        assertDoesNotThrow(() ->
                function.doCalculate(BigDecimal.ONE, VALID_EPS));
    }
}

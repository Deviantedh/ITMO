package org.example.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.EquationSystem;
import org.example.log.*;
import org.example.trig.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EquationSystemTest {

    private static final BigDecimal EPS = new BigDecimal("0.000001");

    private EquationSystem system;

    @BeforeEach
    void init() {
        Sin sin = new Sin();
        Cos cos = new Cos(sin);
        Tan tan = new Tan(sin, cos);

        Ln ln = new Ln();
        LogNBase log2 = new LogNBase(ln, 2);
        LogNBase log3 = new LogNBase(ln, 3);
        LogNBase log5 = new LogNBase(ln, 5);
        LogNBase log10 = new LogNBase(ln, 10);

        system = new EquationSystem(
                sin, cos, tan,
                ln, log2, log3, log5, log10
        );
    }

    // Проверяет граничный случай: ноль обрабатывается левой веткой без исключения.
    @Test
    void zeroValue() {
        assertDoesNotThrow(() -> system.calculate(BigDecimal.ZERO, EPS));
    }

    // Проверяет типовые отрицательные значения для тригонометрической ветки.
    @Test
    void negativeValues() {
        assertAll(
                () -> assertDoesNotThrow(() -> system.calculate(new BigDecimal("-0.5"), EPS)),
                () -> assertDoesNotThrow(() -> system.calculate(new BigDecimal("-1"), EPS)),
                () -> assertDoesNotThrow(() -> system.calculate(new BigDecimal("-2"), EPS))
        );
    }

    // Проверяет точки разрыва тангенса в левой ветке.
    @Test
    void tanDiscontinuity() {
        BigDecimal x = BigDecimal.valueOf(-Math.PI / 2);
        assertThrows(ArithmeticException.class,
                () -> system.calculate(x, EPS));

        BigDecimal x2 = BigDecimal.valueOf(-3 * Math.PI / 2);
        assertThrows(ArithmeticException.class,
                () -> system.calculate(x2, EPS));
    }

    // Проверяет специальные отрицательные углы без выхода в исключение.
    @Test
    void specialNegativeValues() {
        assertAll(
                () -> assertDoesNotThrow(() -> system.calculate(BigDecimal.valueOf(-Math.PI / 4), EPS)),
                () -> assertDoesNotThrow(() -> system.calculate(BigDecimal.valueOf(-Math.PI / 6), EPS))
        );
    }

    // Проверяет деление на ноль в правой ветке при x = 1.
    @Test
    void xEqualsOne() {
        assertThrows(ArithmeticException.class,
                () -> system.calculate(BigDecimal.ONE, EPS));
    }

    // Проверяет типовые положительные значения для логарифмической ветки.
    @Test
    void validPositiveValues() {
        assertAll(
                () -> assertDoesNotThrow(() -> system.calculate(new BigDecimal("2"), EPS)),
                () -> assertDoesNotThrow(() -> system.calculate(new BigDecimal("5"), EPS)),
                () -> assertDoesNotThrow(() -> system.calculate(new BigDecimal("10"), EPS))
        );
    }

    // Проверяет корректную работу для дробных положительных аргументов.
    @Test
    void fractionalPositiveValues() {
        assertAll(
                () -> assertDoesNotThrow(() -> system.calculate(new BigDecimal("0.5"), EPS)),
                () -> assertDoesNotThrow(() -> system.calculate(new BigDecimal("0.2"), EPS))
        );
    }

    // Проверяет деление на ноль в знаменателе при log_3(x) = 0.
    @Test
    void denominatorShouldThrowExceptionAtOne() {
        assertThrows(ArithmeticException.class,
                () -> system.calculate(BigDecimal.ONE, EPS));
    }

    // Проверяет отказ на большом аргументе из-за несходимости логарифмов.
    @Test
    void largePositiveShouldThrowException() {
        assertThrows(ArithmeticException.class,
                () -> system.calculate(new BigDecimal("1000"), EPS));
    }
}

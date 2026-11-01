package org.example.unit.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.log.Ln;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.*;

class LnTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
    private static final double DELTA = 0.000001;
    private static final MathContext MC = new MathContext(25, RoundingMode.HALF_EVEN);
    private Ln ln;

    @BeforeEach
    void init() {
        ln = new Ln();
    }

    // Проверяет валидацию области определения: ln(0) не определен.
    @Test
    void shouldNotCalculateForZero() {
        assertThrows(ArithmeticException.class, () -> ln.calculate(ZERO, PRECISION));
    }

    // Проверяет валидацию области определения: отрицательный аргумент недопустим.
    @Test
    void shouldNotCalculateForNegative() {
        assertThrows(ArithmeticException.class, () -> ln.calculate(new BigDecimal("-1"), PRECISION));
    }

    // Проверяет валидацию области определения для другого отрицательного значения.
    @Test
    void shouldNotCalculateForNegativeFive() {
        assertThrows(ArithmeticException.class, () -> ln.calculate(new BigDecimal("-5"), PRECISION));
    }

    // Проверяет базовое значение: ln(1) равен нулю.
    @Test
    void shouldCalculateForOne() {
        BigDecimal expected = ZERO.setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = ln.calculate(ONE, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет специальное значение: ln(e) близок к единице.
    @Test
    void shouldCalculateForE() {
        BigDecimal e = new BigDecimal("2.718281828459045");
        BigDecimal expected = ONE.setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = ln.calculate(e, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет вычисление для аргумента больше единицы.
    @Test
    void shouldCalculateForTen() {
        BigDecimal arg = BigDecimal.TEN;
        BigDecimal expected = new BigDecimal("2.3025851").setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = ln.calculate(arg, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет свойство ln(1/e) = -1.
    @Test
    void shouldCalculateForOneOverE() {
        BigDecimal e = new BigDecimal("2.718281828459045");
        BigDecimal arg = ONE.divide(e, MC);
        BigDecimal expected = ONE.negate().setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = ln.calculate(arg, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет вычисление для аргумента из интервала (0, 1).
    @Test
    void shouldCalculateForHalf() {
        BigDecimal arg = new BigDecimal("0.5");
        BigDecimal expected = new BigDecimal("-0.6931472").setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = ln.calculate(arg, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет вычисление для малого положительного аргумента.
    @Test
    void shouldCalculateForOneTenth() {
        BigDecimal arg = new BigDecimal("0.1");
        BigDecimal expected = new BigDecimal("-2.3025851").setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = ln.calculate(arg, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет типовой положительный сценарий для x = 2.
    @Test
    void shouldCalculateForTwo() {
        BigDecimal arg = new BigDecimal("2");
        BigDecimal expected = new BigDecimal("0.6931472").setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = ln.calculate(arg, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет типовой положительный сценарий для x = 3.
    @Test
    void shouldCalculateForThree() {
        BigDecimal arg = new BigDecimal("3");
        BigDecimal expected = new BigDecimal("1.0986123").setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = ln.calculate(arg, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет монотонность: значения ln(x) возрастают вместе с x.
    @Test
    void shouldBeIncreasing() {
        BigDecimal x1 = new BigDecimal("0.5");
        BigDecimal x2 = new BigDecimal("1");
        BigDecimal x3 = new BigDecimal("2");
        
        BigDecimal y1 = ln.calculate(x1, PRECISION);
        BigDecimal y2 = ln.calculate(x2, PRECISION);
        BigDecimal y3 = ln.calculate(x3, PRECISION);
        
        assertTrue(y1.compareTo(y2) < 0);
        assertTrue(y2.compareTo(y3) < 0);
    }

    // Проверяет знак функции: для x < 1 логарифм отрицателен.
    @Test
    void shouldBeNegativeForXLessThanOne() {
        BigDecimal arg = new BigDecimal("0.5");
        BigDecimal result = ln.calculate(arg, PRECISION);
        assertTrue(result.compareTo(ZERO) < 0);
    }

    // Проверяет знак функции: для x > 1 логарифм положителен.
    @Test
    void shouldBePositiveForXGreaterThanOne() {
        BigDecimal arg = new BigDecimal("2");
        BigDecimal result = ln.calculate(arg, PRECISION);
        assertTrue(result.compareTo(ZERO) > 0);
    }

    // Проверяет логарифмическое свойство: ln(ab) = ln(a) + ln(b).
    @Test
    void shouldHavePropertyLnAB() {
        BigDecimal a = new BigDecimal("2");
        BigDecimal b = new BigDecimal("3");
        
        BigDecimal lnA = ln.calculate(a, PRECISION);
        BigDecimal lnB = ln.calculate(b, PRECISION);
        BigDecimal lnAB = ln.calculate(a.multiply(b, MC), PRECISION);
        
        assertEquals(lnA.add(lnB, MC).doubleValue(), lnAB.doubleValue(), DELTA * 10);
    }

    // Проверяет логарифмическое свойство: ln(a / b) = ln(a) - ln(b).
    @Test
    void shouldHavePropertyLnDiv() {
        BigDecimal a = new BigDecimal("6");
        BigDecimal b = new BigDecimal("2");
        
        BigDecimal lnA = ln.calculate(a, PRECISION);
        BigDecimal lnB = ln.calculate(b, PRECISION);
        BigDecimal lnDiv = ln.calculate(a.divide(b, MC), PRECISION);
        
        assertEquals(lnA.subtract(lnB, MC).doubleValue(), lnDiv.doubleValue(), DELTA * 10);
    }

    // Проверяет логарифмическое свойство: ln(a^n) = n * ln(a).
    @Test
    void shouldHavePropertyLnPow() {
        BigDecimal a = new BigDecimal("2");
        int n = 3;
        BigDecimal aPowN = a.pow(n, MC);
        
        BigDecimal lnA = ln.calculate(a, PRECISION);
        BigDecimal lnAPowN = ln.calculate(aPowN, PRECISION);
        
        assertEquals(lnA.multiply(new BigDecimal(n), MC).doubleValue(), lnAPowN.doubleValue(), DELTA * 10);
    }

    // Проверяет, что более строгая точность меняет численный результат.
    @Test
    void shouldCalculateWithDifferentPrecisions() {
        BigDecimal arg = new BigDecimal("2");
        BigDecimal lowPrecision = new BigDecimal("0.01");
        BigDecimal highPrecision = new BigDecimal("0.00000001");
        
        BigDecimal resultLow = ln.calculate(arg, lowPrecision);
        BigDecimal resultHigh = ln.calculate(arg, highPrecision);
        
        assertNotEquals(resultLow, resultHigh);
        assertTrue(resultHigh.subtract(resultLow, MC).abs().compareTo(lowPrecision) < 0);
    }

    // Проверяет отказ по ограничению итераций для большого аргумента.
    @Test
    void shouldFailForThousand() {
        assertThrows(ArithmeticException.class,
            () -> ln.calculate(new BigDecimal("1000"), PRECISION));
    }

    // Проверяет, что при несходимости возвращается осмысленное сообщение об ошибке.
    @Test
    void shouldFailWithCorrectMessageForLargeX() {
        ArithmeticException ex = assertThrows(ArithmeticException.class,
            () -> ln.calculate(new BigDecimal("1000"), PRECISION));
        assertTrue(ex.getMessage().contains("did not converge"));
    }

    // Проверяет отказ по ограничению итераций для очень малого положительного аргумента.
    @Test
    void shouldFailForVerySmallX() {
        assertThrows(ArithmeticException.class,
            () -> ln.calculate(new BigDecimal("0.001"), PRECISION));
    }

    // Проверяет валидацию точности: null-значение eps недопустимо.
    @Test
    void shouldThrowForNullPrecision() {
        assertThrows(IllegalArgumentException.class,
                () -> ln.calculate(new BigDecimal("2"), null, 100));
    }

    // Проверяет валидацию точности: нулевая точность недопустима.
    @Test
    void shouldThrowForZeroPrecision() {
        assertThrows(IllegalArgumentException.class,
                () -> ln.calculate(new BigDecimal("2"), BigDecimal.ZERO, 100));
    }

    // Проверяет, что getLastTermsCount возвращает число итераций последнего расчета.
    @Test
    void shouldReturnLastTermsCountAfterCalculation() {
        ln.calculate(new BigDecimal("2"), PRECISION);

        assertTrue(ln.getLastTermsCount() > 0);
    }

    // Проверяет, что для ln(1) счетчик итераций сбрасывается в ноль.
    @Test
    void shouldResetLastTermsCountForOne() {
        ln.calculate(new BigDecimal("2"), PRECISION);

        BigDecimal result = ln.calculate(BigDecimal.ONE, PRECISION);

        assertEquals(ZERO.setScale(PRECISION.scale(), HALF_EVEN), result);
        assertEquals(0, ln.getLastTermsCount());
    }

}

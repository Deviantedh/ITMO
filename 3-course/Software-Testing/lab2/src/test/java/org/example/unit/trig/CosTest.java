package org.example.unit.trig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.trig.Cos;
import org.example.trig.Sin;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CosTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
    private static final double DELTA = 0.0000001; 
    private static final MathContext MC = new MathContext(25, RoundingMode.HALF_EVEN);
    private static final BigDecimal PI = new BigDecimal(Math.PI, MC);
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal HALF_PI = PI.divide(TWO, MC);
    private static final BigDecimal THREE = new BigDecimal("3");
    private Cos cos;

    @BeforeEach
    void init() {
        cos = new Cos(new Sin());
    }

    // Проверяет базовое значение: cos(0) = 1.
    @Test
    void shouldCalculateForZero() {
        BigDecimal expected = ONE.setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(ZERO, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет нуль функции в точке pi/2.
    @Test
    void shouldCalculateForPiHalf() {
        BigDecimal expected = ZERO.setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(HALF_PI, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет нуль функции в точке -pi/2.
    @Test
    void shouldCalculateForNegativePiHalf() {
        BigDecimal expected = ZERO.setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(HALF_PI.negate(), PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет нуль функции в точке 3pi/2.
    @Test
    void shouldCalculateForThreePiHalf() {
        BigDecimal threePiHalf = THREE.multiply(HALF_PI, MC);
        BigDecimal expected = ZERO.setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(threePiHalf, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет значение cos(pi) = -1.
    @Test
    void shouldCalculateForPi() {
        BigDecimal expected = ONE.negate().setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(PI, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет четность функции в точке -pi.
    @Test
    void shouldCalculateForNegativePi() {
        BigDecimal expected = ONE.negate().setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(PI.negate(), PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет периодичность в точке 2pi.
    @Test
    void shouldCalculateForTwoPi() {
        BigDecimal twoPi = TWO.multiply(PI, MC);
        BigDecimal expected = ONE.setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(twoPi, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет специальное значение cos(pi/3) = 0.5.
    @Test
    void shouldCalculateForPiThree() {
        BigDecimal arg = PI.divide(new BigDecimal("3"), MC);
        BigDecimal expected = new BigDecimal("0.5").setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(arg, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет специальное значение cos(pi/4).
    @Test
    void shouldCalculateForPiFour() {
        BigDecimal arg = PI.divide(new BigDecimal("4"), MC);
        BigDecimal expected = new BigDecimal("0.7071068").setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(arg, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет специальное значение cos(pi/6).
    @Test
    void shouldCalculateForPiSix() {
        BigDecimal arg = PI.divide(new BigDecimal("6"), MC);
        BigDecimal expected = new BigDecimal("0.8660254").setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(arg, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет четность функции на аргументе -pi/3.
    @Test
    void shouldCalculateForNegativePiThree() {
        BigDecimal arg = PI.divide(new BigDecimal("3"), MC).negate();
        BigDecimal expected = new BigDecimal("0.5").setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(arg, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }

    // Проверяет устойчивость вычисления на большом аргументе.
    @Test
    void shouldCalculateForLargeValue() {
        BigDecimal hundredPi = new BigDecimal("100").multiply(PI, MC);
        BigDecimal expected = ONE.setScale(PRECISION.scale(), HALF_EVEN);
        BigDecimal actual = cos.calculate(hundredPi, PRECISION);
        assertEquals(expected.doubleValue(), actual.doubleValue(), DELTA);
    }
}

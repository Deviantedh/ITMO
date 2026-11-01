package org.devi;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SectantTest {

    private static final MathContext MC = new MathContext(40, RoundingMode.HALF_EVEN);
    private static final BigDecimal EPS = new BigDecimal("1e-12");
    private static final int MAX_TERMS = 120;

    // Проверяет точку разложения Маклорена: sec(0) = 1.
    @Test
    void zeroMaclaurinCenter() {
        sectant.Evaluation result = sectant.secWithDetails(BigDecimal.ZERO, EPS, MAX_TERMS);
        logCase("x=0", BigDecimal.ONE, result.getValue());
        assertEquals(0, result.getValue().compareTo(BigDecimal.ONE));
        assertTrue(result.getTermsUsed() > 0);
        assertTrue(result.getTermsUsed() <= MAX_TERMS);
    }

    // Проверяет очень малые аргументы и чётность sec(-x) = sec(x).
    @Test
    void tinyArgumentsAndEvenness() {
        BigDecimal x = new BigDecimal("1e-10");
        sectant.Evaluation positive = sectant.secWithDetails(x, EPS, MAX_TERMS);
        sectant.Evaluation negative = sectant.secWithDetails(x.negate(), EPS, MAX_TERMS);

        assertClose("x=1e-10", positive.getValue(), expectedByMath(x), new BigDecimal("1e-12"));
        assertClose("x=-1e-10", negative.getValue(), expectedByMath(x.negate()), new BigDecimal("1e-12"));
        assertClose("evenness sec(x)=sec(-x)", positive.getValue(), negative.getValue(), new BigDecimal("1e-20"));

        assertTrue(positive.getTermsUsed() > 0);
        assertTrue(positive.getTermsUsed() <= MAX_TERMS);
        assertTrue(negative.getTermsUsed() > 0);
        assertTrue(negative.getTermsUsed() <= MAX_TERMS);
    }

    // Проверяет известные значения в первой четверти.
    @Test
    void firstQuadrantReferenceAngles() {
        BigDecimal[] angles = {
                sectant.PI.divide(BigDecimal.valueOf(6L), MC),
                sectant.PI.divide(BigDecimal.valueOf(4L), MC),
                sectant.PI.divide(BigDecimal.valueOf(3L), MC)
        };
        String[] labels = {"pi/6", "pi/4", "pi/3"};

        for (int i = 0; i < angles.length; i++) {
            BigDecimal angle = angles[i];
            sectant.Evaluation result = sectant.secWithDetails(angle, EPS, MAX_TERMS);
            assertClose(labels[i], result.getValue(), expectedByMath(angle), new BigDecimal("1e-12"));
            assertTrue(result.getTermsUsed() > 0);
            assertTrue(result.getTermsUsed() <= MAX_TERMS);
        }
    }

    // Проверяет обработку в окрестности полюса pi/2 +- 1e-10.
    @Test
    void nearPoleBehavior() {
        BigDecimal delta = new BigDecimal("1e-10");
        BigDecimal left = sectant.HALF_PI.subtract(delta, MC);
        BigDecimal right = sectant.HALF_PI.add(delta, MC);

        assertNearPoleHandled(left, 1);
        assertNearPoleHandled(right, -1);
    }

    // Проверяет, что в точках разрыва функция сигнализирует об ошибке.
    @Test
    void polesMustThrow() {
        assertThrows(ArithmeticException.class, () -> sectant.sec(sectant.HALF_PI));
        assertThrows(ArithmeticException.class, () -> sectant.sec(sectant.HALF_PI.negate()));
    }

    // Проверяет знак и точность во второй четверти (cos < 0, sec < 0).
    @Test
    void secondQuadrantSignAndAccuracy() {
        BigDecimal[] angles = {
                sectant.PI.multiply(BigDecimal.valueOf(2L), MC).divide(BigDecimal.valueOf(3L), MC),
                sectant.PI.multiply(BigDecimal.valueOf(3L), MC).divide(BigDecimal.valueOf(4L), MC),
                sectant.PI.multiply(BigDecimal.valueOf(5L), MC).divide(BigDecimal.valueOf(6L), MC)
        };
        String[] labels = {"2pi/3", "3pi/4", "5pi/6"};

        for (int i = 0; i < angles.length; i++) {
            BigDecimal angle = angles[i];
            BigDecimal value = sectant.sec(angle);
            assertTrue(value.signum() < 0);
            assertClose(labels[i], value, expectedByMath(angle), new BigDecimal("1e-12"));
        }
    }

    // Проверяет граничные значения полупериода: sec(pi) = sec(-pi) = -1.
    @Test
    void piAndMinusPi() {
        assertClose("pi", sectant.sec(sectant.PI), BigDecimal.ONE.negate(), new BigDecimal("1e-12"));
        assertClose("-pi", sectant.sec(sectant.PI.negate()), BigDecimal.ONE.negate(), new BigDecimal("1e-12"));
    }

    // Проверяет периодичность для углов вне одного периода.
    @Test
    void periodicityOutsideSinglePeriod() {
        BigDecimal deg390 = degreesToRadians(new BigDecimal("390"));
        BigDecimal degMinus750 = degreesToRadians(new BigDecimal("-750"));

        BigDecimal[] angles = {
                sectant.PI.multiply(BigDecimal.valueOf(7L), MC).divide(BigDecimal.valueOf(4L), MC),
                sectant.PI.multiply(BigDecimal.valueOf(-7L), MC).divide(BigDecimal.valueOf(4L), MC),
                deg390,
                degMinus750
        };
        String[] labels = {"7pi/4", "-7pi/4", "390deg", "-750deg"};

        for (int i = 0; i < angles.length; i++) {
            BigDecimal angle = angles[i];
            assertClose(labels[i], sectant.sec(angle), expectedByMath(angle), new BigDecimal("1e-12"));
        }
    }

    // Проверяет устойчивость редукции аргумента на больших |x|.
    @Test
    void veryLargeArguments() {
        BigDecimal[] values = {
                new BigDecimal("1e6"),
                new BigDecimal("-1e6")
        };
        String[] labels = {"1e6", "-1e6"};

        for (int i = 0; i < values.length; i++) {
            BigDecimal x = values[i];
            sectant.Evaluation result = sectant.secWithDetails(x, EPS, MAX_TERMS);
            assertClose(labels[i], result.getValue(), expectedByMath(x), new BigDecimal("1e-12"));
            assertTrue(result.getTermsUsed() > 0);
            assertTrue(result.getTermsUsed() <= MAX_TERMS);
        }
    }

    // Проверяет валидацию входных параметров.
    @Test
    void inputValidation() {
        assertThrows(IllegalArgumentException.class, () -> sectant.secWithDetails(null, EPS, MAX_TERMS));
        assertThrows(IllegalArgumentException.class, () -> sectant.secWithDetails(BigDecimal.ZERO, BigDecimal.ZERO, MAX_TERMS));
        assertThrows(IllegalArgumentException.class, () -> sectant.secWithDetails(BigDecimal.ZERO, EPS, 0));
    }

    private static void assertNearPoleHandled(BigDecimal x, int expectedSign) {
        try {
            BigDecimal value = sectant.sec(x);
            logCase("около полюса x=" + x.toPlainString(), null, value);
            assertTrue(value.signum() == expectedSign);
            assertTrue(value.abs().compareTo(new BigDecimal("1e8")) > 0);
        } catch (ArithmeticException ex) {
            System.out.printf("[КЕЙС] около полюса x=%s | ожидаемое=особый случай | полученное=ИСКЛЮЧЕНИЕ: %s%n",
                    x.toPlainString(), ex.getMessage());
            assertTrue(ex.getMessage().contains("полюсу") || ex.getMessage().contains("не определен"));
        }
    }

    private static BigDecimal expectedByMath(BigDecimal x) {
        return BigDecimal.valueOf(1.0d / Math.cos(x.doubleValue()));
    }

    private static BigDecimal degreesToRadians(BigDecimal degrees) {
        return degrees.multiply(sectant.PI, MC).divide(BigDecimal.valueOf(180L), MC);
    }

    private static void assertClose(String label, BigDecimal actual, BigDecimal expected, BigDecimal tolerance) {
        BigDecimal error = actual.subtract(expected, MC).abs();
        logCase(label, expected, actual);
        assertTrue(
                error.compareTo(tolerance) <= 0,
                () -> "Ожидалось " + expected.toPlainString() + ", получено " + actual.toPlainString() + ", ошибка=" + error
        );
    }

    private static void logCase(String label, BigDecimal expected, BigDecimal actual) {
        String expectedText = expected == null ? "особый случай" : expected.toPlainString();
        BigDecimal error = expected == null ? null : actual.subtract(expected, MC).abs();
        String errorText = error == null ? "-" : error.toPlainString();
        System.out.printf("[КЕЙС] %s | ожидаемое=%s | полученное=%s | ошибка=%s%n",
                label, expectedText, actual.toPlainString(), errorText);
    }
}

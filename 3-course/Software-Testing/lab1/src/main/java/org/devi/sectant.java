package org.devi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public final class sectant {

    private sectant() {
    }

    private static final MathContext MC = new MathContext(60, RoundingMode.HALF_EVEN);

    public static final BigDecimal PI = new BigDecimal(
            "3.14159265358979323846264338327950288419716939937510"
    );
    public static final BigDecimal HALF_PI = PI.divide(BigDecimal.valueOf(2L), MC);

    public static final BigDecimal DEFAULT_EPS = new BigDecimal("1e-20");
    public static final int DEFAULT_MAX_TERMS = 120;
    private static final BigDecimal NEAR_POLE_THRESHOLD = new BigDecimal("1e-9");

    /** Считаем sec(x) по базовым параметрам */
    public static BigDecimal sec(BigDecimal x) {
        return secWithDetails(x, DEFAULT_EPS, DEFAULT_MAX_TERMS).getValue();
    }

    /** Считаем sec(x) со своим эпсилоном и кол итераций */
    public static BigDecimal sec(BigDecimal x, BigDecimal eps, int maxTerms) {
        return secWithDetails(x, eps, maxTerms).getValue();
    }

    /** sec(x) с доп инфой для тестера */
    public static Evaluation secWithDetails(BigDecimal x, BigDecimal eps, int maxTerms) {
        validateInputs(x, eps, maxTerms);

        Reduction reduction = reduce(x);
        BigDecimal xr = reduction.reducedX;
        BigDecimal absXr = xr.abs();

        if (absXr.compareTo(HALF_PI) >= 0) {
            throw new ArithmeticException("sec(x) не определен в точке x = pi/2 + k*pi");
        }

        BigDecimal distToPole = HALF_PI.subtract(absXr, MC);
        if (distToPole.compareTo(NEAR_POLE_THRESHOLD) <= 0) {
            throw new ArithmeticException("x слишком близок к полюсу sec(x)");
        }

        BigDecimal[] factorial = factorialTable(2 * (maxTerms - 1));
        BigDecimal[] coeff = new BigDecimal[maxTerms];
        coeff[0] = BigDecimal.ONE;

        BigDecimal x2 = xr.multiply(xr, MC);
        BigDecimal xPow = BigDecimal.ONE;
        BigDecimal sum = BigDecimal.ONE;

        int termsUsed = 1;
        boolean converged = false;

        for (int n = 1; n < maxTerms; n++) {
            coeff[n] = nextSecCoefficient(n, coeff, factorial);

            xPow = xPow.multiply(x2, MC);
            BigDecimal term = coeff[n].multiply(xPow, MC);
            sum = sum.add(term, MC);
            termsUsed = n + 1;

            if (term.abs().compareTo(eps) < 0) {
                converged = true;
                break;
            }
        }

        BigDecimal value = reduction.sign > 0 ? sum : sum.negate(MC);
        return new Evaluation(value, termsUsed, converged);
    }

    private static void validateInputs(BigDecimal x, BigDecimal eps, int maxTerms) {
        if (x == null) {
            throw new IllegalArgumentException("x не должен быть null");
        }
        if (eps == null || eps.signum() <= 0) {
            throw new IllegalArgumentException("eps должен быть больше 0");
        }
        if (maxTerms <= 0) {
            throw new IllegalArgumentException("maxTerms должен быть больше 0");
        }
    }

    /** Сокращаем аргумент по формуле sec(x + pi) = -sec(x) */
    private static Reduction reduce(BigDecimal x) {
        BigDecimal shifted = x.add(HALF_PI, MC);
        BigInteger k = floorDivide(shifted, PI);

        BigDecimal reduced = x.subtract(PI.multiply(new BigDecimal(k), MC), MC);
        int sign = k.testBit(0) ? -1 : 1;
        return new Reduction(reduced, sign);
    }

    /** Целочисленное деление вниз: floor(numerator / denominator) */
    private static BigInteger floorDivide(BigDecimal numerator, BigDecimal denominator) {
        BigDecimal q = numerator.divideToIntegralValue(denominator);
        BigDecimal r = numerator.remainder(denominator);
        BigInteger qi = q.toBigIntegerExact();

        if (numerator.signum() < 0 && r.signum() != 0) {
            qi = qi.subtract(BigInteger.ONE);
        }

        return qi;
    }

    private static BigDecimal[] factorialTable(int n) {
        BigDecimal[] f = new BigDecimal[n + 1];
        f[0] = BigDecimal.ONE;
        for (int i = 1; i <= n; i++) {
            f[i] = f[i - 1].multiply(BigDecimal.valueOf(i), MC);
        }
        return f;
    }

    /** Считаем следующий коэффициент из найденных */
    private static BigDecimal nextSecCoefficient(int n, BigDecimal[] coeff, BigDecimal[] factorial) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int k = 0; k < n; k++) {
            int m = 2 * n - 2 * k;
            BigDecimal part = coeff[k].divide(factorial[m], MC);
            if (((n - k - 1) & 1) == 0) {
                sum = sum.add(part, MC);
            } else {
                sum = sum.subtract(part, MC);
            }
        }
        return sum;
    }

    private static final class Reduction {
        private final BigDecimal reducedX;
        private final int sign;

        private Reduction(BigDecimal reducedX, int sign) {
            this.reducedX = reducedX;
            this.sign = sign;
        }
    }

    public static final class Evaluation {
        private final BigDecimal value;
        private final int termsUsed;
        private final boolean converged;

        private Evaluation(BigDecimal value, int termsUsed, boolean converged) {
            this.value = value;
            this.termsUsed = termsUsed;
            this.converged = converged;
        }

        public BigDecimal getValue() {
            return value;
        }

        public int getTermsUsed() {
            return termsUsed;
        }

        public boolean isConverged() {
            return converged;
        }
    }
}

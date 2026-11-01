package org.example;

import org.example.function.AbstractMathFunction;

import java.math.*;

/** Вычисляет систему уравнений */
public class EquationSystem {

    private static final MathContext mc = new MathContext(25);

    private final AbstractMathFunction sin;
    private final AbstractMathFunction cos;
    private final AbstractMathFunction tan;

    private final AbstractMathFunction ln;
    private final AbstractMathFunction log2;
    private final AbstractMathFunction log3;
    private final AbstractMathFunction log5;
    private final AbstractMathFunction log10;

    /** Создает систему из модулей функций. */
    public EquationSystem(AbstractMathFunction sin, AbstractMathFunction cos, AbstractMathFunction tan,
                          AbstractMathFunction ln, AbstractMathFunction log2, AbstractMathFunction log3,
                          AbstractMathFunction log5, AbstractMathFunction log10) {

        this.sin = sin;
        this.cos = cos;
        this.tan = tan;
        this.ln = ln;
        this.log2 = log2;
        this.log3 = log3;
        this.log5 = log5;
        this.log10 = log10;
    }

    /** Вычисляет значение системы для заданного аргумента и точности. */
    public BigDecimal calculate(BigDecimal x, BigDecimal eps) {

        if (x.compareTo(BigDecimal.ZERO) <= 0) {

            BigDecimal sinVal = sin.calculate(x, eps);
            BigDecimal cosVal = cos.calculate(x, eps);
            BigDecimal tanVal = tan.calculate(x, eps);

            return cosVal.add(sinVal, mc)
                    .subtract(cosVal, mc)
                    .subtract(sinVal.multiply(sinVal, mc).add(tanVal, mc), mc);

        } else {

            BigDecimal l2 = log2.calculate(x, eps);
            BigDecimal l10 = log10.calculate(x, eps);
            BigDecimal l5 = log5.calculate(x, eps);
            BigDecimal l3 = log3.calculate(x, eps);
            BigDecimal lnVal = ln.calculate(x, eps);

            if (l3.abs().compareTo(eps) < 0) {
                throw new ArithmeticException("log3(x)=0 → division by zero, x=" + x);
            }

            BigDecimal numerator = l2.multiply(l5, mc)
                    .add(l5, mc)
                    .multiply(l10.subtract(lnVal, mc).pow(2, mc), mc)
                    .pow(2, mc);

            return numerator.divide(l3.pow(2, mc), mc);
        }
    }
}

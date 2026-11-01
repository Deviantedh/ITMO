package org.example.trig;

import org.example.function.AbstractMathFunction;

import java.math.BigDecimal;
import java.math.MathContext;

/** Вычисляет тангенс как отношение синуса к косинусу. */
public class Tan extends AbstractMathFunction {

    private final Sin sin;
    private final Cos cos;
    private static final MathContext mc = new MathContext(25);

    /** Создает тангенс на основе синуса и косинуса. */
    public Tan(Sin sin, Cos cos) {
        this.sin = sin;
        this.cos = cos;
    }

    /** Возвращает tan(x), если cos(x) не обращается в ноль. */
    @Override
    public BigDecimal calculate(BigDecimal x, BigDecimal eps) {
        BigDecimal cosVal = cos.calculate(x, eps);

        if (cosVal.abs().compareTo(eps) < 0) {
            throw new ArithmeticException("tan undefined: cos(x)=0 at x=" + x);
        }

        return sin.calculate(x, eps)
                .divide(cosVal, mc);
    }
}

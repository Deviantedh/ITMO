package org.example.trig;

import org.example.function.AbstractMathFunction;

import java.math.BigDecimal;
import java.math.MathContext;

/** Вычисляет косинус через сдвиг аргумента для синуса. */
public class Cos extends AbstractMathFunction {

    private final Sin sin;
    private static final MathContext mc = new MathContext(25);
    private static final BigDecimal HALF_PI = BigDecimal.valueOf(Math.PI / 2);

    /** Создает косинус на основе реализации синуса. */
    public Cos(Sin sin) {
        this.sin = sin;
    }

    /** Возвращает cos(x) с заданной точностью. */
    @Override
    public BigDecimal calculate(BigDecimal x, BigDecimal eps) {
        return sin.calculate(x.add(HALF_PI, mc), eps);
    }
}

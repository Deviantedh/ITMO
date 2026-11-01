package org.example.integration;

import org.example.EquationSystem;
import org.example.function.AbstractMathFunction;
import org.example.log.Ln;
import org.example.trig.Sin;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class EquationSystemIntegrationSinLnTest {

    // Проверяет левую ветку после подключения sin и ln к системе.
    @Test
    public void testLeftBranchWithSinLn() {
        BigDecimal x = new BigDecimal("-1");

        Sin sin = new Sin();
        Ln ln = new Ln();
        AbstractMathFunction cos = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction tan = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log2 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log3 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log5 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log10 = Mockito.mock(AbstractMathFunction.class);

        BigDecimal sinVal = sin.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal cosVal = new BigDecimal(Double.toString(Math.cos(x.doubleValue())));
        BigDecimal tanVal = new BigDecimal(Double.toString(Math.tan(x.doubleValue())));

        when(cos.calculate(eq(x), eq(EquationSystemIntegrationSupport.EPS))).thenReturn(cosVal);
        when(tan.calculate(eq(x), eq(EquationSystemIntegrationSupport.EPS))).thenReturn(tanVal);

        EquationSystem system = new EquationSystem(
                sin, cos, tan,
                ln, log2, log3, log5, log10
        );

        BigDecimal actual = system.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal expected = EquationSystemIntegrationSupport.leftExpected(sinVal, cosVal, tanVal);

        assertTrue(actual.subtract(expected).abs().compareTo(EquationSystemIntegrationSupport.EPS) <= 0);
    }

    // Проверяет правую ветку после подключения sin и ln к системе.
    @Test
    public void testRightBranchWithSinLn() {
        BigDecimal x = new BigDecimal("2.718281828459045");

        Sin sin = new Sin();
        Ln ln = new Ln();
        AbstractMathFunction cos = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction tan = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log2 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log3 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log5 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log10 = Mockito.mock(AbstractMathFunction.class);

        BigDecimal lnVal = ln.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal log2Val = new BigDecimal("1.4426950408889634");
        BigDecimal log3Val = new BigDecimal("0.9102392266268373");
        BigDecimal log5Val = new BigDecimal("0.6213349345596119");
        BigDecimal log10Val = new BigDecimal("0.4342944819032518");

        when(log2.calculate(eq(x), eq(EquationSystemIntegrationSupport.EPS))).thenReturn(log2Val);
        when(log3.calculate(eq(x), eq(EquationSystemIntegrationSupport.EPS))).thenReturn(log3Val);
        when(log5.calculate(eq(x), eq(EquationSystemIntegrationSupport.EPS))).thenReturn(log5Val);
        when(log10.calculate(eq(x), eq(EquationSystemIntegrationSupport.EPS))).thenReturn(log10Val);

        EquationSystem system = new EquationSystem(
                sin, cos, tan,
                ln, log2, log3, log5, log10
        );

        BigDecimal actual = system.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal expected = EquationSystemIntegrationSupport.rightExpected(
                lnVal, log2Val, log3Val, log5Val, log10Val
        );

        assertTrue(actual.subtract(expected).abs().compareTo(EquationSystemIntegrationSupport.EPS) <= 0);
    }
}

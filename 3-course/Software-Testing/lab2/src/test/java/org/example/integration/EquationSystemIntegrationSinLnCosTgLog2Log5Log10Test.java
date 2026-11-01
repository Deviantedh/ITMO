package org.example.integration;

import org.example.EquationSystem;
import org.example.function.AbstractMathFunction;
import org.example.log.Ln;
import org.example.log.LogNBase;
import org.example.trig.Cos;
import org.example.trig.Sin;
import org.example.trig.Tan;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class EquationSystemIntegrationSinLnCosTgLog2Log5Log10Test {

    // Проверяет левую ветку после добавления реального log_10 в систему.
    @Test
    public void testLeftBranchWithLog10Added() {
        BigDecimal x = new BigDecimal("-0.5");

        Sin sin = new Sin();
        Cos cos = new Cos(sin);
        Tan tan = new Tan(sin, cos);
        Ln ln = new Ln();
        LogNBase log2 = new LogNBase(ln, 2);
        LogNBase log5 = new LogNBase(ln, 5);
        LogNBase log10 = new LogNBase(ln, 10);
        AbstractMathFunction log3 = Mockito.mock(AbstractMathFunction.class);

        BigDecimal sinVal = sin.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal cosVal = cos.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal tanVal = tan.calculate(x, EquationSystemIntegrationSupport.EPS);

        EquationSystem system = new EquationSystem(
                sin, cos, tan,
                ln, log2, log3, log5, log10
        );

        BigDecimal actual = system.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal expected = EquationSystemIntegrationSupport.leftExpected(sinVal, cosVal, tanVal);

        assertTrue(actual.subtract(expected).abs().compareTo(EquationSystemIntegrationSupport.EPS) <= 0);
    }

    // Проверяет правую ветку после добавления реального log_10 в систему.
    @Test
    public void testRightBranchWithLog10Added() {
        BigDecimal x = new BigDecimal("2");

        Sin sin = new Sin();
        Cos cos = new Cos(sin);
        Tan tan = new Tan(sin, cos);
        Ln ln = new Ln();
        LogNBase log2 = new LogNBase(ln, 2);
        LogNBase log5 = new LogNBase(ln, 5);
        LogNBase log10 = new LogNBase(ln, 10);
        AbstractMathFunction log3 = Mockito.mock(AbstractMathFunction.class);

        BigDecimal lnVal = ln.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal log2Val = log2.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal log3Val = new BigDecimal("0.6309297535714574");
        BigDecimal log5Val = log5.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal log10Val = log10.calculate(x, EquationSystemIntegrationSupport.EPS);

        when(log3.calculate(eq(x), eq(EquationSystemIntegrationSupport.EPS))).thenReturn(log3Val);

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

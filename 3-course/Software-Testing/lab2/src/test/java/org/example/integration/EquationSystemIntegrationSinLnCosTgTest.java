package org.example.integration;

import org.example.EquationSystem;
import org.example.function.AbstractMathFunction;
import org.example.log.Ln;
import org.example.trig.Cos;
import org.example.trig.Sin;
import org.example.trig.Tan;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class EquationSystemIntegrationSinLnCosTgTest {

    // Проверяет левую ветку после подключения sin, ln, cos и tan.
    @Test
    public void testLeftBranchWithSinLnCosTan() {
        BigDecimal x = new BigDecimal("-0.5");

        Sin sin = new Sin();
        Cos cos = new Cos(sin);
        Tan tan = new Tan(sin, cos);
        Ln ln = new Ln();
        AbstractMathFunction log2 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log3 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log5 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log10 = Mockito.mock(AbstractMathFunction.class);

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

    // Проверяет правую ветку после подключения sin, ln, cos и tan.
    @Test
    public void testRightBranchWithSinLnCosTan() {
        BigDecimal x = new BigDecimal("2");

        Sin sin = new Sin();
        Cos cos = new Cos(sin);
        Tan tan = new Tan(sin, cos);
        Ln ln = new Ln();
        AbstractMathFunction log2 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log3 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log5 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log10 = Mockito.mock(AbstractMathFunction.class);

        BigDecimal lnVal = ln.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal log2Val = new BigDecimal("1.0");
        BigDecimal log3Val = new BigDecimal("0.6309297535714574");
        BigDecimal log5Val = new BigDecimal("0.43067655807339306");
        BigDecimal log10Val = new BigDecimal("0.3010299956639812");

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

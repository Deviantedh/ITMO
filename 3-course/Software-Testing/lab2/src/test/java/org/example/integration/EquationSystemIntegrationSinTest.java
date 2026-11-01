package org.example.integration;

import org.example.EquationSystem;
import org.example.function.AbstractMathFunction;
import org.example.trig.Sin;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class EquationSystemIntegrationSinTest {

    // Проверяет левую ветку системы при подключенном только модуле sin.
    @Test
    public void testLeftBranchWithSin() {
        BigDecimal x = new BigDecimal("-0.7853981633974483");

        Sin sin = new Sin();
        AbstractMathFunction cos = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction tan = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction ln = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log2 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log3 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log5 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log10 = Mockito.mock(AbstractMathFunction.class);

        BigDecimal sinVal = sin.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal cosVal = new BigDecimal("0.7071067811865476");
        BigDecimal tanVal = new BigDecimal("-1.0");

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

    // Проверяет правую ветку системы при заглушках остальных модулей.
    @Test
    public void testRightBranchWithSin() {
        BigDecimal x = new BigDecimal("2");

        Sin sin = new Sin();
        AbstractMathFunction cos = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction tan = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction ln = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log2 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log3 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log5 = Mockito.mock(AbstractMathFunction.class);
        AbstractMathFunction log10 = Mockito.mock(AbstractMathFunction.class);

        BigDecimal lnVal = new BigDecimal("0.6931471805599453");
        BigDecimal log2Val = new BigDecimal("1.0");
        BigDecimal log3Val = new BigDecimal("0.6309297535714574");
        BigDecimal log5Val = new BigDecimal("0.43067655807339306");
        BigDecimal log10Val = new BigDecimal("0.3010299956639812");

        when(ln.calculate(eq(x), eq(EquationSystemIntegrationSupport.EPS))).thenReturn(lnVal);
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

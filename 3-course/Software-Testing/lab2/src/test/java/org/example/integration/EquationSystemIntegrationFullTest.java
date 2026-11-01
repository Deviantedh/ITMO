package org.example.integration;

import org.example.EquationSystem;
import org.example.log.Ln;
import org.example.log.LogNBase;
import org.example.trig.Cos;
import org.example.trig.Sin;
import org.example.trig.Tan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EquationSystemIntegrationFullTest {

    // Проверяет полностью собранную левую ветку с реальными модулями.
    @Test
    public void testLeftBranchFullyIntegrated() {
        BigDecimal x = new BigDecimal("-1");

        Sin sin = new Sin();
        Cos cos = new Cos(sin);
        Tan tan = new Tan(sin, cos);
        Ln ln = new Ln();
        LogNBase log2 = new LogNBase(ln, 2);
        LogNBase log3 = new LogNBase(ln, 3);
        LogNBase log5 = new LogNBase(ln, 5);
        LogNBase log10 = new LogNBase(ln, 10);

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

    // Проверяет полностью собранную правую ветку с реальными модулями.
    @Test
    public void testRightBranchFullyIntegrated() {
        BigDecimal x = new BigDecimal("2");

        Sin sin = new Sin();
        Cos cos = new Cos(sin);
        Tan tan = new Tan(sin, cos);
        Ln ln = new Ln();
        LogNBase log2 = new LogNBase(ln, 2);
        LogNBase log3 = new LogNBase(ln, 3);
        LogNBase log5 = new LogNBase(ln, 5);
        LogNBase log10 = new LogNBase(ln, 10);

        BigDecimal lnVal = ln.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal log2Val = log2.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal log3Val = log3.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal log5Val = log5.calculate(x, EquationSystemIntegrationSupport.EPS);
        BigDecimal log10Val = log10.calculate(x, EquationSystemIntegrationSupport.EPS);

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

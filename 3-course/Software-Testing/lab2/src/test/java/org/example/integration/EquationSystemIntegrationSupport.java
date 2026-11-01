package org.example.integration;

import java.math.BigDecimal;
import java.math.MathContext;

final class EquationSystemIntegrationSupport {

    static final BigDecimal EPS = new BigDecimal("0.000000000000001");
    static final MathContext MC = MathContext.DECIMAL128;

    private EquationSystemIntegrationSupport() {
    }

    static BigDecimal leftExpected(BigDecimal sinVal, BigDecimal cosVal, BigDecimal tanVal) {
        return cosVal.add(sinVal, MC)
                .subtract(cosVal, MC)
                .subtract(sinVal.multiply(sinVal, MC).add(tanVal, MC), MC);
    }

    static BigDecimal rightExpected(BigDecimal lnVal, BigDecimal log2Val, BigDecimal log3Val,
                                    BigDecimal log5Val, BigDecimal log10Val) {
        BigDecimal numerator = log2Val.multiply(log5Val, MC)
                .add(log5Val, MC)
                .multiply(log10Val.subtract(lnVal, MC).pow(2, MC), MC)
                .pow(2, MC);

        return numerator.divide(log3Val.pow(2, MC), MC);
    }
}

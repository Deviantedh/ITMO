package org.example;

import org.example.log.*;
import org.example.trig.*;
import org.example.util.CsvWriter;

import java.math.BigDecimal;

public class Main {

    /** Вычисляет значения системы на диапазоне и сохраняет их в CSV. */
    public static void main(String[] args) {

        BigDecimal eps = new BigDecimal("0.00001");

        Sin sin = new Sin();
        Cos cos = new Cos(sin);
        Tan tan = new Tan(sin, cos);

        Ln ln = new Ln();
        LogNBase log2 = new LogNBase(ln, 2);
        LogNBase log3 = new LogNBase(ln, 3);
        LogNBase log5 = new LogNBase(ln, 5);
        LogNBase log10 = new LogNBase(ln, 10);


        EquationSystem system = new EquationSystem(
                sin, cos, tan,
                ln, log2, log3, log5, log10
        );

        String filename = "results.csv";

        CsvWriter.write(
            filename,
            new BigDecimal("-35"),
            new BigDecimal("35"),
            new BigDecimal("0.001"),
            x -> system.calculate(x, eps)
        );
    }
}

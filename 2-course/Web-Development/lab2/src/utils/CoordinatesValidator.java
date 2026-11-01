package utils;

import java.util.Arrays;
import java.util.List;

public class CoordinatesValidator {
    private final double x, y, r;

    public CoordinatesValidator(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public boolean checkData() {
        if (!checkX()) System.out.println("X validation haven't passed");
        if (!checkY()) System.out.println("Y validation haven't passed");
        if (!checkR()) System.out.println("R validation haven't passed");
        System.out.println(x);
        System.out.println(y);
        System.out.println(r);
        return checkX() && checkY() && checkR();
    }

    private boolean checkX() {
        return x >= -5 && x <= 3;
    }

    private boolean checkY() {
        return y >= -3 && y <= 3;
    }

    private boolean checkR() {
        List<Double> validRValues = Arrays.asList(1.0, 1.5, 2.0, 2.5, 3.0);
        return validRValues.contains(r) && r == r;

    }
}

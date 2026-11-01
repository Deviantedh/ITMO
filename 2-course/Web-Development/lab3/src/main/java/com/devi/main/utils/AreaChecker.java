package com.devi.main.utils;

public class AreaChecker {

    public static boolean isInArea(double x, double y, double r) {
        if (r == 0) {
            return false;
        }
        // Check for the rectangle in the top-right quadrant
        if (x >= 0 && y >= 0) {
            return x <= r && y <= r;
        }
        // Check for the semicircle in the bottom-left quadrant
        else if (x >= 0 && y <= 0) {
            return (x * x + y * y) <= (r * r) / 4;
        }
        // Check for the triangle in the bottom-right quadrant
        else if (x <= 0 && y >= 0) {
            return (-x <= r/2) && y <= (r/2 + x);
        }
        return false;
    }
}

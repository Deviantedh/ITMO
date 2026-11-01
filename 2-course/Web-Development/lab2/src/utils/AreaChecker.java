package utils;

public class AreaChecker {
    public static boolean isInArea(double x, double y, double r) {
        // Прямоугольник (справа сверху)
        if (x >= 0 && y >= 0) {
            return x <= r / 2 && y <= r;
        }
        // Треугольник (справа снизу)
        if (x >= 0 && y <= 0) {
            return (x >= -r) && (y >= 2* x - r);
        }
        // Часть круга (слева снизу)
        if (x <= 0 && y <= 0) {
            return x * x + y * y <= Math.pow(r / 2, 2);
        }
        // Если точка не попадает ни в одну из областей
        return false;
    }
}





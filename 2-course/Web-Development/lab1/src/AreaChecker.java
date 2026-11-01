class AreaChecker {
    public static boolean isInArea(double x, double y, double r) {
        if (x <= 0 && y <= 0) {
            return (x * x + y * y) <= (r * r) / 2;
        }
        if (x >= 0 && y <= 0) {
            return (y >= -2 * x + r);
        }
        if (x <= 0 && y >= 0) {
            return (x >= -r && y <= r);
        }
        return false;
    }
}

import java.util.Arrays;
import java.util.List;

class CoordinatesValidator {
    private double x;
    private double y;
    private double r;

    public CoordinatesValidator(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public boolean checkData() {
        return checkX() && checkY() && checkR();
    }

    private boolean checkX() {
        List<Double> validXValues = Arrays.asList(-4.0, -5.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0);
        return validXValues.contains(this.x);
    }

    private boolean checkY() {
        return this.y >= -5 && this.y <= 5;
    }

    private boolean checkR() {
        List<Double> validRValues = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        return validRValues.contains(this.r);
    }
}
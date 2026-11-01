package CollectionObject;

import java.io.Serializable;

public class Location implements Serializable {
    private Float x;
    private Long y;
    private double z;
    private String name;

    public Location(Float x, Long y, double z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }
    public Location(){
    }

    public Float getLX() {
        return x;
    }

    public void setLX(Float x) {
        this.x = x;
    }

    public Long getLY() {
        return y;
    }

    public void setLY(Long y) {
        this.y = y;
    }

    public double getLZ() {
        return z;
    }

    public void setLZ(double z) {
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Переопределение метода toString() для вывода информации о местоположении
    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", name='" + name + '\'' +
                '}';
    }
}

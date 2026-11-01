package CollectionObject;

import java.io.Serializable;

public class Location implements Serializable {
    private Float x;
    private int y;
    private Float z;
    private String name;

    public Location(Float x, int y, Float z, String name) {
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

    public int getLY() {
        return y;
    }

    public void setLY(int y) {
        this.y = y;
    }

    public Float getLZ() {
        return z;
    }

    public void setLZ(float z) {
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
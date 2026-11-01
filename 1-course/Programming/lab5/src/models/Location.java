package CollectionObject;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Location {
    private Float x;
    private Float y;
    private double z;
    private String name;

    public Location(Float x, Float y, double z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
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
        return "models.Location{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", name='" + name + '\'' +
                '}';
    }

    public static Location fromString(Node xmlString) {
        try {
            Element locationElement = (Element) xmlString;
            Float x = Float.parseFloat(locationElement.getElementsByTagName("x").item(0).getTextContent());
            Float y = Float.parseFloat(locationElement.getElementsByTagName("y").item(0).getTextContent());
            double z = Double.parseDouble(locationElement.getElementsByTagName("z").item(0).getTextContent());
            String name = locationElement.getElementsByTagName("name").item(0).getTextContent();
            return new Location(x, y, z, name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
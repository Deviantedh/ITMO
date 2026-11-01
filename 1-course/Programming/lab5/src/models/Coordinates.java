package models;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

class Coordinates {
    private Float x;
    private Float y;

    public Coordinates(float x, Float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "models.Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public static Coordinates fromString(Node xmlString) {
        try {
            Element coordinatesElement = (Element) xmlString;
            Float x = Float.parseFloat(coordinatesElement.getElementsByTagName("x").item(0).getTextContent());
            Float y = Float.parseFloat(coordinatesElement.getElementsByTagName("y").item(0).getTextContent());
            return new Coordinates(x, y);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
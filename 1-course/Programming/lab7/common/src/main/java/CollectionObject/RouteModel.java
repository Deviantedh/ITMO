package CollectionObject;

import Network.User;

import java.io.Serializable;

public class RouteModel implements Serializable {
    private String name;
    private Coordinates coordinates; //Поле не может быть null
    private Location from;
    private Location to;
    private int distance;
    private User user;

    public RouteModel(String name, Coordinates coordinates, Location from, Location to, int distance, User user) {
        this.name = name;
        this.coordinates = coordinates;
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.user = user;
    }

    public RouteModel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Route{" +
                "name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", from=" + from +
                ", to=" + to +
                ", distance=" + distance +
                '}';
    }
}

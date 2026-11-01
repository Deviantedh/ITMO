package CollectionObject;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

public class Route {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime  creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Location from;
    private Location to;
    private int distance; //Поле не может быть null
    private String creator;

    public Route(Long id, String name, Coordinates coordinates, Location from, Location to, int distance, String creator) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now();
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.creator = creator;
    }

    public Route(Long id, String name, Coordinates coordinates, Timestamp creationDate, Location from, Location to, int distance, String creator){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate.toInstant().atZone(ZoneId.systemDefault());
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.creator = creator;
    }

//    public Route(long id, String name, Coordinates coordinates, Date creationDate, Location location, Location location1, int distance, int user) {
//        this.id = id;
//        this.name = name;
//        this.coordinates = coordinates;
//        this.creationDate = ZonedDateTime.now();
//        this.from = from;
//        this.to = to;
//        this.distance = distance;
//        this.creator = creator;
//    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

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

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Route route = (Route) object;
        return Float.compare(distance, route.distance) == 0 && Objects.equals(id, route.id) && Objects.equals(name, route.name) && Objects.equals(coordinates, route.coordinates) && Objects.equals(creationDate, route.creationDate) && Objects.equals(from, route.from) && Objects.equals(to, route.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, to, from, distance);
    }

    @Override
    public String toString() {
        return "\n\nRoute{\n" +
                "id = " + id +
                ", \nname = '" + name + '\'' +
                ", \ncoordinates = " + coordinates +
                ", \ncreationDate = " + creationDate +
                ", \nfrom = " + from +
                ", \nto = " + to +
                ", \ndistance = " + distance +
                ", \ncreator = '" + creator + '\'' +
                '}';
    }
}

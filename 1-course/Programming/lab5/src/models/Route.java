package CollectionObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.time.ZonedDateTime;

public class Route {
    private long id;
    private String name;
    private Coordinates coordinates;
    private ZonedDateTime creationDate;
    private Location from;
    private Location to;
    private int distance;

    public Route(long id, String name, int distance) {
        this.id = id;
        this.name = name;
        //this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now();
        //this.from = from;
        //this.to = to;
        this.distance = distance;
    }

    public long getId() {
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

    // Переопределение метода toString() для вывода информации о маршруте
    @Override
    public String toString() {
        return "models.Route{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", from=" + from +
                ", to=" + to +
                ", distance=" + distance +
                '}';
    }

        public static Route fromString(String input) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new ByteArrayInputStream(input.getBytes()));

                Element root = document.getDocumentElement();

                long id = Long.parseLong(root.getElementsByTagName("id").item(0).getTextContent());
                String name = root.getElementsByTagName("name").item(0).getTextContent();
                Coordinates coordinates = Coordinates.fromString(root.getElementsByTagName("coordinates").item(0));
                ZonedDateTime creationDate = ZonedDateTime.parse(root.getElementsByTagName("creationDate").item(0).getTextContent());
                Location from = Location.fromString(root.getElementsByTagName("from").item(0));
                Location to = Location.fromString(root.getElementsByTagName("to").item(0));
                int distance = Integer.parseInt(root.getElementsByTagName("distance").item(0).getTextContent());

                Route route = new Route(id, name, distance);
                route.setCoordinates(coordinates);
                route.setFrom(from);
                route.setTo(to);
                route.setCreationDate(creationDate);

                return route;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    public String toXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<models.Route>");
        sb.append("<id>").append(id).append("</id>");
        sb.append("<name>").append(name).append("</name>");
        sb.append("<coordinates>");
        sb.append("<x>").append(coordinates.getX()).append("</x>");
        sb.append("<y>").append(coordinates.getY()).append("</y>");
        sb.append("</coordinates>");
        sb.append("<creationDate>").append(creationDate).append("</creationDate>");
        sb.append("<from>");
        sb.append("<x>").append(from.getX()).append("</x>");
        sb.append("<y>").append(from.getY()).append("</y>");
        sb.append("<z>").append(from.getZ()).append("</z>");
        sb.append("<name>").append(from.getName()).append("</name>");
        sb.append("</from>");
        sb.append("<to>");
        sb.append("<x>").append(to.getX()).append("</x>");
        sb.append("<y>").append(to.getY()).append("</y>");
        sb.append("<z>").append(to.getZ()).append("</z>");
        sb.append("<name>").append(to.getName()).append("</name>");
        sb.append("</to>");
        sb.append("<distance>").append(distance).append("</distance>");
        sb.append("</models.Route>");
        return sb.toString();
    }
}
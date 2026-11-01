package Modules;

import CollectionObject.Coordinates;
import CollectionObject.Route;
import CollectionObject.RouteModel;
import CollectionObject.Location;
import Network.User;

import java.sql.*;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DBProvider{
    private static Connection connection;
//    static String url = "jdbc:postgresql://pg:5432/studs";
//    static String user = "s368598";
//    static String password = "GHV1SVf30QOy6zpk";
    static String url = "jdbc:postgresql://localhost:5432/postgres";
    static String user = "devianted";
    static String password = "";
    private static final Lock lock = new ReentrantLock();

    public static void establishConnection(){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection(){
        try{
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void rollback(){
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkUserExistence(String username){
        DBProvider.establishConnection();
        lock.lock();
        String query = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)";

        try (PreparedStatement p = connection.prepareStatement(query)){

            p.setString(1, username);
            ResultSet res = p.executeQuery();
            if (res.next()){
                closeConnection();
                lock.unlock();
                return res.getBoolean(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            lock.unlock();
            return false;
        }
        closeConnection();
        lock.unlock();
        return false;
    }

    public static boolean checkUserPassword(User user){
        DBProvider.establishConnection();
        lock.lock();
        var username = user.getUsername();
        var hashedPassword = user.getPassword();

        String query = "SELECT hashedpassword FROM users WHERE username = ?";

        try (PreparedStatement p = connection.prepareStatement(query)){

            p.setString(1, username);
            ResultSet res = p.executeQuery();

            if (res.next()){
                String storedHashedPassword = res.getString("hashedpassword");
                closeConnection();
                lock.unlock();
                return storedHashedPassword.equals(hashedPassword);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            lock.unlock();
            return false;
        }
        closeConnection();
        lock.unlock();
        return false;
    }

    public static void addUser(User user){
        DBProvider.establishConnection();
        lock.lock();
        var username = user.getUsername();
        var hashedPassword = user.getPassword();

        String query = "INSERT INTO users (username, hashedpassword) VALUES (?, ?)";

        try (PreparedStatement p = connection.prepareStatement(query)){ //ghjxb

            p.setString(1, username);
            p.setString(2, hashedPassword);
            p.executeUpdate();
            connection.commit();
            closeConnection();

        } catch (SQLException e) {
            rollback();
            closeConnection();
            e.printStackTrace();
        }
        lock.unlock();
    }



    public static void load() {
        CollectionService.elementsCount = loadElCount();

        establishConnection();
        lock.lock();
        CollectionService.collection.clear();

        String query = "SELECT routes.id, routes.name, routes.x, routes.y, routes.creationdate, routes.xlf," +
                "routes.ylf, routes.zlf, routes.namef, routes.xlt, routes.ylt, routes.zlt, routes.namet, routes.distance, users.username FROM routes JOIN users ON users.id = routes.creatorid";
        //String query = "SELECT COUNT(*) FROM users";
        try (PreparedStatement p = connection.prepareStatement(query))
        {
            ResultSet res = p.executeQuery();

            while (res.next()){
                try {
                    long id = res.getLong(1);
                    String name = res.getString(2);
                    float coordX = res.getFloat(3);
                    float coordY = res.getFloat(4);
                    Timestamp creationDate = res.getTimestamp(5); // Проверить формат даты
                    float locFromX = res.getFloat(6);
                    int locFromY = res.getInt(7);
                    float locFromZ = res.getFloat(8);
                    String locFromName = res.getString(9);
                    float locToX = res.getFloat(10);
                    int locToY = res.getInt(11);
                    float locToZ = res.getFloat(12);
                    String locToName = res.getString(13);
                    int distance = res.getInt(14);
                    String user = res.getString(15);

                    // Проверьте порядок и типы параметров конструктора Route
                    var element = new Route(
                            id,
                            name,
                            new Coordinates(coordX, coordY),
                            creationDate,
                            new Location(locFromX, locFromY, locFromZ, locFromName),
                            new Location(locToX, locToY, locToZ, locToName),
                            distance,
                            user
                    );
                    if (checkUserExistence(element.getCreator())){
                        CollectionService.collection.add(element);
                    }


                } catch (IllegalArgumentException e){
                    closeConnection();
                    Server.logger.error("Повреждённый атрибут у элемента с id " + res.getLong(1));
                }
            }
            closeConnection();
        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();
        }
        lock.unlock();
    }

    public static boolean addRoute(RouteModel routeModel){
        DBProvider.establishConnection();
        lock.lock();
        String query = "INSERT INTO routes (name, x, y, creationDate, xlf, ylf, zlf, namef, xlt, ylt, zlt, namet, distance, creatorid)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, (SELECT id FROM users WHERE username = ?))";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, routeModel.getName());
            p.setFloat(2, routeModel.getCoordinates().getX());
            p.setFloat(3, routeModel.getCoordinates().getY());

            long dateInMilliseconds = new Date().getTime();
            p.setTimestamp(4, new Timestamp(dateInMilliseconds));

            p.setFloat(5, routeModel.getFrom().getLX());
            p.setInt(6, routeModel.getFrom().getLY());
            p.setFloat(7, routeModel.getFrom().getLZ());
            p.setString(8, routeModel.getFrom().getName());
            p.setFloat(9, routeModel.getTo().getLX());
            p.setInt(10, routeModel.getTo().getLY());
            p.setFloat(11, routeModel.getTo().getLZ());
            p.setString(12, routeModel.getTo().getName());
            p.setFloat(13, routeModel.getDistance());
            p.setString(14, routeModel.getUser().getUsername());

            p.executeUpdate();
            connection.commit();
            closeConnection();
            lock.unlock();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            rollback();
            closeConnection();
            lock.unlock();
            return false;
        }
    }

    public static boolean updateRoute(User user, long id, RouteModel routeModel){
        DBProvider.establishConnection();
        lock.lock();
        String query = "UPDATE routes SET name = ?, x = ?, y = ?, xlf = ?, ylf = ?, zlf = ?, namef = ?," +
                " xlt = ?, ylt = ?, zlt = ?, namet = ?, distance = ? WHERE (id = ? AND creatorid IN (SELECT id FROM users WHERE username = ?))";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, routeModel.getName());
            p.setFloat(2, routeModel.getCoordinates().getX());
            p.setDouble(3, routeModel.getCoordinates().getY());
            p.setFloat(4, routeModel.getFrom().getLX());
            p.setInt(5, routeModel.getFrom().getLY());
            p.setFloat(6, routeModel.getFrom().getLZ());
            p.setString(7, routeModel.getFrom().getName());
            p.setFloat(8, routeModel.getTo().getLX());
            p.setInt(9, routeModel.getTo().getLY());
            p.setFloat(10, routeModel.getTo().getLZ());
            p.setString(11, routeModel.getTo().getName());
            p.setFloat(12, routeModel.getDistance());
            p.setInt(13, (int) id);
            p.setString(14, user.getUsername());

            p.executeUpdate();
            connection.commit();
            closeConnection();
            lock.unlock();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            rollback();
            closeConnection();
            lock.unlock();
            return false;
        }
    }

    public static boolean removeRouteById(long id){
        DBProvider.establishConnection();
        lock.lock();
        String query = "DELETE FROM routes WHERE id = ?";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setLong(1, id);
            p.executeUpdate();
            connection.commit();
            closeConnection();
            lock.unlock();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            rollback();
            closeConnection();
            lock.unlock();
            return false;
        }
    }

    public static boolean removeGreater(User user, long id){
        DBProvider.establishConnection();
        lock.lock();
        String query = "DELETE FROM routes WHERE (id = ? AND creatorid IN (SELECT id FROM users WHERE username = ?))";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setLong(1, id);
            p.setString(2, user.getUsername());
            p.executeUpdate();
            connection.commit();
            closeConnection();
            lock.unlock();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            rollback();
            closeConnection();
            lock.unlock();
            return false;
        }
    }

    public static int averageOfDistance() {
        DBProvider.establishConnection();
        lock.lock();

        String query = "SELECT AVG(column_name) AS average_value FROM table_name";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet res = p.executeQuery();
            closeConnection();
            lock.unlock();
            return res.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            lock.unlock();
            return 0;
        }
    }

        public static boolean clearRoutes(User user){
        DBProvider.establishConnection();
        lock.lock();
        String query = "DELETE FROM routes WHERE creatorid IN (SELECT id FROM users WHERE username = ?)";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, user.getUsername());
            p.executeUpdate();
            connection.commit();
            closeConnection();
            lock.unlock();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            rollback();
            closeConnection();
            lock.unlock();
            return false;
        }
    }

    public static long loadElCount(){
        DBProvider.establishConnection();
        lock.lock();
        String query = " select last_value from routes_id_seq";

        try (PreparedStatement p = connection.prepareStatement(query)){
            ResultSet res = p.executeQuery();

            if (res.next()){
                closeConnection();
                lock.unlock();
                return res.getLong(1);
            }
            closeConnection();
            lock.unlock();
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            lock.unlock();
            return -1;
        }
    }

}

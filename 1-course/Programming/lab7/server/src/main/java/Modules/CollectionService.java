
package Modules;

import CollectionObject.Route;
import CollectionObject.RouteModel;
import Exceptions.DBProviderException;
import Exceptions.NonExistingElementException;
import Network.User;

import java.util.Comparator;
import java.util.Date;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


public class CollectionService {
    protected static Long elementsCount = 0L;
    private Date initializationDate;
    protected static Stack<Route> collection;
    private CompareRoute comparator;
    private ReentrantLock locker;


    public CollectionService() {
        collection = new Stack<>();
        this.initializationDate = new Date();
        this.comparator = new CompareRoute();
        this.locker = new ReentrantLock();
    }

    private class CompareRoute implements Comparator<Route>{

        @Override
        public int compare(Route o1, Route o2) {
            return (int) (o1.getCoordinates().getX() - o2.getCoordinates().getX());
        }

        @Override
        public Comparator<Route> reversed() {
            return Comparator.super.reversed();
        }
    }

    public Stack<Route> show(){
        DBProvider.load();
        return sortByCoords(collection);
    }

    public String info(){
        return "Тип коллекции: " + collection.getClass() + "\n"
                + "Дата создания: " + initializationDate + "\n"
                + "Количество элементов: " + collection.size();
    }

    public Stack<Route> add(RouteModel element) throws DBProviderException {
        try {
        if (DBProvider.addRoute(element)){
            ++elementsCount;
            Route newElement = new Route(
                    elementsCount,
                    element.getName(),
                    element.getCoordinates(),
                    element.getFrom(),
                    element.getTo(),
                    element.getDistance(),
                    element.getUser().getUsername()
            );
            collection.add(newElement);
            System.out.println("Элемент успешно добавлен");
            return new Stack<>();
        }} catch (Exception e) {
            throw new DBProviderException("произошла ошибка при добавлении элемента");
        }
        return null;
    }

    public  Stack<Route> update(User user, long current_id, RouteModel element) throws DBProviderException {

        try {
            if (DBProvider.updateRoute(user, current_id, element)) {

                for (Route route : collection) {
                    if (current_id == route.getId() && route.getCreator().equals(user.getUsername())) {
                        collection.remove(route);

                        Route newElement = new Route(
                                current_id,
                                element.getName(),
                                element.getCoordinates(),
                                element.getFrom(),
                                element.getTo(),
                                element.getDistance(),
                                element.getUser().getUsername()
                        );

                        collection.add(newElement);
                        break;
                    }
                }
                return new Stack<>();
            }
            throw new DBProviderException("Произошла ошибка во время изменения элемента");
        } finally {

        }
    }

    public  Stack<Route> removeById(User user, long id) throws DBProviderException {

        try {
            if (DBProvider.removeRouteById(id)){
                collection.removeIf(route -> route.getId() == id);
                return new Stack<>();
            }
            throw new DBProviderException("Произошла ошибка при удалении элемента. Возможно элемента с таким id не существует");
        } finally {

        }
    }

    public  Stack<Route> clear(User user) throws DBProviderException {

        try {
            if (DBProvider.clearRoutes(user)){
                collection.removeIf(route -> route.getCreator().equals(user.getUsername()));
                return sortByCoords(collection);
            }
            throw new DBProviderException("произошла ошибка при добавлении элемента");
        } finally {

        }
    }

    public  Stack<Route> removeGreater(User user, long startId) throws NonExistingElementException, DBProviderException {

        try {
            long endId = elementsCount;
            if (startId > endId){
                throw new NonExistingElementException("Элемента с таким id не существует");
            }

            if (DBProvider.removeGreater(user, startId)){
                collection.removeIf(route -> route.getId() >= startId && route.getCreator().equals(user.getUsername()));
                return new Stack<>();
            }
            throw new DBProviderException("Произошла ошибка при удалении элементов");
        } finally {

        }
    }

    public Route head(){
        return collection.firstElement();
    }

//    public  Stack<Route> averageOfDistance(User user, int distance) throws NonExistingElementException, DBProviderException {
//
//        try {
//            var routes = collection.stream().filter(route -> route.getDistance().equals(distance)
//                    && route.getCreator().equals(user.getUsername())).toList();
//            if (routes.isEmpty()){
//                throw new NonExistingElementException("Элементов с таким типом ТС не существует");
//            }
//
//            if (DBProvider.averageOfDistance(user, distance)){
//                collection.removeAll(routes);
//                return new Stack<>();
//            }
//            throw new DBProviderException("Произошла ошибка при удалении элементов");
//        } finally {
//
//        }
//    }

    public double averageOfDistance(User user) throws NonExistingElementException {
        DBProvider.load();
        try {
            var routes = collection.stream()
                    .filter(route -> route.getCreator().equals(user.getUsername()))
                    .toList();

            if (routes.isEmpty()) {
                throw new NonExistingElementException("Маршрутов, созданных пользователем, не существует");
            }

            double averageDistance = routes.stream()
                    .mapToDouble(Route::getDistance)
                    .average()
                    .orElseThrow(() -> new NonExistingElementException("Не удалось вычислить среднюю дистанцию"));

            return averageDistance;
        } finally {

        }
    }


    public int countByDistance(int distance){
        return (int) collection.stream().filter(route -> route.getDistance() > distance).count();
    }

    public Stack<Route> filterStartsWithName(String name) throws NonExistingElementException {
        var filteredCollection = collection.stream().filter(route -> route.getName().startsWith(name)).collect(Collectors.toCollection(Stack::new));

        if (filteredCollection.isEmpty()){
            throw new NonExistingElementException("Элементов с таким именем не существует");
        }
        return sortByCoords(filteredCollection);
    }

    public Stack<Route> filterGreaterThanDistance(int distance) throws NonExistingElementException {
        Stack<Route> filter = collection.stream()
                .filter(route -> route.getDistance() > distance)
                .collect(Collectors.toCollection(Stack::new));

        return filter;
    }

    private synchronized Stack<Route> sortByCoords(Stack<Route> collection){
        return collection.stream().sorted(comparator).collect(Collectors.toCollection(Stack::new));
    }
}
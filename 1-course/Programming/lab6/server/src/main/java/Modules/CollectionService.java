
package Modules;

import CollectionObject.RouteModel;
import CollectionObject.Route;
import Exceptions.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CollectionService{
    protected static Long elementsCount = 0L;
    private Date initializationDate;
    protected static Stack<Route> collection;
    private boolean isReversed = false;


    public CollectionService() {
        collection = new Stack<>();
        this.initializationDate = new Date();
    }

    public Stack<Route> add(RouteModel element){

        elementsCount+=1;
        Route newElement = new Route(
                elementsCount,
                element.getName(),
                element.getCoordinates(),
                element.getFrom(),
                element.getTo(),
                element.getDistance()
        );

        collection.addElement(newElement);
        System.out.println("Элемент успешно добавлен");
        return collection;
    }

    public String info(){
        return "Тип коллекции: " + collection.getClass() + "\n"
                + "Дата создания: " + initializationDate + "\n"
                + "Количество элементов: " + collection.size();
    }

    public Stack<Route> show(){
        return collection;
    }

    public Stack<Route> update(long current_id, RouteModel element) throws NonExistingElementException {
        if (!collection.contains(collection.get((int) current_id))){
            throw new NonExistingElementException("Элемента с таким id не существует");
        }

        for (Route route :collection) {
            if (current_id == route.getId()){
                collection.remove(route);

                Route newElement = new Route(
                        current_id,
                        element.getName(),
                        element.getCoordinates(),
                        element.getFrom(),
                        element.getTo(),
                        element.getDistance()
                );

                collection.add(newElement);
                break;
            }
        }
        return collection;
    }

    public Stack<Route> removeById(long id) throws NonExistingElementException {
        if (!collection.removeIf(vehicle -> vehicle.getId() == id)){
            throw new NonExistingElementException("Элемента с таким id не существует");
        }
        return collection;
    }

    public Stack<Route> clear(){
        collection.clear();
        return collection;
    }

    public Stack<Route> removeGreater(long startId) throws NonExistingElementException {
        long endId = collection.size();
        if (startId > endId){
            throw new NonExistingElementException("Элемента с таким id не существует");
        }

        collection.removeIf(vehicle -> vehicle.getId() >= startId);
        return collection;
    }

    public Route head(){
        return collection.firstElement();
    }

//    public String averageOfDistance(){
//        if (!collection.isEmpty()) {
//            int totalDistance = 0;
//            for (Route route : collection) {
//                totalDistance += route.getDistance();
//            }
//            double averageDistance = (double) totalDistance / collection.size();
//            return String.valueOf(averageDistance);
//        } else {
//            return "коллекция пуста";
//        }
//    }
    public String averageOfDistance() {
        if (collection.isEmpty()) {
            return "коллекция пуста";
        }
        double averageDistance = collection.stream()
                .mapToInt(Route::getDistance)
                .average()
                .orElse(0.0);
        return String.valueOf(averageDistance);
    }

    public long countByDistance(int distance){
        long counter;
        Stream<Route> stream = collection.stream();
        counter = stream.filter(x-> x.getDistance() == distance).count();
        return counter;
    }

//    public Stack<Route> filterGreaterThanDistance(int distance){
//        boolean hasItem = false;
//        Stack<Route> filter = new Stack<>();
//        for (Route el : collection) {
//            if (el.getDistance() > distance) {
//                hasItem = true;
//                break;
//            }
//        }
//        if (hasItem){
//            for (Route route : collection) {
//                if (route.getDistance() > distance){
//                    filter.add(route);
//                }
//            }
//        }
//        return filter;
//    }
    public Stack<Route> filterGreaterThanDistance(int distance) {
        Stack<Route> filter = collection.stream()
                .filter(route -> route.getDistance() > distance)
                .collect(Collectors.toCollection(Stack::new));

        return filter;
    }
}

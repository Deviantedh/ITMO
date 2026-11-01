import models.Coordinates;
import models.Location;
import models.Route;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class RouteManager {
    private static LinkedList<Route> routeCollection;
    private Deque<String> commandHistory;

    public RouteManager() {
        routeCollection = new LinkedList<>();
        commandHistory = new LinkedList<>();
    }

    public LinkedList<Route> getRouteCollection() {
        return routeCollection;
    }

    // Метод для считывания данных из файла и заполнения коллекции
    public void loadRoutesFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Route route = Route.fromString(line);
                if (route != null)
                    routeCollection.add(route);
            System.out.println("Collection loaded");
            }
        } catch (IOException e) {
            System.err.println("Error loading routes from file: " + e.getMessage());
        }
    }

    // Метод для сохранения данных в файл в формате XML
    public void saveRoutesToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            for (Route route : routeCollection) {
                writer.write(route.toXmlString());
                writer.newLine();
            }
            System.out.println("Collection Saved");
        } catch (IOException e) {
            System.err.println("Error saving routes to file: " + e.getMessage());
        }
    }

    public void help() {
        System.out.println("Available commands:");
        System.out.println("help: display help");
        System.out.println("info: display information about the collection");
        System.out.println("show: display all routes");
        System.out.println("add {id} {name} {distance}: add a new route");
        System.out.println("update {id} {name} {distance}: update route by id");
        System.out.println("remove_by_id {id}: remove route by id");
        System.out.println("clear: clear the collection");
        System.out.println("save: save routes to file");
        System.out.println("execute_script {file_name}: execute commands from script file");
        System.out.println("remove_greater {distance}: remove routes greater than specified");
        System.out.println("count_by_distance {distance}: count routes by distance");
        System.out.println("filter_greater_than_distance {distance}: display routes with distance greater than specified");
        System.out.println("exit: exit the program");
    }

    // Метод для добавления нового маршрута
    public void addRoute(String[] string) {
        Route route = new Route(Long.parseLong(string[1]),
                    string[2], Integer.valueOf(string[3]));
        setCoord(route);
        routeCollection.add(route);
    }

    public void setCoord(Route route){
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Enter from");
        System.out.println("x:");
        input = scanner.nextLine().trim();
        Float x = Float.parseFloat(input);
        System.out.println("y:");
        input = scanner.nextLine().trim();
        Float y = Float.parseFloat(input);
        System.out.println("z:");
        input = scanner.nextLine().trim();
        double z = Double.parseDouble(input);
        System.out.println("name:");
        input = scanner.nextLine().trim();
        String name = input;
        route.setFrom(new Location(x,y,z,name));

        System.out.println("Enter to");
        System.out.println("x:");
        input = scanner.nextLine().trim();
        x = Float.parseFloat(input);
        System.out.println("y:");
        input = scanner.nextLine().trim();
        y = Float.parseFloat(input);
        System.out.println("z:");
        input = scanner.nextLine().trim();
        z = Double.parseDouble(input);
        System.out.println("name:");
        input = scanner.nextLine().trim();
        name = input;
        route.setTo(new Location(x,y,z,name));

        System.out.println("Enter coordinates");
        System.out.println("x:");
        input = scanner.nextLine().trim();
        x = Float.parseFloat(input);
        System.out.println("y:");
        input = scanner.nextLine().trim();
        y = Float.parseFloat(input);
        route.setCoordinates(new Coordinates(x,y));
    }

    // Метод для удаления маршрута по его id
    public void removeRoute(long id) {
        routeCollection.removeIf(route -> route.getId() == id);
    }

    public void updateRoute(long id, String[] param){
        for (Route route : routeCollection) {
            if (route.getId() == id) {
                route.setName(param[2]);
                route.setDistance(Integer.valueOf(param[3]));
                setCoord(route);
                break; // Если элемент найден, выходим из цикла
            }
        }
    }

    // Метод для очистки коллекции
    public void clearRoutes() {
        routeCollection.clear();
        System.out.println("Collection cleared.");
    }

    // Метод для вывода информации о коллекции
    public void printCollectionInfo() {
        System.out.println("Collection type: LinkedList");
        System.out.println("Initialization date: " + ZonedDateTime.now());
        System.out.println("Number of elements: " + routeCollection.size());
    }

    // Метод для вывода всех маршрутов в консоль
    public void displayRoutes() {
        if (!routeCollection.isEmpty()) {
            for (Route route : routeCollection) {
                int x = 0;
                int y = x++ + 1;
                System.out.println("Маршрут №" + y);
                System.out.println(route);
            }
        } else {
            System.out.println("Collection is empty.");
        }
    }

    // Метод для вывода первого элемента коллекции
    public void displayHead() {
        if (!routeCollection.isEmpty()) {
            System.out.println(routeCollection.getFirst());
        } else {
            System.out.println("Collection is empty.");
        }
    }

    // Метод для сохранения последних пяти команд в историю
    public void updateCommandHistory(String command) {
        commandHistory.offer(command);
        if (commandHistory.size() > 5) {
            commandHistory.poll();
        }
    }

    public void removeGreater(int dist) {
        int counter = 0;
        for (Route route : routeCollection) {
            if (route.getDistance() < dist) {
                routeCollection.remove(route);
                counter += 1;
            }
        }
        System.out.println("Operation was finished successfully. " + counter + " elements were deleted.");
    }

    // Метод для вывода истории команд
    public void printCommandHistory() {
        System.out.println("Command history:");
        for (String command : commandHistory) {
            System.out.println(command);
        }
    }

    // Метод для вычисления среднего значения поля distance
    public void printAverageDistance() {
        if (!routeCollection.isEmpty()) {
            int totalDistance = 0;
            for (Route route : routeCollection) {
                totalDistance += route.getDistance();
            }
            double averageDistance = (double) totalDistance / routeCollection.size();
            System.out.println("Average distance: " + averageDistance);
        } else {
            System.out.println("Collection is empty.");
        }
    }

    // Метод для подсчета количества элементов с заданным значением поля distance
    public void countRoutesByDistance(int distance) {
        long count = routeCollection.stream().filter(route -> route.getDistance() == distance).count();
        System.out.println("Number of routes with distance " + distance + ": " + count);
    }

    // Метод для фильтрации элементов по значению поля distance
    public void filterRoutesByDistance(int distance) {
        List<Route> filteredRoutes = new ArrayList<>();
        for (Route route : routeCollection) {
            if (route.getDistance() > distance) {
                filteredRoutes.add(route);
            }
        }
        if (!filteredRoutes.isEmpty()) {
            System.out.println("Routes with distance greater than " + distance + ":");
            for (Route route : filteredRoutes) {
                System.out.println(route);
            }
        } else {
            System.out.println("No routes with distance greater than " + distance + ".");
        }
    }

    public void executeScript(String nameOfFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(nameOfFile));
            String[] finalUserCommand;
            String command;
            while((command = reader.readLine()) != null) {
                finalUserCommand = command.trim().toLowerCase().split(" ", 2);
                switch (finalUserCommand[0]) {
                    case "help":
                        help();
                        break;
                    case "info":
                        printCollectionInfo();
                        break;
                    case "show":
                        displayRoutes();
                        break;
                    case "add":
                        addRoute(finalUserCommand);
                        break;
                    case "update":
                        updateRoute(Long.parseLong(finalUserCommand[1]), finalUserCommand);
                        break;
                    case "remove_by_id":
                        removeRoute(Long.parseLong(finalUserCommand[1]));
                        break;
                    case "clear":
                        clearRoutes();
                        System.out.println("Collection cleared.");
                        break;
                    case "save":
                        saveRoutesToFile("routes.xml");
                        break;
                    case "execute_script":
                        System.out.println("This script cannot to contain this command.");
                        break;
                    case "exit":
                        System.out.println("Exiting...");
                        return;
                    case "head":
                        displayHead();
                        break;
                    case "remove_greater":
                        removeGreater(Integer.parseInt(finalUserCommand[1]));
                        break;
                    case "history":
                        printCommandHistory();
                        break;
                    case "average_of_distance":
                        printAverageDistance();
                        break;
                    case "count_by_distance":
                        countRoutesByDistance(Integer.parseInt(finalUserCommand[1]));
                        break;
                    case "filter_greater_than_distance":
                        filterRoutesByDistance(Integer.parseInt(finalUserCommand[1]));
                        break;
                    default:
                        reader.readLine();
                        break;
                }
                System.out.println("Command is ended.");
            }
            System.out.println("Commands are ended.");
            reader.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found. Try again.");
        } catch (IOException ioException) {
            System.out.println("File reading exception. Try again.");
        }
    }
}
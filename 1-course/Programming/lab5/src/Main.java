import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        if (args.length != 1) {
            System.out.println("Usage: java RouteManager <filename>");
            return;
        }

        RouteManager routeManager = new RouteManager();
        routeManager.loadRoutesFromFile(args[0]);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim();
            routeManager.updateCommandHistory(input);

            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();
            switch (command) {
                case "help":
                    routeManager.help();
                    break;
                case "info":
                    routeManager.printCollectionInfo();
                    break;
                case "show":
                    routeManager.displayRoutes();
                    break;
                case "add":
                    routeManager.addRoute(parts);
                    break;
                case "update":
                    routeManager.updateRoute(Long.parseLong(parts[1]), parts);
                    break;
                case "remove_by_id":
                    routeManager.removeRoute(Long.parseLong(parts[1]));
                    break;
                case "clear":
                    routeManager.clearRoutes();
                    System.out.println("Collection cleared.");
                    break;
                case "save":
                    routeManager.saveRoutesToFile("routes1.xml");
                    break;
                case "execute_script":
                    routeManager.executeScript(parts[1]);
                    break;
                case "exit":
                    System.out.println("Exiting...");
                    return;
                case "head":
                    routeManager.displayHead();
                    break;
                case "remove_greater":
                    routeManager.removeGreater(Integer.parseInt(parts[1]));
                    break;
                case "history":
                    routeManager.printCommandHistory();
                    break;
                case "average_of_distance":
                    routeManager.printAverageDistance();
                    break;
                case "count_by_distance":
                    routeManager.countRoutesByDistance(Integer.parseInt(parts[1]));
                    break;
                case "filter_greater_than_distance":
                    routeManager.filterRoutesByDistance(Integer.parseInt(parts[1]));
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
    }
}
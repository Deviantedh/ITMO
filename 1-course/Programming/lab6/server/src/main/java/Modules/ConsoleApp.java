package Modules;

import CollectionObject.RouteModel;
import Commands.Command;
import Network.Response;

import java.util.HashMap;

public class ConsoleApp {
    // хэшмапа команд. Ключ - имя команды; Значение - класс-оболочка команды
    public static HashMap<String, Command> commandList = new HashMap<>();
    private Command help;
    private Command info;
    private Command show;
    private Command add;
    private Command update;
    private Command removeById;
    private Command clear;
    private Command removeGreater;
    private Command head;
    private Command history;
    private Command averageOfDistance;
    private Command countByDistance;
    private Command filterGreaterThanDistance;

    public ConsoleApp(Command help, Command info, Command show, Command add, Command update, Command removeById,
                      Command clear, Command removeGreater, Command head, Command history, Command averageOfDistance, Command countByDistance,
                      Command filterStartsWithName) {
        this.help = help;
        this.info = info;
        this.show = show;
        this.add = add;
        this.update = update;
        this.removeById = removeById;
        this.clear = clear;
        this.removeGreater = removeGreater;
        this.head = head;
        this.history = history;
        this.averageOfDistance = averageOfDistance;
        this.countByDistance = countByDistance;
        this.filterGreaterThanDistance = filterStartsWithName;
    }

    public Response help(String arguments, RouteModel objectArg){
        return help.execute(arguments, objectArg);
    }

    public Response info(String arguments, RouteModel objectArg){
        return info.execute(arguments, objectArg);
    }

    public Response show(String arguments, RouteModel objectArg){
        return show.execute(arguments, objectArg);
    }

    public Response add(String arguments, RouteModel objectArg){
        return add.execute(arguments, objectArg);
    }

    public Response update(String arguments, RouteModel objectArg){
        return update.execute(arguments, objectArg);
    }

    public Response removeById(String arguments, RouteModel objectArg){
        return removeById.execute(arguments, objectArg);
    }

    public Response clear(String arguments, RouteModel objectArg){
        return clear.execute(arguments, objectArg);
    }

    public Response removeGreater(String arguments, RouteModel objectArg){
        return removeGreater.execute(arguments, objectArg);
    }

    public Response head(String arguments, RouteModel objectArg){
        return head.execute(arguments, objectArg);
    }

    public Response history(String arguments, RouteModel objectArg){
        return history.execute(arguments, objectArg);
    }

    public Response averageOfDistance(String arguments, RouteModel objectArg){
        return averageOfDistance.execute(arguments, objectArg);
    }

    public Response countByDistance(String arguments, RouteModel objectArg){
        return countByDistance.execute(arguments, objectArg);
    }

    public Response filterGreaterThanDistance(String arguments, RouteModel objectArg){
        return filterGreaterThanDistance.execute(arguments, objectArg);
    }
}

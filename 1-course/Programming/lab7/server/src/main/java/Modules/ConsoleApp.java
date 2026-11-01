package Modules;

import CollectionObject.RouteModel;
import Commands.Command;
import Network.Response;
import Network.User;

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
                      Command clear, Command removeGreater, Command head, Command history, Command averageOfDistance,
                      Command countByDistance, Command filterGreaterThanDistance){
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
        this.filterGreaterThanDistance = filterGreaterThanDistance;
    }

    public Response help(User user, String arguments, RouteModel objectArg) {
        return help.execute(user, arguments, objectArg);
    }

    public Response info(User user, String arguments, RouteModel objectArg) {
        return info.execute(user, arguments, objectArg);
    }

    public Response show(User user, String arguments, RouteModel objectArg) {
        return show.execute(user, arguments, objectArg);
    }

    public Response add(User user, String arguments, RouteModel objectArg) {
        return add.execute(user, arguments, objectArg);
    }

    public Response update(User user, String arguments, RouteModel objectArg) {
        return update.execute(user, arguments, objectArg);
    }

    public Response removeById(User user, String arguments, RouteModel objectArg) {
        return removeById.execute(user, arguments, objectArg);
    }

    public Response clear(User user, String arguments, RouteModel objectArg) {
        return clear.execute(user, arguments, objectArg);
    }

    public Response removeGreater(User user, String arguments, RouteModel objectArg) {
        return removeGreater.execute(user, arguments, objectArg);
    }

    public Response head(User user, String arguments, RouteModel objectArg) {
        return head.execute(user, arguments, objectArg);
    }

    public Response history(User user, String arguments, RouteModel objectArg) {
        return history.execute(user, arguments, objectArg);
    }

    public Response averageOfDistance(User user, String arguments, RouteModel objectArg) {
        return averageOfDistance.execute(user, arguments, objectArg);
    }

    public Response countByDistance(User user, String arguments, RouteModel objectArg) {
        return countByDistance.execute(user, arguments, objectArg);
    }

    public Response filterGreaterThanDistance(User user, String arguments, RouteModel objectArg) {
        return filterGreaterThanDistance.execute(user, arguments, objectArg);
    }
}

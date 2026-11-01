package Commands;

import CollectionObject.RouteModel;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class AverageOfDistanceCommand implements Command{
    private CommandHandler commandHandler;

    public AverageOfDistanceCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("averageOfDistance", this);
    }

    @Override
    public Response execute(String arguments, RouteModel objectArg) {
        return commandHandler.averageOfDistance(arguments, objectArg);
    }
}

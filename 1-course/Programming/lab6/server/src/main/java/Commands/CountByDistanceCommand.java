package Commands;

import CollectionObject.RouteModel;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class CountByDistanceCommand implements Command {
    private CommandHandler commandHandler;

    public CountByDistanceCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("countByDistance", this);
    }

    @Override
    public Response execute(String arguments, RouteModel objectArg) {
        return commandHandler.countByDistance(arguments, objectArg);
    }
}

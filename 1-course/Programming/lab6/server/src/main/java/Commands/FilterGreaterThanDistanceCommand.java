package Commands;

import CollectionObject.RouteModel;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class FilterGreaterThanDistanceCommand implements Command{
    private CommandHandler commandHandler;

    public FilterGreaterThanDistanceCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("filterGreaterThanDistance", this);
    }

    @Override
    public Response execute(String arguments, RouteModel objectArg) {
        return commandHandler.filterGreaterThanDistance(arguments, objectArg);
    }
}

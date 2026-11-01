package Commands;

import CollectionObject.RouteModel;
import Modules.CommandHandler;
import Modules.ConsoleApp;
import Network.Response;

public class HeadCommand implements Command{
    private CommandHandler commandHandler;

    public HeadCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        ConsoleApp.commandList.put("head", this);
    }

    @Override
    public Response execute(String arguments, RouteModel objectArg) {
        return commandHandler.head(arguments, objectArg);
    }
}

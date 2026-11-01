package Commands;

import CollectionObject.RouteModel;
import Network.Response;

public interface Command {
    Response execute(String strArgument, RouteModel objArgument);
}

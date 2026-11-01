package Commands;

import CollectionObject.RouteModel;
import Network.Response;
import Network.User;

public interface Command {
    Response execute(User user, String strArgument, RouteModel objArgument);
}

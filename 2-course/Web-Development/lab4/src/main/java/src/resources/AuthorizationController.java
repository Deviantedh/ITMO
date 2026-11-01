package src.resources;


import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import src.Beans.UserService;

@Path("/authorization")
public class AuthorizationController {
    @EJB
    UserService userService;

    @GET
    @Path("/authorize")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorize(@QueryParam("username") String username,
                                       @QueryParam("password") String password) {
        String result = userService.authorizeUser(username, password);
        return Response.ok(result).build();
    }

    @GET
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@QueryParam("username") String username,
                              @QueryParam("password") String password) {
        String result = userService.registerUser(username, password);
        return Response.ok(result).build();
    }
}
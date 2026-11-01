package src.Beans;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import src.DAOServices.UserDAO;
import src.Utils.PasswordHash;
import src.Utils.TokenGenerator;
import src.entities.User;


@Stateless
public class UserService {
    //UserDAO userDAO = new UserDAO();
    //private final TokenGenerator tokenGenerator = new TokenGenerator();
    //private final PasswordHash passwordHash = new PasswordHash();
    @Inject
    private UserDAO userDAO;

    @Inject
    private TokenGenerator tokenGenerator;

    @Inject
    private PasswordHash passwordHash;

    public String registerUser(String username, String password) {
        if(userDAO.userExists(username))
            return "user already exists";
        User user = new User();
        user.setUsername(username);
        String hashedPassword = passwordHash.toSHA384(password);
        user.setPassword(hashedPassword);
        String token = tokenGenerator.getNewToken();
        user.setToken(token);
        userDAO.saveUser(user);
        int id = userDAO.getUserID(username);
        return "Authorized:"+id+"%"+token;
    }

    public String authorizeUser(String username, String password){
        if(!userDAO.userExists(username))
            return "Invalid username";
        String hashedPassword = passwordHash.toSHA384(password);
        if(!userDAO.userPasswordMatches(username, hashedPassword))
            return "Invalid password";
        int id = userDAO.getUserID(username);
        String token = tokenGenerator.getNewToken();
        userDAO.saveToken(id, token);
        return "Authorized:"+id+"%"+token;
    }

    public boolean validateAuthorizedUser(String id, String token){
        int id_=-1;
        try{
            id_ = Integer.parseInt(id);
        }catch (NumberFormatException e){
            return false;
        }
        return userDAO.validateAuthorizedUser(id_, token);
    }

    public void deleteUserById(String id){
        int id_=-1;
        try{
            id_ = Integer.parseInt(id);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        userDAO.deleteUserById(id_);
    }

}

package app.carport.Persistence;

import app.carport.Entities.Carport;
import app.carport.Entities.User;

public class UserMapper {
    public User login(String email, String password, ConnectionPool connectionPool) {
        return null;
    }

    public boolean register(String email, String password, ConnectionPool connectionPool) {
        return false;
    }

    public boolean doesUserExist(String email, ConnectionPool connectionPool) {
        return false;
    }

    public boolean orderCarport(User user, Carport carport, ConnectionPool connectionPool) {
        if (user != null) {
            // Purchase order without user null check.
            return false;
        }
        return false;
    }
}
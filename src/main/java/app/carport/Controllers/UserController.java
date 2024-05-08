package app.carport.Controllers;

import app.carport.Entities.Material;
import app.carport.Entities.Order;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.MaterialMapper;
import app.carport.Persistence.OrderMapper;
import app.carport.Persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

import static app.carport.Persistence.UserMapper.createUser;


public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
        app.post("createUser", ctx -> createUser(ctx, false, connectionPool));
        app.get("/getUserCompleteData", ctx -> renderOrdrer(ctx, connectionPool));
        app.post("updateUser", ctx -> updateUser(ctx, connectionPool));
    }

    public static void createUser(Context ctx, boolean isadmin, ConnectionPool connectionPool) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String role = isadmin ? "admin" : "bruger";

        try {
            if (!UserMapper.checkIfUserExistsByName(username, connectionPool)) {
               // UserMapper.createUser(username, password, role, connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med brugernavn: " + username + ". Nu skal du logge på");
                ctx.render("bestilling1.html");
            } else {
                ctx.attribute("message", "Brugernavnet eksisterer allerede. Vælg venligst et andet brugernavn.");
                ctx.render("login.html");
            }
        } catch (DatabaseException e) {
            ctx.attribute("message", "Der opstod en fejl under oprettelsen. Prøv venligst igen.");
            ctx.render("index.html");
        }
    }

    public static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String mail = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(mail, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.render("index.html");

        } catch (DatabaseException e) {
            //hvis nej send tilbage til login side med fejl
            ctx.attribute("message", "Forkert login, Prøv venligst igen.");
            ctx.render("login.html");
        }
    }
    public static void renderOrdrer(Context ctx, ConnectionPool connectionPool) {
        try {
            User user = ctx.sessionAttribute("currentuser");
            ctx.render("ordrer.html");

        } catch (RuntimeException e) {
            ctx.attribute("message", "An error occurred while fetching the user data.");
            ctx.render("login.html"); // Render an error page
        }
    }
    public static void updateUser(Context ctx, ConnectionPool connectionPool) {
        // Retrieve form data
        String firstName = ctx.formParam("firstName");
        String lastName = ctx.formParam("lastName");
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        // Get the current user from the session
        User currentUser = ctx.sessionAttribute("currentUser");

        // Create a new User object with the updated information
        User updatedUser = new User(currentUser.getUserID(), email, password, currentUser.isAdmin(), firstName, lastName, currentUser.getAdress());

        try {
            // Update the user in the database
            UserMapper.updateUser(updatedUser, connectionPool);

            // Update the user in the session
            ctx.sessionAttribute("currentUser", updatedUser);

            // Redirect to a success page
            ctx.render("ordrer.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "An error occurred while updating the user information.");
            ctx.render("ordrer.html");
        }
    }


}

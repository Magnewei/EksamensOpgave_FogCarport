package app.carport.Controllers;

import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import static app.carport.Persistence.UserMapper.createUser;

//import static app.carport.Controllers.CarportController.reRenderCupcakeShop; TODO fix

public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("createUser", ctx -> ctx.render("index.html"));
        app.post("createUser", ctx -> createUser(ctx, false, connectionPool));
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
    }



    public static void createUser(Context ctx, boolean isadmin, ConnectionPool connectionPool) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String role = isadmin ? "admin" : "bruger";

        try {
            if (!UserMapper.checkIfUserExistsByName(username, connectionPool)) {
                UserMapper.createUser(username, password, role, connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med brugernavn: " + username + ". Nu skal du logge på");
                ctx.render("index.html");
            } else {
                ctx.attribute("message", "Brugernavnet eksisterer allerede. Vælg venligst et andet brugernavn.");
                ctx.render("index.html");
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
}

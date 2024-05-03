package app.carport.Controllers;

<<<<<<< HEAD
import io.javalin.Javalin;
import io.javalin.http.Context;
=======
>>>>>>> master
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.UserMapper;
<<<<<<< HEAD

import static app.carport.Controllers.OrderCarportController.reRenderBestilling;


public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("createadmin", ctx -> ctx.render("login.html"));
        app.get("createuser", ctx -> ctx.render("login.html"));
        app.post("createuser", ctx -> createUser(ctx, true, connectionPool));
=======
import io.javalin.Javalin;
import io.javalin.http.Context;

import static app.carport.Persistence.UserMapper.createUser;

//import static app.carport.Controllers.CarportController.reRenderCupcakeShop; TODO fix

public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("createadmin", ctx -> ctx.render("createadmin.html"));
        app.get("createuser", ctx -> ctx.render("createuser.html"));
        /*
        app.post("createuser", ctx -> createUser(ctx, true, connectionPool));
        app.post("createadmin", ctx -> createUser(ctx, false, connectionPool));

         */
>>>>>>> master
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
    }

<<<<<<< HEAD
    private static void createUser(Context ctx, boolean isadmin, ConnectionPool connectionPool) throws DatabaseException {
=======
    /*

    public static void createUser(Context ctx, boolean isadmin, ConnectionPool connectionPool) {
>>>>>>> master
        String username = ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        String role = isadmin ? "bruger" : "admin";

        if (password1.equals(password2)) {
            try {
<<<<<<< HEAD
                if (!UserMapper.checkIfUserExistsByName(username, connectionPool)) {
                    UserMapper.createUser(username, password1, role, connectionPool);
                    ctx.attribute("message", "Du er hermed oprettet med brugernavn: " + username + ". Nu skal du logge på");
                    ctx.render("login.html");
                } else {
                    ctx.attribute("message", "ERROR 404 .");
                    ctx.render("login.html");
                }
            } catch (DatabaseException e) {
                ctx.attribute("message", "Der opstod en fejl under oprettelsen. Prøv venligst igen.");
                ctx.render("login.html");
            }
        } else {
            ctx.attribute("message", "Der er fejl ved din adgangskode. Prøv venligst igen.");
            ctx.render("login.html");
        }
    }

    private static void logout(Context ctx) {
=======
                if (!UserMapper.checkIfUserExistsByName(username, connectionPool)) {  // TODO: Fix
                    UserMapper.createUser(username, password1, role, connectionPool);
                    ctx.attribute("message", "Du er hermed oprettet med brugernavn: " + username + ". Nu skal du logge på");
                    ctx.render("index.html");
                } else {
                    ctx.attribute("message", "Brugernavnet eksisterer allerede. Vælg venligst et andet brugernavn.");
                    ctx.render("createuser.html");
                }
            } catch (DatabaseException e) {
                ctx.attribute("message", "Der opstod en fejl under oprettelsen. Prøv venligst igen.");
                ctx.render("createuser.html");
            }
        } else {
            ctx.attribute("message", "Der er fejl ved din adgangskode. Prøv venligst igen.");
            ctx.render("createuser.html");
        }
    }

  */

    public static void logout(Context ctx) {
>>>>>>> master
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String mail = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(mail, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
<<<<<<< HEAD
            reRenderBestilling(ctx, connectionPool, "");
=======
            //reRenderCupcakeShop(ctx, connectionPool, ""); TODO: Fix
>>>>>>> master

        } catch (DatabaseException e) {
            //hvis nej send tilbage til login side med fejl
            ctx.attribute("message", "Forkert login, Prøv venligst igen.");
<<<<<<< HEAD
            ctx.render("login.html");
=======
            ctx.render("index.html");
>>>>>>> master
        }
    }
}

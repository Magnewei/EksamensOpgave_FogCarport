package app.carport.Controllers;

import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class HeaderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("loadadmin", ctx -> goToAdmin(connectionPool, ctx));
        app.post("goToLogin", ctx -> goToLogin(ctx));
        app.post("goToOrdrer", ctx -> goToOrdrer(ctx, connectionPool));
        app.post("goToIndex", ctx -> goToIndex(ctx));

    }

    private static void goToAdmin(ConnectionPool connectionPool, Context ctx) {
        try {
            AdminPanelController.renderAdmin(connectionPool,ctx);
        } catch (NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("admin.html");
        }
    }

    public static void goToLogin(Context ctx) {

            ctx.render("login.html");
        }



    public static void goToOrdrer(Context ctx, ConnectionPool connectionPool) {
        try {
            UserController.renderOrdrer(ctx, connectionPool);
        } catch (NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("ordrer.html");
        }
    }


    public static void goToIndex(Context ctx) {
           ctx.render("index.html");
        }
    }

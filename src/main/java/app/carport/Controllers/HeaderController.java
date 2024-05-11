package app.carport.Controllers;

import app.carport.Persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import static app.carport.Controllers.UserController.logout;

public class HeaderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("loadadmin", ctx -> goToAdmin(connectionPool, ctx));
        app.post("goToLogin", ctx -> goToLogin(ctx));
        app.post("goToUserSite", ctx -> goToUserSite(ctx, connectionPool));
        app.post("goToIndex", ctx -> goToIndex(ctx));
        app.post("logout", ctx -> logout(ctx));
    }

    private static void goToAdmin(ConnectionPool connectionPool, Context ctx) {
            AdminPanelController.renderAdmin(connectionPool, ctx);
    }

    public static void goToLogin(Context ctx) {
            ctx.render("login.html");
    }

    public static void goToUserSite(Context ctx, ConnectionPool connectionPool) {
            try {
                UserController.renderUserSite(ctx, connectionPool);
            } catch (Exception e) {
                ctx.attribute("message", "De skal være logget ind for at se dine ordrer");
                ctx.render("login.html");
            }
    }

    public static void goToIndex(Context ctx) {
        ctx.render("index.html");
    }
}

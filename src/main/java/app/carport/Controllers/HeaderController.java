package app.carport.Controllers;

import app.carport.Persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import static app.carport.Controllers.UserController.logout;

public class HeaderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("loadadmin", ctx -> goToAdmin(connectionPool, ctx));
        app.post("goToLogin", ctx -> goToLogin(ctx));
        app.post("goToOrdrer", ctx -> goToOrder(ctx, connectionPool));
        app.post("goToIndex", ctx -> goToIndex(ctx));
        app.post("/logout", ctx -> logout(ctx));
    }

    private static void goToAdmin(ConnectionPool connectionPool, Context ctx) {
            AdminPanelController.renderAdmin(connectionPool, ctx);
    }

    public static void goToLogin(Context ctx) {
            ctx.render("login.html");
    }

    public static void goToOrdrer(Context ctx, ConnectionPool connectionPool) {
            UserController.renderOrdrer(ctx, connectionPool);
    }

    public static void goToOrder(Context ctx, ConnectionPool connectionPool) {
            UserController.renderOrder(ctx, connectionPool);
    }

    public static void goToIndex(Context ctx) {
        ctx.render("index.html");
    }
}

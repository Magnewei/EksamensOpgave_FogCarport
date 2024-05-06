package app.carport.Controllers;

import app.carport.Persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class HeaderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("loadadmin", ctx -> gotoAdmin(connectionPool, ctx));
    }

    private static void gotoAdmin(ConnectionPool connectionPool, Context ctx) {
        try {
            AdminPanelController.renderAdmin(connectionPool,ctx);
        } catch (NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }
    }

    public static void gotoShop(ConnectionPool connectionPool) {
    }

    public static void gotoCreateUser(ConnectionPool connectionPool) {
    }

    public static void gotoLogin(ConnectionPool connectionPool) {
    }

    public static void gotoOrder(ConnectionPool connectionPool) {
    }
}

package app.carport.Controllers;

import app.carport.Entities.User;
import app.carport.Persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class HeaderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("loadadmin", ctx -> goToAdmin(connectionPool, ctx));
        app.post("goToLogin", ctx -> goToLogin(ctx));
        app.post("goToUserSite", ctx -> goToUserSite(ctx, connectionPool));
        app.post("goToIndex", ctx -> goToIndex(ctx));
        app.post("logout", ctx -> UserController.logout(ctx));
        app.post("loadCustomerChat", ctx -> loadCustomerChat(ctx));
    }


    private static void loadCustomerChat(Context ctx) {
        String username = ctx.formParam("tempUsername");

        if (ctx.sessionAttribute("currentUser") != null) {
            User user = ctx.sessionAttribute("currentUser");
            ctx.sessionAttribute("customerUsername", ((User) ctx.sessionAttribute("currentUser")).getFullName());
            ctx.render("chat.html");
            return;
        }

        if (username == null || username.isEmpty()) {
            ctx.attribute("message", "You need to type a name into the input field, before you can load the chat.");
            ctx.render("index.html");
            return;
        }

        User user = new User(username, " ");
        ctx.sessionAttribute("currentUser", user);
        ctx.sessionAttribute("customerUsername", user.getFullName());
        ctx.sessionAttribute("adminName", "admin");
        ctx.render("chat.html");
    }

    /**
     * Redirects to the admin panel view by rendering it.
     *
     * @param connectionPool Connection pool for database connections.
     * @param ctx            Context for handling the request.
     */
    private static void goToAdmin(ConnectionPool connectionPool, Context ctx) {
        AdminPanelController.renderAdmin(connectionPool, ctx);
    }

    /**
     * Redirects to the login page view.
     *
     * @param ctx Context for handling the request.
     */
    public static void goToLogin(Context ctx) {
        ctx.render("login.html");
    }

    /**
     * Redirects to the user site page view, handling any required session validation.
     *
     * @param ctx            Context for handling the request.
     * @param connectionPool Connection pool for database connections.
     */
    public static void goToUserSite(Context ctx, ConnectionPool connectionPool) {
        try {
            UserController.renderUserSite(ctx, connectionPool);
        } catch (Exception e) {
            ctx.attribute("message", "De skal være logget ind for at se dine ordrer");
            ctx.render("login.html");
        }
    }

    /**
     * Redirects to the home page view.
     *
     * @param ctx Context for handling the request.
     */
    public static void goToIndex(Context ctx) {
        ctx.render("index.html");
    }
}

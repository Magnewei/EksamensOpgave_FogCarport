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
        app.post("loadCustomerChat", ctx -> loadCustomerChat(ctx));
    }


    private static void loadCustomerChat(Context ctx) {
        // Loads a username from the header input field.
        // The input field is only loaded if it hasn't already been set or currentUser == null.
        String username = ctx.formParam("tempUsername");
        ctx.cachedSessionAttribute("chatUsername", username);
        ctx.render("customerChat.html");
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

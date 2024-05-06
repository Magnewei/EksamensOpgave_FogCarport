package app.carport.Controllers;

import io.javalin.http.Context;
import app.carport.Persistence.ConnectionPool;

public class OrderController {
    public static void renderBestilling(Context ctx, ConnectionPool connectionPool, String message) {
        ctx.attribute("message", message);
        ctx.render("bestilling.html");
    }
}

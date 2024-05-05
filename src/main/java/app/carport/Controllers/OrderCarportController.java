package app.carport.Controllers;

import io.javalin.http.Context;
import app.carport.Entities.*;
import app.carport.Persistence.ConnectionPool;

import app.carport.Persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.carport.Exceptions.DatabaseException;

import java.util.ArrayList;
import java.util.List;
public class OrderCarportController {
    public static void reRenderBestilling(Context ctx, ConnectionPool connectionPool, String message) {

        ctx.attribute("message", message);
        ctx.render("bestilling.html");

    }
}

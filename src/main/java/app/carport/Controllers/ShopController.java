package app.carport.Controllers;

import app.carport.Entities.Carport;
import app.carport.Persistence.ConnectionPool;
import app.carport.SVG.CarportSVG;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ShopController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("order", ctx -> orderCarport(connectionPool, ctx));
    }

    public static void orderButtonOne(ConnectionPool connectionPool) {

    }

    public static void orderButtonTwo(ConnectionPool connectionPool) {

    }

    public static void orderButtonThree(ConnectionPool connectionPool) {

    }

    public static void orderCarport(ConnectionPool connectionPool, Context ctx) {

        // TODO: Byg carport object med ThymeLeaf
        Carport carport = null;
        drawCarport(carport, ctx);
    }

    private static void drawCarport(Carport carport, Context ctx) {

        // TODO: Integere forneden kræver
        int height = carport.getHeight();
        int width = carport.getWidth();


        CarportSVG svg = new CarportSVG(width, height);
        ctx.attribute("svg", svg.toString());
    }

}

package app.carport.Controllers;

import app.carport.Entities.Carport;
import app.carport.Persistence.ConnectionPool;
import app.carport.SVG.CarportSVG;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Locale;

public class ShopController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
    }

    public static void orderButtonOne(ConnectionPool connectionPool) {

    }

    public static void orderButtonTwo(ConnectionPool connectionPool) {

    }

    public static void orderButtonThree(ConnectionPool connectionPool) {

    }

    public static void orderCarport(ConnectionPool connectionPool) {

    }

    public static void showOrder(Context ctx, Carport carport) {
        // TODO: Sørg for at størrelsesforhold er passende til HTML-siden.
        // Tag potentielt højde for skærmstørrelse samt skaler bredde og højde til passende mål.
        int height = carport.getHeight();
        int width = carport.getWidth();

        Locale.setDefault(new Locale("US"));
        CarportSVG svg = new CarportSVG(width, height);
        ctx.attribute("svg", svg.toString());
        ctx.render("SVGTest.html");
    }
}

package app.carport.Controllers;

import app.carport.Entities.Carport;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.MaterialMapper;
import app.carport.SVG.CarportSVG;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ShopController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("order", ctx -> orderCarport(connectionPool, ctx));
        app.post("CustomizeCarport",ctx->orderButtonOne(connectionPool,ctx));
        app.post("Continue",ctx->orderButtonTwo(connectionPool,ctx));

    }

    public static void orderButtonOne(ConnectionPool connectionPool,Context ctx) throws DatabaseException {
       reRenderCarportShop(ctx,connectionPool);
    }

    public static void orderButtonTwo(ConnectionPool connectionPool,Context ctx) {
    ctx.render("bestilling2.html");
    try{
    } catch(DatabaseException e){

    }


      




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





    public static void reRenderCarportShop(Context ctx, ConnectionPool connectionPool) {
        try {
            ctx.attribute("LengthList", MaterialMapper.getAllLength(connectionPool));
            ctx.attribute("WidthList", MaterialMapper.getAllWidth(connectionPool));
            ctx.attribute("NameList", MaterialMapper.getAllName(connectionPool));
            ctx.render("bestilling1.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getCause());
            ctx.render("bestolling1.html");
        }

    }
}

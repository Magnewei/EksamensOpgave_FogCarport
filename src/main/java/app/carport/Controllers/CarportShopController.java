package app.carport.Controllers;

import app.carport.Entities.Carport;
import app.carport.Entities.Material;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.*;
import app.carport.Services.CarportSVG;
import io.javalin.Javalin;
import io.javalin.http.Context;

import javax.xml.crypto.Data;
import java.util.Locale;
import java.util.Map;

public class CarportShopController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("order", ctx -> orderCarport(connectionPool, ctx));
        app.post("CustomizeCarport", ctx -> orderButtonOne(connectionPool, ctx));
        app.post("Continue", ctx -> orderButtonTwo(connectionPool, ctx));
        app.post("Order", ctx -> orderButtonThree(connectionPool, ctx));
        app.post("OrderNoUser", ctx -> orderButtonThree(connectionPool, ctx));
    }

    public static void orderButtonOne(ConnectionPool connectionPool, Context ctx) throws DatabaseException {
        renderCarportShop(ctx, connectionPool);
    }

    public static void orderButtonTwo(ConnectionPool connectionPool, Context ctx) {
        try {
            Locale.setDefault(new Locale("US"));
            ctx.render("orderSite2.html");
            double length = Double.valueOf(ctx.formParam("lengthValue"));
            double width = Double.valueOf(ctx.formParam("widthValue"));
            boolean withRoof = Boolean.valueOf(ctx.formParam("withRoof"));

            Carport carport = new Carport(length, width, withRoof);

            ctx.sessionAttribute("carport", carport);
            CarportSVG svg = new CarportSVG(width, length);
            ctx.sessionAttribute("svg", svg.toString());

        } catch (Error e) {
            ctx.attribute("message", "Noget gik galt i oprettelsen af carport");
        }
    }

    public static void orderButtonThree(ConnectionPool connectionPool, Context ctx) throws DatabaseException {
       User user =null;
        try{

            if(ctx.formParam("currentUser") ==null){
                String name = ctx.formParam("name");
                String lastname = ctx.formParam("lastName");
                String streetname = ctx.formParam("streetName");
                int postalCode = Integer.parseInt(ctx.formParam("postalCode"));
                int phonenumber = Integer.parseInt(ctx.formParam("phoneNumber"));
                String email = ctx.formParam("mail");
                user = new User(name, lastname, streetname, postalCode, phonenumber, email);
                user.setUserID(UserMapper.getLastUserId(connectionPool) + 1);
                UserMapper.createTempUser(user, connectionPool);
                ctx.sessionAttribute("currentUser",user);

            }
            System.out.println(user);
            Carport carport = ctx.sessionAttribute("carport");

           if (!checkNames(ctx, ctx.formParam("name"), ctx.formParam("lastName"), ctx.formParam("streetName"), ctx.formParam("phoneNumber"))) {
               ctx.render("orderSite2.html");
               return;
           }

           carport.setMaterialList(connectionPool);
           Map<Material, Integer> carportMaterials = carport.getMaterialList();
           carport.setMaterialList(carportMaterials);
           double price = carport.calculateTotalPrice();

            int carportId = CarportMapper.getCarportByWidthAndLength(carport.getWidth(), carport.getLength(), carport.isWithRoof(), connectionPool);
           OrderMapper.insertNewOrder(user, carportId,price, connectionPool);
           ctx.render("orderSite3.html");

       } catch(DatabaseException e){
           ctx.attribute("message","Der noget galt med databasen");
           ctx.render("orderSite2.html");
       }

    }
    public static boolean checkNames(Context ctx, String name, String lastname, String streetname, String phonenumber) {
        if (!name.matches("[a-zA-Z]+")) {
            ctx.attribute("message", "Name must only contain letters");
        } else if (!lastname.matches("[a-zA-Z]+")) {
            ctx.attribute("message", "Last name must only contain letters");
        } else if (name.equalsIgnoreCase(lastname)) {
            ctx.attribute("message", "First and last cant be the same name");
        } else if (!streetname.matches("[a-zA-Z ]+")) {
            ctx.attribute("message", "Street name must only contain letters");
        } else if (!phonenumber.matches("\\d+")) {
            ctx.attribute("message", "Phone number must only contain digits.");
        } else {
            return true;
        }
        return false;
    }


    public static void orderCarport(ConnectionPool connectionPool, Context ctx) {
        try {
            double length = Double.valueOf(ctx.formParam("carportlength"));
            double width = Double.valueOf(ctx.formParam("carportwidth"));
            boolean hasShed = Boolean.valueOf(ctx.formParam("hasShed"));
            Carport Carport = new Carport(length, width, hasShed);
            ctx.sessionAttribute("carportlength", length);
            ctx.sessionAttribute("carportwidth", width);
            ctx.sessionAttribute("hasShed", hasShed);
            ctx.sessionAttribute("Carport", Carport);

        } catch (NumberFormatException e) {
            ctx.attribute("message", "Noget gik galt i oprettelsen af carport");
        }
    }

    public static void renderCarportShop(Context ctx, ConnectionPool connectionPool) {
        try {
            ctx.attribute("lengthList", MaterialMapper.getAllLength(connectionPool));
            ctx.attribute("widthList", MaterialMapper.getAllWidth(connectionPool));
            ctx.render("orderSite1.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getCause());
            ctx.render("orderSite1.html");
        }
    }
}

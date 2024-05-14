package app.carport.Controllers;

import app.carport.Entities.Carport;
import app.carport.Entities.Material;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.*;
import app.carport.SVG.CarportSVG;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Locale;
import java.util.Map;

public class CarportShopController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("order", ctx -> orderCarport(connectionPool, ctx));
        app.post("CustomizeCarport", ctx -> orderButtonOne(connectionPool, ctx));
        app.post("Continue", ctx -> orderButtonTwo(connectionPool, ctx));
        app.post("Order", ctx -> orderButtonThree(connectionPool, ctx));
        app.post("OrderNoUser", ctx -> orderButtonThreeNoUser(connectionPool, ctx));
    }

    public static void orderButtonOne(ConnectionPool connectionPool, Context ctx) {
        renderCarportShop(ctx, connectionPool);
    }

    public static void orderButtonTwo(ConnectionPool connectionPool, Context ctx) {
        try {
            Locale.setDefault(new Locale("US"));
            ctx.render("orderSite2.html");
            Double length = Double.valueOf(ctx.formParam("lengthValue"));
            Double width = Double.valueOf(ctx.formParam("widthValue"));
            boolean withRoof = Boolean.valueOf(ctx.formParam("withRoof"));
            ctx.sessionAttribute("carportLength", length);
            ctx.sessionAttribute("carportWidth", width);

            Carport carport = new Carport(length, width, withRoof);
            Map<Material, Integer> carportMaterials = carport.calculateMaterialList(connectionPool);
            carport.setMaterialList(carportMaterials);
            CarportMapper.addMaterialsToDb(carport, connectionPool);
            ctx.sessionAttribute("withRoof", withRoof);
            ctx.sessionAttribute("carport", carport);
            CarportSVG svg = new CarportSVG(width, length);
            ctx.sessionAttribute("svg", svg.toString());

        } catch (Error | DatabaseException e) {
            ctx.attribute("message", "Noget gik galt i oprettelsen af carport");
        }
    }

    public static void orderButtonThree(ConnectionPool connectionPool, Context ctx) throws DatabaseException {
        renderNames(ctx, connectionPool);

        User user = ctx.sessionAttribute("currentUser");
        Carport carport = ctx.sessionAttribute("carport");

        if (!checkNames(ctx, ctx.formParam("name"), ctx.formParam("lastName"), ctx.formParam("streetName"), ctx.formParam("phoneNumber"))) {
            ctx.render("orderSite2.html");
            return;
        }

        int carportId = CarportMapper.getCarportByWidthAndLength(carport.getWidth(), carport.getLength(), carport.isWithRoof(), connectionPool);
        OrderMapper.insertNewOrder(user, carportId, connectionPool);
        ctx.render("orderSite3.html");
    }

    public static void orderButtonThreeNoUser(ConnectionPool connectionPool, Context ctx) {
        try {
            renderNames(ctx, connectionPool);

            Carport carport = ctx.sessionAttribute("carport");
            User user = ctx.sessionAttribute("currentUser");
            user.setUserID(UserMapper.getLastUserId(connectionPool) + 1);
            UserMapper.createTempUser(user, connectionPool);

            int carportId = CarportMapper.getCarportByWidthAndLength(carport.getWidth(), carport.getLength(), carport.isWithRoof(), connectionPool);
            OrderMapper.insertNewOrder(user, carportId, connectionPool);
            ctx.render("orderSite3.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt i oprettelsen af carport");
            ctx.render("orderSite2.html");
        }
    }


    public static void renderNames(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        User user;
        if (!checkNames(ctx, ctx.formParam("name"), ctx.formParam("lastName"), ctx.formParam("streetName"), ctx.formParam("phoneNumber"))) {
            ctx.render("orderSite2.html");
            return;
        }

        if (ctx.sessionAttribute("currentUser") == null) {
            String name = ctx.formParam("name");
            String lastname = ctx.formParam("lastName");
            String streetname = ctx.formParam("streetName");
            String number = ctx.formParam("streetNumber");
            String phonenumber = ctx.formParam("phoneNumber");
            String email = ctx.formParam("mail");
            user = new User(name, lastname, streetname, number, phonenumber, email);
            ctx.sessionAttribute("currentUser", user);

        } else {

            user = ctx.sessionAttribute("currentUser");
            ctx.attribute("name", user.getFirstName());
            ctx.attribute("lastName", user.getLastName());
            ctx.attribute("streetName", user.getAddress().getStreetName());
            ctx.attribute("streetNumber", user.getAddress().getHouseNumber());
            ctx.attribute("phoneNumber", user.getPhoneNumber());
            ctx.attribute("email", user.getEmail());
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

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
        app.post("OrderNoUser", ctx -> orderButtonThreeNouser(connectionPool, ctx));
    }

    public static void orderButtonOne(ConnectionPool connectionPool, Context ctx) throws DatabaseException {
        renderCarportShop(ctx, connectionPool);
    }

    public static void orderButtonTwo(ConnectionPool connectionPool, Context ctx) {
        Locale.setDefault(new Locale("US"));
        ctx.render("orderSite2.html");
        Double length = Double.valueOf(ctx.formParam("Længdevalue"));
        Double width = Double.valueOf(ctx.formParam("Breddevalue"));
        boolean withRoof = Boolean.valueOf(ctx.formParam("withRoof"));
        ctx.sessionAttribute("Carportlength", length);
        ctx.sessionAttribute("Carportwidth", width);
        try {
            Carport carport = new Carport(length, width, withRoof);
            Map<Material, Integer> carportMaterials = carport.calculateMaterialList(connectionPool);
            carport.setMaterialList(carportMaterials);
            CarportMapper.addMaterialsToDb(carport, connectionPool);
            ctx.sessionAttribute("withRoof", withRoof);
            ctx.sessionAttribute("Carport", carport);
            CarportSVG svg = new CarportSVG(width, length);
            ctx.sessionAttribute("svg", svg.toString());

        } catch (Error | DatabaseException e) {
            ctx.attribute("message", "Noget gik galt i oprettelsen af carport");
        }
    }

    public static void orderButtonThree(ConnectionPool connectionPool, Context ctx) throws DatabaseException {
        renderNames(ctx, connectionPool);

        User user = ctx.sessionAttribute("currentUser");
        Carport carport = ctx.sessionAttribute("Carport");

        if (!checkNames(ctx, ctx.formParam("name"), ctx.formParam("lastname"), ctx.formParam("streetname"), ctx.formParam("phonenumber"))) {
            ctx.render("orderSite2.html");
            return;
        }

        int carportId = CarportMapper.getCarportByWidthAndLength(carport.getWidth(), carport.getLength(), carport.isWithRoof(), connectionPool);
        boolean NewOrder = OrderMapper.insertNewOrder(user, carportId, connectionPool);
        ctx.render("orderSite3.html");
    }

    public static void orderButtonThreeNouser(ConnectionPool connectionPool, Context ctx) throws DatabaseException {
        try {
            renderNames(ctx, connectionPool);

            Carport carport = ctx.sessionAttribute("Carport");
            User user = ctx.sessionAttribute("currentUser");
            user.setUserID(UserMapper.getLastUserId(connectionPool) + 1);
            UserMapper.createTempUser(user, connectionPool);

            int carportId = CarportMapper.getCarportByWidthAndLength(carport.getWidth(), carport.getLength(), carport.isWithRoof(), connectionPool);
            boolean NewOrder = OrderMapper.insertNewOrder(user, carportId, connectionPool);
            ctx.render("orderSite3.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt i oprettelsen af carport");
            ctx.render("orderSite2.html");
        }

    }


    public static void renderNames(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        User user;
        if (!checkNames(ctx, ctx.formParam("name"), ctx.formParam("lastname"), ctx.formParam("streetname"), ctx.formParam("phonenumber"))) {
            ctx.render("orderSite2.html");
            return;
        }

        if (ctx.sessionAttribute("currentUser") == null) {
            String name = ctx.formParam("name");
            String lastname = ctx.formParam("lastname");
            String streetname = ctx.formParam("streetname");
            String number = ctx.formParam("streetnumber");
            String phonenumber = ctx.formParam("phonenumber");
            String email = ctx.formParam("mail");
            user = new User(name, lastname, streetname, number, phonenumber, email);
            ctx.sessionAttribute("currentUser", user);

        } else {

            user = ctx.sessionAttribute("currentUser");
            ctx.attribute("name", user.getFirstName());
            ctx.attribute("lastname", user.getLastName());
            ctx.attribute("streetname", user.getAddress().getStreetName());
            ctx.attribute("streetnumber", user.getAddress().getHouseNumber());
            ctx.attribute("phonenumber", user.getPhoneNumber());
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
            ctx.attribute("LengthList", MaterialMapper.getAllLength(connectionPool));
            ctx.attribute("WidthList", MaterialMapper.getAllWidth(connectionPool));
            ctx.render("orderSite1.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getCause());
            ctx.render("orderSite1.html");
        }
    }
}

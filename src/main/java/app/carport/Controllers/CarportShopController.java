package app.carport.Controllers;

import app.carport.Entities.Carport;
import app.carport.Entities.Material;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.*;
import app.carport.Services.CarportSVG;
import app.carport.Services.MailServer;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CarportShopController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("order", ctx -> orderCarport(connectionPool, ctx));
        app.post("CustomizeCarport", ctx -> orderButtonOne(connectionPool, ctx));
        app.post("Continue", ctx -> orderButtonTwo(connectionPool, ctx));
        app.post("Order", ctx -> orderButtonThree(connectionPool, ctx));
    }

    /**
     * Processes the initial step in ordering a carport by rendering the carport shop page.
     *
     * @param connectionPool the database connection pool
     * @param ctx            the web context from Javalin
     * @throws DatabaseException if an error occurs during database access
     */
    public static void orderButtonOne(ConnectionPool connectionPool, Context ctx) throws DatabaseException {
        //CarportMapper.addAllPossibleMaterialsToDb(connectionPool);  //Blev brugt til at populate materialUsage table.
        renderCarportShop(ctx, connectionPool);
    }

    /**
     * Handles the continuation of the carport order process, capturing user input and updating the session.
     *
     * @param connectionPool the database connection pool
     * @param ctx            the web context from Javalin
     */
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

        } catch (NumberFormatException e) {
            ctx.attribute("message", "Noget gik galt i oprettelsen af carport");
        }
    }

    /**
     * Finalizes the carport order, either creating a new user or associating the order with an existing user.
     *
     * @param connectionPool the database connection pool
     * @param ctx            the web context from Javalin
     */
    public static void orderButtonThree(ConnectionPool connectionPool, Context ctx) {
        try {
            User user;

            // If the given input was invalid, re-render the site and throw an error.
            if (!checkNames(ctx, ctx.formParam("firstName"), ctx.formParam("lastName"), ctx.formParam("streetName"), ctx.formParam("phoneNumber"))) {
                ctx.render("orderSite2.html");
                return;
            }

            if (ctx.sessionAttribute("currentUser") == null) {
                String name = ctx.formParam("firstName");
                String lastname = ctx.formParam("lastName");
                String streetname = ctx.formParam("streetName");
                int postalCode = Integer.parseInt(ctx.formParam("postalCode"));
                int phonenumber = Integer.parseInt(ctx.formParam("phoneNumber"));
                String email = ctx.formParam("email");

                // Sets the userID for the temporary user and inserts the user into the database.
                int userID = UserMapper.getLastUserId(connectionPool) + 1;
                if(!UserMapper.checkIfUserExistsByName(email,connectionPool)){
                    user = new User(userID, name, lastname, streetname, postalCode, phonenumber, email);
                    UserMapper.createTempUser(user, connectionPool);
                    ctx.sessionAttribute("currentUser",user);
                }
                else{
                    ctx.attribute("message","Brugernavnet findes allerede");
                    ctx.render("OrderSite2.html");
                    return;
                }

            } else {  // If there's a logged in user, simply assign the user variable the currentUser.
                user = ctx.sessionAttribute("currentUser");
            }

            // Calculates the carport objects total amount of materials. Sets the carportID from width and length.
            Carport carport = ctx.sessionAttribute("carport");
            carport.setMaterialList(connectionPool);
            // Renders the materials as a list on the site.
            List<String> materialListAsString = convertMaterialList(carport.getMaterialList());
            ctx.attribute("materialString", materialListAsString);


            // Then inserts the order on either temporary or a logged in user, combined with the carport and it's price.
            carport.setCarportID(CarportMapper.getCarportIDByWidthAndLength(carport.getWidth(), carport.getLength(), carport.isWithRoof(), connectionPool));
            double price = carport.calculateTotalPrice();
            OrderMapper.insertNewOrder(user, carport.getCarportID(), price, connectionPool);


            user.setOrder(OrderMapper.getOrderByOrderId(OrderMapper.getLastOrderID(connectionPool), connectionPool));
            ctx.attribute("order", user.getOrder());

            // Sends the user a mail on a successful insertion of the order and removes the used materials from the stock.
            MailServer.mailOnOrder(user);
            MaterialMapper.removeMaterialStockOnOrder(carport.getMaterialList(), connectionPool);
            ctx.render("orderSite3.html");

        } catch (DatabaseException e) {
            ctx.attribute("message", "Error while retrieving or inserting data.");
            ctx.render("orderSite2.html");

        } catch (NumberFormatException e) {
            ctx.attribute("message", "Dine indtastede oplysninger kunne ikke læses, prøv igen.");
            ctx.render("orderSite2.html");
        }
    }


    /**
     * Validates the input names and contact details provided by the user.
     *
     * @param ctx         the web context
     * @param name        the first name of the user
     * @param lastname    the last name of the user
     * @param streetname  the street name of the user's address
     * @param phonenumber the phone number of the user
     * @return true if all inputs are valid, false otherwise
     */
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


    /**
     * Initiates the creation of a new carport based on the specifications provided by the user.
     *
     * @param connectionPool the database connection pool
     * @param ctx            the web context from Javalin
     */
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


    /**
     * Renders the initial carport shop page, including options for carport dimensions.
     *
     * @param ctx            the web context from Javalin
     * @param connectionPool the database connection pool
     */
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
    public static List<String> convertMaterialList(Map<Material, Integer> materialList) {
        List<String> materialListAsString = new ArrayList<>();

        for (Map.Entry<Material, Integer> entry : materialList.entrySet()) {
            Material material = entry.getKey();
            int quantity = entry.getValue();

            String materialString = "Materiale: " + material.getName() +
                    ", Pris: " + material.getPrice() +
                    ", Længde: " + material.getLength() +
                    " " + material.getUnit() +
                    ", Mængde: " + quantity;

            materialListAsString.add(materialString);
        }

        return materialListAsString;
    }


}

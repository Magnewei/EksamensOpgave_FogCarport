package app.carport.Controllers;

import app.carport.Entities.Address;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;

public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
        app.post("createUser", ctx -> createUser(ctx, false, connectionPool));
        app.get("/getUserCompleteData", ctx -> renderOrder(ctx, connectionPool));
        app.post("updateUser", ctx -> updateUser(ctx, connectionPool));
        app.post("goTocreateUser", ctx -> goTocreateUser(ctx));

    }

public static void createUser(Context ctx, boolean isadmin, ConnectionPool connectionPool) {
    String firstName = ctx.formParam("firstName");
    String lastName = ctx.formParam("lastName");
    String email = ctx.formParam("username");
    String password = ctx.formParam("password");
    int phoneNumber = Integer.parseInt(ctx.formParam("phoneNumber"));
    String streetName = ctx.formParam("streetName");
    int houseNumber = Integer.parseInt(ctx.formParam("houseNumber"));
    String cityName = ctx.formParam("cityName");
    int postalCode = Integer.parseInt(ctx.formParam("postalCode"));

    Address address = new Address(0, postalCode, houseNumber, cityName, streetName);

    User user = new User(0, email, password, isadmin, firstName, lastName, address, phoneNumber);

    try {
        if (!UserMapper.checkIfUserExistsByName(email, connectionPool)) {
            UserMapper.createUser(user, connectionPool);
            ctx.attribute("message", "Du er hermed oprettet med brugernavn: " + email + ". Nu skal du logge på");
            ctx.render("login.html");
        } else {
            ctx.attribute("message", "Brugernavnet eksisterer allerede. Vælg venligst et andet brugernavn.");
            ctx.render("createUser.html");
        }
    } catch (DatabaseException | NumberFormatException e) {
        ctx.attribute("message", "Der opstod en fejl under oprettelsen. Prøv venligst igen.");
        ctx.render("index.html");
    }
}

    public static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String mail = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(mail, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.render("index.html");

        } catch (DatabaseException e) {
            ctx.attribute("message", "Forkert login, Prøv venligst igen.");
            ctx.render("login.html");
        }
    }
    public static void renderOrder(Context ctx, ConnectionPool connectionPool) {
        try {
            User user = ctx.sessionAttribute("currentuser");
            ctx.render("ordrer.html");

        } catch (NullPointerException e) {
            ctx.attribute("message", "An error occurred while fetching the user data.");
            ctx.render("login.html");
        }
    }
    public static void updateUser(Context ctx, ConnectionPool connectionPool) {
        try {
            User currentUser = ctx.sessionAttribute("currentUser");
            currentUser.setFirstName(ctx.formParam("firstName"));
            currentUser.setLastName(ctx.formParam("lastName"));
            currentUser.setEmail(ctx.formParam("email"));
            currentUser.setPassword(ctx.formParam("password"));
            currentUser.setPhoneNumber(Integer.parseInt(ctx.formParam("phoneNumber")));

            Address address = currentUser.getAddress();
            address.setCityName(ctx.formParam("cityName"));
            address.setPostalCode(Integer.parseInt(ctx.formParam("postalCode")));
            address.setHouseNumber(Integer.parseInt(ctx.formParam("houseNumber")));
            address.setStreetName(ctx.formParam("streetName"));
            UserMapper.updateUser(currentUser, connectionPool); // Pass phonenumber to updateUser method
            ctx.sessionAttribute("currentUser", currentUser);

            // Redirect to a success page
            ctx.render("ordrer.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", "An error occurred while updating the user information.");
            ctx.render("ordrer.html");
        }
    }

    public static void goTocreateUser(Context ctx) {
        ctx.render("createUser.html");
    }
}

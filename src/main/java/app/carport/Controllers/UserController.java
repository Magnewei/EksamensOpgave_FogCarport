package app.carport.Controllers;

import app.carport.Entities.Address;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
        app.post("createUser", ctx -> createUser(ctx, false, connectionPool));
        app.get("getUserSite", ctx -> renderUserSite(ctx, connectionPool));
        app.post("updateUser", ctx -> updateUser(ctx, connectionPool));
        app.post("goTocreateUser", ctx -> goTocreateUser(ctx));

    }



    public static void createUser(Context ctx, boolean isadmin, ConnectionPool connectionPool) {
        try {
        String firstName = ctx.formParam("firstName");
        String lastName = ctx.formParam("lastName");
        String email = ctx.formParam("username");
        String password = ctx.formParam("password");
        int phoneNumber = Integer.parseInt(ctx.formParam("phoneNumber"));
        String streetName = ctx.formParam("streetName");
        int houseNumber = Integer.parseInt(ctx.formParam("houseNumber"));
        String cityName = ctx.formParam("cityName");
        int postalCode = Integer.parseInt(ctx.formParam("postalCode"));

        // Regular expression pattern for password validation
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=.*[a-zA-Z\\d@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]{8,}$";

        // Compile the regular expression pattern & create a matcher with the input password
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        // Perform password validation
        if (!matcher.matches()) {
            ctx.attribute("message", "Password must contain at least one capitalized and lowercase letter, one digit, one symbol and be at least 8 characters long.");
            ctx.render("createUser.html");
            return; // Exit the method if password validation fails
        }

        Address address = new Address(0, postalCode, houseNumber, cityName, streetName);
        User user = new User(0, email, password, isadmin, firstName, lastName, address, phoneNumber);


            if (!UserMapper.checkIfUserExistsByName(email, connectionPool)) {
                UserMapper.createUser(user, connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med brugernavn: " + email + ". Nu skal du logge på");
                ctx.render("login.html");
            } else {
                ctx.attribute("message", "Brugernavnet eksisterer allerede. Vælg venligst et andet brugernavn.");
                ctx.render("createUser.html");
            }
        } catch (DatabaseException e) {
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
            if (password== null || password.isEmpty()) {
                ctx.attribute("message", "Password is required.");
                ctx.render("login.html");
                return;
            }
            User user = UserMapper.login(mail, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.render("index.html");

        } catch (DatabaseException e) {
            //hvis nej send tilbage til login side med fejl
            ctx.attribute("message", "Forkert login, Prøv venligst igen.");
            ctx.render("login.html");
        }
    }
    public static void renderUserSite(Context ctx, ConnectionPool connectionPool) {
            User user = ctx.sessionAttribute("currentUser");
            ctx.render("userSite.html");
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
            ctx.render("userSite.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", "An error occurred while updating the user information.");
            ctx.render("userSite.html");
        }
    }
    public static void goTocreateUser(Context ctx) {

        ctx.render("createUser.html");
    }


}

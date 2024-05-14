package app.carport.Controllers;

import app.carport.Entities.Address;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.OrderMapper;
import app.carport.Persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
        app.post("createUser", ctx -> createUser(ctx, false, connectionPool));
        app.get("getUserSite", ctx -> renderUserSite(ctx, connectionPool));
        app.post("updateUser", ctx -> updateUser(ctx, connectionPool));
        app.post("goTocreateUser", ctx -> goTocreateUser(ctx));
    }

    /**
     * Attempts to create a new user account from the provided form parameters.
     *
     * @param ctx            Context for handling the request, contains form parameters.
     * @param isadmin        Boolean flag indicating if the user should have admin privileges.
     * @param connectionPool Connection pool for database connections.
     */
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


            if (!checkPassword(ctx, ctx.formParam("password"))) {
                ctx.render("createUser.html");
                return;
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
            ctx.render("createUser.html");
        }
    }

    /**
     * Validates the password based on specific criteria such as length and character composition.
     *
     * @param ctx      Context for handling the request, can display messages.
     * @param password Password string to validate.
     * @return true if the password meets all criteria, false otherwise.
     */
    public static boolean checkPassword(Context ctx, String password) {
        if (password.length() < 8) {
            ctx.attribute("message", "Password must be at least 8 characters long.");
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            ctx.attribute("message", "Password must contain at least one lowercase letter.");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            ctx.attribute("message", "Password must contain at least one uppercase letter.");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            ctx.attribute("message", "Password must contain at least one digit.");
            return false;
        }

        if (!password.matches(".*[@#$%^&+=!].*")) {
            ctx.attribute("message", "Password must contain at least one special character (@, #, $, %, ^, &, +, =, !).");
            return false;
        }

        return true;
        // man kunne argumentere for denne skulle placeres i user mappe.
    }

    /**
     * Logs out the current user by invalidating the session and redirecting to the home page.
     *
     * @param ctx Context for handling the request.
     */
    public static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    /**
     * Attempts to log in a user with provided credentials. On success, stores user in session.
     *
     * @param ctx            Context for handling the request, contains form parameters.
     * @param connectionPool Connection pool for database connections.
     */
    public static void login(Context ctx, ConnectionPool connectionPool) {
        String mail = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            if (password == null || password.isEmpty()) {
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

    /**
     * Renders the user-specific site, including user data and related orders.
     *
     * @param ctx            Context for handling the request.
     * @param connectionPool Connection pool for database connections.
     */
    public static void renderUserSite(Context ctx, ConnectionPool connectionPool) {
        try {
            User user = ctx.sessionAttribute("currentUser");
            ctx.attribute("orderList", OrderMapper.getOrdersByUserId(user.getUserID(), connectionPool));
            ctx.render("userSite.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Error. Couldn't load the users orders.");
            ctx.render("index.html");
        }
    }

    /**
     * Updates user information based on provided form parameters and saves it to the database.
     *
     * @param ctx            Context for handling the request, contains form parameters.
     * @param connectionPool Connection pool for database connections.
     */
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

    /**
     * Renders the page for creating a new user.
     *
     * @param ctx Context for handling the request.
     */
    public static void goTocreateUser(Context ctx) {
        ctx.render("createUser.html");
    }
}

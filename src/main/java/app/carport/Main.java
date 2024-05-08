package app.carport;

import app.carport.Controllers.*;
import app.carport.Entities.Address;
import app.carport.Entities.User;
import app.carport.MailServer.MailServer;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import app.carport.Persistence.ConnectionPool;
import app.carport.Thymeleaf.ThymeleafConfig;

import static app.carport.Persistence.UserMapper.getUserByUserId;

public class Main {
    private static final String USER = System.getenv("JDBC_USER");
    private static final String PASSWORD = System.getenv("JDBC_PASSWORD");
    private static final String URL = System.getenv("JDBC_CONNECTION_STRING");
    private static final String DB = System.getenv("JDBC_DB");
    public static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7071);

        app.get("/", ctx -> ctx.render("index.html"));
        AdminPanelController.addRoutes(app, connectionPool);
        UserController.addRoutes(app, connectionPool);
        HeaderController.addRoutes(app, connectionPool);
        CarportShopController.addRoutes(app, connectionPool);
    }
}
package app.carport;

import app.carport.Controllers.AdminController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import app.carport.Persistence.ConnectionPool;
import app.carport.Thymeleaf.ThymeleafConfig;

public class Main {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "cupcake";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7071);

        // Routing
        app.get("/", ctx -> ctx.render("index.html"));
        AdminController.addRoutes(app, connectionPool);
    }
}
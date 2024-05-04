package app.carport;

import app.carport.Controllers.*;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import app.carport.Persistence.ConnectionPool;
import app.carport.Thymeleaf.ThymeleafConfig;

import java.io.IOException;

public class Main {
    private static final String USER = System.getenv("JDBC_USER");
    private static final String PASSWORD = System.getenv("JDBC_PASSWORD");
    private static final String URL = System.getenv("JDBC_CONNECTION_STRING");
    private static final String DB = System.getenv("JDBC_DB");
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7071);



        // Routing
        app.get("/", ctx -> ctx.render("index.html"));
        AdminPanelController.addRoutes(app, connectionPool);
        LoginController.addRoutes(app, connectionPool);
        HeaderController.addRoutes(app, connectionPool);
        ShopController.addRoutes(app, connectionPool);

        try {
            testMail();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testMail() throws IOException {
        String API_KEY = System.getenv("SENDGRID_API_KEY");
        Email from = new Email("magnewei@icloud.com");
        from.setName("Johannes Fog Byggemarked");
        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();

        /* Erstat kunde@gmail.com, name, email og zip med egne værdier ****/
        /* I test-fasen - brug din egen email, så du kan modtage beskeden */

        personalization.addTo(new Email("magnewei@icloud.com"));
        personalization.addDynamicTemplateData("name", "Anders And");
        personalization.addDynamicTemplateData("email", "anders@and.dk");
        personalization.addDynamicTemplateData("zip", "2100");
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-5b0856a83de84ed0b5825176c7fb50a6";
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());

        } catch (IOException ex) {
            System.out.println("Error sending mail");
            throw ex;
        }
    }
}
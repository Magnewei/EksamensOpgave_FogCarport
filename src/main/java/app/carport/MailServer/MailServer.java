package app.carport.MailServer;

import app.carport.Entities.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import java.io.IOException;

public class MailServer {
    private final static String API_KEY = System.getenv("SENDGRID_API_KEY");
    private static final String email = "fogprojekt@icloud.com"; // Mail address which is used for sending to the customer.

    /**
     * Sends an email notification when an order is completed.
     * @param user User to whom the email will be sent.
     * @return true if the email was sent successfully, false otherwise.
     */
    public static boolean mailOnOrderDone(User user) {
        int responseCode = 0;

        // Get api key
        SendGrid sg = new SendGrid(API_KEY);

        // Email that we're sending our company mail from:
        Email from = new Email(email);
        from.setName("Johannes Fog Byggemarked");
        Mail mail = new Mail();
        mail.setFrom(from);
        Personalization personalization = new Personalization();

        // Instantiate customer details into a mail.
        personalization.addTo(new Email(user.getEmail()));
        personalization.addDynamicTemplateData("name", user.getEmail());
        personalization.addDynamicTemplateData("orderID", user.getOrder().getOrderId());
        mail.addPersonalization(personalization);
        mail.addCategory("carportapp");

        // Send mail
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // TemplateID is corresponding with the template created on SendGrid
            mail.templateId = "d-20b61e76c5374d138d3636e8c9247716";
            request.setBody(mail.build());
            Response response = sg.api(request);

            // Get response code for return statement.
            responseCode = response.getStatusCode();

        } catch (IOException e) {
            System.out.println("Error sending mail");
            System.out.println(e.getMessage());
        }
        return responseCode == 202;
    }

    /**
     * Sends an email notification when an order's status is updated.
     * @param user User to whom the email will be sent. This method currently has static data for demonstration.
     * @return true if the email was sent successfully, false otherwise.
     */
    public static boolean mailOnStatusUpdate(User user) {
        int responseCode = 0;

        // Email that we're sending our company mail from:
        Email from = new Email(email);
        from.setName("Johannes Fog Byggemarked");
        Mail mail = new Mail();
        mail.setFrom(from);
        Personalization personalization = new Personalization();

        // Instantiate customer details into a mail.
        personalization.addTo(new Email("magnewei@icloud.com"));
        personalization.addDynamicTemplateData("name", "Magnus");
        personalization.addDynamicTemplateData("orderID", "3");
        personalization.addDynamicTemplateData("orderStatus", "denied");
        mail.addPersonalization(personalization);
        mail.addCategory("carportapp");

        // Send mail
        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // TemplateID is corresponding with the template created on SendGrid
            mail.templateId = "d-953b6496a4194c4cb16cf9c8aa718805";
            request.setBody(mail.build());
            Response response = sg.api(request);

            // Get response code for return statement.
            responseCode = response.getStatusCode();
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());

        } catch (IOException e) {
            System.out.println("Error sending mail");
            System.out.println(e.getMessage());
        }
        return responseCode == 202;
    }

    /**
     * Sends an email notification when a user's information is changed.
     * @param user User to whom the email will be sent.
     * @return true if the email was sent successfully, false otherwise.
     */
    public static boolean mailOnUserChanges(User user) {
        int responseCode = 0;

        // Get api key
        SendGrid sg = new SendGrid(API_KEY);

        // Email that we're sending our company mail from:
        Email from = new Email(email);
        from.setName("Johannes Fog Byggemarked");
        Mail mail = new Mail();
        mail.setFrom(from);
        Personalization personalization = new Personalization();

        // Instantiate customer details into a mail.
        personalization.addTo(new Email(user.getEmail()));
        personalization.addDynamicTemplateData("name", user.getEmail());
        personalization.addDynamicTemplateData("email", user.getEmail());
        personalization.addDynamicTemplateData("adress", user.getAddress());
        mail.addPersonalization(personalization);
        mail.addCategory("carportapp");

        // Send mail
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // TemplateID is corresponding with the template created on SendGrid
            mail.templateId = "d-90c56f8653974d2eb5c1eb3221df13bc";
            request.setBody(mail.build());
            Response response = sg.api(request);

            // Get response code for return statement.
            responseCode = response.getStatusCode();

        } catch (IOException e) {
            System.out.println("Error sending mail");
            System.out.println(e.getMessage());
        }
        return responseCode == 202;
    }

    /**
     * Sends an email notification about an order.
     * @param user User to whom the email will be sent.
     * @return true if the email was sent successfully, false otherwise.
     */
    public static boolean mailOnOrder(User user) {
        int responseCode = 0;

        // Get api key
        SendGrid sg = new SendGrid(API_KEY);

        // Email that we're sending our company mail from:
        Email from = new Email(email);
        from.setName("Johannes Fog Byggemarked");
        Mail mail = new Mail();
        mail.setFrom(from);
        Personalization personalization = new Personalization();

        // Instantiate customer details into a mail.
        personalization.addTo(new Email(user.getEmail()));
        personalization.addDynamicTemplateData("name", user.getEmail());
        personalization.addDynamicTemplateData("orderID", user.getOrder().getOrderId());
        mail.addPersonalization(personalization);
        mail.addCategory("carportapp");

        // Send mail
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // TemplateID is corresponding with the template created on SendGrid
            mail.templateId = "d-98455b553a7b4c778081b7d4a29c7ef7";
            request.setBody(mail.build());
            Response response = sg.api(request);

            // Get response code for return statement.
            responseCode = response.getStatusCode();

        } catch (IOException e) {
            System.out.println("Error sending mail");
            System.out.println(e.getMessage());
        }
        return responseCode == 202;
    }
}

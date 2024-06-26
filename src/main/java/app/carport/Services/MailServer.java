package app.carport.Services;

import app.carport.Entities.Carport;
import app.carport.Entities.Material;
import app.carport.Entities.Order;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import java.io.IOException;
import java.util.Map;

public class MailServer {
    private final static String API_KEY = System.getenv("SENDGRID_API_KEY");
    private static final String email = "fogprojekt@icloud.com"; // Mail address which is used for sending to the customer.

    /**
     * Sends an email notification when an order is completed.
     *
     * @param user User to whom the email will be sent.
     * @return true if the email was sent successfully, false otherwise.
     */
    public static boolean mailOnOrderDone(Order order, ConnectionPool connectionPool) throws DatabaseException {
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
        personalization.addTo(new Email(order.getUser().getEmail()));
        personalization.addDynamicTemplateData("name", order.getUser().getFirstName() + " " + order.getUser().getLastName());
        personalization.addDynamicTemplateData("orderID", order.getOrderId());
        personalization.addDynamicTemplateData("materialList", printCarportMaterials(order.getCarport(), connectionPool));
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
     *
     * @param user User to whom the email will be sent. This method currently has static data for demonstration.
     * @return true if the email was sent successfully, false otherwise.
     */
    public static boolean mailOnStatusUpdate(Order order) {
        int responseCode = 0;

        // Email that we're sending our company mail from:
        Email from = new Email(email);
        from.setName("Johannes Fog Byggemarked");
        Mail mail = new Mail();
        mail.setFrom(from);
        Personalization personalization = new Personalization();

        // Instantiate customer details into a mail.
        personalization.addTo(new Email(order.getUser().getEmail()));
        personalization.addDynamicTemplateData("name", order.getUser().getFirstName() + " " + order.getUser().getLastName());
        personalization.addDynamicTemplateData("orderID", order.getOrderId());
        personalization.addDynamicTemplateData("orderStatus", order.getStatus());
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
     *
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
        personalization.addDynamicTemplateData("name", user.getFirstName() + " " + user.getLastName());
        personalization.addDynamicTemplateData("email", user.getEmail());
        personalization.addDynamicTemplateData("address", user.getAddress());
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
     *
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
        personalization.addDynamicTemplateData("name", user.getFirstName() + " " + user.getLastName());
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


    /**
     * Constructs a string representation of the materials needed for a given carport.
     * This includes each material and its corresponding quantity formatted in a list.
     *
     * @param carport The Carport object containing the list of materials.
     * @return A string detailing the list of materials and their quantities for the carport.
     */
    public static String printCarportMaterials(Carport carport, ConnectionPool connectionPool) throws DatabaseException {
        try {
            StringBuilder carportMaterials = new StringBuilder();
            carport.setMaterialList(connectionPool);
            Map<Material, Integer> carportMaterialsMap = carport.getMaterialList();

            for (Map.Entry<Material, Integer> entry : carportMaterialsMap.entrySet()) {
                Material material = entry.getKey();
                int quantity = entry.getValue();
                carportMaterials.append(material.getName() + " af en mængde på " + quantity + "\n");
            }
            return carportMaterials.toString();
        } catch (DatabaseException e) {
            throw new DatabaseException("Couldn't get the material list for the mail.");
        }
    }
}

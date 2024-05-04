package app.carport.MailServer;

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

    public static boolean sendMail(String to, String name, String zip) {
        // Used for return statement;
        int responseCode = 0;

        // Get api key
        SendGrid sg = new SendGrid(API_KEY);

        // Email that we're sending our company mail from:
        String email = "fogprojekt@icloud.com";
        Email from = new Email(email);
        from.setName("Johannes Fog Byggemarked");
        Mail mail = new Mail();
        mail.setFrom(from);
        Personalization personalization = new Personalization();

        // Instantiate customer details into a mail.
        personalization.addTo(new Email(to));
        personalization.addDynamicTemplateData("name", name);
        personalization.addDynamicTemplateData("email", to);
        personalization.addDynamicTemplateData("zip", zip);
        mail.addPersonalization(personalization);
        mail.addCategory("carportapp");

        // Send mail
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // TemplateID is corresponding with the template created on SendGrid
            mail.templateId = "d-5b0856a83de84ed0b5825176c7fb50a6";
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

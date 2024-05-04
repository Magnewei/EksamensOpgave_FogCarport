package app.carport.MailServer;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import java.io.IOException;

public class YapMail {
    public static void sendMail() {

        String API_KEY = System.getenv("SENDGRID_API_KEY");


        Email from = new Email("fogprojekt@icloud.com");
        from.setName("Johannes Fog Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();

        /* Erstat kunde@gmail.com, name, email og zip med egne værdier ****/
        /* I test-fasen - brug din egen email, så du kan modtage beskeden */

        personalization.addTo(new Email("fogprojekt@icloud.com"));
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
            System.out.println(ex.getMessage());
        }
    }
}


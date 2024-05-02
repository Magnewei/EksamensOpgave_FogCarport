package app.carport.Controllers;

import app.carport.Entities.Carport;
import app.carport.SVG.CarportSVG;
import io.javalin.http.Context;
import java.util.Locale;

public class SVGTest {

    // Skal måske kaldes i main ved hjælp af route???
    // Logikken fra metoden herunder skal ind i den korretkte controller.
    // Height or width skal hentes fra længden som kunden vælger, og derefter have et passende størrelsesforhold.



    public static void showOrder(Context ctx, Carport carport) {

        // TODO: Sørg for at størrelsesforhold er passende til HTML-siden.
        // Tag potentielt højde for skærmstørrelse samt skaler bredde og højde til passende mål.
        int height = carport.getHeight();
        int width = carport.getWidth();



        Locale.setDefault(new Locale("US"));
        CarportSVG svg = new CarportSVG(width, height);
        ctx.attribute("svg", svg.toString());
        ctx.render("SVGTest.html");
    }
}

    package app.carport.Controllers;

    import app.carport.Entities.Carport;
    import app.carport.Exceptions.DatabaseException;
    import app.carport.MailServer.MailServer;
    import app.carport.Persistence.ConnectionPool;
    import app.carport.Persistence.MaterialMapper;
    import app.carport.SVG.CarportSVG;
    import app.carport.SVG.SVGDrawer;
    import com.sendgrid.helpers.mail.Mail;
    import io.javalin.Javalin;
    import io.javalin.http.Context;

    import java.util.Locale;

    public class CarportShopController {
        public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
            app.post("order", ctx -> orderCarport(connectionPool, ctx));
            app.post("CustomizeCarport", ctx -> orderButtonOne(connectionPool, ctx));
            app.post("Continue", ctx -> orderButtonTwo(connectionPool, ctx));
            app.post("Order", ctx -> orderButtonThree(connectionPool, ctx));

        }

        public static void orderButtonOne(ConnectionPool connectionPool, Context ctx) throws DatabaseException {
            reRenderCarportShop(ctx, connectionPool);
        }

        public static void orderButtonTwo(ConnectionPool connectionPool, Context ctx) {
            Locale.setDefault(new Locale("US"));
            ctx.render("bestilling2.html");
            Double length = Double.valueOf(ctx.formParam("Længdevalue"));
            Double width = Double.valueOf(ctx.formParam("Breddevalue"));
            boolean hasShed = Boolean.valueOf(ctx.formParam("hasShed"));
            ctx.sessionAttribute("Carportlength",length);
            ctx.sessionAttribute("Carportwidth",width);
            try {
                Carport carport = new Carport(length,width,hasShed);
                ctx.sessionAttribute("hasShed",hasShed);
                ctx.sessionAttribute("Carport",carport);

                CarportSVG svg = new CarportSVG(width,length);

                ctx.sessionAttribute("svg",svg.toString());
            } catch(Error e){
                ctx.attribute("message", "Noget gik galt i oprettelsen af carport");
            }
        }

        public static void orderButtonThree(ConnectionPool connectionPool, Context ctx) {
            String name = ctx.formParam("name");
                String lastname = ctx.formParam("lastname");
                String streetname = ctx.formParam("streetname");
                String streetnumber = ctx.formParam("streetnumber");
                String phonenumber = ctx.formParam("phonenumber");
                String email = ctx.formParam("mail");
            ctx.attribute("name",name);
                ctx.attribute("lastname",lastname);
                ctx.attribute("streetname",streetname);
                ctx.attribute("number",streetnumber);
                ctx.attribute("phonenumber",phonenumber);
                ctx.attribute("email",email);

            if (!name.matches("[a-zA-Z]+")) {
                    ctx.attribute("message", "Name must only contain letters");
                } else if (!lastname.matches("[a-zA-Z]+")) {
                    ctx.attribute("message", "Last name must only contain letters");
                } else if (name.equalsIgnoreCase(lastname)) {
                    ctx.attribute("message", "First and last cant be the same name");
                } else if (!streetname.matches("[a-zA-Z ]+")) {
                    ctx.attribute("message", "Street name must only contain letters");
                } else if (!phonenumber.matches("\\d+")) {
                    ctx.attribute("message", "Phone number must only contain digits.");
                } else {

                ctx.render("bestilling3.html");
                MailServer mailserver = new MailServer();
                return;

                }
                ctx.render("bestilling2.html");
            }



        public static void orderCarport(ConnectionPool connectionPool, Context ctx) {
            try{
                double length = Double.valueOf(ctx.formParam("carportlength"));
                double width = Double.valueOf(ctx.formParam("carportwidth"));
                boolean hasShed = Boolean.valueOf(ctx.formParam("hasShed"));
                Carport Carport = new Carport(length,width,hasShed);
                ctx.sessionAttribute("carportlength",length);
                ctx.sessionAttribute("carportwidth",width);
                ctx.sessionAttribute("hasShed",hasShed);
                ctx.sessionAttribute("Carport",Carport);
            } catch(Error e){
                ctx.attribute("message", "Noget gik galt i oprettelsen af carport");

            }

        }

        private static void drawCarport(Carport carport, Context ctx) {

            // TODO: Integere forneden kræver
            double length = carport.getLength();
            double width = carport.getWidth();


          //  CarportSVG svg = new CarportSVG(width, height);
           // ctx.attribute("svg", svg.toString());
        }


        public static void reRenderCarportShop(Context ctx, ConnectionPool connectionPool) {
            try {
                ctx.attribute("LengthList", MaterialMapper.getAllLength(connectionPool));
                ctx.attribute("WidthList", MaterialMapper.getAllWidth(connectionPool));
                //ctx.attribute("NameList", MaterialMapper.getAllName(connectionPool));
                ctx.render("bestilling1.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", e.getCause());
                ctx.render("bestilling1.html");
            }

        }
    }

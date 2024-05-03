    package app.carport.Controllers;

    import app.carport.Entities.Carport;
    import app.carport.Exceptions.DatabaseException;
    import app.carport.Persistence.ConnectionPool;
    import app.carport.Persistence.MaterialMapper;
    import app.carport.SVG.CarportSVG;
    import io.javalin.Javalin;
    import io.javalin.http.Context;

    public class ShopController {
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
            ctx.render("bestilling2.html");
            String length = ctx.formParam("Længdevalue");
            String width = ctx.formParam("Breddevalue");
            String name = ctx.formParam("Materialevalue");

        }

        public static void orderButtonThree(ConnectionPool connectionPool, Context ctx) {
                String name = ctx.formParam("name");
                String lastname = ctx.formParam("lastname");
                String streetname = ctx.formParam("streetname");
                String streetnumber = ctx.formParam("streetnumber");
                String phonenumber = ctx.formParam("phonenumber");

                if (!name.matches("[a-zA-Z]+")) {
                    ctx.attribute("message", "Name must only contain letters");
                } else if (!lastname.matches("[a-zA-Z]+")) {
                    ctx.attribute("message", "Last name must only contain letters");
                } else if (name.equalsIgnoreCase(lastname)) {
                    ctx.attribute("message", "First and last cant be the same name");
                } else if (!streetname.matches("[a-zA-Z ]+")) {
                    ctx.attribute("message", "Street name must only contain letters");
                } else if (!streetnumber.matches("\\d+")) {
                    ctx.attribute("message", "Street number must only contain digits.");
                } else if (!phonenumber.matches("\\d+")) {
                    ctx.attribute("message", "Phone number must only contain digits.");
                } else {
                    ctx.render("bestilling3.html");
                    return;

                }
                ctx.render("bestilling2.html");
            }



        public static void orderCarport(ConnectionPool connectionPool, Context ctx) {

            // TODO: Byg carport object med ThymeLeaf
            Carport carport = null;
            drawCarport(carport, ctx);
        }

        private static void drawCarport(Carport carport, Context ctx) {

            // TODO: Integere forneden kræver
            int height = carport.getHeight();
            int width = carport.getWidth();


            CarportSVG svg = new CarportSVG(width, height);
            ctx.attribute("svg", svg.toString());
        }





        public static void reRenderCarportShop(Context ctx, ConnectionPool connectionPool) {
            try {
                ctx.attribute("LengthList", MaterialMapper.getAllLength(connectionPool));
                ctx.attribute("WidthList", MaterialMapper.getAllWidth(connectionPool));
                ctx.attribute("NameList", MaterialMapper.getAllName(connectionPool));
                ctx.render("bestilling1.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", e.getCause());
                ctx.render("bestilling1.html");
            }

        }
    }

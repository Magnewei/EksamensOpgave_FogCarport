package app.carport.Controllers;

import app.carport.Entities.Carport;
import app.carport.Entities.Material;
import app.carport.Entities.Order;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.MaterialMapper;
import app.carport.Persistence.OrderMapper;
import app.carport.Services.CarportSVG;
import app.carport.Services.MailServer;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Locale;

public class AdminPanelController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("removeorder", ctx -> removeOrder(connectionPool, ctx));
        app.post("removematerial", ctx -> removeMaterial(connectionPool, ctx));
        app.post("addmaterial", ctx -> addMaterial(connectionPool, ctx));
        app.post("renderadmin", ctx -> renderAdmin(connectionPool, ctx));
        app.post("acceptorder", ctx -> acceptOrder(connectionPool, ctx));
        app.post("denyorder", ctx -> denyOrder(connectionPool, ctx));
        app.post("addMaterialStock", ctx -> addMaterialStock(connectionPool, ctx));
        app.post("editMaterial", ctx -> renderEditMaterial(connectionPool, ctx));
        app.post("changeMaterialPrice", ctx -> changeMaterialPrice(connectionPool, ctx));
        app.post("inspectOrder", ctx -> inspectOrder(connectionPool, ctx));
    }

    /**
     * Adds a new material to the inventory database.
     *
     * @param connectionPool Connection pool for database connections.
     * @param ctx            Context for handling the request, contains form parameters.
     */
    private static void addMaterial(ConnectionPool connectionPool, Context ctx) {
        try {
            String name = ctx.formParam("name");
            double price = Double.parseDouble(ctx.formParam("price"));
            double length = Double.parseDouble(ctx.formParam("length"));
            String unit = ctx.formParam("unit");
            int quantityInStock = Integer.parseInt(ctx.formParam("quantityInStock"));
            MaterialMapper.addMaterial(connectionPool, name, price, length, unit, quantityInStock);
            renderAdmin(connectionPool, ctx);
        } catch (NumberFormatException | DatabaseException e) {
            renderAdmin(connectionPool, ctx);
        }
    }

    /**
     * Denies an order and sends a notification email to the user.
     *
     * @param connectionPool Connection pool for database connections.
     * @param ctx            Context for handling the request, contains form parameters.
     */
    private static void denyOrder(ConnectionPool connectionPool, Context ctx) {
        try {
            int orderID = Integer.parseInt(ctx.formParam("deny_order"));
            Order order = OrderMapper.getOrderByOrderId(orderID, connectionPool);

            OrderMapper.denyOrder(connectionPool, orderID);
            MailServer.mailOnStatusUpdate(order);
            renderAdmin(connectionPool, ctx);
        } catch (NumberFormatException | DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            renderAdmin(connectionPool, ctx);
        }
    }

    /**
     * Removes an order from the system.
     *
     * @param connectionPool Connection pool for database connections.
     * @param ctx            Context for handling the request, contains form parameters.
     */
    private static void removeOrder(ConnectionPool connectionPool, Context ctx) {
        try {
            int orderID = Integer.parseInt(ctx.formParam("remove_order"));

            OrderMapper.deleteOrderById(orderID, connectionPool);
            renderAdmin(connectionPool, ctx);
        } catch (NumberFormatException | DatabaseException e) {
            renderAdmin(connectionPool, ctx);
        }
    }

    /**
     * Accepts an order and sends a notification email to the user.
     *
     * @param connectionPool Connection pool for database connections.
     * @param ctx            Context for handling the request, contains form parameters.
     */
    private static void acceptOrder(ConnectionPool connectionPool, Context ctx) {
        try {
            int orderID = Integer.parseInt(ctx.formParam("accept_order"));

            OrderMapper.acceptOrder(connectionPool, orderID);
            Order order = OrderMapper.getOrderByOrderId(orderID, connectionPool);
            MailServer.mailOnOrderDone(order, connectionPool);

            renderAdmin(connectionPool, ctx);
        } catch (NumberFormatException | DatabaseException e) {
            renderAdmin(connectionPool, ctx);
        }
    }

    /**
     * Removes a material from the inventory database.
     *
     * @param connectionPool Connection pool for database connections.
     * @param ctx            Context for handling the request, contains form parameters.
     */
    private static void removeMaterial(ConnectionPool connectionPool, Context ctx) {
        try {
            int materialID = Integer.parseInt(ctx.formParam("remove_material"));
            MaterialMapper.deleteMaterialById(connectionPool, materialID);
            renderAdmin(connectionPool, ctx);
        } catch (NumberFormatException | DatabaseException e) {
            renderAdmin(connectionPool, ctx);
        }
    }

    /**
     * Removes a material from the inventory database.
     *
     * @param connectionPool Connection pool for database connections.
     * @param ctx            Context for handling the request, contains form parameters.
     */
    private static void addMaterialStock(ConnectionPool connectionPool, Context ctx) {
        try {
            int materialID = Integer.parseInt(ctx.formParam("material_id"));
            int quantityToAdd = Integer.parseInt(ctx.formParam("quantityToAdd"));


            MaterialMapper.addMaterialStock(materialID, quantityToAdd, connectionPool);
            renderAdmin(connectionPool, ctx);
        } catch (NumberFormatException | DatabaseException e) {
            renderAdmin(connectionPool, ctx);
        }
    }

    /**
     * Renders the admin panel, displaying all orders and materials.
     *
     * @param connectionPool Connection pool for database connections.
     * @param ctx            Context for handling the request.
     */
    public static void renderAdmin(ConnectionPool connectionPool, Context ctx) {
        try {
            List<Order> orderList = OrderMapper.getAllOrders(connectionPool);
            List<Material> materialList = MaterialMapper.getAllMaterials(connectionPool);

            ctx.attribute("orderlist", orderList);
            ctx.attribute("materiallist", materialList);
            ctx.render("admin.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("admin.html");
        }
    }


    public static void renderEditMaterial(ConnectionPool connectionPool, Context ctx) {
        try {
            int materialID = Integer.parseInt(ctx.formParam("material_id"));
            Material material = MaterialMapper.getMaterialById(materialID, connectionPool);

            ctx.attribute("material", material);
            ctx.render("editMaterial.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("editMaterial.html");
        }
    }

    private static void changeMaterialPrice(ConnectionPool connectionPool, Context ctx) {
        try {
            int materialID = Integer.parseInt(ctx.formParam("material_id"));
            double materialPrice = Double.parseDouble(ctx.formParam("material_price"));

            Material material = MaterialMapper.getMaterialById(materialID, connectionPool);
            MaterialMapper.changeMaterialPrice(materialPrice, materialID, connectionPool);
            material.setPrice(materialPrice);

            ctx.attribute("material", material);
            ctx.attribute("message", material.getName() + " had it's price changed to" + materialPrice);
            ctx.render("editMaterial.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("editMaterial.html");
        }
    }

    private static void inspectOrder(ConnectionPool connectionPool, Context ctx) {
        try {
            Locale.setDefault(new Locale("US"));
            int orderID = Integer.parseInt(ctx.formParam("order_id"));
            Order order = OrderMapper.getOrderByOrderId(orderID, connectionPool);
            Carport carport = order.getCarport();

            CarportSVG carportSVG = new CarportSVG(carport.getWidth(), carport.getLength());

            ctx.attribute("order", order);
            ctx.sessionAttribute("svg", carportSVG.toString());
            ctx.sessionAttribute("carport", carport);
            ctx.render("orderSite3.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("admin.html");
        }
    }

}


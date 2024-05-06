package app.carport.Controllers;

import app.carport.Entities.Material;
import app.carport.Entities.Order;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.MaterialMapper;
import app.carport.Persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class AdminPanelController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("removerOrder", ctx -> removeOrder(connectionPool, ctx));
        app.post("removematerial", ctx -> removeMaterial(connectionPool, ctx));
        app.post("addmaterial", ctx -> addMaterial(connectionPool, ctx));
        app.post("renderadmin",ctx -> renderAdmin(connectionPool,ctx));
        app.post("acceptorder",ctx -> acceptOrder(connectionPool,ctx));
        app.post("denyorder",ctx -> denyOrder(connectionPool,ctx));
    }

    private static void addMaterial(ConnectionPool connectionPool, Context ctx) {
        try {
            String name = ctx.formParam("name");
            double price = Double.parseDouble(ctx.formParam("price"));
            double length = Double.parseDouble(ctx.formParam("length"));
            String unit = ctx.formParam("unit");
            int quantityInStock = Integer.parseInt(ctx.formParam("quantityInStock"));
            MaterialMapper.addMaterial(connectionPool, name, price, length, unit, quantityInStock);
            renderAdmin(connectionPool,ctx);
        } catch (NumberFormatException e) {
            renderAdmin(connectionPool,ctx);
        }
    }

    private static void denyOrder(ConnectionPool connectionPool, Context ctx) {
        try {
            int orderID = Integer.parseInt(ctx.formParam("deny_order"));
            OrderMapper.denyOrder(connectionPool, orderID);
            renderAdmin(connectionPool,ctx);
        } catch (NumberFormatException e) {
            renderAdmin(connectionPool,ctx);
        }
    }

    private static void removeOrder(ConnectionPool connectionPool, Context ctx) {
        try {
            int orderID = Integer.parseInt(ctx.formParam("remove_order"));
            OrderMapper.deleteOrderById(orderID, connectionPool);
            renderAdmin(connectionPool,ctx);
        } catch (NumberFormatException | DatabaseException e) {
            renderAdmin(connectionPool,ctx);
        }
    }

    private static void acceptOrder(ConnectionPool connectionPool, Context ctx) {
        try {
            int orderID = Integer.parseInt(ctx.formParam("accept_order"));
            OrderMapper.acceptOrder(connectionPool, orderID);
            renderAdmin(connectionPool,ctx);
        } catch (NumberFormatException e) {
            renderAdmin(connectionPool,ctx);
        }
    }

    private static void removeMaterial(ConnectionPool connectionPool, Context ctx) {
        try{
            int materialID = Integer.parseInt(ctx.formParam("remove_material"));
            MaterialMapper.deleteMaterialById(connectionPool,materialID);
            renderAdmin(connectionPool,ctx);
        } catch (NumberFormatException | DatabaseException e) {
            renderAdmin(connectionPool,ctx);
        }
    }

    public static void renderAdmin(ConnectionPool connectionPool,Context ctx) {
        try {
            List<Order> orderList = OrderMapper.getAllOrders(connectionPool);
            orderList.forEach(order -> {
                double totalPrice = order.getCarport().getTotalPrice();
                order.getCarport().setTotalPrice(totalPrice);
            });
            List<Material> materialList = MaterialMapper.getAllMaterials(connectionPool);
            ctx.attribute("orderlist", orderList);
            ctx.attribute("materiallist", materialList);
            ctx.render("admin.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("admin.html");
        }
    }
}


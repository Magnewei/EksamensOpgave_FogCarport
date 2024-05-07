package app.carport.Persistence;

import app.carport.Entities.*;
import app.carport.Exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class OrderMapper {
    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orderList = new ArrayList<>();
        String sql = "select * from orders";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("orderID");
                String status = rs.getString("status");
                int userId = rs.getInt("userID");
                int carportId = rs.getInt("carportID");

                User user = UserMapper.getUserByUserId(userId,connectionPool);
                Carport carport = CarportMapper.getCarportByCarportId(carportId,connectionPool);
                orderList.add(new Order(orderId, status, user,carport));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl!!!!", e.getMessage());
        }
        return orderList;
    }

    public static void deleteOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sqlDeleteOrderline = "DELETE FROM orderline WHERE \"orderID\" = ?";
        String sqlDeleteOrder = "DELETE FROM orders WHERE \"orderID\" = ?";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement psOrderline = connection.prepareStatement(sqlDeleteOrderline)) {
                psOrderline.setInt(1, orderId);
                psOrderline.executeUpdate();
            }
            try (PreparedStatement psOrder = connection.prepareStatement(sqlDeleteOrder)) {
                psOrder.setInt(1, orderId);
                int rowsAffected = psOrder.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DatabaseException("Fejl i opdatering af en order");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved sletning af en order", e.getMessage());
        }
    }

    public static int getLastOrder(ConnectionPool connectionPool) throws DatabaseException {
        int orderNumber = 0;
        String sql = "SELECT \"orderID\" " + "FROM orders " + "ORDER BY \"orderID\" DESC " + "LIMIT 1;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            if (rs.next()) {
                orderNumber = rs.getInt("orderID");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving the latest order ID", e.getMessage());
        }
        return orderNumber;
    }

    //TODO Evt. lav om så den tager en Carport som argument og finder dennes id via .getCarportId metode
    public static void insertNewOrder(User user, String status, int carportId, ConnectionPool connectionPool) throws DatabaseException {
        String sqlMakeOrder = "INSERT INTO orders (\"status\",\"userID\",\"carportID\") VALUES (?,?,?)";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlMakeOrder)) {
            ps.setString(1, status);
            ps.setInt(2, user.getUserID());
            ps.setInt(3, carportId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl i opdatering af ordrer, se String sqlMakeOrder");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved indsættelse af ordre", e.getMessage());
        }
    }

    public static void updateStatus(int orderId, String status, ConnectionPool connectionPool) throws DatabaseException {
        String sqlUpdateStatus = "UPDATE orders SET \"status\" = ? WHERE \"orderID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlUpdateStatus)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Error updating order status, see sqlUpdateStatus");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating order status", e.getMessage());
        }
    }

    public static void acceptOrder(ConnectionPool connectionPool, int orderID) {
        String sql = "UPDATE orders SET status = 'accepted' WHERE \"orderID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void denyOrder(ConnectionPool connectionPool, int orderID) {
        String sql = "UPDATE orders SET status = 'denied' WHERE \"orderID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Order getOrderByOrderId(int orderID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders WHERE \"orderID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                User user = UserMapper.getUserByUserId(rs.getInt("userID"), connectionPool);
                Carport carport = CarportMapper.getCarportByCarportId(rs.getInt("carportID"), connectionPool);
                return new Order(orderID, status, user, carport);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Get order by orderID fejl", e.getMessage());
        }
        return null;
    }
}


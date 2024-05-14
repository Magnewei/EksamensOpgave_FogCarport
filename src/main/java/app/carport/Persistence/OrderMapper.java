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
        String sql = "select * from orders INNER JOIN users ON orders.\"userID\" = users.\"userID\" INNER JOIN carport ON orders.\"carportID\" = carport.\"carportID\"";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("orderID");
                String status = rs.getString("status");
                int userId = rs.getInt("userID");

                String email = rs.getString("email");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");

                double length = rs.getDouble("length");
                double width = rs.getDouble("width");
                boolean withRoof = rs.getBoolean("withRoof");

                User user = new User(userId, email, firstName, lastName);
                Carport carport = new Carport(length, width, withRoof);
                orderList.add(new Order(orderId, status, user, carport));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get orders from database.", e.getMessage());
        }
        return orderList;
    }


    public static List<Order> getAllOrdersJoin(ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orderList = new ArrayList<>();


        String sql = "select * from orders";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("orderID");
                String status = rs.getString("status");
                int userId = rs.getInt("userID");
                int carportId = rs.getInt("carportID");

                User user = UserMapper.getLimitedUserByUserId(userId, connectionPool);
                Carport carport = CarportMapper.getCarportByCarportId(carportId, connectionPool);
                orderList.add(new Order(orderId, status, user, carport));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get orders from database.", e.getMessage());
        }
        return orderList;
    }

    public static List<Order> getReducedOrdersWithUsers(ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orderList = new ArrayList<>();
        String sql = "select \"orderID\", status, \"userID\", email, \"firstName\", \"lastName\" from orders INNER JOIN users ON orders.\"userID\" = users.\"userID\"";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("orderID");
                String status = rs.getString("status");

                int userID = rs.getInt("userID");
                String email = rs.getString("email");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");

                User user = new User(userID, email, firstName, lastName);

                orderList.add(new Order(orderId, status, user));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get reduced orders and users from database.", e.getMessage());
        }
        return orderList;
    }

    public static boolean deleteOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM orders WHERE \"orderID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't delete the order from orderID.", e.getMessage());
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

    public static boolean insertNewOrder(User user, int carportId, ConnectionPool connectionPool) throws DatabaseException {
        String sqlMakeOrder = "INSERT INTO orders (\"userID\",\"carportID\") VALUES (?,?)";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlMakeOrder)) {

            ps.setInt(1, user.getUserID());
            ps.setInt(2, carportId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't insert the order.", e.getMessage());
        }
    }

    public static boolean updateStatus(int orderId, String status, ConnectionPool connectionPool) throws DatabaseException {
        String sqlUpdateStatus = "UPDATE orders SET \"status\" = ? WHERE \"orderID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlUpdateStatus)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error updating order status", e.getMessage());
        }
    }

    public static boolean acceptOrder(ConnectionPool connectionPool, int orderID) throws DatabaseException {
        String sql = "UPDATE orders SET status = 'accepted' WHERE \"orderID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error accepting order.", e.getMessage());
        }
    }

    public static boolean denyOrder(ConnectionPool connectionPool, int orderID) throws DatabaseException {
        String sql = "UPDATE orders SET status = 'denied' WHERE \"orderID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error denying order.", e.getMessage());
        }
    }

    public static Order getOrderByUserId(int userID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders INNER JOIN carport ON orders.\"carportID\" = carport.\"carportID\" WHERE \"userID\" = ? ORDER BY \"orderID\" DESC LIMIT 1";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int orderID = rs.getInt("orderID");
                String status = rs.getString("status");

                double length = rs.getDouble("length");
                double width = rs.getDouble("width");
                boolean withRoof = rs.getBoolean("withRoof");

                Carport carport = new Carport(length, width, withRoof);
                return new Order(orderID, status, carport);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get order from userID.", e.getMessage());
        }
        return null;
    }

    public static Order getReducedOrderByUserId(int userID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT \"orderID\", \"status\" FROM orders WHERE \"userID\" = ? ORDER BY \"orderID\" DESC LIMIT 1";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int orderID = rs.getInt("orderID");
                String status = rs.getString("status");
                return new Order(orderID, status);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get reduced order from userID.", e.getMessage());
        }
        return null;
    }

    public static boolean checkIfUserHasOrder(int userID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT COUNT(*) AS count FROM orders WHERE \"userID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't find an order from the userID, or the userID doesn't exist.", e.getMessage());
        }
        return false;
    }

    public static Order getOrderByOrderId(int orderID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders INNER JOIN carport ON orders.\"carportID\" = carport.\"carportID\" WHERE \"orderID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                int userID = rs.getInt("userID");

                double length = rs.getDouble("length");
                double width = rs.getDouble("width");
                boolean withRoof = rs.getBoolean("withRoof");

                String email = rs.getString("email");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");

                Carport carport = new Carport(length, width, withRoof);
                User user = new User(userID, email, firstName, lastName);
                return new Order(orderID, status, user, carport);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get order from orderID.", e.getMessage());
        }
        return null;
    }
}


package app.carport.Persistence;

import app.carport.Entities.Carport;
import app.carport.Entities.Order;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import app.carport.Services.MailServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for order entities within the application.
 */
public class OrderMapper {

    /**
     * Retrieves a list of all orders from the database, including related user and carport details.
     *
     * @param connectionPool Connection pool for database connections.
     * @return A list of all orders.
     * @throws DatabaseException If there is a problem executing the query.
     */
    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orderList = new ArrayList<>();
        String sql = "select * from orders INNER JOIN users ON orders.\"userID\" = users.\"userID\" INNER JOIN carport ON orders.\"carportID\" = carport.\"carportID\"";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("orderID");
                String status = rs.getString("status");
                int price = rs.getInt("price");
                int userId = rs.getInt("userID");

                String email = rs.getString("email");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");

                double length = rs.getDouble("length");
                double width = rs.getDouble("width");
                boolean withRoof = rs.getBoolean("withRoof");

                User user = new User(userId, email, firstName, lastName);
                Carport carport = new Carport(length, width, withRoof);
                orderList.add(new Order(orderId, status, user, carport, price));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get orders from database.", e.getMessage());
        }
        return orderList;
    }

    /**
     * Deletes an order by its ID from the database.
     *
     * @param orderId        The ID of the order to delete.
     * @param connectionPool Connection pool for database connections.
     * @return true if the deletion was successful, false otherwise.
     * @throws DatabaseException If there is a problem executing the delete operation.
     */
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

    /**
     * Retrieves the latest order ID from the database.
     *
     * @param connectionPool Connection pool for database connections.
     * @return The highest order ID.
     * @throws DatabaseException If there is a problem executing the query.
     */
    public static int getLastOrderID(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT \"orderID\" " + "FROM orders " + "ORDER BY \"orderID\" DESC " + "LIMIT 1;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            if (rs.next()) {
                return rs.getInt("orderID");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving the latest order ID", e.getMessage());
        }
        return 0;
    }

    /**
     * Inserts a new order into the database.
     *
     * @param user           User associated with the order.
     * @param carportId      ID of the carport associated with the order.
     * @param connectionPool Connection pool for database connections.
     * @return true if the insertion was successful, false otherwise.
     * @throws DatabaseException If there is a problem executing the insert operation.
     */
    public static boolean insertNewOrder(User user, int carportId,double price ,ConnectionPool connectionPool) throws DatabaseException {
        String sqlMakeOrder = "INSERT INTO orders (\"userID\",\"carportID\",\"price\") VALUES (?,?,?)";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlMakeOrder)) {

            ps.setInt(1, user.getUserID());
            ps.setInt(2, carportId);
            ps.setDouble(3,price);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't insert the order.", e.getMessage());
        }
    }

    /**
     * Updates the status of an existing order in the database.
     *
     * @param orderId        The ID of the order to update.
     * @param status         New status to be set.
     * @param connectionPool Connection pool for database connections.
     * @return true if the update was successful, false otherwise.
     * @throws DatabaseException If there is a problem executing the update operation.
     */
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

    /**
     * Marks an order as accepted within the database.
     *
     * @param orderID        The ID of the order to mark as accepted.
     * @param connectionPool Connection pool for database connections.
     * @return true if the status was updated to 'accepted', false otherwise.
     * @throws DatabaseException If there is a problem executing the update operation.
     */
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

    /**
     * Marks an order as denied within the database.
     *
     * @param orderID        The ID of the order to mark as denied.
     * @param connectionPool Connection pool for database connections.
     * @return true if the status was updated to 'denied', false otherwise.
     * @throws DatabaseException If there is a problem executing the update operation.
     */
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

    /**
     * Retrieves all orders associated with a specific user ID.
     *
     * @param userID         The ID of the user whose orders are to be retrieved.
     * @param connectionPool Connection pool for database connections.
     * @return A list of orders belonging to the specified user.
     * @throws DatabaseException If there is a problem executing the query.
     */
    public static List<Order> getOrdersByUserId(int userID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT \"orderID\",\"status\",\"price\" FROM orders * WHERE \"userID\" = ?;";
        List<Order> orders = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int orderID = rs.getInt("orderID");
                String status = rs.getString("status");
                int price = rs.getInt("price");

                orders.add(new Order(orderID, status, price));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get order from userID.", e.getMessage());
        }
        return orders;
    }

    /**
     * Checks if a specific user has any orders in the database.
     *
     * @param userID         The ID of the user to check.
     * @param connectionPool Connection pool for database connections.
     * @return true if the user has one or more orders, false otherwise.
     * @throws DatabaseException If there is a problem executing the query.
     */
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

    /**
     * Retrieves a specific order by its order ID, including detailed user and carport information.
     *
     * @param orderID        The ID of the order to retrieve.
     * @param connectionPool Connection pool for database connections.
     * @return The order if found, or null if not found.
     * @throws DatabaseException If there is a problem executing the query.
     */
    public static Order getOrderByOrderId(int orderID, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM users INNER JOIN orders ON users.\"userID\" = orders.\"userID\" INNER JOIN carport ON orders.\"carportID\" = carport.\"carportID\" WHERE \"orderID\" = ?";
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


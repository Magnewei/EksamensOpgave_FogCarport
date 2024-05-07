package app.carport.Persistence;

import app.carport.Entities.Address;
import app.carport.Entities.Order;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from users where email=? and password=?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("userID");
                boolean isAdmin = rs.getBoolean("isAdmin");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                Address address = AddressMapper.getAddressByAddressId(rs.getInt("addressID"), connectionPool);

                // If the user has an order, return the user object with the order.
                Order order = OrderMapper.getOrderByUserId(userId, connectionPool);
                return new User(userId, email, password, isAdmin, firstName, lastName, address, order);

            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Database does not contain a user with the given information.", e.getMessage());
        }
        return null;
    }

    public static void createUser(String Email, String password, int phoneNumber, Address address, String firstName, String lastName, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into users (email, password, role, phonenumber, firstname, lastname) values (?,?,?,?,?,?)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, Email);
            ps.setString(2, password);
            ps.setBoolean(3, false);  // Every user created should be a non-admin.
            ps.setInt(4, phoneNumber);
            ps.setString(5, firstName);
            ps.setString(6, lastName);
            AddressMapper.insertAddress(address, connectionPool);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl ved oprettelse af ny bruger");
            }
        } catch (SQLException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "Brugernavnet findes allerede. Vælg et andet";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static void deleteUser(int userID, ConnectionPool connectionPool) throws DatabaseException {
        String deleteOrderLinesSQL = "DELETE FROM orderline WHERE \"orderID\" IN (SELECT \"orderID\" FROM orders WHERE \"userID\" = ?)";
        String deleteOrdersSQL = "DELETE FROM orders WHERE \"userID\" = ?";
        String deleteUserSQL = "DELETE FROM users WHERE \"userID\" = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement deleteOrderLinesStatement = connection.prepareStatement(deleteOrderLinesSQL);
                PreparedStatement deleteOrdersStatement = connection.prepareStatement(deleteOrdersSQL);
                PreparedStatement deleteUserStatement = connection.prepareStatement(deleteUserSQL)
        ) {
            connection.setAutoCommit(false);
            deleteOrderLinesStatement.setInt(1, userID);
            deleteOrderLinesStatement.executeUpdate();
            deleteOrdersStatement.setInt(1, userID);
            deleteOrdersStatement.executeUpdate();

            deleteUserStatement.setInt(1, userID);
            int userRowsAffected = deleteUserStatement.executeUpdate();
            if (userRowsAffected != 1) {
                throw new DatabaseException("Error deleting user. User not found or multiple users deleted.");
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while deleting user.", e.getMessage());
        }
    }

    public static boolean checkIfUserExistsByName(String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT COUNT(*) AS count FROM users WHERE email = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kan ikke finde bruger ud fra navn.", e.getMessage());
        }
        return false;
    }

    public static List<User> getAllUsers(ConnectionPool connectionPool) throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("userID");
                String email = rs.getString("email");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isAdmin");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                Address address = AddressMapper.getAddressByAddressId(rs.getInt("addressID"), connectionPool);
                Order order = OrderMapper.getOrderByUserId(rs.getInt("addressID"), connectionPool);
                User user = new User(userId, email, password, isAdmin, firstName, lastName, address, order);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kan ikke hente alle brugerne fra databasen.", e.getMessage());

        }
        // Return users if List contains objects.
        return users = !users.isEmpty() ? users : null;
    }

    public static User getUserByUserId(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE \"userID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String email = rs.getString("email");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isAdmin");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                Address address = AddressMapper.getAddressByAddressId(rs.getInt("addressID"), connectionPool);
                // If the user has an order, return the user object with the order.

                Order order = OrderMapper.getOrderByUserId(userId, connectionPool);
                return new User(userId, email, password, isAdmin, firstName, lastName, address, order);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Get user fejl", e.getMessage());
        }
        return null;
    }

    public static User getLimitedUserByUserId(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT \"userID\", \"email\", \"firstName\", \"lastName\" FROM users WHERE \"userID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userID = rs.getInt("userID");
                String email = rs.getString("email");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                // If the user has an order, return the user object with the order.

                Order order = OrderMapper.getReducedOrderByUserId(userId, connectionPool);
                return new User(userID, email, firstName, lastName, order);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Get user fejl", e.getMessage());
        }
        return null;
    }

}
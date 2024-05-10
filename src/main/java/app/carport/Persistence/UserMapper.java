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
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("userID");
                boolean isAdmin = rs.getBoolean("isAdmin");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                Address address = AddressMapper.getAddressByAddressId(rs.getInt("addressID"), connectionPool);
                int phoneNumber = rs.getInt("phoneNumber");

                // If the user has an order, return the user object with the order.
                Order order = OrderMapper.getOrderByUserId(userId, connectionPool);
                return new User(userId, isAdmin, firstName, lastName, address, order, email, password, phoneNumber);

            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Database does not contain a user with the given information.", e.getMessage());
        }
        return null;
    }



    public static boolean createUser(User user, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into users (email, password, \"isAdmin\", phonenumber, \"firstName\", \"lastName\", \"addressID\") values (?,?,?,?,?,?,?)";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.isAdmin());  // Every user created should be a non-admin.
            ps.setInt(4, user.getPhoneNumber());
            ps.setString(5, user.getFirstName());
            ps.setString(6, user.getLastName());
            int addressId = AddressMapper.insertAddress(user.getAddress(), connectionPool);
            ps.setInt(7, addressId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            String msg = "An error occured. Try again.";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "The username already exists. Please choose another one.";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static boolean deleteUser(int userID, ConnectionPool connectionPool) throws DatabaseException {
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
            connection.commit(); // Commit transaction

            return userRowsAffected > 0;
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while deleting user.", e.getMessage());
        }
    }

    public static boolean checkIfUserExistsByName(String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT COUNT(*) AS count FROM users WHERE email = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get user from email.", e.getMessage());
        }
        return false;
    }

    public static List<User> getAllUsers(ConnectionPool connectionPool) throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
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
            throw new DatabaseException("Error. Couldn't get users from database.", e.getMessage());

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
                int phoneNumber = rs.getInt("phonenumber");

                Address address = AddressMapper.getAddressByAddressId(rs.getInt("addressID"), connectionPool);

                Order order = OrderMapper.getOrderByUserId(userId, connectionPool);

                return new User(userId, email, password, isAdmin, firstName, lastName, address, phoneNumber);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Error. Couldn't get user from userID.", e.getMessage());
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
            throw new DatabaseException("Error. Couldn't get reduced user from userID.", e.getMessage());
        }
        return null;
    }


    public static boolean updateUser(User user, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE users SET \"firstName\" = ?, \"lastName\" = ?, email = ?, password = ?, phoneNumber = ? WHERE \"userID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setInt(5, user.getphoneNumber());
            ps.setInt(6, user.getUserID());
            AddressMapper.updateAddress(user.getAddress(), connectionPool);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't update user from userID.", e.getMessage());
        }
    }

}
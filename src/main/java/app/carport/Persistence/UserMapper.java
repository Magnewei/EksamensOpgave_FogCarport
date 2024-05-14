package app.carport.Persistence;

import app.carport.Entities.Address;
import app.carport.Entities.Carport;
import app.carport.Entities.Order;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides database operations for managing user entities within the application.
 */
public class UserMapper {

    /**
     * Attempts to log in a user by verifying their email and password against the database.
     *
     * @param email          User's email.
     * @param password       User's password.
     * @param connectionPool Connection pool for database connections.
     * @return User object if credentials are valid, otherwise returns null.
     * @throws DatabaseException If there is an issue accessing the database.
     */
    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from users INNER JOIN address ON users.\"addressID\" = address.\"addressID\" INNER JOIN orders ON users.\"userID\" = orders.\"userID\" INNER JOIN postalcode ON address.postalcode = postalcode.postalcode INNER JOIN carport ON orders.\"carportID\" = carport.\"carportID\" WHERE email = ? AND password = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("userID");
                boolean isAdmin = rs.getBoolean("isAdmin");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                int phoneNumber = rs.getInt("phoneNumber");

                int addressId = rs.getInt("addressID");
                int postalCode = rs.getInt("postalcode");
                int houseNumber = rs.getInt("housenumber");
                String streetName = rs.getString("streetname");
                String cityName = rs.getString("cityname");

                Address address = new Address(addressId, postalCode, houseNumber, cityName, streetName);

                int orderID = rs.getInt("orderID");
                String status = rs.getString("status");

                double length = rs.getDouble("length");
                double width = rs.getDouble("width");
                boolean withRoof = rs.getBoolean("withRoof");
                Carport carport = new Carport(length, width, withRoof);

                // If the user has an order, return the user object with the order.
                Order order = new Order(orderID, status, carport);
                return new User(userId, isAdmin, firstName, lastName, address, order, email, password, phoneNumber);

            }
        } catch (SQLException e) {
            throw new DatabaseException("Database does not contain a user with the given information.", e.getMessage());
        }
        return null;
    }

    /**
     * Creates a new user in the database.
     *
     * @param user           User object containing all necessary user information.
     * @param connectionPool Connection pool for database connections.
     * @return true if the user is created successfully, false otherwise.
     * @throws DatabaseException If there is an issue executing the database operation.
     */
    public static boolean createUser(User user, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into users (email, password, \"isAdmin\", phonenumber, \"firstName\", \"lastName\", \"addressID\") values (?,?,?,?,?,?,?)";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.isAdmin());  // Every user created should be a non-admin.
            ps.setInt(4, user.getPhoneNumber());
            ps.setString(5, user.getFirstName());
            ps.setString(6, user.getLastName());
            int addressId = AddressMapper.insertAddress(user.getAddress(), connectionPool).getAddressID();
            ps.setInt(7, addressId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            String msg = "An error occured while creating a user. Try again.";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "The username already exists. Please choose another one.";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    /**
     * Creates a temporary user in the database. This function may not store all user details.
     *
     * @param user           User object containing minimal user information.
     * @param connectionPool Connection pool for database connections.
     * @return true if the temporary user is created successfully, false otherwise.
     * @throws DatabaseException If there is an issue executing the database operation.
     */
    public static boolean createTempUser(User user, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into users (email, \"isAdmin\", phonenumber, \"firstName\", \"lastName\", \"addressID\") values (?,?,?,?,?,?)";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setBoolean(2, user.isAdmin());  // Every user created should be a non-admin.
            ps.setInt(3, user.getPhoneNumber());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            int addressId = AddressMapper.insertAddress(user.getAddress(), connectionPool).getAddressID();
            ps.setInt(6, addressId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            String msg = "An error occured while creating a temporary user. Try again.";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "The username already exists. Please choose another one.";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    /**
     * Deletes a user by their ID, including all related records such as orders.
     *
     * @param userID         The ID of the user to delete.
     * @param connectionPool Connection pool for database connections.
     * @return true if the deletion is successful, false otherwise.
     * @throws DatabaseException If there is an issue executing the database operation.
     */
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

    /**
     * Checks if a user exists in the database based on their email.
     *
     * @param email          The email to check against the database.
     * @param connectionPool Connection pool for database connections.
     * @return true if the user exists, false otherwise.
     * @throws DatabaseException If there is an issue executing the database query.
     */
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

    /**
     * Retrieves all users from the database, including related orders and addresses.
     *
     * @param connectionPool Connection pool for database connections.
     * @return A list of users with their details.
     * @throws DatabaseException If there is an issue executing the database query.
     */
    public static List<User> getAllUsers(ConnectionPool connectionPool) throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users INNER JOIN address ON users.\"addressID\" = address.\"addressID\" INNER JOIN orders ON users.\"userID\" = orders.\"userID\" INNER JOIN postalcode ON address.postalcode = postalcode.postalcode INNER JOIN carport ON orders.\"carportID\" = carport.\"carportID\"";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("userID");
                String email = rs.getString("email");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isAdmin");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");

                int addressId = rs.getInt("addressID");
                int postalCode = rs.getInt("postalcode");
                int houseNumber = rs.getInt("housenumber");
                String streetName = rs.getString("streetname");
                String cityName = rs.getString("cityname");

                int orderID = rs.getInt("orderID");
                String status = rs.getString("status");

                double length = rs.getDouble("length");
                double width = rs.getDouble("width");
                boolean withRoof = rs.getBoolean("withRoof");

                Carport carport = new Carport(length, width, withRoof);

                Address address = new Address(addressId, postalCode, houseNumber, cityName, streetName);
                Order order = new Order(orderID, status, carport);
                User user = new User(userId, email, password, isAdmin, firstName, lastName, address, order);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get users from database.", e.getMessage());

        }
        // Return users if List contains objects.
        return users = !users.isEmpty() ? users : null;
    }

    /**
     * Retrieves a user by their user ID from the database.
     *
     * @param userId         The user ID to search for.
     * @param connectionPool Connection pool for database connections.
     * @return User object if found, otherwise null.
     * @throws DatabaseException If there is an issue accessing the database.
     */
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

                int addressId = rs.getInt("addressID");
                int postalCode = rs.getInt("postalcode");
                int houseNumber = rs.getInt("housenumber");
                String streetName = rs.getString("streetname");
                String cityName = rs.getString("cityname");

                Address address = new Address(addressId, postalCode, houseNumber, cityName, streetName);

                return new User(userId, email, password, isAdmin, firstName, lastName, address, phoneNumber);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get user from userID.", e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a limited user by their user ID, intended for situations where minimal user data is necessary.
     *
     * @param userId         The user ID to search for.
     * @param connectionPool Connection pool for database connections.
     * @return User object with limited details if found, otherwise null.
     * @throws DatabaseException If there is an issue accessing the database.
     */
    public static User getLimitedUserByUserId(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT \"userID\", \"email\", \"firstName\", \"lastName\", \"orderID\",status FROM users INNER JOIN orders ON users.\"userID\" = orders.\"userID\" WHERE users.\"userID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userID = rs.getInt("userID");
                String email = rs.getString("email");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                // If the user has an order, return the user object with the order.

                int orderID = rs.getInt("orderID");
                String status = rs.getString("status");

                Order order = new Order(orderID, status);
                return new User(userID, email, firstName, lastName, order);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get reduced user from userID.", e.getMessage());
        }
        return null;
    }

    /**
     * Updates a user's information in the database.
     *
     * @param user           User object containing updated details.
     * @param connectionPool Connection pool for database connections.
     * @return true if the update is successful, false otherwise.
     * @throws DatabaseException If there is an issue executing the database operation.
     */
    public static boolean updateUser(User user, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE users SET \"firstName\" = ?, \"lastName\" = ?, email = ?, password = ?, phonenumber = ? WHERE \"userID\" = ?";
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

    /**
     * Retrieves the latest user ID from the database. Typically used to get the most recently created user.
     *
     * @param connectionPool Connection pool for database connections.
     * @return The latest user ID.
     * @throws DatabaseException If there is an issue retrieving the latest user ID.
     */
    public static int getLastUserId(ConnectionPool connectionPool) throws DatabaseException {
        int orderNumber = 0;
        String sql = "SELECT \"userID\" " + "FROM users " + "ORDER BY \"userID\" DESC " + "LIMIT 1;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            if (rs.next()) {
                orderNumber = rs.getInt("userID");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving the latest user ID", e.getMessage());
        }
        return orderNumber;
    }
}
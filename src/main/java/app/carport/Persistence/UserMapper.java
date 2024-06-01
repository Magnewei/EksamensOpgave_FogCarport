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
        User user = null;

        String sql = "select * from users INNER JOIN address ON users.\"addressID\" = address.\"addressID\" " +
                "INNER JOIN postalcode ON address.postalcode = postalcode.postalcode " +
                "WHERE email = ? AND password = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("userID");
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

                user = new User(userId, isAdmin, firstName, lastName, address, null, email, password, phoneNumber);

            }

        } catch (SQLException e) {
            throw new DatabaseException("Database does not contain a user with the given information.", e.getMessage());
        }
        // If a user was not found in the above statement, then throw exception,
        // return to LoginController.login() and then warn user that email or password was incorrect.
        if (user == null) {
            throw new DatabaseException("User not found.");
        }

        String sqlOrdersCarports = "SELECT * FROM orders " +
                "INNER JOIN carport ON orders.\"carportID\" = carport.\"carportID\" " +
                "WHERE orders.\"userID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlOrdersCarports)) {
            ps.setInt(1, user.getUserID());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int orderID = rs.getInt("orderID");
                    String status = rs.getString("status");
                    double length = rs.getDouble("length");
                    double width = rs.getDouble("width");
                    boolean withRoof = rs.getBoolean("withRoof");

                    Carport carport = new Carport(length, width, withRoof);
                    Order order = new Order(orderID, status, carport);
                    user.setOrder(order);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

        return user;
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
            throw new DatabaseException("Error. Couldn't get all users from the database.", e.getMessage());

        }
        // Return users if List contains objects.
        return users = !users.isEmpty() ? users : null;
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
            throw new DatabaseException("Error. Couldn't update user from the given userID.", e.getMessage());
        }
    }


    /**
     * Updates the password for a given user in the database.
     *
     * @param user           The User object containing the user's ID and new password.
     * @param connectionPool The ConnectionPool from which to get the database connection.
     * @return true if the password was successfully updated, false otherwise.
     * @throws DatabaseException if there is an error during the update process.
     */
    public static boolean updatePassword(User user, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE users SET password = ? WHERE \"userID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getPassword());
            ps.setInt(2, user.getUserID());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't update user from the given userID.", e.getMessage());
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
            throw new DatabaseException("Error retrieving the latest userID", e.getMessage());
        }
        return orderNumber;
    }

    public static List<String> getAdminNames(ConnectionPool connectionPool) throws DatabaseException {
        List<String> adminNames = new ArrayList<>();
        String sql = "SELECT firstName, lastName FROM users WHERE isAdmin = true";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                adminNames.add(firstName + " " + lastName);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve admin names from the database.", e.getMessage());
        }
        return adminNames;
    }
}
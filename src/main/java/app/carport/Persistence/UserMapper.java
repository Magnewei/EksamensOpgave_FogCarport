package app.carport.Persistence;

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
                int id = rs.getInt("userID");
                boolean isAdmin = rs.getBoolean("isAdmin");
                return new User(id, email, password, isAdmin);
            } else {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        } catch (SQLException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }

    public static void createUser(String Email, String password, String role, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into users (email, password,role) values (?,?,?)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, Email);
            ps.setString(2, password);
            ps.setString(3, role);

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

    public static List<User> getAllUsers(ConnectionPool connectionPool) throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("userID");
                String email = rs.getString("email");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isAdmin");

                User user = new User(id, email, password, isAdmin);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kan ikke hente alle brugerne fra databasen.", e.getMessage());

        }
        return null;
    }

    public static User getUserByUserId(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE \"userID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userID = rs.getInt("userID");
                String email = rs.getString("email");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isAdmin");
                return new User(userID, email, password, isAdmin);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Get user fejl", e.getMessage());
        }
        return null;
    }
}
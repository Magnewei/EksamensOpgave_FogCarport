package app.carport.Persistence;

import app.carport.Entities.Carport;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class CarportMapper {
    public static Carport getCarportFromCarportId(int carportId, ConnectionPool connectionPool) {
        String sql = "SELECT * FROM users WHERE \"carportID\" = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setInt(1, carportId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("carportID");
                int length = rs.getInt("length");


                User user = new User(id, email, password, role);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kan ikke hente alle brugerne fra databasen.", e.getMessage());
        }
        return users;
    }
}

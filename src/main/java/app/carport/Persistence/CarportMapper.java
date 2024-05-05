package app.carport.Persistence;

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
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class CarportMapper {
    public static Carport getCarportByCarportId(int carportId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM carport WHERE \"carportID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, carportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int carportID = rs.getInt("carportID"); //
                double length = rs.getDouble("length");
                double width = rs.getDouble("width");
                boolean hasShed = rs.getBoolean("hasShed");
                //TODO: Få liste af materialer der skal bruges til en carport med
                return new Carport(carportID, length, width, hasShed,new ArrayList<>());

            }
        } catch (SQLException e) {
            throw new DatabaseException("Get user fejl", e.getMessage());
        }
        return null;
    }

    public Carport getCarportByOrder(Order order, ConnectionPool connectionPool) {
        return null;
    }
}

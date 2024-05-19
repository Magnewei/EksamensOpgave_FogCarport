package app.carport.Persistence;

import app.carport.Entities.Carport;
import app.carport.Exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles database operations for carport entities within the application.
 */
public class CarportMapper {

    /**
     * Retrieves a Carport by its unique ID from the database.
     *
     * @param carportId      The ID of the carport to retrieve.
     * @param connectionPool Connection pool for database connections.
     * @return Carport object if found, otherwise returns null.
     * @throws DatabaseException If there is a problem executing the query.
     */
    public static Carport getCarportByCarportId(int carportId, ConnectionPool connectionPool) throws DatabaseException {
        String sqlGetCarportData = "SELECT * FROM carport WHERE \"carportID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlGetCarportData)) {
            ps.setInt(1, carportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double length = rs.getDouble("length");
                double width = rs.getDouble("width");
                boolean withRoof = rs.getBoolean("withRoof");
                return new Carport(length, width, withRoof);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't retrieve carport from carportID.", e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a carport ID by matching dimensions and roof presence.
     *
     * @param width          Width of the carport to find.
     * @param length         Length of the carport to find.
     * @param withRoof       Boolean indicating if the carport includes a roof.
     * @param connectionPool Connection pool for database connections.
     * @return The ID of the matching carport or 0 if no match is found.
     * @throws DatabaseException If there is a problem executing the query.
     */
    public static int getCarportIDByWidthAndLength(double width, double length, boolean withRoof, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM carport WHERE \"width\" = ? AND \"length\" = ? AND \"withRoof\"=?;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, width);
            ps.setDouble(2, length);
            ps.setBoolean(3, withRoof);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int carportId = rs.getInt("carportID");
                return carportId;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't retrieve the carportID from the given width and length.", e.getMessage());
        }
        return 0;
    }
}

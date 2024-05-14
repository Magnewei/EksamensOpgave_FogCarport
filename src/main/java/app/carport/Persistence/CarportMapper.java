package app.carport.Persistence;

import app.carport.Entities.Carport;
import app.carport.Entities.Material;
import app.carport.Exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class CarportMapper {
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

    public static int getCarportByWidthAndLength(double width, double length, boolean withRoof, ConnectionPool connectionPool) throws DatabaseException {
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
            throw new DatabaseException("Error. Couldn't retrieve the adress data from the given addressID.", e.getMessage());
        }
        return 0;
    }

    public static void addAllPossibleMaterialsToDb(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO \"materialUsage\" (\"carportID\", \"materialID\", \"quantity\") VALUES (?,?,?)";
        double[] possibleLengths = {420, 480, 540, 600, 660, 720, 780}; // replace with your possible lengths
        double[] possibleWidths = {300, 360, 420, 480, 540, 600}; // replace with your possible widths
        boolean[] possibleRoofTypes = {true, false}; // with or without roof
        Carport test = new Carport(660,360,true);
        test.setMaterialList(connectionPool);

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            for (double length : possibleLengths) {
                for (double width : possibleWidths) {
                    for (boolean withRoof : possibleRoofTypes) {
                        Carport carport = new Carport(length, width, withRoof);
                        carport.setMaterialList(connectionPool);
                        int carportId = getCarportByWidthAndLength(carport.getWidth(), carport.getLength(), carport.isWithRoof(), connectionPool);
                        for (Map.Entry<Material, Integer> entry : carport.getMaterialList().entrySet()) {
                            ps.setInt(1, carportId);
                            ps.setInt(2, entry.getKey().getMaterialID());
                            ps.setInt(3, entry.getValue());
                            ps.addBatch();
                        }
                    }
                }
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new DatabaseException("Error while adding materials to the database.", e.getMessage());
        }
    }
}

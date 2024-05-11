package app.carport.Persistence;

import app.carport.Entities.*;
import app.carport.Exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class CarportMapper {
    public static Carport getCarportByCarportId(int carportId, ConnectionPool connectionPool) throws DatabaseException {
        String sqlGetCarportData = "SELECT * FROM carport WHERE \"carportID\" = ?";
        String sqlGetMaterials = "SELECT * FROM \"materialUsage\" WHERE \"carportID\" = ?";
        int carportID = 0;
        double length = 0;
        double width = 0;
        boolean withRoof = false;
        Map<Material,Integer> materialList = new HashMap<>();

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlGetCarportData);) {
            ps.setInt(1, carportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                carportID = rs.getInt("carportID"); //
                length = rs.getDouble("length");
                width = rs.getDouble("width");
                withRoof = rs.getBoolean("withRoof");
            }
        }catch (SQLException e) {
            throw new DatabaseException("Get carport data fejl", e.getMessage());
        }

        try(Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlGetMaterials);){
            ps.setInt(1, carportId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int materialId = rs.getInt("materialID");
                int quantity = rs.getInt("quantity");
                materialList.put(MaterialMapper.getMaterialById(materialId,connectionPool),quantity);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get the carport from the given carportID.", e.getMessage());
        }

        return new Carport(carportID,length,width,withRoof,materialList);

    }
    public static int getCarportByWidthAndLength(double width,double length, boolean withRoof, ConnectionPool connectionPool) throws DatabaseException {
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
}

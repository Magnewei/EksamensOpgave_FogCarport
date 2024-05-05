package app.carport.Persistence;

import app.carport.Entities.Carport;
import app.carport.Entities.Material;
import app.carport.Entities.Order;
import app.carport.Entities.User;
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
        boolean hasShed = false;

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlGetCarportData);) {
            ps.setInt(1, carportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                carportID = rs.getInt("carportID"); //
                length = rs.getDouble("length");
                width = rs.getDouble("width");
                hasShed = rs.getBoolean("hasShed");
            }
        }catch (SQLException e) {
            throw new DatabaseException("Get carport data fejl", e.getMessage());
        }

        try(Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sqlGetMaterials);){
            ps.setInt(1, carportId);
            ResultSet rs = ps.executeQuery();
            Map<Material,Integer> materialList = new HashMap<>();
            while (rs.next()) {
                int materialId = rs.getInt("materialID");
                int quantity = rs.getInt("quantity");
                materialList.put(MaterialMapper.getMaterialById(materialId,connectionPool),quantity);
                return new Carport(carportID,length,width,hasShed,materialList);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Get user fejl", e.getMessage());
        }

        return null;
    }

}

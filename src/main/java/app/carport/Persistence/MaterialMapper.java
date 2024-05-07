package app.carport.Persistence;

import app.carport.Entities.Material;
import app.carport.Exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialMapper {

    public static List<Double> getAllLength(ConnectionPool connectionPool) throws DatabaseException {
        List<Double> LengthList = new ArrayList<>();
        String sql = "SELECT DISTINCT length FROM carport ORDER BY length;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Double length = rs.getDouble("Length");
                LengthList.add(length);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Length fejl fejl", e.getMessage());
        }
        return LengthList;
    }


    public static List<Double> getAllWidth(ConnectionPool connectionPool) throws DatabaseException {
        List<Double> WidthList = new ArrayList<>();
        String sql = "SELECT DISTINCT width FROM carport ORDER BY width;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Double Width = rs.getDouble("width");
                WidthList.add(Width);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Width fejl", e.getMessage());
        }
        return WidthList;
    }

    public static List<Material> getAllMaterials(ConnectionPool connectionPool) throws DatabaseException {
        List<Material> materialList = new ArrayList<>();
        String sql = "select * from material";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int materialId = rs.getInt("materialID");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                double length = rs.getDouble("length");
                String unit = rs.getString("unit");
                int quantityInStock = rs.getInt("quantityInStock");
                materialList.add(new Material(materialId, name, price, length, unit, quantityInStock));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved returnering af materialeliste", e.getMessage());
        }
        return materialList;
    }

    public static void deleteMaterialById(ConnectionPool connectionPool, int materialID) throws DatabaseException {
        String sql = "DELETE FROM material WHERE \"materialID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialID);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl ved sletning af materiale");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved sletning af en task", e.getMessage());
        }
    }

    public static Material getMaterialById(int materialId,ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM material WHERE \"materialID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int materialID = rs.getInt("materialID");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                double length = rs.getDouble("length");
                String unit = rs.getString("unit");
                int quantityInStock = rs.getInt("quantityInStock");
                return new Material(materialId,name,price,length,unit,quantityInStock);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Get user fejl", e.getMessage());
        }
        return null;
    }

    public static void addMaterial(ConnectionPool connectionPool, String name, double price, double length, String unit, int quantityInStock) {
        String sql = "INSERT INTO material (name, price, length, unit, quantityInStock) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setDouble(3, length);
            ps.setString(4, unit);
            ps.setInt(5, quantityInStock);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




  /* public static List<String> getAllName(ConnectionPool connectionPool) throws DatabaseException {
        List<String> NameList = new ArrayList<>();
        String sql = "SELECT DISTINCT name FROM material;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String Name = rs.getString("name");
                NameList.add(Name);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Navn Fejl", e.getMessage());
        }
        return NameList;
    }

*/
}

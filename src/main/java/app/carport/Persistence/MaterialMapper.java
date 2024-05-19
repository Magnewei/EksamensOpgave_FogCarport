package app.carport.Persistence;

import app.carport.Entities.Material;
import app.carport.Exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides database operations for managing materials within the application.
 */
public class MaterialMapper {

    /**
     * Retrieves a list of all distinct lengths of carports stored in the database.
     *
     * @param connectionPool Connection pool for database connections.
     * @return A list of all distinct lengths.
     * @throws DatabaseException If there is a problem executing the query.
     */
    public static List<Double> getAllLength(ConnectionPool connectionPool) throws DatabaseException {
        List<Double> LengthList = new ArrayList<>();
        String sql = "SELECT DISTINCT length FROM carport ORDER BY length;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Double length = rs.getDouble("Length");
                LengthList.add(length);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get retrieve length element from database.", e.getMessage());
        }
        return LengthList;
    }

    /**
     * Retrieves a list of all distinct widths of carports stored in the database.
     *
     * @param connectionPool Connection pool for database connections.
     * @return A list of all distinct widths.
     * @throws DatabaseException If there is a problem executing the query.
     */
    public static List<Double> getAllWidth(ConnectionPool connectionPool) throws DatabaseException {
        List<Double> WidthList = new ArrayList<>();
        String sql = "SELECT DISTINCT width FROM carport ORDER BY width;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Double Width = rs.getDouble("width");
                WidthList.add(Width);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get retrieve width element from database.", e.getMessage());
        }
        return WidthList;
    }

    /**
     * Retrieves a list of all materials available in the database.
     *
     * @param connectionPool Connection pool for database connections.
     * @return A list of materials.
     * @throws DatabaseException If there is a problem executing the query.
     */
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
            throw new DatabaseException("Error. Couldn't get list containing all materials from database.", e.getMessage());
        }
        return materialList;
    }

    /**
     * Deletes a material by its ID from the database.
     *
     * @param connectionPool Connection pool for database connections.
     * @param materialID     The ID of the material to delete.
     * @return true if the deletion was successful, false otherwise.
     * @throws DatabaseException If there is a problem executing the delete operation.
     */
    public static boolean deleteMaterialById(ConnectionPool connectionPool, int materialID) throws DatabaseException {
        String sql = "DELETE FROM material WHERE \"materialID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't delete material from the given materialID.", e.getMessage());
        }
    }

    /**
     * Retrieves a specific material by its ID from the database.
     *
     * @param materialId     The ID of the material to retrieve.
     * @param connectionPool Connection pool for database connections.
     * @return The retrieved Material object, or null if not found.
     * @throws DatabaseException If there is a problem executing the query.
     */
    public static Material getMaterialById(int materialId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM material WHERE \"materialID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                double length = rs.getDouble("length");
                String unit = rs.getString("unit");
                int quantityInStock = rs.getInt("quantityInStock");
                return new Material(materialId, name, price, length, unit, quantityInStock);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't get material from the given materialID.", e.getMessage());
        }
        return null;
    }

    /**
     * Adds a new material to the database.
     *
     * @param connectionPool  Connection pool for database connections.
     * @param name            Name of the material.
     * @param price           Price of the material.
     * @param length          Length of the material.
     * @param unit            Unit of measurement for the material.
     * @param quantityInStock Quantity of the material in stock.
     * @return true if the material was added successfully, false otherwise.
     * @throws DatabaseException If there is a problem executing the insert operation.
     */
    public static boolean addMaterial(ConnectionPool connectionPool, String name, double price, double length, String unit, int quantityInStock) throws DatabaseException {
        String sql = "INSERT INTO material (name, price, length, unit, \"quantityInStock\") VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setDouble(3, length);
            ps.setString(4, unit);
            ps.setInt(5, quantityInStock);
            int executeUpdate = ps.executeUpdate();
            return executeUpdate > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't add the material to the database from the given information.", e.getMessage());
        }
    }

    /**
     * Updates the details of an existing material in the database.
     *
     * @param connectionPool  The ConnectionPool from which to get the database connection.
     * @param materialID      The ID of the material to update.
     * @param name            The new name of the material.
     * @param price           The new price of the material.
     * @param length          The new length of the material.
     * @param unit            The unit of measurement for the material.
     * @param quantityInStock The new stock quantity of the material.
     */
    public static boolean updateMaterial(ConnectionPool connectionPool, int materialID, String name, double price, double length, String unit, int quantityInStock) throws DatabaseException {
        String sql = "UPDATE material SET name = ?, price = ?, length = ?, unit = ?, \"quantityInStock\" = ? WHERE \"materialID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setDouble(3, length);
            ps.setString(4, unit);
            ps.setInt(5, quantityInStock);
            ps.setInt(6, materialID);
            int executeUpdate = ps.executeUpdate();
            return executeUpdate > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't update the material information from the given information.", e.getMessage());
        }
    }

    /**
     * Updates the stock quantities of materials in the database by subtracting the specified quantities.
     * This method iterates over a map of materials and their quantities to be removed, updating the database accordingly.
     *
     * @param materialList   A map containing Material objects as keys and integers representing the quantity of each material to be removed.
     * @param connectionPool The connection pool from which to obtain database connections.
     * @return true if the update operation completes successfully for all materials.
     * @throws DatabaseException If a SQLException occurs during database update operations.
     */
    public static boolean removeMaterialStockOnOrder(Map<Material, Integer> materialList, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE material SET \"quantityInStock\" = \"quantityInStock\" - ? WHERE \"name\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Map.Entry<Material, Integer> entry : materialList.entrySet()) {
                ps.setInt(1, entry.getValue()); // quantityToRemove
                ps.setString(2, entry.getKey().getName()); // materialName
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't remove material stock from the database.", e.getMessage());
        }
    }
}

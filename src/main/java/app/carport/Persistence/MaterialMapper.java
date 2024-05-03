package app.carport.Persistence;

import app.carport.Exceptions.DatabaseException;

import javax.naming.Name;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialMapper {

    public static List<String> getAllLength(ConnectionPool connectionPool) throws DatabaseException {
        List<String> LengthList = new ArrayList<>();
        String sql = "SELECT DISTINCT length FROM material;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String length = rs.getString("Length");
                LengthList.add(length);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Length fejl fejl", e.getMessage());
        }
        return LengthList;
    }


    public static List<String> getAllWidth(ConnectionPool connectionPool) throws DatabaseException {
        List<String> WidthList = new ArrayList<>();
        String sql = "SELECT DISTINCT width FROM material;";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql);) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String Width = rs.getString("width");
                WidthList.add(Width);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Width fejl", e.getMessage());
        }
        return WidthList;
    }




    public static List<String> getAllName(ConnectionPool connectionPool) throws DatabaseException {
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


}

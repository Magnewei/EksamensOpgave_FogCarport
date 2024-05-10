package app.carport.Persistence;

import app.carport.Entities.Address;
import app.carport.Exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressMapper {
    public static int insertAddress(Address address, ConnectionPool connectionPool) throws DatabaseException {
        String checkSql = "SELECT \"addressID\" FROM address WHERE postalcode = ? AND housenumber = ? AND streetname = ?";
        String insertSql = "INSERT INTO address (postalcode, housenumber, streetname) VALUES (?, ?, ?) RETURNING \"addressID\"";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement checkPs = connection.prepareStatement(checkSql);
                PreparedStatement insertPs = connection.prepareStatement(insertSql)
        ) {
            checkPs.setInt(1, address.getPostalCode());
            checkPs.setInt(2, address.getHouseNumber());
            checkPs.setString(3, address.getStreetName());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                // Address already exists, return the existing addressID
                return rs.getInt("addressID");
            } else {
                // Address does not exist, insert new address and return the new addressID
                insertPs.setInt(1, address.getPostalCode());
                insertPs.setInt(2, address.getHouseNumber());
                insertPs.setString(3, address.getStreetName());
                ResultSet rsInsert = insertPs.executeQuery();
                if (rsInsert.next()) {
                    return rsInsert.getInt("addressID");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while inserting the address.", e.getMessage());
        }
        return -1; // Return -1 if address could not be inserted or retrieved
    }

    public static boolean updateAddress(Address address, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE address SET \"streetname\"=?,\"postalcode\"=?,\"housenumber\"=? WHERE \"addressID\" = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, address.getStreetName());
            ps.setInt(3, address.getHouseNumber());
            ps.setInt(4, address.getAddressID());
            upgradeCityData(address, connectionPool);
            ps.setInt(2, address.getPostalCode());

            int rowsAffected = ps.executeUpdate();

            // Return true if insert was successful.
            return rowsAffected == 1;

        } catch (SQLException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "Addressen findes allerede. Vælg et andet";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static Address getAddressByAddressId(int addressId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM address WHERE \"addressID\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int postalCode = rs.getInt("postalcode");
                int houseNumber = rs.getInt("housenumber");
                String streetName = rs.getString("streetname");
                String cityName = getCtyNameFromPostcode(postalCode, connectionPool);
                return new Address(addressId, postalCode, houseNumber, cityName, streetName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Get address fejl", e.getMessage());
        }
        return null;
    }

    public static boolean insertCityData(Address address, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into postalcode (postalcode, cityname) values (?,?)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, address.getPostalCode());
            ps.setString(2, address.getCityName());

            int rowsAffected = ps.executeUpdate();

            // Return true if insert was successful.
            return rowsAffected == 1;

        } catch (SQLException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "Brugernavnet findes allerede. Vælg et andet";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static boolean upgradeCityData(Address address, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE postalcode SET \"postalcode\" = ?, \"cityname\" = ? WHERE \"postalcode\" = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, address.getPostalCode());
            ps.setString(2, address.getCityName());
            ps.setInt(3, address.getPostalCode());

            int rowsAffected = ps.executeUpdate();

            // Return true if insert was successful.
            return rowsAffected == 1;

        } catch (SQLException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "Brugernavnet findes allerede. Vælg et andet";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static String getCtyNameFromPostcode(int postalcode, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT cityname FROM postalcode WHERE \"postalcode\" = ?";

        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postalcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String cityname = rs.getString("cityname");

                return cityname;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Get address fejl", e.getMessage());
        }
        return null;
    }
}

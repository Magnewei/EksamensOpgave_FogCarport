package app.carport.Persistence;

import app.carport.Entities.Address;
import app.carport.Exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddressMapper {
    public static boolean insertAddress(Address address, ConnectionPool connectionPool) throws DatabaseException {
        // Assuming Address class has a getCityName method.
        String insertPostalCodeSQL = "INSERT INTO postalcode (postalcode, cityname) VALUES (?, ?) ON CONFLICT (postalcode) DO NOTHING;";
        String insertAddressSQL = "INSERT INTO address (streetname, postalcode, housenumber) VALUES (?, ?, ?);";

        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Insert or update postalcode entry
            try (PreparedStatement ps = connection.prepareStatement(insertPostalCodeSQL)) {
                ps.setInt(1, address.getPostalCode());
                ps.setString(2, address.getCityName());
                ps.executeUpdate(); // This might not change rows if postalcode already exists
            }

            // Insert address entry
            try (PreparedStatement ps = connection.prepareStatement(insertAddressSQL)) {
                ps.setString(1, address.getStreetName());
                ps.setInt(2, address.getPostalCode());
                ps.setInt(3, address.getHouseNumber());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected != 1) {
                    connection.rollback();
                    return false; // Rollback if address insert fails
                }
            }

            connection.commit(); // Commit both operations as a single transaction
            return true;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Ensure rollback on error
                } catch (SQLException ex) {
                    // Handle potential rollback error
                }
            }
            String msg = "Error occurred. Please try again";
            if (e.getMessage().contains("duplicate key value")) {
                msg = "An entry with the same key already exists. Please choose another.";
            }
            throw new DatabaseException(msg, e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit
                    connection.close();
                } catch (SQLException e) {
                    // Handle potential errors on closing
                }
            }
        }
    }
}

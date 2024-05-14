package app.carport.Persistence;

import app.carport.Entities.Address;
import app.carport.Exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides database operations for managing addresses within the application.
 */
public class AddressMapper {

    /**
     * Inserts a new address into the database and returns the newly created address with its generated ID.
     * @param address Address to be inserted.
     * @param connectionPool Connection pool for database connections.
     * @return The Address object with updated ID.
     * @throws DatabaseException If there is a problem executing the insert operation.
     */
    public static Address insertAddress(Address address, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO address (postalcode, housenumber, streetname) VALUES (?,?,?) RETURNING \"addressID\"";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            System.out.println("postalcode");
            ps.setInt(1, address.getPostalCode());
            ps.setInt(2, address.getHouseNumber());
            ps.setString(3, address.getStreetName());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int addressId = rs.getInt(1);
                return new Address(addressId, address.getPostalCode(), address.getHouseNumber(), address.getCityName(), address.getStreetName());
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while inserting the address.", e.getMessage());
        }
        return null;
    }

    /**
     * Updates an existing address in the database.
     * @param address Address to be updated.
     * @param connectionPool Connection pool for database connections.
     * @return true if the address was updated successfully, false otherwise.
     * @throws DatabaseException If there is a problem executing the update operation.
     */
    public static boolean updateAddress(Address address, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE address SET \"streetname\"=?,\"postalcode\"=?,\"housenumber\"=? WHERE \"addressID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, address.getStreetName());
            ps.setInt(3, address.getHouseNumber());
            ps.setInt(4, address.getAddressID());
            updateCityData(address, connectionPool);
            ps.setInt(2, address.getPostalCode());

            // Return true if insert was successful.
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't update the users address.", e.getMessage());
        }
    }

    /**
     * Retrieves an address by its ID from the database.
     * @param addressId The ID of the address to retrieve.
     * @param connectionPool Connection pool for database connections.
     * @return The retrieved Address object or null if no address is found.
     * @throws DatabaseException If there is a problem executing the query.
     */
    public static Address getAddressByAddressId(int addressId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM address INNER JOIN postalcode ON address.postalcode = postalcode.postalcode WHERE \"addressID\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int postalCode = rs.getInt("postalcode");
                int houseNumber = rs.getInt("housenumber");
                String streetName = rs.getString("streetname");
                String cityName = rs.getString("cityname");
                return new Address(addressId, postalCode, houseNumber, cityName, streetName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't retrieve the adress data from the given addressID.", e.getMessage());
        }
        return null;
    }

    /**
     * Inserts city data associated with an address into the database.
     * @param address Address containing the city data to be inserted.
     * @param connectionPool Connection pool for database connections.
     * @return true if the city data was inserted successfully, false otherwise.
     * @throws DatabaseException If there is a problem executing the insert operation.
     */
    public static boolean insertCityData(Address address, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into postalcode (postalcode, cityname) values (?,?)";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, address.getPostalCode());
            ps.setString(2, address.getCityName());

            int rowsAffected = ps.executeUpdate();

            // Return true if insert was successful.
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't insert the users city data.", e.getMessage());
        }
    }

    /**
     * Updates city data associated with an address in the database.
     * @param address Address containing the city data to be updated.
     * @param connectionPool Connection pool for database connections.
     * @return true if the city data was updated successfully, false otherwise.
     * @throws DatabaseException If there is a problem executing the update operation.
     */
    public static boolean updateCityData(Address address, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE postalcode SET \"postalcode\" = ?, \"cityname\" = ? WHERE \"postalcode\" = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, address.getPostalCode());
            ps.setString(2, address.getCityName());
            ps.setInt(3, address.getPostalCode());

            int rowsAffected = ps.executeUpdate();

            // Return true if insert was successful.
            return rowsAffected == 1;

        } catch (SQLException e) {
            throw new DatabaseException("Error. Couldn't update the users city data.", e.getMessage());
        }
    }
}

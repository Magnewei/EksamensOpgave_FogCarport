package app.carport.Persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class AddressMapperTest {

    private final static String DB_SCHEMA = "fogprojekt_test";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(
            System.getenv("JDBC_USER"),
            System.getenv("JDBC_PASSWORD"),
            System.getenv("JDBC_CONNECTION_STRING"),
            DB_SCHEMA);

    @BeforeAll
    static void setupClass() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement statement = connection.createStatement()) {

            } catch (SQLException e) {
                e.printStackTrace();
                fail("The connection to the database failed.");
            }
        }
    }


    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement statement = connection.createStatement()) {


            } catch (SQLException e) {
                e.printStackTrace();
                fail("The connection to the database failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("The connection to the database failed.");
        }
    }


    @Test
    void insertAddress() {
    }

    @Test
    void updateAddress() {
    }

    @Test
    void getAddressByAddressId() {
    }

    @Test
    void insertCityData() {
    }

    @Test
    void updateCityData() {
    }
}
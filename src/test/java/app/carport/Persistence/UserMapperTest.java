package app.carport.Persistence;

import app.carport.Entities.Address;
import app.carport.Entities.User;
import app.carport.Exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private static final String USER = System.getenv("JDBC_USER");
    private static final String PASSWORD = System.getenv("JDBC_PASSWORD");
    private static final String TEST_URL = System.getenv("JDBC_CONNECTION_STRING_TEST");
    private static final String DB = System.getenv("JDBC_DB");
    public static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, TEST_URL, DB);

    @BeforeAll
    static void setupClass() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test.users CASCADE;");
                stmt.execute("DROP TABLE IF EXISTS test.address CASCADE;");
                stmt.execute("DROP TABLE IF EXISTS test.postalcode CASCADE;");
                stmt.execute("DROP TABLE IF EXISTS test.orders CASCADE;");
                stmt.execute("DROP TABLE IF EXISTS test.carport CASCADE;");

                // Create sequences
                stmt.execute("DROP SEQUENCE IF EXISTS test.users_userid_seq;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_orderid_seq;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.carport_carportid_seq;");

                // Create test tables
                stmt.execute("CREATE TABLE test.users AS (SELECT * FROM public.\"users\") WITH NO DATA;");
                stmt.execute("CREATE TABLE test.address AS (SELECT * FROM public.\"address\") WITH NO DATA;");
                stmt.execute("CREATE TABLE test.postalcode AS (SELECT * FROM public.\"postalcode\") WITH NO DATA;");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * FROM public.\"orders\") WITH NO DATA;");
                stmt.execute("CREATE TABLE test.carport AS (SELECT * FROM public.\"carport\") WITH NO DATA;");

                // Set sequences
                stmt.execute("CREATE SEQUENCE test.users_userid_seq;");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN \"userID\" SET DEFAULT nextval('test.users_userid_seq');");

                stmt.execute("CREATE SEQUENCE test.orders_orderid_seq;");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN \"orderID\" SET DEFAULT nextval('test.orders_orderid_seq');");

                stmt.execute("CREATE SEQUENCE test.carport_carportid_seq;");
                stmt.execute("ALTER TABLE test.carport ALTER COLUMN \"carportID\" SET DEFAULT nextval('test.carport_carportid_seq');");

            } catch (SQLException e) {
                e.printStackTrace();
                fail("The connection to the database failed.");
            }
        }
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Clean up test tables
                stmt.execute("DELETE FROM test.users;");
                stmt.execute("DELETE FROM test.address;");
                stmt.execute("DELETE FROM test.postalcode;");
                stmt.execute("DELETE FROM test.orders;");
                stmt.execute("DELETE FROM test.carport;");

                // Insert test data
                stmt.execute("INSERT INTO test.postalcode (postalcode, cityname) VALUES (12345, 'TestCity');");
                stmt.execute("INSERT INTO test.address (\"addressID\", postalcode, housenumber, streetname) VALUES (1, 12345, 10, 'TestStreet');");

                stmt.execute("INSERT INTO test.users (\"userID\", \"firstName\", \"lastName\", \"password\", \"isAdmin\", \"email\", \"phonenumber\", \"addressID\") " +
                        "VALUES (1, 'magnus', 'nord', 'FogP1234#', true, 'Magnus@gmail.com', 3400, 1), " +
                        "(2, 'christian', 'syd', 'FogP1234!', false, 'christian@gmail.com', 3400, 1);");

                stmt.execute("INSERT INTO test.carport (\"carportID\", \"length\", \"width\", \"withRoof\") VALUES (1, 6.0, 3.0, true);");
                stmt.execute("INSERT INTO test.orders (\"orderID\", \"status\", \"userID\", \"carportID\") VALUES (1, 'completed', 1, 1);");

                // Set sequences
                stmt.execute("SELECT setval('test.users_userid_seq', COALESCE((SELECT MAX(\"userID\") + 1 FROM test.users), 1), false);");
                stmt.execute("SELECT setval('test.orders_orderid_seq', COALESCE((SELECT MAX(\"orderID\") + 1 FROM test.orders), 1), false);");
                stmt.execute("SELECT setval('test.carport_carportid_seq', COALESCE((SELECT MAX(\"carportID\") + 1 FROM test.carport), 1), false);");

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
    void login() {
        try {
            User loggedInUser = UserMapper.login("Magnus@gmail.com", "FogP1234#", connectionPool);
            assertNotNull(loggedInUser);
            assertEquals("magnus", loggedInUser.getFirstName());
            assertEquals("nord", loggedInUser.getLastName());
            assertEquals(1, loggedInUser.getOrder().getOrderId());
            assertEquals("completed", loggedInUser.getOrder().getStatus());
        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void createUser() {
        try {
            Address address = new Address(0, 3400, 10, "fredensborg", "kbhvej");
            User newUser = new User(0, false, "jon", "mikkel", address, null, "jon@gmail.com", "Password123!", 234234);

            boolean userCreated = UserMapper.createUser(newUser, connectionPool);
            assertTrue(userCreated);

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void checkIfUserExistsByName() {
        try {
            boolean userByname = UserMapper.checkIfUserExistsByName("christian@gmail.com", connectionPool);
            assertTrue(userByname);

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }


    @Test
    void updatePassword() {
        try {
            Address address = new Address(0, 3400, 10, "fredensborg", "kbhvej");
            User newUser = new User(1, false, "jon", "mikkel",address , null, "jon@gmail.com", "Password123!", 234234);
            boolean updatePassword = UserMapper.updatePassword(newUser, connectionPool);
            assertTrue(updatePassword);

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

}
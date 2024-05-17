package app.carport.Persistence;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.fail;

class AddressMapperTest {

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @BeforeAll
    static void setupClass() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS public.users");
                stmt.execute("DROP TABLE IF EXISTS public.orders");
                stmt.execute("DROP SEQUENCE IF EXISTS public.users_user_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS public.orders_order_id_seq CASCADE;");

                // Create tables as copy of original public schema structure
                stmt.execute("CREATE TABLE public.users AS (SELECT * from public.users) WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");

                // Create sequences for auto generating id's for users and orders
                stmt.execute("CREATE SEQUENCE test.users_user_id_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_user_id_seq')");
                stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");

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
package app.carport.Persistence;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @BeforeAll
    static void setupClass() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test.orders CASCADE;");
                stmt.execute("DROP TABLE IF EXISTS test.users CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.users_userid_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_orderID_seq CASCADE;");

                stmt.execute("CREATE TABLE test.users AS (SELECT * from public.\"users\") WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.\"orders\") WITH NO DATA");

                stmt.execute("CREATE SEQUENCE test.users_userID_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN \"userID\" SET DEFAULT nextval('test.users_userid_seq')");
                stmt.execute("CREATE SEQUENCE test.orders_orderID_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN \"orderID\" SET DEFAULT nextval('test.orders_orderID_seq')");

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
                stmt.execute("DELETE FROM test.orders");
                stmt.execute("DELETE FROM test.users");

                stmt.execute("INSERT INTO test.users (\"userID\",\"firstName\", \"lastName\", \"password\", \"isAdmin\")" +
                        "VALUES  (1, 'magnus','nord', 'FogP1234#', true), (2, 'christian', 'syd', 'FogP1234!', false)");

                stmt.execute("INSERT INTO test.orders (\"orderID\", \"status\", \"userID\", \"carportID\", \"price\") " +
                        "VALUES (1, 'denied', 1, 5, 20000), (2, 'accepted', 2, 2, 15000), (3, 'awaiting approval', 2, 5, 14000)") ;
                // Set sequence to continue from the largest member_id

                stmt.execute("SELECT setval('test.orders_orderID_seq', COALESCE((SELECT MAX(\"orderID\") + 1 FROM test.orders), 1), false)");
                stmt.execute("SELECT setval('test.users_userid_seq', COALESCE((SELECT MAX(\"userID\") + 1 FROM test.users), 1), false)");

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
    void getAllOrders() {
    }

    @Test
    void deleteOrderById() {
    }

    @Test
    void getLastOrderID() {
    }

    @Test
    void insertNewOrder() {
    }

    @Test
    void acceptOrder() {
    }

    @Test
    void denyOrder() {
    }

    @Test
    void getOrdersByUserId() {
    }

    @Test
    void getOrderByOrderId() {
    }
}
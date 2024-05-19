package app.carport.Persistence;

import app.carport.Entities.Order;
import app.carport.Exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @BeforeAll
    static void setupClass() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {

                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test.orders;");
                stmt.execute("DROP TABLE IF EXISTS test.users;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.users_userid_seq;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_orderid_seq;");

                stmt.execute("CREATE TABLE test.users AS (SELECT * from public.\"users\") WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.\"orders\") WITH NO DATA");

                stmt.execute("CREATE SEQUENCE test.users_userid_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN \"userID\" SET DEFAULT nextval('test.users_userid_seq')");
                stmt.execute("CREATE SEQUENCE test.orders_orderid_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN \"orderID\" SET DEFAULT nextval('test.orders_orderid_seq')");

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
                        "VALUES (1, 'denied', 1, 5, 20000), (2, 'accepted', 2, 2, 15000), (3, 'awaiting approval', 2, 5, 14000)");

                // Set sequence to continue from the largest primary keys.
                stmt.execute("SELECT setval('test.orders_orderid_seq', COALESCE((SELECT MAX(\"orderID\") + 1 FROM test.orders), 1), false)");
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
    void deleteOrderById() {
        try {
            boolean deleteOrder = OrderMapper.deleteOrderById(1, connectionPool);
            assertTrue(deleteOrder);

        } catch (DatabaseException e) {
            fail("Fail while deleting order -  test." + e.getMessage());
        }
    }


    @Test
    void acceptOrder() {
        try {
            boolean result = OrderMapper.acceptOrder(connectionPool, 1);
            assertTrue(result);

        } catch (DatabaseException e) {
            fail("Fail while accepting order -  test." + e.getMessage());
        }
    }

    @Test
    void denyOrder() {
        try {
            boolean result = OrderMapper.denyOrder(connectionPool, 3);
            assertTrue(result);

        } catch (DatabaseException e) {
            fail("Fail while denying order -  test." + e.getMessage());
        }
    }

    @Test
    void getOrdersByUserId() {
        try {
            List<Order> userOneOrders = OrderMapper.getOrdersByUserId(1, connectionPool);
            assertNotNull(userOneOrders);
            assertEquals(1, userOneOrders.size());

            List<Order> userTwoOrders = OrderMapper.getOrdersByUserId(2, connectionPool);
            assertNotNull(userTwoOrders);
            assertEquals(2, userTwoOrders.size());

            // User ID has not been inserted into the test.database, therefore we expect an exception.
            int UnusedUserID = 5;
            assertThrows(DatabaseException.class, () -> {
                OrderMapper.getOrdersByUserId(UnusedUserID, connectionPool);
            });

        } catch (DatabaseException e) {
            fail("Fail while denying order -  test." + e.getMessage());
        }
    }
}
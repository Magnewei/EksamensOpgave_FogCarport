package app.carport.Persistence;

import app.carport.Entities.Material;
import app.carport.Exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MaterialMapperTest {
    private static final String USER = System.getenv("JDBC_USER");
    private static final String PASSWORD = System.getenv("JDBC_PASSWORD");
    private static final String TEST_URL = System.getenv("JDBC_CONNECTION_STRING_TEST");
    private static final String DB = System.getenv("JDBC_DB");
    public static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, TEST_URL, DB);

    @BeforeAll
    static void setupClass() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Drop existing test tables and sequences if they exist
                stmt.execute("DROP TABLE IF EXISTS test.orders CASCADE;");
                stmt.execute("DROP TABLE IF EXISTS test.users CASCADE;");
                stmt.execute("DROP TABLE IF EXISTS test.material CASCADE;");
                stmt.execute("DROP TABLE IF EXISTS test.materialUsage CASCADE;");

                // Drop sequences if they exist
                stmt.execute("DROP SEQUENCE IF EXISTS test.users_userid_seq;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_orderid_seq;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.material_materialID_seq;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.materialUsage_materialUsageID_seq;");

                // Create test tables
                stmt.execute("CREATE TABLE test.users AS (SELECT * FROM public.\"users\") WITH NO DATA;");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * FROM public.\"orders\") WITH NO DATA;");
                stmt.execute("CREATE TABLE test.material AS (SELECT * FROM public.\"material\") WITH NO DATA;");
                stmt.execute("CREATE TABLE test.materialUsage AS (SELECT * FROM public.\"materialUsage\") WITH NO DATA;");

                // Create sequences
                stmt.execute("CREATE SEQUENCE test.users_userid_seq;");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN \"userID\" SET DEFAULT nextval('test.users_userid_seq');");
                stmt.execute("CREATE SEQUENCE test.orders_orderid_seq;");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN \"orderID\" SET DEFAULT nextval('test.orders_orderid_seq');");
                stmt.execute("CREATE SEQUENCE test.material_materialID_seq;");
                stmt.execute("ALTER TABLE test.material ALTER COLUMN \"materialID\" SET DEFAULT nextval('test.material_materialID_seq');");
                stmt.execute("CREATE SEQUENCE test.materialUsage_materialUsageID_seq;");
                stmt.execute("ALTER TABLE test.materialUsage ALTER COLUMN \"materialUsageID\" SET DEFAULT nextval('test.materialUsage_materialUsageID_seq');");
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
                stmt.execute("DELETE FROM test.orders;");
                stmt.execute("DELETE FROM test.users;");
                stmt.execute("DELETE FROM test.materialUsage;");
                stmt.execute("DELETE FROM test.material;");

                // Insert test data
                stmt.execute("INSERT INTO test.users (\"userID\", \"firstName\", \"lastName\", \"password\", \"isAdmin\", \"email\", \"phonenumber\", \"addressID\") " +
                        "VALUES (1, 'magnus', 'nord', 'FogP1234#', true, 'Magnus@gmail.com', 12345678, 1), " +
                        "(2, 'christian', 'syd', 'FogP1234!', false, 'christian@gmail.com', 87654321, 1);");

                stmt.execute("INSERT INTO test.orders (\"orderID\", \"status\", \"userID\", \"carportID\", \"price\") " +
                        "VALUES (1, 'denied', 1, 5, 20000), (2, 'accepted', 2, 2, 15000), (3, 'awaiting approval', 2, 5, 14000);");

                stmt.execute("INSERT INTO test.material (\"materialID\", \"name\", \"price\", \"length\", \"unit\", \"quantityInStock\") " +
                        "VALUES (1, 'Træ', 50.0, 5.0, 'meter', 100), (2, 'Skruer', 0.1, 0.05, 'kg', 500);");

                // Set sequences
                stmt.execute("SELECT setval('test.orders_orderid_seq', COALESCE((SELECT MAX(\"orderID\") + 1 FROM test.orders), 1), false);");
                stmt.execute("SELECT setval('test.users_userid_seq', COALESCE((SELECT MAX(\"userID\") + 1 FROM test.users), 1), false);");
                stmt.execute("SELECT setval('test.material_materialID_seq', COALESCE((SELECT MAX(\"materialID\") + 1 FROM test.material), 1), false);");
                stmt.execute("SELECT setval('test.materialUsage_materialUsageID_seq', COALESCE((SELECT MAX(\"materialUsageID\") + 1 FROM test.materialUsage), 1), false);");

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
    void getMaterialById() {
        try {
            Material material = MaterialMapper.getMaterialById(1, connectionPool);
            assertNotNull(material);
            assertEquals(1, material.getMaterialID());
            assertEquals("Træ", material.getName());
            assertEquals(50.0, material.getPrice());
            assertEquals(5.0, material.getLength());
            assertEquals("meter", material.getUnit());
            assertEquals(100, material.getQuantityInStock());
        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void removeMaterialStockOnOrder() {
        try {
            Material wood = new Material(1, "Træ", 50.0, 5.0, "meter", 100);
            Material nails = new Material(2, "Skruer", 0.1, 0.05, "kg", 500);

            Map<Material, Integer> materialList = new HashMap<>();
            materialList.put(wood, 10);
            materialList.put(nails, 50);

            boolean result = MaterialMapper.removeMaterialStockOnOrder(materialList, connectionPool);
            assertTrue(result);

            Material updatedWood = MaterialMapper.getMaterialById(1, connectionPool);
            Material updatedNails = MaterialMapper.getMaterialById(2, connectionPool);

            assertEquals(90, updatedWood.getQuantityInStock());
            assertEquals(450, updatedNails.getQuantityInStock());
        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }
}
package app.carport.Persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MaterialMapperTest {
    private final static String USER = System.getenv("");
    private final static String PASSWORD = System.getenv("");
    private final static String URL = System.getenv("");
    private final static String DB = "test";
    private final static ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllLength() {
    }

    @Test
    void getAllWidth() {
    }

    @Test
    void getAllMaterials() {
    }

    @Test
    void deleteMaterialById() {
    }

    @Test
    void getMaterialById() {
    }

    @Test
    void addMaterial() {
    }
}
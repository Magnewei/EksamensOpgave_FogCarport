package app.carport.entities;

import app.carport.Entities.Carport;
import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class CarportTest {

    private static final String USER = System.getenv("JDBC_USER");
    private static final String PASSWORD = System.getenv("JDBC_PASSWORD");
    private static final String URL = System.getenv("JDBC_CONNECTION_STRING");
    private static final String DB = System.getenv("JDBC_DB");
    public static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @Test
    void calcPostQuantityShortCarport() throws DatabaseException {
        //Arrange
        Carport carport = new Carport(420,300,true);

        //Act
        carport.setMaterialList(connectionPool);
        int expected = 4;
        int actual = carport.getMaterialList().entrySet().stream().filter(entry -> entry.getKey().getMaterialID() == 12).map(Map.Entry::getValue).findFirst().orElse(0);
        //Assert
        assertEquals(expected,actual);
    }

    @Test
    void calcPostQuantityLongCarport() throws DatabaseException {
        //Arrange
        Carport carport = new Carport(540,300,true);

        //Act
        carport.setMaterialList(connectionPool);
        int expected = 6;
        int actual = carport.getMaterialList().entrySet().stream().filter(entry -> entry.getKey().getMaterialID() == 12).map(Map.Entry::getValue).findFirst().orElse(0);
        //Assert
        assertEquals(expected,actual);
    }


    @Test
    void calcRemLengthLongCarport() throws DatabaseException {
        //Arrange
        Carport carport = new Carport(720,300,true);

        //Act
        carport.setMaterialList(connectionPool);
        int expected = 4;
        //MaterialeID 7 er spærtræ på 360 cm.
        int actual = carport.getMaterialList().entrySet().stream().filter(entry -> entry.getKey().getMaterialID() == 7).map(Map.Entry::getValue).findFirst().orElse(0);
        //Assert
        assertEquals(expected,actual);
    }

    @Test
    void calcSternAmountShortCarportNotQuadratic() throws DatabaseException {
        //Arrange
        Carport carport = new Carport(480,300,true);

        //Act
        carport.setMaterialList(connectionPool);
        int expected = 2;
        //MaterialeID 22 er sterntræ på 480 cm.
        int actual = carport.getMaterialList().entrySet().stream().filter(entry -> entry.getKey().getMaterialID() == 22).map(Map.Entry::getValue).findFirst().orElse(0);
        //Assert
        assertEquals(expected,actual);
    }

    @Test
    void calcSternAmountShortCarportQuadratic() throws DatabaseException {
        //Arrange
        Carport carport = new Carport(480,480,true);

        //Act
        carport.setMaterialList(connectionPool);
        int expected = 3;
        //MaterialeID 22 er sterntræ på 480 cm.
        int actual = carport.getMaterialList().entrySet().stream().filter(entry -> entry.getKey().getMaterialID() == 22).map(Map.Entry::getValue).findFirst().orElse(0);
        //Assert
        assertEquals(expected,actual);
    }

    @Test
    void calcSternAmountLongCarportNotQuadratic() throws DatabaseException {
        //Arrange
        Carport carport = new Carport(660,300,true);

        //Act
        carport.setMaterialList(connectionPool);
        int expected = 4;
        //MaterialeID 20 er sterntræ på 360 cm.
        int actual = carport.getMaterialList().entrySet().stream().filter(entry -> entry.getKey().getMaterialID() == 20).map(Map.Entry::getValue).findFirst().orElse(0);
        //Assert
        assertEquals(expected,actual);
    }

    @Test
    void calcSpærAndRemAmountQuadratic() throws DatabaseException {
        //Arrange
        Carport carport = new Carport(420,420,true);

        //Act
        carport.setMaterialList(connectionPool);
        //2 remme og 7 spær
        int expected = 9;
        //MaterialeID 8 er spærtræ på 420 cm.
        int actual = carport.getMaterialList().entrySet().stream().filter(entry -> entry.getKey().getMaterialID() == 8).map(Map.Entry::getValue).findFirst().orElse(0);
        //Assert
        assertEquals(expected,actual);
    }

    @Test
    void calcRoofplateQuantity() throws DatabaseException {
        //Arrange
        Carport carport = new Carport(420,300,true);

        //Act
        carport.setMaterialList(connectionPool);
        int expected = 5;

        //MaterialID 13 er tagplader på 300 cm.
        int actual = carport.getMaterialList().entrySet().stream().filter(entry -> entry.getKey().getMaterialID() == 13).map(Map.Entry::getValue).findFirst().orElse(0);
        //Assert
        assertEquals(expected,actual);
    }



}

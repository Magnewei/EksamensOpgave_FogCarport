package app.carport.Entities;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */

public class Carport {
    private final int carportID;
    private final String material;
    private final int width;
    private final int height;
    private final int price;

    public Carport(int carportID, String material, int width, int height, int price) {
        this.carportID = carportID;
        this.material = material;
        this.width = width;
        this.height = height;
        this.price = price;
    }

    public int getCarportID() {
        return carportID;
    }

    public String getMaterial() {
        return material;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPrice() {
        return price;
    }
}

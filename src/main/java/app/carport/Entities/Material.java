package app.carport.Entities;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class Material {
    private int materialID;
    private double price;
    private String name;
    private String unit;
    private double length;
    private int quantityInStock;

    public Material(int materialID, String name, double price, double length, String unit, int quantityInStock) {
        this.materialID = materialID;
        this.name = name;
        this.price = price;
        this.length = length;
        this.unit = unit;
        this.quantityInStock = quantityInStock;
    }

    public int getMaterialID() {
        return materialID;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getLength() {
        return length;
    }

    public String getUnit() {
        return unit;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }
}

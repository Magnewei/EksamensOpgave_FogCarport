package app.carport.Entities;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */

public class Carport {
    private  int carportID;
    private  String material;
    private double width;
    private  double height;
    private  int price;
    private  Boolean hasShed;

    public Carport(Double width, Double height, Boolean hasShed){
     this.width=width;
     this.height=height;
     this.hasShed=hasShed;
    }

    public int getCarportID() {
        return carportID;
    }

    public String getMaterial() {
        return material;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getPrice() {
        return price;
    }
}

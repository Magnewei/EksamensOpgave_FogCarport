package app.carport.Entities;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */

public class Carport {
    private int carportID;
    private double length;
    private double width;
    private boolean hasShed;
    private List<Material> materialList;

    public Carport(double length, double width, boolean hasShed){
        this.length = length;
        this.width = width;
        this.hasShed = hasShed;
    }

    public Carport(int carportID, double length, double width, boolean hasShed, List<Material> materialList){
     this.carportID = carportID;
     this.length = length;
     this.width = width;
     this.hasShed = hasShed;
     this.materialList = materialList;
    }

    public int getCarportID() {
        return carportID;
    }

    public double getLength() {
      return length;
    }

    public double getWidth() {
        return width;
    }

    public boolean hasShed(){
        return hasShed;
    }

    public List<Material> getMaterialList() {
      return materialList;
      }

    public double getTotalPrice(){
        double totalPrice = 0;
        for(Material material : materialList){
            totalPrice += material.getPrice();
        }
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
    }
}

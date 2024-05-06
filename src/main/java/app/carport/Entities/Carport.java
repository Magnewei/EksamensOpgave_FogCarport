package app.carport.Entities;

import java.util.List;
import java.util.Map;

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
    private Map<Material,Integer> materialList;

    public Carport(double length, double width, boolean hasShed){
        this.length = length;
        this.width = width;
        this.hasShed = hasShed;
    }

    public Carport(int carportID, double length, double width, boolean hasShed, Map<Material,Integer> materialList){
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

    public Map<Material,Integer> getMaterialList(){
        return materialList;
    }

    public void setCarportID(int carportID) {
        this.carportID = carportID;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHasShed(boolean hasShed) {
        this.hasShed = hasShed;
    }

    public void setMaterialList(Map<Material, Integer> materialList) {
        this.materialList = materialList;
    }

    public double getTotalPrice(){
        double totalPrice = 0;
        for(Map.Entry<Material,Integer> entry:materialList.entrySet()){
            Material material = entry.getKey();
            int quantity = entry.getValue();
            totalPrice += material.getPrice() * quantity;
        }
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
    }
}

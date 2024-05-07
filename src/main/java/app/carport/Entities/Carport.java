package app.carport.Entities;

import app.carport.Exceptions.DatabaseException;
import app.carport.Persistence.ConnectionPool;
import app.carport.Persistence.MaterialMapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
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
    private double totalPrice;

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

    public static Material getShortestSpærThatFits(List<Material> allMaterials, double length){
        for(Material material : allMaterials){
            if(material.getLength() == length){
                return material;
            }else if(material.getLength() >= length && material.getLength()-length <= 30){
                return material;
            }else if(material.getLength() >= length/2 && material.getLength()-(length/2) <= 30){
                return material;
            }else{
                return null;
            }
        }
        return null;
    }

    public Map<Material,Integer> calculateMaterialList(ConnectionPool connectionPool) throws DatabaseException {
        Map<Material,Integer> materialList = new HashMap<>();
        List<Material> allMaterials = MaterialMapper.getAllMaterials(connectionPool);

        //Laver en liste med spærtræ som skal bruges til remme og spær.
        List<Material> spærMaterials = new ArrayList<>();
        for(Material material:allMaterials){
            if (material.getName().toLowerCase().contains("spær")){
                spærMaterials.add(material);
            }
        }


        //Først udregnes remme til carportens sider. Ved længder under 600 bruges én brædde til hele carportens længde.
        //Ved længder over 600 cm bruges 2 i forlængelse af den mindst mulige der kan nå
        if(length <= 600) {
            materialList.put(getShortestSpærThatFits(spærMaterials, length), 2);
        }else{
            materialList.put(getShortestSpærThatFits(spærMaterials, length), 4);
        }

        //Så skal der regnes stolper. Vi går ud fra en maksimal aftsand på 300 cm mellem stolperne, med maks 100 cm fra enden
        //materialId 12 er stolpen.
        if(length <= 500) {
            materialList.put(MaterialMapper.getMaterialById(12, connectionPool), 4);
        }else{
            materialList.put(MaterialMapper.getMaterialById(12, connectionPool), 6);
        }

        //Så mangler der kun spær. Vi går ud fra en afstand på 55 cm mellem spærene.
        //Ved at caste længden til int, kan vi finde ud af hvor mange spær der skal bruges.
        materialList.put(getShortestSpærThatFits(spærMaterials, width), ((int) length/55) + 1);
        return materialList;

    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

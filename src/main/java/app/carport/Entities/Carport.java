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

    public boolean isWithRoof() {
        return withRoof;
    }

    private boolean withRoof;
    private Map<Material,Integer> materialList;
    private double totalPrice;

    public Carport(double length, double width, boolean withRoof){
        this.length = length;
        this.width = width;
        this.withRoof = withRoof;
    }
    public Carport(double length, double width, boolean withRoof, int carportID){
        this.length = length;
        this.width = width;
        this.withRoof = withRoof;
        this.carportID= carportID;
    }
    public Carport(int carportID, double length, double width, boolean withRoof, Map<Material,Integer> materialList){
     this.carportID = carportID;
     this.length = length;
     this.width = width;
     this.withRoof = withRoof;
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

    public boolean withRoof(){
        return withRoof;
    }

    public Map<Material,Integer> getMaterialList(){
        return materialList;
    }

    public double getTotalPrice() {
        return totalPrice;
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

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    //Vi tager 30% ekstra end materialeomkostninger i pris.
    public double calculateTotalPrice(){
        double totalPrice = 0;
        for(Material material : materialList.keySet()){
            totalPrice += material.getPrice() * materialList.get(material);
        }
        return totalPrice * 1.3;
    }

    public static Material getShortestWoodThatFits(List<Material> materials, double length){
        for(Material material : materials){
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

    //Tagpaldernes længder passer perfekt med de bredder vi tilbyder.
    public static Material getShortestTagpladeThatFits(List<Material> materials, double length){
        for(Material material : materials){
            if(material.getLength() == length) {
                return material;
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

        //Laver en liste med sterntræ som skal bruges til remme og spær.
        List<Material> sternMaterials = new ArrayList<>();
        for(Material material:allMaterials){
            if (material.getName().toLowerCase().contains("trykimp")){
                spærMaterials.add(material);
            }
        }

        //Laber en liste med tagplader.
        List<Material> tagplader = new ArrayList<>();
        for(Material material:allMaterials){
            if (material.getName().toLowerCase().contains("trapezplade")){
                tagplader.add(material);
            }
        }


        //Først udregnes remme til carportens sider. Ved længder under 600 bruges én brædde til hele carportens længde.
        //Ved længder over 600 cm bruges 2 i forlængelse af den mindst mulige der kan nå
        if(length <= 600) {
            materialList.put(getShortestWoodThatFits(spærMaterials, length), 2);
        }else{
            materialList.put(getShortestWoodThatFits(spærMaterials, length), 4);
        }

        //Så skal der regnes stolper. Vi går ud fra en maksimal aftsand på 300 cm mellem stolperne, med maks 100 cm fra enden
        if(length <= 500) {
            materialList.put(MaterialMapper.getMaterialById(12, connectionPool), 4);
        }else{
            materialList.put(MaterialMapper.getMaterialById(12, connectionPool), 6);
        }

        //Der skal også bruges sternbrædder. Der skal bruges en overstern og en understern, påsiderne og bagenden, men kun overstern foran
        if(length <= 600) {
            materialList.put(getShortestWoodThatFits(sternMaterials, length), 2);
            materialList.put(getShortestWoodThatFits(sternMaterials, width), 1);
        }else{
            materialList.put(getShortestWoodThatFits(sternMaterials, length), 4);
            materialList.put(getShortestWoodThatFits(sternMaterials, width), 1);
        }


        //Så mangler der kun spær. Vi går ud fra en afstand på 55 cm mellem spærene, og en spærbredde på 4.5 cm.
        //Ved at caste længden til int, kan vi finde ud af hvor mange spær der skal bruges.
        int spærAmount = ((int) ((length-(59.5/2))/(55+4.5)) + 1);
        materialList.put(getShortestWoodThatFits(spærMaterials, width), spærAmount);

        //Der skal også bruges tagplader hvis kunden vælger det. Tagpladerne lægges på tværs, og findes i de samme længder, som der er bredder tilgængelige.
        //Tagpladerne er 109 cm brede, så vi sikrer at kunden får nok tagplader med, og så må kunden save til så det passer,
        //Tagpladerne skal også overlappe lidt, så vi regner ud fra en længde der er lidt kortere end den reelle, så de kan overlappe.
        if(withRoof) {
            materialList.put(getShortestTagpladeThatFits(tagplader, length), (int) (width / 100) + 1);
        }


        //Det var alt træet, så mangler der skruer, beslag og lign.
        //Vi starter med skruer til at montere spær på remmene. Der skal bruges to pr spær
        materialList.put(MaterialMapper.getMaterialById(5, connectionPool), spærAmount*2);

        //Der skal bruges 2 ruller hulbånd til et vindkryds.
        materialList.put(MaterialMapper.getMaterialById(4, connectionPool), 2);

        //Der skal også bruges en pakke skruer til at montere hulbåndet.
        materialList.put(MaterialMapper.getMaterialById(2, connectionPool), 1);

        //Der skal også bruges skruer til at montere tagplader. Der skal bruges 12 pr m2, og de kommer i pakker af 250.
        materialList.put(MaterialMapper.getMaterialById(3, connectionPool), (int) ((length*width/10000)*12/200)+1);

        //Og en pakke skruer til sterntræet
        materialList.put(MaterialMapper.getMaterialById(1, connectionPool), 1);

        return materialList;

    }


}

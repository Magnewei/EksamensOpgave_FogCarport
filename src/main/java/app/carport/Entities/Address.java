package app.carport.Entities;

public class Address {
    private final int addressID;
    private  int postalCode;
    private  int houseNumber;
    private  String cityName;
    private  String streetName;

    public Address(int addressID, int postalCode, int houseNumber,String cityName, String streetName) {
        this.addressID = addressID;
        this.postalCode = postalCode;
        this.cityName = cityName;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
    }

    public Address(String streetname, int postalCode, int houseNumber) {
        this.streetName = streetname;
        this.postalCode = postalCode;
        this.houseNumber = houseNumber;
        this.addressID = 0;

    }

    public int getAddressID() {
        return addressID;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public String getCityName() {
        return cityName;
    }

    public String getStreetName() {
        return streetName;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
}

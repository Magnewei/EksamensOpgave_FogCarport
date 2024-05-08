package app.carport.Entities;

public class Address {
    private final int addressID;
    private final int postalCode;
    private final int houseNumber;
    private final String cityName;
    private final String streetName;

    public Address(int addressID, int postalCode, int houseNumber,String cityName, String streetName) {
        this.addressID = addressID;
        this.postalCode = postalCode;
        this.cityName = cityName;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
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
}

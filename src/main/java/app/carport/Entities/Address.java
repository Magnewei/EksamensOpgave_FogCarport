package app.carport.Entities;

public class Address {
    private final int addressID;
    private final int postalCode;
    private final String cityName;
    private final String streetName;

    public Address(int addressID, int postalCode, String cityName, String streetName) {
        this.addressID = addressID;
        this.postalCode = postalCode;
        this.cityName = cityName;
        this.streetName = streetName;
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
}

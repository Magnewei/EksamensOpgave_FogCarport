package app.carport.Entities;

public class User {
    private final int userID;
    private final String email;
    private final String password;
    private final boolean isAdmin;
    private final String firstName;
    private final String lastName;
    private Address adress;
    private Order order;

    public User(int userID, String email, String password, boolean isAdmin, String firstName, String lastName, Address adress, Order order) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.adress = adress;
        this.order = order;
    }

    public int getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getAdress() {
        return adress;
    }

    public Order getOrder() {
        return order;
    }
}

package app.carport.Entities;

public class User {
    private final int userID;
    private final String email;
    private final String password;
    private final boolean role;
    private final Address adress;
    private final Order order;

    public User(int userID, String email, String password, boolean role, Address adress, Order order) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public boolean isRole() {
        return role;
    }

    public Address getAdress() {
        return adress;
    }

    public Order getOrder() {
        return order;
    }
}

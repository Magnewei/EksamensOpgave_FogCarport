package app.carport.Entities;

public class User {
    private final int userID;
    private final boolean isAdmin;
    private final String firstName;
    private final String lastName;
    private Address address;
    private Order order;
    private String email;
    private String password;

    public User(int userID, String email, String password, boolean isAdmin, String firstName, String lastName, Address address, Order order) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.order = order;
    }

    // Constructor contains no order, for when a user is first created.
    public User(int userID, String email, String password, boolean isAdmin, String firstName, String lastName, Address address) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
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
        return address;
    }

    public Order getOrder() {
        return order;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

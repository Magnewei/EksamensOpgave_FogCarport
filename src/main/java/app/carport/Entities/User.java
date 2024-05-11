package app.carport.Entities;

public class User {
    private int userID;
    private boolean isAdmin;
    private String firstName;
    private String lastName;
    private Address address;
    private Order order;
    private String email;
    private String password;
    private int phoneNumber;

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
    public User(int userID, String email, String password, boolean isAdmin, String firstName, String lastName, Address address, int phoneNumber) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }


    public User(int userID, boolean isAdmin, String firstName, String lastName, Address address, Order order, String email, String password, int phoneNumber) {
        this.userID = userID;
        this.isAdmin = isAdmin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.order = order;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public User(int userID, String email, String firstName, String lastName, Order order) {
        this.userID = userID;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.order = order;
    }
    public User(String name, String lastname, String streetname, String streetnumber, String phonenumber, String mail) {
        this.userID = 0;
        this.email = mail;
        this.firstName = name;
        this.lastName = lastname;
        this.address = new Address(streetname, Integer.parseInt(streetnumber), 0);
        this.phoneNumber = Integer.parseInt(phonenumber);
    }

    public int getphoneNumber() {return phoneNumber;}
    public void setPhoneNumber(int phoneNumber) {this.phoneNumber = phoneNumber;}


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

    public Address getAddress() {
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

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }
}

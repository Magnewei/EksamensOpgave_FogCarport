package app.carport.Entities;

public class User {
    private final int userID;
    private final String email;
    private final String password;
    private final boolean isAdmin;
    private Order order;

    public User(int userID, String email, String password, boolean isAdmin) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}

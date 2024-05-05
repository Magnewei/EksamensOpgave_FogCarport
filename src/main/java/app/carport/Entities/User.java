package app.carport.Entities;

public class User {
    private final int userID;
    private final String email;
    private final String password;
    private final String role;

    public User(int userID, String email, String password, String role) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String role() {
        return role;
    }

}

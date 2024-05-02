package app.carport.Entities;

public class User {
    private final int userID;
    private final String email;
    private final String password;
    private final String role;

    public User(int userId, String email, String password, String role) {
        this.userID = userId;
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

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userID +
                ", userName='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }


}

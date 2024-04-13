package app.carport.Entities;

public class User {
    private final int userID;
    private final String email;
    private final String password;
    private final String role;
    private int balance;

    public User(int userId, String email, String password, String role, int balance) {
        this.userID = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.balance = balance;
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

    public int getBalance() {
        return balance;
    }

    public void removeBalance(int amount) {
        int balanceChange = balance - amount;
        balance = balanceChange;
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

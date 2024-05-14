package app.carport.Entities;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class Order {
    private final int orderId;
    private final String status;
    private User user;
    private Carport carport;
    private int totalPrice;

    public Order(int orderId, String status, User user, Carport carport) {
        this.orderId = orderId;
        this.status = status;
        this.user = user;
        this.carport = carport;
    }

    public Order(int orderId, String status, Carport carport) {
        this.orderId = orderId;
        this.status = status;
        this.carport = carport;
    }

    public Order(int orderId, String status, User user) {
        this.orderId = orderId;
        this.status = status;
        this.user = user;
    }

    public Order(int orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public Order(int orderId, String status, int totalPrice) {
        this.orderId = orderId;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public Carport getCarport() {
        return carport;
    }

    private void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", user=" + user +
                ", carport=" + carport +
                '}';

    }
}

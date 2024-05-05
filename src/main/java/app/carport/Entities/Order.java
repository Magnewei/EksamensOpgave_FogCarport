package app.carport.Entities;


/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class Order {
    private int orderId;
    private String status;
    private User user;
    private Carport carport;

    public Order(int orderId, String status, User user, Carport carport) {
        this.orderId = orderId;
        this.status = status;
        this.user = user;
        this.carport = carport;
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

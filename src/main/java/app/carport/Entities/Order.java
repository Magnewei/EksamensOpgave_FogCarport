package app.carport.Entities;


/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class Order {
    private int orderId;
    private String status;
    private int userId;
    private int carportId;

    public Order(int orderId, String status, int userId, int carportId) {
        this.orderId = orderId;
        this.status = status;
        this.userId = userId;
        this.carportId = carportId;
    }

    public int getOrderId() {
        return orderId;


    public String getStatus() {
        return status;
    }


    public int getUserId() {
        return userId;
    }

    public int getCarportId() {
        return carportId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", userId=" + userId +
                ", carportId=" + carportId +
                '}';

    public Carport getCarport() {
        return carport;

    }
}

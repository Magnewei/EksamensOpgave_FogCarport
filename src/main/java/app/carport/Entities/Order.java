package app.carport.Entities;

public class Order {
    private final int orderID;
    private final String status;
    private final Carport carport;

    public Order(int orderID, String status, Carport carport) {
        this.orderID = orderID;
        this.status = status;
        this.carport = carport;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getStatus() {
        return status;
    }

    public Carport getCarport() {
        return carport;
    }
}

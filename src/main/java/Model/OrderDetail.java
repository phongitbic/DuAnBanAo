
package Model;

public class OrderDetail {
    private int orderDetailsId;
    private String orderDetailsCode;
    private int orderId;
    private int quantity;
    private double unitPrice;  // Đã đổi kiểu từ BigDecimal sang double
    private int productDetailsId;

    public OrderDetail() {
    }

    public OrderDetail(int orderDetailsId, String orderDetailsCode, int orderId, int quantity, double unitPrice, int productDetailsId) {
        this.orderDetailsId = orderDetailsId;
        this.orderDetailsCode = orderDetailsCode;
        this.orderId = orderId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.productDetailsId = productDetailsId;
    }

    public int getOrderDetailsId() {
        return orderDetailsId;
    }

    public void setOrderDetailsId(int orderDetailsId) {
        this.orderDetailsId = orderDetailsId;
    }

    public String getOrderDetailsCode() {
        return orderDetailsCode;
    }

    public void setOrderDetailsCode(String orderDetailsCode) {
        this.orderDetailsCode = orderDetailsCode;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getProductDetailsId() {
        return productDetailsId;
    }

    public void setProductDetailsId(int productDetailsId) {
        this.productDetailsId = productDetailsId;
    }

    
}

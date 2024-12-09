
package Model;


public class Orders {
    private int orderId;
    private int userId;
    private String orderDate;
    private String status;
    private int customerId;
    private String couponsId;
    private double VAT;
    private int paymentId;
    private double totalPrice;
    private String lastUpdateDatePoint;

    public Orders() {
    }

    public Orders(int orderId, int userId, String orderDate, String status, int customerId, String couponsId, double VAT, int paymentId, double totalPrice, String lastUpdateDatePoint) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.customerId = customerId;
        this.couponsId = couponsId;
        this.VAT = VAT;
        this.paymentId = paymentId;
        this.totalPrice = totalPrice;
        this.lastUpdateDatePoint = lastUpdateDatePoint;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCouponsId() {
        return couponsId;
    }

    public void setCouponsId(String couponsId) {
        this.couponsId = couponsId;
    }

    public double getVAT() {
        return VAT;
    }

    public void setVAT(double VAT) {
        this.VAT = VAT;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getLastUpdateDatePoint() {
        return lastUpdateDatePoint;
    }

    public void setLastUpdateDatePoint(String lastUpdateDatePoint) {
        this.lastUpdateDatePoint = lastUpdateDatePoint;
    }
    
    
}

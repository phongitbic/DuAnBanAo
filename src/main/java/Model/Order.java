package Model;

public class Order {
    private String orderCode;
    private String userCode;
    private String userName;
    private String customerCode;
    private String customerName;
    private String phone; // Thêm số điện thoại
    private String orderDate;
    private String voucherCode;
    private double vat;
    private String paymentMethod;
    private double totalPrice;
    private String status;

    // Cập nhật constructor để bao gồm số điện thoại
    public Order(String orderCode, String userCode, String userName, String customerCode, 
                 String customerName, String phone, String orderDate, String voucherCode, 
                 double vat, String paymentMethod, double totalPrice, String status) {
        this.orderCode = orderCode;
        this.userCode = userCode;
        this.userName = userName;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.phone = phone;
        this.orderDate = orderDate;
        this.voucherCode = voucherCode;
        this.vat = vat;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Order() {
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

   
}

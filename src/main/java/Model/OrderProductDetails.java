package Model;

public class OrderProductDetails {

    private String productDetailCode;
    private String productName;
    private String productType;
    private String color;
    private String size;
    private String style;
    private int quantity;
    private double unitPrice;

    public OrderProductDetails() {
    }

    public OrderProductDetails(String productDetailCode, String productName, String productType, String color, 
                               String size, String style, int quantity, double unitPrice) {
        this.productDetailCode = productDetailCode;
        this.productName = productName;
        this.productType = productType;
        this.color = color;
        this.size = size;
        this.style = style;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductDetailCode() {
        return productDetailCode;
    }

    public void setProductDetailCode(String productDetailCode) {
        this.productDetailCode = productDetailCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
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
}

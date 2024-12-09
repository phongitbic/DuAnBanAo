package Model;

/**
 *
 * @author HoaNguyenVan
 */
public class Discount {

    private String discountId;
    private int productDetailsId;
    private String startDate;
    private String endDate;
    private double discountAmount;
    private String reducedDetails;
    private String detailedType;
    private String productDetails_code;
    private String style;
    private String size;
    private String color;

    public Discount(String discountId, String startDate, String endDate, double discountAmount, String productDetails_code, String style, String size, String color) {
        this.discountId = discountId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountAmount = discountAmount;
        this.productDetails_code = productDetails_code;
        this.style = style;
        this.size = size;
        this.color = color;
    }

    public String getProductDetails_code() {
        return productDetails_code;
    }

    public void setProductDetails_code(String productDetails_code) {
        this.productDetails_code = productDetails_code;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getReducedDetails() {
        return reducedDetails;
    }

    public void setReducedDetails(String reducedDetails) {
        this.reducedDetails = reducedDetails;
    }

    public String getDetailedType() {
        return detailedType;
    }

    public void setDetailedType(String detailedType) {
        this.detailedType = detailedType;
    }

    public Discount() {
    }

    public Discount(String discountId, int productDetailsId, String startDate, String endDate, double discountAmount) {
        this.discountId = discountId;
        this.productDetailsId = productDetailsId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountAmount = discountAmount;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public int getProductDetailsId() {
        return productDetailsId;
    }

    public void setProductDetailsId(int productDetailsId) {
        this.productDetailsId = productDetailsId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

}

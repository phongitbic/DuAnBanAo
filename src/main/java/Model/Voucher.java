package Model;

/**
 *
 * @author HoaNguyenVan
 */
public class Voucher {

    private int voucherId;
    private String voucherCode;
    private String description;
    private double discountValue;
    private String startDate;
    private String endDate;
    private String status;
     private double minMonye;
    private double maxMonye;
    private int quantity;
    public Voucher() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Voucher(int voucherId, String voucherCode, String description, double discountValue, String startDate, String endDate, String status, double minMonye, double maxMonye, int quantity) {
        this.voucherId = voucherId;
        this.voucherCode = voucherCode;
        this.description = description;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.minMonye = minMonye;
        this.maxMonye = maxMonye;
        this.quantity = quantity;
    }


    public double getMinMonye() {
        return minMonye;
    }

    public void setMinMonye(double minMonye) {
        this.minMonye = minMonye;
    }

    public double getMaxMonye() {
        return maxMonye;
    }

    public void setMaxMonye(double maxMonye) {
        this.maxMonye = maxMonye;
    }

    public Voucher(String description, double discountValue, String startDate, String endDate, String status, double minMonye, double maxMonye ,int quantity) {
        this.description = description;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.minMonye = minMonye;
        this.maxMonye = maxMonye;
        this.quantity = quantity;
    }

    

    

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

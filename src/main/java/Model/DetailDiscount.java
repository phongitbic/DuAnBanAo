package Model;



/**
 *
 * @author HoaNguyenVan
 */
public class DetailDiscount {

    private int productDetailsId;
    private String discountId;

    public DetailDiscount() {
    }

    public DetailDiscount(int productDetailsId, String discountId) {
        this.productDetailsId = productDetailsId;
        this.discountId = discountId;
    }

    public int getProductDetailsId() {
        return productDetailsId;
    }

    public void setProductDetailsId(int productDetailsId) {
        this.productDetailsId = productDetailsId;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

}

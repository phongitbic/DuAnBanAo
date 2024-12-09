package Model;

/**
 *
 * @author DƯƠNG MINH HÀ
 */
public class ProductDetails {

    private Integer productDetail_id;
    private Integer product_id;
    private String product_code;
    private String productDetail_code;
    private String product_name;
    private String style;
    private String size;
    private String color;
    private String image;
    private Double price;
    private Integer quantity;
    private String category_name;
    private int productDetailsId;
    private String productDetailsCode;

    public ProductDetails() {
    }

    public ProductDetails(Integer productDetail_id, Integer product_id, String product_code, String productDetail_code, String product_name, String style, String size, String color, String image, Double price, Integer quantity, String category_name) {
        this.productDetail_id = productDetail_id;
        this.product_id = product_id;
        this.product_code = product_code;
        this.productDetail_code = productDetail_code;
        this.product_name = product_name;
        this.style = style;
        this.size = size;
        this.color = color;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.category_name = category_name;
    }

    public ProductDetails(Integer product_id, String style, String size, String color, Double price, Integer quantity) {
        this.product_id = product_id;
        this.style = style;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
    }

    public ProductDetails(Integer productDetail_id, Integer product_id, String style, String size, String color, Double price, Integer quantity) {
        this.productDetail_id = productDetail_id;
        this.product_id = product_id;
        this.style = style;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
    }

    // Đông
    public ProductDetails(String productDetailsCode, double price, String style, String size, String color) {
        this.productDetailsCode = productDetailsCode;
        this.price = price;
        this.style = style;
        this.size = size;
        this.color = color;
    }

// end Đông
    //Hòa
    //Add
    public ProductDetails(Integer product_id, String style, String size, String color, Double price, Integer quantity, String image) {
        this.product_id = product_id;
        this.style = style;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    // Update
    public ProductDetails(Integer productDetail_id, Integer product_id, String style, String size, String color, Double price, Integer quantity, String image) {
        this.productDetail_id = productDetail_id;
        this.product_id = product_id;
        this.style = style;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    // Nhập sp từ Excel
    public ProductDetails(String productDetail_code, String style, String size, String color) {
        this.productDetail_code = productDetail_code;
        this.style = style;
        this.size = size;
        this.color = color;
    }

    public ProductDetails(String product_code, String style, String size, String color, String image, Double price, Integer quantity) {
        this.product_code = product_code;
        this.style = style;
        this.size = size;
        this.color = color;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
    }

    //END
    public int getProductDetailsId() {
        return productDetailsId;
    }

    public void setProductDetailsId(int productDetailsId) {
        this.productDetailsId = productDetailsId;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProductDetail_code() {
        return productDetail_code;
    }

    public void setProductDetail_code(String productDetail_code) {
        this.productDetail_code = productDetail_code;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
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

    public String getProductDetailsCode() {
        return productDetailsCode;
    }

    public void setProductDetailsCode(String productDetailsCode) {
        this.productDetailsCode = productDetailsCode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getProductDetail_id() {
        return productDetail_id;
    }

    public void setProductDetail_id(Integer productDetail_id) {
        this.productDetail_id = productDetail_id;
    }
}

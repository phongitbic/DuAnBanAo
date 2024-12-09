package Model;

/**
 *
 * @author HoaNguyenVan
 */
public class Products {

    private Integer product_id;
    private String product_code;
    private String product_name;
    private Categories category;
    private Integer product_quantity;
    private String description;
    private Suppliers supplier;
    private Boolean status;

    public Products() {
    }

    public Products(String product_name, Categories category, String description, Suppliers supplier, Boolean status) {
        this.product_name = product_name;
        this.category = category;
        this.description = description;
        this.supplier = supplier;
        this.status = status;
    }

    public Products(String product_code, String product_name, Categories category, String description, Suppliers supplier, Boolean status) {
        this.product_code = product_code;
        this.product_name = product_name;
        this.category = category;
        this.description = description;
        this.supplier = supplier;
        this.status = status;
    }

    public Products(String product_code, String product_name, Categories category, Integer product_quantity, String description, Suppliers supplier, Boolean status) {
        this.product_code = product_code;
        this.product_name = product_name;
        this.category = category;
        this.product_quantity = product_quantity;
        this.description = description;
        this.supplier = supplier;
        this.status = status;
    }

    public Products(Integer product_id, String product_code, String product_name, Categories category, Integer product_quantity, String description, Suppliers supplier, Boolean status) {
        this.product_id = product_id;
        this.product_code = product_code;
        this.product_name = product_name;
        this.category = category;
        this.product_quantity = product_quantity;
        this.description = description;
        this.supplier = supplier;
        this.status = status;
    }
    
    

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public Integer getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(Integer product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Suppliers getSupplier() {
        return supplier;
    }

    public void setSupplier(Suppliers supplier) {
        this.supplier = supplier;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }
    

}

package Model;


/**
 *
 * @author HoaNguyenVan
 */
public class Suppliers {

    private String supplier_id;
    private String supplier_name;
    private String phone;
    private String email;
    private String address;

    public Suppliers() {
    }

    public Suppliers(String supplier_id, String supplier_name) {
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
    }
    
    

    public Suppliers(String supplier_id, String supplier_name, String phone, String email, String address) {
        this.supplier_id = supplier_id;
        this.supplier_name = supplier_name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

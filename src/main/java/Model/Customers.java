package Model;

/**
 *
 * @author HoaNguyenVan
 */
public class Customers {

    private Integer customer_id;
    private String customer_code;
    private String customer_name;
    private String phone;
    private Integer point_gained;
    private Integer points_used;

    public Customers() {
    }

    public Customers(Integer customer_id, String customer_code, String customer_name, String phone, Integer point_gained, Integer points_used) {
        this.customer_id = customer_id;
        this.customer_code = customer_code;
        this.customer_name = customer_name;
        this.phone = phone;
        this.point_gained = point_gained;
        this.points_used = points_used;
    }

    public Customers(String customer_name, String phone) {
        this.customer_name = customer_name;
        this.phone = phone;
    }

    public Customers(Integer customer_id, String customer_name, String phone) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.phone = phone;
    }
    
    

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPoint_gained() {
        return point_gained;
    }

    public void setPoint_gained(Integer point_gained) {
        this.point_gained = point_gained;
    }

    public Integer getPoints_used() {
        return points_used;
    }

    public void setPoints_used(Integer points_used) {
        this.points_used = points_used;
    }

}

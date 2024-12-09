package Model;

/**
 *
 * @author HoaNguyenVan
 */
public class Users1 {

    private String usersCode;
    private String hoTen;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private boolean roleId;
    private String createdDate;
    private boolean status;

    public Users1() {
    }
   
    public Users1(String usersCode, String hoTen, String username, String password, String email, String phone, String address, boolean roleId, String createdDate, boolean status) {
        this.usersCode = usersCode;
        this.hoTen = hoTen;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.roleId = roleId;
        this.createdDate = createdDate;
        this.status = status;
    }

    public Users1(String usersCode, String hoTen, String username, String email, String phone, String address, boolean roleId, String createdDate, boolean status) {
        this.usersCode = usersCode;
        this.hoTen = hoTen;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.roleId = roleId;
        this.createdDate = createdDate;
        this.status = status;
    }
    
   
    
    public String getUsersCode() {
        return usersCode;
    }

    public void setUsersCode(String usersCode) {
        this.usersCode = usersCode;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isRoleId() {
        return roleId;
    }

    public void setRoleId(boolean roleId) {
        this.roleId = roleId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Users1{" + "usersCode=" + usersCode + ", hoTen=" + hoTen + ", username=" + username + ", password=" + password + ", email=" + email + ", phone=" + phone + ", address=" + address + ", roleId=" + roleId + ", createdDate=" + createdDate + ", status=" + status + '}';
    }


   
}

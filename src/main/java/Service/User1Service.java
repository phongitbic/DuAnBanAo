package Service;

import Model.Users1;
import com.toedter.calendar.JDateChooser;
import dao.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

public class User1Service {
     private int currentOrderPagee = 1; // Trang hiện tại của hóa đơn
    private int itemsPerOrderPagee = 6; // Số mục trên mỗi trang hóa đơn
    private int totalOrderItemss; // Tổng số mục hóa đơn
    private int totalOrderPagess; // Tổng số trang hóa đơn
    
    private DatabaseConnectionManager dbManager;

    public User1Service() {
        dbManager = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
    }

    ArrayList<Users1> listUs1 = new ArrayList<>();
    
    

    public ArrayList<Users1> getAllUser1() throws SQLDataException {
        try {
            listUs1.clear();
            Connection cn = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
            String sql = "select users_code, hoTen, username, email, phone, address, role_id, created_date, status from Users";
            
            Statement sm = cn.createStatement();
            ResultSet rs = sm.executeQuery(sql);

            while (rs.next()) {
                Users1 us1 = new Users1();
                us1.setUsersCode(rs.getString("users_code"));
                us1.setHoTen(rs.getString("hoTen"));
                us1.setUsername(rs.getString("username"));
                us1.setEmail(rs.getString("email"));
                us1.setPhone(rs.getString("phone"));
                us1.setAddress(rs.getString("address"));
                us1.setRoleId(rs.getBoolean("role_id"));
                us1.setCreatedDate(rs.getString("created_date"));
                us1.setStatus(rs.getBoolean("status"));
                listUs1.add(us1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listUs1;

    }    
    
    public ArrayList<Users1> TimKiemUser1(String hoTenCt) throws SQLDataException {
       ArrayList<Users1> listUser= new ArrayList<>();
        for (Users1 users1 : getAllUser1()) {
            if (removeVietnameseAccents(users1.getHoTen()).toUpperCase().contains(removeVietnameseAccents(hoTenCt).toUpperCase())||
                    removeVietnameseAccents(users1.getPhone()).toUpperCase().contains(removeVietnameseAccents(hoTenCt).toUpperCase())||
                    removeVietnameseAccents(users1.getUsername()).toUpperCase().contains(removeVietnameseAccents(hoTenCt).toUpperCase())) {
                listUser.add(users1);
                
            }
        }
        return listUser;
    }
    
    private String removeVietnameseAccents(String str) {
        str = str.replaceAll("[đĐ]", "d");
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }
    

    public Integer addNv(Users1 us1) throws SQLDataException {
        Integer row = null;
        try {
            Connection cn = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
            String sql = "insert into Users(hoTen, username,[password], email, phone, [address], role_id, created_date, [status]) \n"
                    + "	values(?, ?, ?,?,?,?, ?,?,?)";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, us1.getHoTen());
            ps.setString(2, us1.getUsername());
            ps.setString(3, us1.getPassword());
            ps.setString(4, us1.getEmail());
            ps.setString(5, us1.getPhone());
            ps.setString(6, us1.getAddress());
            ps.setBoolean(7, us1.isRoleId());
            ps.setString(8, us1.getCreatedDate());
            ps.setBoolean(9, us1.isStatus());
            row = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    public Integer updateNv(Users1 us1) throws SQLDataException {
        Integer row = null;
        try {
            Connection cn = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
            String sql = "update Users set hoTen=?, email=?, phone=?, address=?, role_id=?, created_date=?, status=? where username=?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, us1.getHoTen());
            ps.setString(2, us1.getEmail());
            ps.setString(3, us1.getPhone());
            ps.setString(4, us1.getAddress());
            ps.setBoolean(5, us1.isRoleId());
            ps.setString(6, us1.getCreatedDate());
            ps.setBoolean(7, us1.isStatus());
            ps.setString(8, us1.getUsername());
            row = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    public void loadDataTable(JTable tblNhanVien) throws SQLDataException {

        ArrayList<Users1> listUs = getAllUser1();
        DefaultTableModel dtm = (DefaultTableModel) tblNhanVien.getModel();
        dtm.setRowCount(0); // Xóa tất cả các hàng hiện có

        for (Users1 us : listUs) {
            dtm.addRow(new Object[]{
                us.getUsersCode(),
                us.getHoTen(),
                us.getUsername(),
                us.getEmail(),
                us.getPhone(),
                us.getAddress(),
                us.isRoleId() ? "Quản lý" : "Nhân viên",
                us.getCreatedDate(),
                us.isStatus() ? "Đang làm" : "Nghỉ làm"
            });
        }
    }


//    public void showDataNv(int index, JTextField txtHoTen1, JTextField txtTenDangNhap,
//            JTextField txtEmail1, JTextField txtSDT, JTextField txtDiaChi1, JComboBox<String> cbbVaiTro,
//            JDateChooser dateNgayTao, JRadioButton rdoDanglam, JRadioButton rdoNghilam, JTable tblNhanVien) {
//
//        try {
//            listUs1 = getAllUser1();
//            if (index >= 0 && index < listUs1.size()) {
//                Users1 us1 = listUs1.get(index);
//                txtHoTen1.setText(us1.getHoTen());
//                txtTenDangNhap.setText(us1.getUsername());
//                txtEmail1.setText(us1.getEmail());
//                txtSDT.setText(us1.getPhone());
//                txtDiaChi1.setText(us1.getAddress());
//                cbbVaiTro.setSelectedItem(us1.isRoleId() ? "Quản lý" : "Nhân viên");
//                index = tblNhanVien.getSelectedRow();
//                String ngay = tblNhanVien.getValueAt(index, 7).toString();
//                LocalDate lcd = null;
//                try {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                    lcd = LocalDate.parse(ngay, formatter);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (lcd != null) {
//                    // chuyển đổi sang định dạng dd/MM/yyyy 
//                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//                    String formattedDate = lcd.format(outputFormatter);
//                    try {
//                        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(formattedDate);
//                        dateNgayTao.setDate(date);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                rdoDanglam.setSelected(us1.isStatus());
//                rdoNghilam.setSelected(!us1.isStatus());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
      
    
        
      public Users1 getUsersByCode(String user) throws SQLDataException{
        for(Users1 u : getAllUser1())
        {
            if(u.getUsersCode().equalsIgnoreCase(user)){
                return u;
            }
        }
        return null;
    }
      
public void showDataNv(String maNhanVien, JTextField txtMaNhanVienn,JTextField txtHoTen1, JTextField txtTenDangNhap,
        JTextField txtEmail1, JTextField txtSDT, JTextField txtDiaChi1, JComboBox<String> cbbVaiTro,
        JDateChooser dateNgayTao, JRadioButton rdoDanglam, JRadioButton rdoNghilam, JTable tblNhanVien) {

    try {
        Users1 us1 = getUsersByCode(maNhanVien); // Lấy thông tin nhân viên bằng mã nhân viên
        if (us1 != null) {
            txtMaNhanVienn.setText(us1.getUsersCode());
            txtHoTen1.setText(us1.getHoTen());
            txtTenDangNhap.setText(us1.getUsername());
            txtEmail1.setText(us1.getEmail());
            txtSDT.setText(us1.getPhone());
            txtDiaChi1.setText(us1.getAddress());
            cbbVaiTro.setSelectedItem(us1.isRoleId() ? "Quản lý" : "Nhân viên");

            // Định dạng và hiển thị ngày
            String ngay = us1.getCreatedDate(); // Giả sử bạn có phương thức getNgayTao() trong Users1
            LocalDate lcd = null;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                lcd = LocalDate.parse(ngay, formatter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (lcd != null) {
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = lcd.format(outputFormatter);
                try {
                    Date date = new SimpleDateFormat("dd/MM/yyyy").parse(formattedDate);
                    dateNgayTao.setDate(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            rdoDanglam.setSelected(us1.isStatus());
            rdoNghilam.setSelected(!us1.isStatus());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}



public Users1 readFormAddNv(JTextField txtHoTen1, JTextField txtTenDangNhap, JPasswordField txtMatKhau,
            JTextField txtEmail1, JTextField txtSDT, JTextField txtDiaChi1, JComboBox<String> cbbVaiTro,
            JDateChooser dateNgayTao, JRadioButton rdoDanglam, JRadioButton rdoNghilam
    ) {
        Users1 us1 = new Users1();
        us1.setHoTen(txtHoTen1.getText());
        us1.setUsername(txtTenDangNhap.getText());
        us1.setPassword(txtMatKhau.getText());
        us1.setEmail(txtEmail1.getText());
        us1.setPhone(txtSDT.getText());
        us1.setAddress(txtDiaChi1.getText());
        us1.setRoleId(cbbVaiTro.getSelectedIndex() == 0);

        Date ngayDuocChon = dateNgayTao.getDate();
        if (ngayDuocChon != null) {
            SimpleDateFormat dinhDangNgay = new SimpleDateFormat("yyyy-MM-dd");
            String ngayDinhDang = dinhDangNgay.format(ngayDuocChon);
            us1.setCreatedDate(ngayDinhDang);
        }
        us1.setStatus(rdoDanglam.isSelected() && !rdoNghilam.isSelected());
        return us1;
    }

    public Users1 readFormUpdateNv(JTextField txtHoTen1, JTextField txtTenDangNhap,
            JTextField txtEmail1, JTextField txtSDT, JTextField txtDiaChi1, JComboBox<String> cbbVaiTro,
            JDateChooser dateNgayTao, JRadioButton rdoDanglam, JRadioButton rdoNghilam) {
        Users1 us1 = new Users1();
        us1.setHoTen(txtHoTen1.getText());
        us1.setUsername(txtTenDangNhap.getText());
        us1.setEmail(txtEmail1.getText());
        us1.setPhone(txtSDT.getText());
        us1.setAddress(txtDiaChi1.getText());
        us1.setRoleId(cbbVaiTro.getSelectedIndex() == 0);

        Date ngayDuocChon = dateNgayTao.getDate();
        if (ngayDuocChon != null) {
            SimpleDateFormat dinhDangNgay = new SimpleDateFormat("yyyy-MM-dd");
            String ngayDinhDang = dinhDangNgay.format(ngayDuocChon);
            us1.setCreatedDate(ngayDinhDang);
        }
        us1.setStatus(rdoDanglam.isSelected() && !rdoNghilam.isSelected());
        return us1;
    }

    public boolean validateAddNv(JTextField txtHoTen1, JTextField txtTenDangNhap,
            JTextField txtEmail1, JTextField txtSDT, JTextField txtDiaChi1, JDateChooser dateNgayTao) {

        try {
            if (txtHoTen1.getText().trim().isEmpty() || txtTenDangNhap.getText().trim().isEmpty()
                    || txtEmail1.getText().trim().isEmpty()
                    || txtSDT.getText().trim().isEmpty() || txtDiaChi1.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Không để trống trường dữ liệu");
                return false;
            }
            if (!txtHoTen1.getText().trim().matches("^[^\\d]*$")||txtHoTen1.getText().trim().matches("^\\d+$")) {
                JOptionPane.showMessageDialog(null, "Họ và tên không được chứa số hoặc chỉ toàn là số");
                return false;
            }
            if (!txtEmail1.getText().trim().matches("^[\\w._%+-]+@[\\w.-]+\\.com$")) {
                JOptionPane.showMessageDialog(null, "Email phải có kí tự @ và đuôi .com");
                return false;
            }
            if (!txtTenDangNhap.getText().trim().matches("^(?=.*[a-zA-Z])(?=.*[0-9]).+$")) {
                JOptionPane.showMessageDialog(null, "Tên đăng nhập phải có chữ và số");
                return false;
            }
            if (txtDiaChi1.getText().trim().matches("^[0-9]+$")) {
                JOptionPane.showMessageDialog(null, "Địa chỉ không được để toàn bộ là số!");
                return false;
            }
            if (!txtSDT.getText().trim().matches("0[0-9]{9}")) {
                JOptionPane.showMessageDialog(null, "Số điện thoại phải bắt đầu bằng số 0 và đủ 10 chữ số");
                return false;
            }
            if (!txtSDT.getText().trim().matches("^(03|05|07|08|09)\\d{8}$")) {
                JOptionPane.showMessageDialog(null, "Số điện thoại phải hợp lệ các đầu số đt ở VN ví dụ: 03,05,07,08,09");
                return false;
            }
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            // Lấy ngày được chọn từ JDateChooser
            Date selectedDate = dateNgayTao.getDate();
            // So sánh ngày hiện tại với ngày được chọn
            if (!sdf.format(currentDate).equals(sdf.format(selectedDate))) {
                JOptionPane.showMessageDialog(null, "Ngày tạo phải là ngày hiện tại");
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Số điện thoại phải nhập số");
        }
        return true;
    }

// đổi mk 
public boolean validateDoiMk(JTextField txtUsername1, JTextField txtPassword, JTextField txtNewPassword, JTextField txtConfirmPassword) {

        if (txtUsername1.getText().trim().isEmpty() || txtPassword.getText().trim().isEmpty()
                || txtNewPassword.getText().trim().isEmpty() || txtConfirmPassword.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không để trống các trường dữ liệu");
            return false;
        }
        if (!txtUsername1.getText().trim().matches("^(?=.*[a-zA-Z])(?=.*[0-9]).+$")) {
            JOptionPane.showMessageDialog(null, "Tên đăng nhập phải có chữ và số");
            return false;
        }
        if (!txtPassword.getText().trim().matches("^(?=.*[a-zA-Z])(?=.*[0-9]).+$")) {
            JOptionPane.showMessageDialog(null, "Mật khẩu phải có chữ và số");
            return false;
        }
        if (!txtNewPassword.getText().trim().matches("^(?=.*[a-zA-Z])(?=.*[0-9]).+$")) {
            JOptionPane.showMessageDialog(null, "Mật khẩu mới phải có chữ và số");
            return false;
        }
        if (!txtNewPassword.getText().trim().equals(txtConfirmPassword.getText().trim())) {
            JOptionPane.showMessageDialog(null, "Xác nhận mật khẩu không trùng khớp với mật khẩu mới");
            return false;
        }
        if (txtPassword.getText().trim().length() <= 6) {
            JOptionPane.showMessageDialog(null, "Mật khẩu phải có độ dài lớn hơn 6");
            return false;
        }

        return true;
    }

    public boolean doiMk(String username, String password, String newPassword) {
        try {
            DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
            Connection connection = dcm.getConnection();
            String sql = "SELECT password FROM Users WHERE username = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String currentPassword = rs.getString("password");
                if (currentPassword.equals(password)) {
                    sql = "UPDATE Users SET password = ? WHERE username = ?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, newPassword);
                    pstmt.setString(2, username);
                    pstmt.executeUpdate();
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Mật khẩu hiện tại không đúng");
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Tên đăng nhập không tồn tại");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

public void saveNV(JTextField txtHoTen1, JTextField txtTenDangNhap, JPasswordField txtMatKhau,
        JTextField txtEmail1, JTextField txtSDT, JTextField txtDiaChi1, JComboBox<String> cbbVaiTro,
        JDateChooser dateNgayTao, JRadioButton rdoDanglam, JRadioButton rdoNghilam, JTable tblNhanVien,String maNhanVien ) throws SQLDataException {
    
//    Users1 us11 = getUsersByCode(maNhanVien); // Lấy thông tin nhân viên bằng mã nhân viên

if (!maNhanVien.trim().isEmpty()){
        // Cập nhật
        dateNgayTao.setEnabled(false);
        if (validateUpdateNv(txtHoTen1, txtTenDangNhap, txtEmail1, txtSDT, txtDiaChi1)) {
            try {
                ArrayList<Users1> listNv = getAllUser1();
                Users1 currentUser = getUsersByCode(maNhanVien);
                
                // Kiểm tra các thay đổi
                boolean thaydoiUser = !txtHoTen1.getText().equalsIgnoreCase(currentUser.getHoTen())
                        || !txtEmail1.getText().equalsIgnoreCase(currentUser.getEmail())
                        || !txtSDT.getText().equalsIgnoreCase(currentUser.getPhone())
                        || !txtDiaChi1.getText().equalsIgnoreCase(currentUser.getAddress())
                        || !cbbVaiTro.getSelectedItem().toString().equalsIgnoreCase(currentUser.isRoleId() ? "Quản lý" : "Nhân viên")
                        || !((JTextField) dateNgayTao.getDateEditor().getUiComponent()).getText().equals(currentUser.getCreatedDate().toString())
                        || rdoDanglam.isSelected() != currentUser.isStatus();
                
                if (!thaydoiUser) {
                    JOptionPane.showMessageDialog(null, "Không có thay đổi nào để lưu");
                    return;
                }
                
                // Kiểm tra tính duy nhất của email và số điện thoại
                for (Users1 us : listNv) {
                    if (!us.getUsername().equals(currentUser.getUsername())) {
                        if (us.getEmail().equalsIgnoreCase(txtEmail1.getText())) {
                            JOptionPane.showMessageDialog(null, "Email đã tồn tại");
                            return;
                        }
                        if (us.getPhone().equalsIgnoreCase(txtSDT.getText())) {
                            JOptionPane.showMessageDialog(null, "SDT đã tồn tại");
                            return;
                        }
                    }
                }
                
                Users1 us1 = readFormUpdateNv(txtHoTen1, txtTenDangNhap, txtEmail1, txtSDT, txtDiaChi1, cbbVaiTro, dateNgayTao, rdoDanglam, rdoNghilam);
                if (updateNv(us1) != null) {
                    JOptionPane.showMessageDialog(null, "Sửa thành công");
                    txtTenDangNhap.setVisible(true);
                    // Cập nhật bảng sau khi sửa thành công
                    tblNhanVien.setModel(loadTableNV());
                } else {
                    JOptionPane.showMessageDialog(null, "Sửa thất bại");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } else {
        // Thêm mới
        if (validateAddNv(txtHoTen1, txtTenDangNhap, txtEmail1, txtSDT, txtDiaChi1, dateNgayTao)) {
            try {
                ArrayList<Users1> listNv = getAllUser1();
                for (Users1 us : listNv) {
                    if (us.getUsername().equalsIgnoreCase(txtTenDangNhap.getText())) {
                        JOptionPane.showMessageDialog(null, "Tên đăng nhập đã tồn tại");
                        return;
                    }
                    if (us.getEmail().equalsIgnoreCase(txtEmail1.getText())) {
                        JOptionPane.showMessageDialog(null, "Email đã tồn tại");
                        return;
                    }
                    if (us.getPhone().equalsIgnoreCase(txtSDT.getText())) {
                        JOptionPane.showMessageDialog(null, "SDT đã tồn tại");
                        return;
                    }
                }

                // Tạo mật khẩu mới
                String generatedPassword = generateCode();
                txtMatKhau.setText(generatedPassword);
                System.out.println("Mật khẩu được tạo: " + generatedPassword);

                Users1 us1 = readFormAddNv(txtHoTen1, txtTenDangNhap, txtMatKhau, txtEmail1, txtSDT, txtDiaChi1, cbbVaiTro, dateNgayTao, rdoDanglam, rdoNghilam);
                System.out.println(us1);
                if (addNv(us1) != null) {
                    // Gửi email thông báo mật khẩu mới
                    System.out.println("Gửi email đến: " + txtEmail1.getText().trim());
                    sendEmail(txtEmail1.getText().trim(), "Email thông báo mật khẩu", "Mật khẩu mới của bạn là: " + generatedPassword);
                    JOptionPane.showMessageDialog(null, "Thêm thành công");
                    // Cập nhật bảng sau khi thêm thành công
                    tblNhanVien.setModel(loadTableNV());
                } else {
                    JOptionPane.showMessageDialog(null, "Thêm thất bại");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra: " + e.getMessage());
            }
        }
    }
}


    public boolean updateTtNv(int idUser, String hoTen, String userName, String email, String sdt, String diaChi, String vaiTro) {

        try {
            Connection cn = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
            String sql = "update Users set hoTen=?, email=?, phone=?, [address]=?, role_id=? where user_id=?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, hoTen);
            ps.setString(2, email);
            ps.setString(3, sdt);
            ps.setString(4, diaChi);
            ps.setString(5, vaiTro);
            ps.setObject(6, idUser);
            ps.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Users1 getUser1(int id) throws SQLDataException {
        try {
            Connection cn = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
            String sql = "select users_code, hoTen, username, email, phone, address, role_id, created_date, status from Users\n"
                    + "where user_id=?";
            PreparedStatement sm = cn.prepareCall(sql);
            sm.setObject(1, id);
            Users1 us1 = null;
            ResultSet rs = sm.executeQuery();
            while (rs.next()) {
                us1 = new Users1();
                us1.setUsersCode(rs.getString("users_code"));
                us1.setHoTen(rs.getString("hoTen"));
                us1.setUsername(rs.getString("username"));

                us1.setEmail(rs.getString("email"));
                us1.setPhone(rs.getString("phone"));
                us1.setAddress(rs.getString("address"));
                us1.setRoleId(rs.getBoolean("role_id"));
                us1.setCreatedDate(rs.getString("created_date"));
                us1.setStatus(rs.getBoolean("status"));
                return us1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    // tự gen mk
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8;

 public static String generateCode() {
    Random random = new Random();
    StringBuilder code = new StringBuilder(CODE_LENGTH);

    for (int i = 0; i < CODE_LENGTH; i++) {
        int index = random.nextInt(CHARACTERS.length());
        code.append(CHARACTERS.charAt(index));
    }

    return code.toString();
}

// send mail

public static void sendEmail(String toEmail, String subject, String body) {
    // Cấu hình thông tin server SMTP
    final String username = "tuanndph47264@fpt.edu.vn";
    final String password = "fdjhxqqmuvgvnnem";
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");

    // Tạo session
    Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    });

    try {
        // Tạo đối tượng MimeMessage
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        // Gửi email
        Transport.send(message);

        System.out.println("Email đã được gửi thành công!");

    } catch (MessagingException e) {
        e.printStackTrace();
        System.out.println("Lỗi khi gửi email: " + e.getMessage());
    }
}

    //06/08
    public boolean checkUpdateTtNv(String hoTen,String email, String sdt, String diaChi) {
        if (hoTen.trim().isEmpty()||email.trim().isEmpty()||sdt.trim().isEmpty()
                ||diaChi.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không để trống các trường dữ liệu");
            return false;
        }
        if (!hoTen.trim().matches("^[^\\d]*$")||hoTen.trim().matches("^\\d+$")) {
                JOptionPane.showMessageDialog(null, "Họ và tên không được chứa số hoặc chỉ toàn là số");
                return false;
            }
        if (!email.trim().matches("^[\\w._%+-]+@[\\w.-]+\\.com$")) {
                JOptionPane.showMessageDialog(null, "Email phải có kí tự @ và đuôi .com");
                return false;
        }
        if (!sdt.trim().matches("0[0-9]{9}")) {
                JOptionPane.showMessageDialog(null, "Số điện thoại phải bắt đầu bằng số 0 và đủ 10 chữ số");
                return false;
        }
        if (!sdt.trim().matches("^(03|05|07|08|09)\\d{8}$")) {
                JOptionPane.showMessageDialog(null, "Số điện thoại phải hợp lệ các đầu số đt ở VN ví dụ: 03,05,07,08,09");
                return false;
        }
        if (diaChi.trim().matches("^[0-9]+$")) {
                JOptionPane.showMessageDialog(null, "Địa chỉ không được nhập toàn bộ là số ");
                return false;
        }
        return true;
    }
    
    
    
    public boolean validateUpdateNv(JTextField txtHoTen1, JTextField txtTenDangNhap,
        JTextField txtEmail1, JTextField txtSDT, JTextField txtDiaChi1) {

    try {
        if (txtHoTen1.getText().trim().isEmpty() || txtTenDangNhap.getText().trim().isEmpty()
                || txtEmail1.getText().trim().isEmpty()
                || txtSDT.getText().trim().isEmpty() || txtDiaChi1.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không để trống trường dữ liệu");
            return false;
        }
        if (!txtHoTen1.getText().trim().matches("^[^\\d]*$")||txtHoTen1.getText().trim().matches("^\\d+$")) {
                JOptionPane.showMessageDialog(null, "Họ và tên không được chứa số hoặc chỉ toàn là số");
                return false;
            }
        if (!txtEmail1.getText().trim().matches("^[\\w._%+-]+@[\\w.-]+\\.com$")) {
            JOptionPane.showMessageDialog(null, "Email phải có kí tự @ và đuôi .com");
            return false;
        }
        if (!txtTenDangNhap.getText().trim().matches("^(?=.*[a-zA-Z])(?=.*[0-9]).+$")) {
            JOptionPane.showMessageDialog(null, "Tên đăng nhập phải có chữ và số");
            return false;
        }
        if (txtDiaChi1.getText().trim().matches("^[0-9]+$")) {
            JOptionPane.showMessageDialog(null, "Địa chỉ không được nhập toàn bộ là số ");
            return false;
        }
        if (!txtSDT.getText().trim().matches("0[0-9]{9}")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại phải bắt đầu bằng số 0 và đủ 10 chữ số");
            return false;
        }
        if (!txtSDT.getText().trim().matches("^(03|05|07|08|09)\\d{8}$")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại phải hợp lệ các đầu số đt ở VN ví dụ: 03,05,07,08,09");
            return false;
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, "Số điện thoại phải nhập số");
    }
    return true;
}
    
    
    
     private void calculatePagination() throws SQLDataException {
    totalOrderItemss = getAllUser1().size(); // Lấy tổng số mục hóa đơn
    totalOrderPagess = (int) Math.ceil((double) totalOrderItemss / itemsPerOrderPagee); // Tính số trang
}

        public void nextPage() {
    if (currentOrderPagee < totalOrderPagess) {
        currentOrderPagee++;
    }
} 
     

public void previousPage() {
    if (currentOrderPagee > 1) {
        currentOrderPagee--;
    }
}

public void goToPage(int page) {
    if (page > 0 && page <= totalOrderPagess) {
        currentOrderPagee = page;
    }
}

public int getCurrentOrderPage() {
    return currentOrderPagee;
}

public int getTotalOrderPages() {
    return totalOrderPagess;
}


public void setCurrentOrderPage(int page) {
    if (page >= 1 && page <= totalOrderPagess) {
        currentOrderPagee = page;
    }
     

}


public DefaultTableModel loadTableNV() throws SQLDataException {
    DefaultTableModel model = new DefaultTableModel();
    model.setColumnIdentifiers(new Object[]{
        "Mã nhân viên", "Họ tên", "Tên đăng nhập", "Email", "SDT", "Địa chỉ", "Vai trò", "Ngày tạo", "Trạng thái"
    });
    calculatePagination();

    String query = "SELECT users_code, hoTen, username, email, phone, address, role_id, created_date, status FROM Users ORDER BY users_code OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    try (Connection connection = dbManager.getConnection();
         PreparedStatement pst = connection.prepareStatement(query)) {
        pst.setInt(1, (currentOrderPagee - 1) * itemsPerOrderPagee); // OFFSET
        pst.setInt(2, itemsPerOrderPagee); // FETCH NEXT

        try (ResultSet rs = pst.executeQuery()) {
            int stt = (currentOrderPagee - 1) * itemsPerOrderPagee + 1; // Đếm số thứ tự bắt đầu từ dòng bắt đầu
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("users_code"),
                    rs.getString("hoTen"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getInt("role_id") == 1 ? "Quản lý" : "Nhân viên",
                    rs.getDate("created_date"),
                    rs.getInt("status") == 1 ? "Đang làm" : "Nghỉ làm",
                });
                stt++;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return model;
}



}

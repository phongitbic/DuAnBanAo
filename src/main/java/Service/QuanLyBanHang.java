package Service;

import Model.ProductDetails;
import dao.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class QuanLyBanHang {

    DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");

    public void loadUserInfo(int userId, JTextField txtHoTen, JTextField txtUsername, JTextField txtEmail, JTextField txtSdt, JTextField txtDiaChi, JTextField txtVaiTro) {
        try {
            DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
            Connection connection = dcm.getConnection();
            String sql = "SELECT hoTen, username, [password], email, phone, [address], role_id FROM Users WHERE user_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtHoTen.setText(rs.getString("hoTen")); // Hiển thị tên người dùng
                txtUsername.setText(rs.getString("username"));
                txtEmail.setText(rs.getString("email"));
                txtSdt.setText(rs.getString("phone"));
                txtDiaChi.setText(rs.getString("address"));
                int roleId = rs.getInt("role_id");
                String roleName = getRoleName(roleId); // Lấy tên vai trò từ role_id
                txtVaiTro.setText(roleName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getRoleName(int roleId) {
        String roleName = "";
        try {
            DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
            Connection connection = dcm.getConnection();
            String sql = "SELECT role_name FROM Roles WHERE role_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                roleName = rs.getString("role_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roleName;
    }

    public void loadVoucher(javax.swing.JComboBox cbbVouchers) {

        cbbVouchers.addItem("");
        String query = "SELECT voucher_code FROM Voucher WHERE status = N'Hoạt động'";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String voucher_code = rs.getString("voucher_code");
                cbbVouchers.addItem(voucher_code);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void DanhSachHoaDonCho(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        String query = "SELECT o.order_code AS 'Mã HĐ', o.order_date AS 'Ngày tạo', c.customer_name AS 'Tên KH', c.phone AS 'Số điện thoại', "
                + "STRING_AGG(pd.productDetails_code + ' (' + CAST(od.quantity AS VARCHAR) + ')', ', ') AS 'Sản phẩm', "
                + "SUM(od.quantity) AS 'Số lượng', "
                + "o.total_price AS 'Tổng giá' "
                + "FROM Orders o "
                + "JOIN Customers c ON o.customer_id = c.customer_id "
                + "JOIN OrderDetails od ON o.order_id = od.order_id "
                + "JOIN ProductDetails pd ON od.productDetails_id = pd.productDetails_id "
                + "WHERE o.status = 'Chưa thanh toán' "
                + "GROUP BY o.order_code, o.order_date, c.customer_name, c.phone, o.total_price";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
            int stt = 1; // Biến đếm số thứ tự
            while (rs.next()) {
                String maHD = rs.getString("Mã HĐ");
                String ngayTao = rs.getString("Ngày tạo");
                String tenKH = rs.getString("Tên KH");
                String soDienThoai = rs.getString("Số điện thoại");
                String sanPham = rs.getString("Sản phẩm");
                int soLuong = rs.getInt("Số lượng");
                double tongGia = rs.getDouble("Tổng giá");

                model.addRow(new Object[]{stt++, maHD, ngayTao, tenKH, soDienThoai, sanPham, soLuong, tongGia});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật danh sách hóa đơn chờ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void LoadDSSPTrongQLBH(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        String query = "SELECT d.productDetails_code AS 'Mã SPCT', p.product_name AS 'Tên SP', c.category_name AS 'Loại SP', "
                + "cl.color_name AS 'Màu sắc', s.size_name AS 'Kích thước', st.style_name AS 'Phong cách', "
                + "d.quantity AS 'Số lượng', d.price AS 'Giá bán' "
                + "FROM Products p "
                + "JOIN Categories c ON p.category_id = c.category_id "
                + "JOIN ProductDetails d ON p.product_id = d.product_id "
                + "JOIN Color cl ON d.color_id = cl.color_id "
                + "JOIN Styles st ON d.style_id = st.style_id "
                + "JOIN Sizes s ON d.size_id = s.size_id";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
            int stt = 1;
            while (rs.next()) {
                String maSPCT = rs.getString("Mã SPCT");
                String tenSP = rs.getString("Tên SP");
                String loaiSP = rs.getString("Loại SP");
                String mauSac = rs.getString("Màu sắc");
                String kichThuoc = rs.getString("Kích thước");
                String phongCach = rs.getString("Phong cách");
                int soLuong = rs.getInt("Số lượng");
                double giaBan = rs.getDouble("Giá bán");
                model.addRow(new Object[]{stt, maSPCT, tenSP, loaiSP, mauSac, kichThuoc, phongCach, soLuong, giaBan});
                stt++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void themKhachHang(String tenKH, String sdtKH) throws SQLException {
        if (!sdtKH.matches("0[0-9]{9}")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ. Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số.");
        }
        String queryCheck = "SELECT customer_id FROM Customers WHERE phone = ?";
        String queryInsert = "INSERT INTO Customers (customer_name, phone, point_gained, points_used) VALUES (?, ?, 0, 0)";

        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstCheck = connection.prepareStatement(queryCheck); PreparedStatement pstInsert = connection.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {

            pstCheck.setString(1, sdtKH);
            try (ResultSet rs = pstCheck.executeQuery()) {
                if (rs.next()) {
                    throw new SQLException("Số điện thoại đã tồn tại trong cơ sở dữ liệu.");
                }
                pstInsert.setString(1, tenKH);
                pstInsert.setString(2, sdtKH);
                pstInsert.executeUpdate();
            }
        }
    }

    public List<ProductDetails> getAllProductDetail() {
        List<ProductDetails> listProductDetail = new ArrayList<>();

        String sql = """
                SELECT 
                     p.product_code,pd.productDetails_code,p.product_name,c.color_name,s.size_name,
                     st.style_name,pd.image_url,pd.quantity,pd.price,ca.category_name,pd.productDetails_id,p.product_id
                     FROM Products p
                     JOIN ProductDetails pd ON p.product_id = pd.product_id
                     JOIN Color c ON pd.color_id = c.color_id
                     JOIN Sizes s ON pd.size_id = s.size_id
                     JOIN Styles st ON pd.style_id = st.style_id
                     JOIN Categories ca ON ca.category_id = p.category_id;""";
        listProductDetail.clear();

        try (Connection con = dcm.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String product_code = rs.getString(1);
                String productDetails_code = rs.getString(2);
                String product_name = rs.getString(3);
                String color_name = rs.getString(4);
                String size_name = rs.getString(5);
                String style_name = rs.getString(6);
                String image_url = rs.getString(7);
                Integer quantity = rs.getInt(8);
                Double price = rs.getDouble(9);
                String category_name = rs.getString(10);
                Integer productDetails_id = rs.getInt(11);
                Integer product_id = rs.getInt(12);
                ProductDetails spct = new ProductDetails(productDetails_id, product_id, product_code, productDetails_code, product_name, style_name, size_name, color_name, image_url, price, quantity, category_name);
                listProductDetail.add(spct);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listProductDetail;
    }

    public List<ProductDetails> getListQRCode(String stringQr) {
        List<ProductDetails> listQR = new ArrayList<ProductDetails>();

        for (ProductDetails productDetails : getAllProductDetail()) {
            if (productDetails.getProductDetail_code().equals(stringQr)) {
                listQR.add(productDetails);
            }
        }
        return listQR;
    }

    public void loadDanhSachSPCT(JTable table, List<ProductDetails> list) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        int stt = 1;
        if (!list.isEmpty()) {
            for (ProductDetails spct : list) {
                Object[] rowData = {
                    stt++,
                    spct.getProductDetail_code(),
                    spct.getProduct_name(),
                    spct.getCategory_name(),
                    spct.getColor(),
                    spct.getSize(),
                    spct.getStyle(),
                    spct.getQuantity(),
                    spct.getPrice()
                };
                dtm.addRow(rowData);
            }
        }
    }

}

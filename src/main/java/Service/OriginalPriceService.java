package Service;

import Model.OriginalPrice;
import dao.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OriginalPriceService {

    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Connection con = null;
    private String sql = "";
    ArrayList<OriginalPrice> listOriginalPrice = new ArrayList<>();

    // Phương thức khởi tạo để thiết lập kết nối cơ sở dữ liệu
    public OriginalPriceService() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
        con = dcm.getConnection();
    }

    // Phương thức lấy tất cả các bản ghi từ bảng OriginalPrice
    public ArrayList<OriginalPrice> getAll() {
        try {
            String sql = "SELECT productDetails_id, original_price FROM dbo.OriginalPrice";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                int productDetailsId = rs.getInt(1);
                double originalPrice = rs.getDouble(2);
                OriginalPrice originalPriceObj = new OriginalPrice(productDetailsId, originalPrice);
                listOriginalPrice.add(originalPriceObj);
            }
            return listOriginalPrice;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Phương thức lấy giá gốc của sản phẩm dựa trên productDetails_id
    public double getOriginalPriceById(int productDetailsId) {
        double originalPrice = 0;
        try {
            String sql = "SELECT original_price FROM OriginalPrice WHERE productDetails_id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, productDetailsId);
            rs = ps.executeQuery();
            if (rs.next()) {
                originalPrice = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return originalPrice;
    }
public ArrayList<OriginalPrice> tim(int id) {
    ArrayList<OriginalPrice> listOriginalPrice = new ArrayList<>();
    try {
        String sql = "SELECT productDetails_id, original_price FROM dbo.OriginalPrice WHERE productDetails_id = ?";
        ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        rs = ps.executeQuery();
        boolean found = false; // Biến theo dõi có tìm thấy kết quả hay không
        while (rs.next()) {
            found = true; // Đã tìm thấy kết quả
            int productDetailsId = rs.getInt("productDetails_id");
            double originalPrice = rs.getDouble("original_price");
            OriginalPrice originalPriceObj = new OriginalPrice(productDetailsId, originalPrice);
            listOriginalPrice.add(originalPriceObj);
        }
        // Nếu không tìm thấy kết quả nào, trả về null
        if (!found) {
            return null;
        }
        return listOriginalPrice;
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}

      
}

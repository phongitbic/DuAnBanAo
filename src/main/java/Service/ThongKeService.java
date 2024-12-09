
package Service;

import Model.Customers;
import Model.Orders;
import Model.ProductDetails;
import Model.Products;
import Model.ProductsInfo;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;
import dao.DatabaseConnectionManager;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.xml.transform.Result;

/**
 *
 * @author Admin
 */
public class ThongKeService {

    public ArrayList<ProductsInfo> getAllProduct() {
    ArrayList<ProductsInfo> list = new ArrayList<>();
    String sql = "SELECT DISTINCT\n"
            + "    p.product_code,\n"
            + "    p.product_name,\n"
            + "    p.category_id,\n"
            + "    s.supplier_name,\n"
            + "    COALESCE(SUM(od.quantity), 0) AS soLuong,\n"
            + "    SUM(od.quantity * od.unit_price) AS TongTien,\n"
            + "    o.[status] AS order_status\n"
            + "FROM\n"
            + "    Suppliers s\n"
            + "JOIN\n"
            + "    Products p ON s.supplier_id = p.supplier_id\n"
            + "JOIN\n"
            + "    ProductDetails pd ON p.product_id = pd.product_id\n"
            + "JOIN\n"
            + "    OrderDetails od ON pd.productDetails_id = od.productDetails_id\n"
            + "JOIN\n"
            + "    Orders o ON od.order_id = o.order_id\n"
            + "WHERE\n"
            + "    o.[status] LIKE N'%Đã thanh toán%'\n"
            + "GROUP BY\n"
            + "    p.product_code,\n"
            + "    p.product_name,\n"
            + "    p.category_id,\n"
            + "    s.supplier_name,\n"
            + "    o.[status];";
    
    try (Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
         Statement stm = c.createStatement();
         ResultSet rs = stm.executeQuery(sql)) {
        
        while (rs.next()) {
            ProductsInfo products = new ProductsInfo();
            products.setProductCode(rs.getString("product_code"));
            products.setProductName(rs.getString("product_name"));
            products.setCategoryId(rs.getString("category_id"));
            products.setSoLuong(rs.getInt("soLuong"));
            products.setTongTien(rs.getBigDecimal("TongTien"));
            products.setNhaCungCap(rs.getString("supplier_name"));
            list.add(products);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}

    public Integer getTongSLSP(String tenSanPham) throws SQLException {
        Integer quantitySP = 0;
        // Kết nối cơ sở dữ liệu
        Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
        String sql = """
    SELECT SUM(pd.quantity) AS Tongquantity 
    FROM Products p 
    JOIN ProductDetails pd ON p.product_id = pd.product_id 
    WHERE p.product_name = ?
    GROUP BY p.product_name;
    """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tenSanPham);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                quantitySP = rs.getInt("Tongquantity");  // Sử dụng alias chính xác không có khoảng trắng
            } else {
                quantitySP = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        return quantitySP;
    }

    public Integer getTongSPDaBan(String tenSanPham) throws SQLException {
        Integer quantity = 0;
        Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
        String sql = """
            SELECT
                    p.product_name,
                    SUM(od.quantity) AS total_sold_quantity
                FROM
                    Orders o
                JOIN
                    OrderDetails od ON o.order_id = od.order_id
                JOIN
                    ProductDetails pd ON od.productDetails_id = pd.productDetails_id
                JOIN
                    Products p ON pd.product_id = p.product_id
                WHERE
                    p.product_name = ? AND o.status = N'Đã thanh toán'
                GROUP BY
                    p.product_name;
        """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tenSanPham);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                quantity = rs.getInt("total_sold_quantity");
            } else {
                quantity = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        return quantity;
    }

   public ArrayList<ProductsInfo> Top10SPBanItNhat() throws SQLException {
        Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
        ArrayList<ProductsInfo> list = new ArrayList<>();
        String sql = "SELECT TOP 10\n"
                + "    p.product_code,\n"
                + "    p.product_name,\n"
                + "    p.category_id,\n"
                + "    s.supplier_name,\n"
                + "    COALESCE(SUM(od.quantity), 0) AS SoLuong,\n"
                + "    COALESCE(SUM(od.quantity * od.unit_price), 0) AS TongTien\n"
                + "FROM \n"
                + "    Suppliers s\n"
                + "JOIN\n"
                + "    Products p ON s.supplier_id = p.supplier_id\n"
                + "LEFT JOIN \n"
                + "    ProductDetails pd ON p.product_id = pd.product_id\n"
                + "LEFT JOIN \n"
                + "    OrderDetails od ON pd.productDetails_id = od.productDetails_id\n"
                + "LEFT JOIN \n"
                + "    Orders o ON od.order_id = o.order_id AND o.status = N'Đã thanh toán'\n"
                + "GROUP BY \n"
                + "    p.product_code, p.product_name, p.category_id, s.supplier_name\n"
                + "ORDER BY \n"
                + "    SoLuong ASC, \n"
                + "    p.product_name;";

        try (Statement stm = c.createStatement(); ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                ProductsInfo products = new ProductsInfo();
                products.setProductCode(rs.getString("product_code"));
                products.setProductName(rs.getString("product_name"));
                products.setCategoryId(rs.getString("category_id"));
                products.setSoLuong(rs.getInt("soLuong"));
                products.setTongTien(rs.getBigDecimal("TongTien"));
                products.setNhaCungCap(rs.getString("supplier_name"));
                list.add(products);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
   
public ArrayList<ProductsInfo> Top10SPBanNhieuNhat() throws SQLException {
        Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
        String sql = "SELECT TOP 10\n"
                + "    p.product_code,\n"
                + "    p.product_name,\n"
                + "    p.category_id,\n"
                + "    s.supplier_name,\n"
                + "    COALESCE(SUM(od.quantity), 0) AS SoLuong,\n"
                + "    COALESCE(SUM(od.quantity * od.unit_price), 0) AS TongTien\n"
                + "FROM \n"
                + "    Suppliers s\n"
                + "JOIN\n"
                + "    Products p ON s.supplier_id = p.supplier_id\n"
                + "LEFT JOIN \n"
                + "    ProductDetails pd ON p.product_id = pd.product_id\n"
                + "LEFT JOIN \n"
                + "    OrderDetails od ON pd.productDetails_id = od.productDetails_id\n"
                + "LEFT JOIN \n"
                + "    Orders o ON od.order_id = o.order_id AND o.status = N'Đã thanh toán'\n"
                + "GROUP BY \n"
                + "    p.product_code, p.product_name, p.category_id, s.supplier_name\n"
                + "ORDER BY \n"
                + "    SoLuong DESC, \n"
                + "    p.product_name;";

        ArrayList<ProductsInfo> list = new ArrayList<>();
        try {
            Statement stm = c.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()) {
                ProductsInfo products = new ProductsInfo();
                products.setProductCode(rs.getString("product_code"));
                products.setProductName(rs.getString("product_name"));
                products.setCategoryId(rs.getString("category_id"));
                products.setSoLuong(rs.getInt("soLuong"));
                products.setTongTien(rs.getBigDecimal("TongTien"));
                products.setNhaCungCap(rs.getString("supplier_name"));
                list.add(products);

            }
        } catch (SQLException e) {
//           
            e.printStackTrace();
        } catch (Exception e) {
//            
            e.printStackTrace();
        } finally {
            try {
                if (c != null && !c.isClosed()) {
                    c.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return list;
    }


    public ArrayList<ProductsInfo> TimKiemTenSP(String nhapten) throws SQLException {
        ArrayList<ProductsInfo> productsInfos = new ArrayList<>();
        String nhapTenProduct = removeVietnameseAccents(nhapten);
        for (ProductsInfo product : getAllProduct()) {
            if (removeVietnameseAccents(product.getProductName()).toUpperCase().contains(nhapTenProduct.toUpperCase())) {
                productsInfos.add(product);
            }

        }
        return productsInfos;
    }

    private String removeVietnameseAccents(String str) {
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    public BigDecimal TongDoanhSo() throws SQLException {
        BigDecimal tongDoanhSo = BigDecimal.ZERO;
        Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
        String status = "Đã thanh toán";

        String sql = "SELECT SUM(total_price) AS total_revenue "
                + "FROM Orders "
                + "WHERE [status] = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                // Chuyển đổi từ BigDecimal từ ResultSet
                BigDecimal totalRevenue = resultSet.getBigDecimal("total_revenue");
                if (totalRevenue != null) {
                    tongDoanhSo = totalRevenue;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongDoanhSo;
    }

    public BigDecimal TongDoanhSoUpdate(Date fromDate, Date toDate) throws SQLException {
        BigDecimal tongDoanhSo = BigDecimal.ZERO;
        String status = "Đã thanh toán";

        String sql = "SELECT SUM(total_price) AS total_revenue\n"
                + "                FROM Orders \n"
                + "                WHERE [status] = ? AND order_date BETWEEN ? AND ?";

        try (Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setDate(2, new java.sql.Date(fromDate.getTime()));
            ps.setDate(3, new java.sql.Date(toDate.getTime()));
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                BigDecimal totalRevenue = resultSet.getBigDecimal("total_revenue");
                if (totalRevenue != null) {
                    tongDoanhSo = totalRevenue;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tongDoanhSo;
    }

    public BigDecimal getDoanhThuTheoThang(int year, int month) throws SQLException {
        BigDecimal DoanhSoThang = BigDecimal.ZERO;
        String sql = "SELECT SUM(o.total_price) AS DoanhSoThang "
                + "FROM Orders o "
                + "WHERE DATEPART(YEAR, o.order_date) = ? "
                + "AND DATEPART(MONTH, o.order_date) = ?";

        try (Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                DoanhSoThang = resultSet.getBigDecimal("DoanhSoThang");
            }
        }
        return DoanhSoThang;
    }

    public BigDecimal getDoanhThuTheoNgay(int year, int month, int day) throws SQLException {
        BigDecimal DoanhSoNgay = BigDecimal.ZERO;
        String sql = """
    SELECT SUM(total_price) AS Doanhthungay 
    FROM Orders 
    WHERE YEAR(order_date) = ? AND MONTH(order_date) = ? AND DAY(order_date) = ?
    """;

        try (Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);
            ps.setInt(3, day);

//            System.out.println("Executing SQL Query: " + sql);
//            System.out.println("Parameters - Year: " + year + ", Month: " + month + ", Day: " + day);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    DoanhSoNgay = resultSet.getBigDecimal("Doanhthungay");
                    if (DoanhSoNgay == null) {
                        DoanhSoNgay = BigDecimal.ZERO;
                    }
//                    System.out.println("DoanhSoNgay: " + DoanhSoNgay);
                } else {
                    System.out.println("No results found for the given date.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return DoanhSoNgay;
    }

    public ArrayList<ProductsInfo> LocKhoangDate(Date startDate, Date endDate) throws SQLException {
        Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
        String sql = "SELECT\n"
                + "    p.product_name,\n"
                + "    p.product_code,\n"
                + "    p.category_id,\n"
                + "    s.supplier_name,\n"
                + "    COALESCE(SUM(od.quantity), 0) AS soLuong,\n"
                + "    COALESCE(SUM(o.total_price), 0) AS tongTien\n"
                + "FROM\n"
                + "    Suppliers s\n"
                + "JOIN\n"
                + "    Products p ON s.supplier_id = p.supplier_id\n"
                + "JOIN\n"
                + "    ProductDetails pd ON p.product_id = pd.product_id\n"
                + "JOIN\n"
                + "    OrderDetails od ON pd.productDetails_id = od.productDetails_id\n"
                + "JOIN\n"
                + "    Orders o ON od.order_id = o.order_id\n"
                + "WHERE\n"
                + "    o.order_date BETWEEN ? AND ?\n"
                + "GROUP BY\n"
                + "    p.product_name,\n"
                + "    p.product_code,\n"
                + "    p.category_id,\n"
                + "    s.supplier_name\n"
                + "ORDER BY\n"
                + "    p.product_name ASC;";
        ArrayList<ProductsInfo> list = new ArrayList<>();
        try {
            PreparedStatement pst = c.prepareStatement(sql);
            pst.setDate(1, startDate);
            pst.setDate(2, endDate);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ProductsInfo products = new ProductsInfo();
                products.setProductCode(rs.getString("product_code"));
                products.setProductName(rs.getString("product_name"));
                products.setCategoryId(rs.getString("category_id"));
                products.setSoLuong(rs.getInt("soLuong"));
                products.setTongTien(rs.getBigDecimal("tongTien"));
                products.setNhaCungCap(rs.getString("supplier_name"));
                list.add(products);
            }
//            System.out.println("Số lượng sản phẩm tìm thấy: " + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void LocDSTheoThang(JYearChooser jDTTNam, JMonthChooser jDTTThang, JLabel lblDoanhThuThang) {
        int selectedYear = jDTTNam.getYear();
        int selectedMonth = jDTTThang.getMonth() + 1;

        if (selectedYear == -1 || selectedMonth < 1 || selectedMonth > 12) {
            lblDoanhThuThang.setText("Chưa chọn năm hoặc tháng.");
            return;
        }

        try {
            BigDecimal totalRevenue = getDoanhThuTheoThang(selectedYear, selectedMonth);

            if (totalRevenue == null) {
                totalRevenue = BigDecimal.ZERO;
            }

            lblDoanhThuThang.setText(""+ selectedMonth + "/" + selectedYear + " là : " + totalRevenue + " VND");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            lblDoanhThuThang.setText("Lỗi: Dữ liệu năm/tháng không hợp lệ.");
        } catch (SQLException e) {
            e.printStackTrace();
            lblDoanhThuThang.setText("Lỗi khi lấy dữ liệu doanh thu.");
        }
    }

    public void UpdateTable(JTable tblDanhSachSPdaBan, List<ProductsInfo> productList) throws SQLException {
        DefaultTableModel tableModel = (DefaultTableModel) tblDanhSachSPdaBan.getModel();
        tableModel.setRowCount(0);
        int STT = 1;
        for (ProductsInfo product : productList) {
            System.out.println("Adding product: " + product.getProductName());
            tableModel.addRow(new Object[]{
                STT++,
                product.getProductCode(),
                product.getProductName(),
                product.getCategoryId(),
                product.getSoLuong(),
                product.getTongTien(),
                product.getNhaCungCap()
            });
        }
    }

    public void LoadTableThongKe(JTable tblDanhSachSPdaBan) throws SQLException {
        DefaultTableModel dcm = (DefaultTableModel) tblDanhSachSPdaBan.getModel();
        dcm.setRowCount(0);
        int i = 1;

        for (ProductsInfo products : getAllProduct()) {
            Object[] row = {
                i++,
                products.getProductCode(),
                products.getProductName(),
                products.getCategoryId(),
                products.getSoLuong(),
                products.getTongTien(),
                products.getNhaCungCap()
            };
            dcm.addRow(row);
        }
    }

    public void TimKiemQuaTenSP(String nhapten, JTable tblDanhSachSPdaBan) throws SQLException {
        DefaultTableModel tkqtensanpham = (DefaultTableModel) tblDanhSachSPdaBan.getModel();
        tkqtensanpham.setRowCount(0);
        int i = 1;
        for (ProductsInfo products : TimKiemTenSP(nhapten)) {
            tkqtensanpham.addRow(new Object[]{
                i++,
                products.getProductCode(),
                products.getProductName(),
                products.getCategoryId(),
                products.getSoLuong(),
                products.getTongTien(),
                products.getNhaCungCap()
            });
        }
    }

    public void LoadTableTop10SPBanItNhat(JTable tblDanhSachSPdaBan) throws SQLException {
        DefaultTableModel dcm = (DefaultTableModel) tblDanhSachSPdaBan.getModel();
        dcm.setRowCount(0);
        int i = 1;

        for (ProductsInfo products : Top10SPBanItNhat()) {
            Object[] row = {
                i++,
                products.getProductCode(),
                products.getProductName(),
                products.getCategoryId(),
                products.getSoLuong(),
                products.getTongTien(),
                products.getNhaCungCap()
            };
            dcm.addRow(row);
        }
    }

    public void LoadTableLocTheoDate(java.sql.Date startDate, java.sql.Date endDate, JTable tblDanhSachSPdaBan) throws SQLException {
        DefaultTableModel locTheoDate = (DefaultTableModel) tblDanhSachSPdaBan.getModel();
        locTheoDate.setRowCount(0);  // Xóa bảng
        int i = 1;
        for (ProductsInfo products : LocKhoangDate(startDate, endDate)) {
            Object[] row = {
                i++,
                products.getProductCode(),
                products.getProductName(),
                products.getDescription(),
                products.getCategoryId(),
                products.getStatus()
            };
            locTheoDate.addRow(row);
        }
    }

    public void LoadTableTop10BanChayNhat(JTable tblDanhSachSPdaBan) throws SQLException {
        DefaultTableModel top10nhieunhat = (DefaultTableModel) tblDanhSachSPdaBan.getModel();
        top10nhieunhat.setRowCount(0);
        int i = 1;

        for (ProductsInfo products : Top10SPBanNhieuNhat()) {
            Object[] row = {
                i++,
                products.getProductCode(),
                products.getProductName(),
                products.getCategoryId(),
                products.getSoLuong(),
                products.getTongTien(),
                products.getNhaCungCap()
            };
            top10nhieunhat.addRow(row);
        }
    }

    public void updateTongDoanhSoTheoNgayThangNam(JDateChooser TuLocTheoKhoangUpdate, JDateChooser DenLocTheoKhoangUpdate, JLabel lblDoanhThuTong) {
    java.util.Date fromDate = TuLocTheoKhoangUpdate.getDate();
    java.util.Date toDate = DenLocTheoKhoangUpdate.getDate();

    if (fromDate != null && toDate != null) {
        // Validate that the start date is earlier than the end date
        if (fromDate.after(toDate)) {
            JOptionPane.showMessageDialog(null, "Ngày bắt đầu phải nhỏ hơn ngày kết thúc", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            java.sql.Date sqlFromDate = new java.sql.Date(fromDate.getTime());
            java.sql.Date sqlToDate = new java.sql.Date(toDate.getTime());

            BigDecimal totalSales = TongDoanhSoUpdate(sqlFromDate, sqlToDate);
            lblDoanhThuTong.setText("Tổng doanh thu: " + totalSales.toString() + " VND");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi cập nhật tổng doanh số.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Vui lòng chọn khoảng thời gian hợp lệ.");
    }
}

    public void LocDoanhSoTheoNgay(JDateChooser jDateDoanhThuNgay, JLabel lblDoanhThuNgay) {
        java.util.Date selectedDate = jDateDoanhThuNgay.getDate();

        if (selectedDate == null) {
            lblDoanhThuNgay.setText("Chưa chọn ngày.");
            return;
        }

        // Chuyển đổi java.util.Date thành LocalDate
        LocalDate localDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();

        // Lấy năm, tháng, và ngày từ LocalDate
        int selectedYear = localDate.getYear();
        int selectedMonth = localDate.getMonthValue();
        int selectedDay = localDate.getDayOfMonth();

        try {
            BigDecimal totalRevenue = getDoanhThuTheoNgay(selectedYear, selectedMonth, selectedDay);

            if (totalRevenue == null) {
                totalRevenue = BigDecimal.ZERO;
            }

            lblDoanhThuNgay.setText( selectedDay + "/" + selectedMonth + "/" + selectedYear + " là : " + totalRevenue + " VND");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            lblDoanhThuNgay.setText("Lỗi: Dữ liệu năm/tháng không hợp lệ.");
        } catch (SQLException e) {
            e.printStackTrace();
            lblDoanhThuNgay.setText("Lỗi khi lấy dữ liệu doanh thu.");
        }
    }

    public void UpdateProductsTheoDate(JDateChooser jTuNgay, JDateChooser jDenNgay, JTable tblDanhSachSPdaBan) {
    if (jTuNgay.getDate() != null && jDenNgay.getDate() != null) {
        java.util.Date startDate = jTuNgay.getDate();
        java.util.Date endDate = jDenNgay.getDate();

        // Validate that the start date is earlier than the end date
        if (startDate.after(endDate)) {
            JOptionPane.showMessageDialog(null, "Ngày bắt đầu phải nhỏ hơn ngày kết thúc", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
        java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

//        System.out.println("Ngày bắt đầu: " + sqlStartDate);
//        System.out.println("Ngày kết thúc: " + sqlEndDate);
        try {
            java.util.List<ProductsInfo> productList = LocKhoangDate(sqlStartDate, sqlEndDate);
            UpdateTable(tblDanhSachSPdaBan, productList);
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý lỗi khi lấy dữ liệu sản phẩm
        }
    }
}

    public Integer TongSoLuong() throws SQLException {
        Integer tongSoLuong = 0;
        Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
        String sql = "SELECT SUM(quantity) AS TongSoLuong FROM ProductDetails";
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        if (rs.next()) {
            tongSoLuong = rs.getInt("tongSoLuong");
        }
        return tongSoLuong;
    }

    public Integer TongSoLuongDaBan() throws SQLException {
    Integer tongSoLuongDaBan = 0;
    Connection c = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
    String sql = "SELECT SUM(od.quantity) AS tongSoLuongDaBan\n" +
                 "FROM\n" +
                 "    Orders o\n" +
                 "JOIN\n" +
                 "    OrderDetails od ON o.order_id = od.order_id\n" +
                 "WHERE\n" +
                 "    o.status = N'đã thanh toán'";
                 
    Statement statement = c.createStatement();
    ResultSet rs = statement.executeQuery(sql);

    if (rs.next()) {
        tongSoLuongDaBan = rs.getInt("tongSoLuongDaBan");
    }
    return tongSoLuongDaBan;
}
     

    public void LoadTongDoanhSo(JLabel lblDoanhThuTong) {
        try {
            BigDecimal totalInteger = TongDoanhSo();
            if (totalInteger != null) {
                lblDoanhThuTong.setText(totalInteger.toString() + " VND");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTongSoLuong(JLabel lblTongSP) {
        try {
            int tongSoLuong = TongSoLuong();
            lblTongSP.setText(tongSoLuong + " Sản phẩm");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi tải tổng số lượng.");
        }
    }

    public void loadTongSoLuongDaBan(JLabel lblSPDaBan1) {
        try {
            int tongSoLuong = TongSoLuongDaBan();
            lblSPDaBan1.setText(tongSoLuong + " Sản phẩm");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi tải tổng số lượng.");
        }
    }

}

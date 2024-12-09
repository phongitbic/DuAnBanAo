package Service;

import Model.Order;
import Model.OrderProductDetails;
import Model.ProductDetails;
import dao.DatabaseConnectionManager;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuanLyHoaDon {

    private int currentOrderPage = 1;
    private int itemsPerOrderPage = 14;
    private int totalOrderItems;
    private int totalOrderPages;

    private DatabaseConnectionManager dbManager;

    public QuanLyHoaDon() {
        dbManager = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.order_code AS 'Mã HĐ', u.users_code AS 'Mã NV', u.hoTen AS 'Tên NV', "
                + "c.customer_code AS 'Mã KH', c.customer_name AS 'Tên KH', c.phone AS 'Số điện thoại', "
                + "o.order_date AS 'Ngày tạo', v.voucher_code AS 'Mã giảm giá', "
                + "o.VAT AS 'VAT', o.payment_method AS 'Phương thức thanh toán', "
                + "o.total_price AS 'Tổng giá', o.status AS 'Trạng thái' "
                + "FROM Orders o "
                + "JOIN Users u ON o.user_id = u.user_id "
                + "JOIN Customers c ON o.customer_id = c.customer_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id";
        try (Connection connection = dbManager.getConnection(); PreparedStatement pst = connection.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String orderCode = rs.getString("Mã HĐ");
                String userCode = rs.getString("Mã NV");
                String userName = rs.getString("Tên NV");
                String customerCode = rs.getString("Mã KH");
                String customerName = rs.getString("Tên KH");
                String phone = rs.getString("Số điện thoại"); // Lấy số điện thoại khách hàng
                String orderDate = rs.getString("Ngày tạo");
                String voucherCode = rs.getString("Mã giảm giá");
                double vat = rs.getDouble("VAT");
                String paymentMethod = rs.getString("Phương thức thanh toán");
                double totalPrice = rs.getDouble("Tổng giá");
                String status = rs.getString("Trạng thái");

                Order order = new Order(orderCode, userCode, userName, customerCode, customerName, phone, orderDate,
                        voucherCode, vat, paymentMethod, totalPrice, status);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public List<OrderProductDetails> getOrderDetails(int orderId) {
        List<OrderProductDetails> details = new ArrayList<>();
        String query = "SELECT pd.productDetails_code AS 'Mã SPCT', p.product_name AS 'Tên sản phẩm', "
                + "ct.category_name AS 'Loại sản phẩm', c.color_name AS 'Màu sắc', s.size_name AS 'Kích thước', "
                + "st.style_name AS 'Phong cách', od.quantity AS 'Số lượng', "
                + "od.unit_price AS 'Đơn giá' "
                + "FROM OrderDetails od "
                + "JOIN ProductDetails pd ON od.productDetails_id = pd.productDetails_id "
                + "JOIN Products p ON pd.product_id = p.product_id "
                + "JOIN Color c ON pd.color_id = c.color_id "
                + "JOIN Sizes s ON pd.size_id = s.size_id "
                + "JOIN Styles st ON pd.style_id = st.style_id "
                + "JOIN Categories ct ON p.category_id = ct.category_id "
                + "WHERE od.order_id = ?";
        try (Connection connection = dbManager.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderProductDetails detail = new OrderProductDetails();
                    detail.setProductDetailCode(rs.getString("Mã SPCT"));
                    detail.setProductName(rs.getString("Tên sản phẩm"));
                    detail.setProductType(rs.getString("Loại sản phẩm"));
                    detail.setColor(rs.getString("Màu sắc"));
                    detail.setSize(rs.getString("Kích thước"));
                    detail.setStyle(rs.getString("Phong cách"));
                    detail.setQuantity(rs.getInt("Số lượng"));
                    detail.setUnitPrice(rs.getDouble("Đơn giá"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public List<String> getAllOrderStatuses() {
        List<String> statuses = new ArrayList<>();
        String query = "SELECT DISTINCT status FROM Orders";
        try (Connection connection = dbManager.getConnection(); PreparedStatement pst = connection.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                statuses.add(rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statuses;
    }

    public DefaultTableModel loadOrderDataByStatus(String status) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
            "STT", "Mã HĐ", "Mã NV", "Tên NV", "Mã KH", "Tên KH", "SĐT KH", "Ngày tạo", "Mã giảm giá", "VAT",
            "Phương thức thanh toán", "Tổng giá", "Trạng thái"
        });

        String query = "SELECT o.order_code AS 'Mã HĐ', u.users_code AS 'Mã NV', u.hoTen AS 'Tên NV', "
                + "c.customer_code AS 'Mã KH', c.customer_name AS 'Tên KH', c.phone AS 'SĐT KH', "
                + "o.order_date AS 'Ngày tạo', v.voucher_code AS 'Mã giảm giá', o.VAT AS 'VAT', "
                + "o.payment_method AS 'Phương thức thanh toán', o.total_price AS 'Tổng giá', o.status AS 'Trạng thái' "
                + "FROM Orders o "
                + "JOIN Users u ON o.user_id = u.user_id "
                + "JOIN Customers c ON o.customer_id = c.customer_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id "
                + "WHERE o.status LIKE ?";
        try (Connection connection = dbManager.getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
            if (status == null || status.isEmpty()) {
                pst.setString(1, "%%"); // Match all statuses if empty
            } else {
                pst.setString(1, "%" + status + "%");
            }
            try (ResultSet rs = pst.executeQuery()) {
                int stt = 1;
                while (rs.next()) {
                    String ordCode = rs.getString("Mã HĐ");
                    String userCode = rs.getString("Mã NV");
                    String userName = rs.getString("Tên NV");
                    String customerCode = rs.getString("Mã KH");
                    String customerName = rs.getString("Tên KH");
                    String customerPhone = rs.getString("SĐT KH");
                    String orderDate = rs.getString("Ngày tạo");
                    String voucherCode = rs.getString("Mã giảm giá");
                    double vat = rs.getDouble("VAT");
                    String paymentMethod = rs.getString("Phương thức thanh toán");
                    double totalPrice = rs.getDouble("Tổng giá");
                    String orderStatus = rs.getString("Trạng thái");

                    model.addRow(new Object[]{
                        stt,
                        ordCode,
                        userCode,
                        userName,
                        customerCode,
                        customerName,
                        customerPhone,
                        orderDate,
                        voucherCode,
                        vat,
                        paymentMethod,
                        totalPrice,
                        orderStatus
                    });
                    stt++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel loadOrderDataByOrderCode(String orderCode) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
            "STT", "Mã HĐ", "Mã NV", "Tên NV", "Mã KH", "Tên KH", "SĐT KH", "Ngày tạo", "Mã giảm giá", "VAT",
            "Phương thức thanh toán", "Tổng giá", "Trạng thái"
        });

        String query = "SELECT o.order_code AS 'Mã HĐ', u.users_code AS 'Mã NV', u.hoTen AS 'Tên NV', "
                + "c.customer_code AS 'Mã KH', c.customer_name AS 'Tên KH', c.phone AS 'SĐT KH', "
                + "o.order_date AS 'Ngày tạo', v.voucher_code AS 'Mã giảm giá', o.VAT AS 'VAT', "
                + "o.payment_method AS 'Phương thức thanh toán', o.total_price AS 'Tổng giá', o.status AS 'Trạng thái' "
                + "FROM Orders o "
                + "JOIN Users u ON o.user_id = u.user_id "
                + "JOIN Customers c ON o.customer_id = c.customer_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id "
                + "WHERE o.order_code LIKE ?";
        try (Connection connection = dbManager.getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, "%" + orderCode + "%");
            try (ResultSet rs = pst.executeQuery()) {
                int stt = 1;
                while (rs.next()) {
                    String ordCode = rs.getString("Mã HĐ");
                    String userCode = rs.getString("Mã NV");
                    String userName = rs.getString("Tên NV");
                    String customerCode = rs.getString("Mã KH");
                    String customerName = rs.getString("Tên KH");
                    String customerPhone = rs.getString("SĐT KH");
                    String orderDate = rs.getString("Ngày tạo");
                    String voucherCode = rs.getString("Mã giảm giá");
                    double vat = rs.getDouble("VAT");
                    String paymentMethod = rs.getString("Phương thức thanh toán");
                    double totalPrice = rs.getDouble("Tổng giá");
                    String orderStatus = rs.getString("Trạng thái");

                    model.addRow(new Object[]{
                        stt,
                        ordCode,
                        userCode,
                        userName,
                        customerCode,
                        customerName,
                        customerPhone,
                        orderDate,
                        voucherCode,
                        vat,
                        paymentMethod,
                        totalPrice,
                        orderStatus
                    });
                    stt++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel loadOrderDataByDateRange(java.util.Date startDate, java.util.Date endDate) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
            "STT", "Mã HĐ", "Mã NV", "Tên NV", "Mã KH", "Tên KH", "SĐT KH", "Ngày tạo", "Mã giảm giá", "VAT",
            "Phương thức thanh toán", "Tổng giá", "Trạng thái"
        });

        String query = "SELECT o.order_code AS 'Mã HĐ', u.users_code AS 'Mã NV', u.hoTen AS 'Tên NV', "
                + "c.customer_code AS 'Mã KH', c.customer_name AS 'Tên KH', c.phone AS 'SĐT KH', "
                + "o.order_date AS 'Ngày tạo', v.voucher_code AS 'Mã giảm giá', o.VAT AS 'VAT', "
                + "o.payment_method AS 'Phương thức thanh toán', o.total_price AS 'Tổng giá', o.status AS 'Trạng thái' "
                + "FROM Orders o "
                + "JOIN Users u ON o.user_id = u.user_id "
                + "JOIN Customers c ON o.customer_id = c.customer_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id "
                + "WHERE o.order_date BETWEEN ? AND ?";
        try (Connection connection = dbManager.getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setDate(1, new java.sql.Date(startDate.getTime()));
            pst.setDate(2, new java.sql.Date(endDate.getTime()));
            try (ResultSet rs = pst.executeQuery()) {
                int stt = 1;
                while (rs.next()) {
                    String ordCode = rs.getString("Mã HĐ");
                    String userCode = rs.getString("Mã NV");
                    String userName = rs.getString("Tên NV");
                    String customerCode = rs.getString("Mã KH");
                    String customerName = rs.getString("Tên KH");
                    String customerPhone = rs.getString("SĐT KH");
                    String orderDate = rs.getString("Ngày tạo");
                    String voucherCode = rs.getString("Mã giảm giá");
                    double vat = rs.getDouble("VAT");
                    String paymentMethod = rs.getString("Phương thức thanh toán");
                    double totalPrice = rs.getDouble("Tổng giá");
                    String orderStatus = rs.getString("Trạng thái");

                    model.addRow(new Object[]{
                        stt,
                        ordCode,
                        userCode,
                        userName,
                        customerCode,
                        customerName,
                        customerPhone,
                        orderDate,
                        voucherCode,
                        vat,
                        paymentMethod,
                        totalPrice,
                        orderStatus
                    });
                    stt++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel loadOrderDataBySearch(String searchText) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
            "STT", "Mã HĐ", "Mã NV", "Tên NV", "Mã KH", "Tên KH", "SĐT KH", "Ngày tạo", "Mã giảm giá", "VAT",
            "Phương thức thanh toán", "Tổng giá", "Trạng thái"
        });

        String query = "SELECT o.order_code AS 'Mã HĐ', u.users_code AS 'Mã NV', u.hoTen AS 'Tên NV', "
                + "c.customer_code AS 'Mã KH', c.customer_name AS 'Tên KH', c.phone AS 'SĐT KH', "
                + // Sử dụng SĐT KH
                "o.order_date AS 'Ngày tạo', v.voucher_code AS 'Mã giảm giá', o.VAT AS 'VAT', "
                + "o.payment_method AS 'Phương thức thanh toán', o.total_price AS 'Tổng giá', o.status AS 'Trạng thái' "
                + "FROM Orders o "
                + "JOIN Users u ON o.user_id = u.user_id "
                + "JOIN Customers c ON o.customer_id = c.customer_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id "
                + "WHERE o.order_code LIKE ? OR c.customer_name LIKE ? OR c.phone LIKE ?";
        try (Connection connection = dbManager.getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchText + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            pst.setString(3, searchPattern);
            try (ResultSet rs = pst.executeQuery()) {
                int stt = 1;
                while (rs.next()) {
                    String ordCode = rs.getString("Mã HĐ");
                    String userCode = rs.getString("Mã NV");
                    String userName = rs.getString("Tên NV");
                    String customerCode = rs.getString("Mã KH");
                    String customerName = rs.getString("Tên KH");
                    String customerPhone = rs.getString("SĐT KH"); // Số điện thoại khách hàng
                    String orderDate = rs.getString("Ngày tạo");
                    String voucherCode = rs.getString("Mã giảm giá");
                    double vat = rs.getDouble("VAT");
                    String paymentMethod = rs.getString("Phương thức thanh toán");
                    double totalPrice = rs.getDouble("Tổng giá");
                    String orderStatus = rs.getString("Trạng thái");

                    model.addRow(new Object[]{
                        stt,
                        ordCode,
                        userCode,
                        userName,
                        customerCode,
                        customerName,
                        customerPhone,
                        orderDate,
                        voucherCode,
                        vat,
                        paymentMethod,
                        totalPrice,
                        orderStatus
                    });
                    stt++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }
    
    
    
    private void calculatePagination() {
    totalOrderItems = getAllOrders().size(); // Lấy tổng số mục hóa đơn
    totalOrderPages = (int) Math.ceil((double) totalOrderItems / itemsPerOrderPage); // Tính số trang
}

    
    public DefaultTableModel loadOrderData() {
    DefaultTableModel model = new DefaultTableModel();
    model.setColumnIdentifiers(new Object[]{
        "STT", "Mã HĐ", "Mã NV", "Tên NV", "Mã KH", "Tên KH", "SĐT KH", "Ngày tạo", "Mã giảm giá", "VAT",
        "Phương thức thanh toán", "Tổng giá", "Trạng thái"
    });

    // Cập nhật phân trang
    calculatePagination();

    String query = "SELECT * FROM (SELECT o.order_code AS 'Mã HĐ', u.users_code AS 'Mã NV', u.hoTen AS 'Tên NV', " +
                   "c.customer_code AS 'Mã KH', c.customer_name AS 'Tên KH', c.phone AS 'SĐT KH', " +
                   "o.order_date AS 'Ngày tạo', v.voucher_code AS 'Mã giảm giá', o.VAT AS 'VAT', " +
                   "o.payment_method AS 'Phương thức thanh toán', o.total_price AS 'Tổng giá', o.status AS 'Trạng thái', " +
                   "ROW_NUMBER() OVER (ORDER BY o.order_code ASC) AS row_num " + // Sắp xếp theo order_code ASC
                   "FROM Orders o " +
                   "JOIN Users u ON o.user_id = u.user_id " +
                   "JOIN Customers c ON o.customer_id = c.customer_id " +
                   "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id) AS orders " +
                   "WHERE row_num BETWEEN ? AND ?";
    try (Connection connection = dbManager.getConnection();
         PreparedStatement pst = connection.prepareStatement(query)) {
        int startRow = (currentOrderPage - 1) * itemsPerOrderPage + 1; // Tính toán chỉ số bắt đầu
        int endRow = startRow + itemsPerOrderPage - 1; // Tính toán chỉ số kết thúc

        pst.setInt(1, startRow);
        pst.setInt(2, endRow);

        try (ResultSet rs = pst.executeQuery()) {
            int stt = startRow;
            while (rs.next()) {
                model.addRow(new Object[]{
                    stt,
                    rs.getString("Mã HĐ"),
                    rs.getString("Mã NV"),
                    rs.getString("Tên NV"),
                    rs.getString("Mã KH"),
                    rs.getString("Tên KH"),
                    rs.getString("SĐT KH"),
                    rs.getString("Ngày tạo"),
                    rs.getString("Mã giảm giá"),
                    rs.getDouble("VAT"),
                    rs.getString("Phương thức thanh toán"),
                    rs.getDouble("Tổng giá"),
                    rs.getString("Trạng thái")
                });
                stt++;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return model;
}
    
    
    public void nextPage() {
    if (currentOrderPage < totalOrderPages) {
        currentOrderPage++;
    }
}

public void previousPage() {
    if (currentOrderPage > 1) {
        currentOrderPage--;
    }
}

public void goToPage(int page) {
    if (page > 0 && page <= totalOrderPages) {
        currentOrderPage = page;
    }
}

public int getCurrentOrderPage() {
    return currentOrderPage;
}

public int getTotalOrderPages() {
    return totalOrderPages;
}


public void setCurrentOrderPage(int page) {
    if (page >= 1 && page <= totalOrderPages) {
        currentOrderPage = page;
    }
}


public ArrayList<Order> getAllOrderss() {
        ArrayList<Order> orders = new ArrayList<>();
        String query = "SELECT o.order_code AS 'Mã HĐ', u.users_code AS 'Mã NV', u.hoTen AS 'Tên NV', "
                + "c.customer_code AS 'Mã KH', c.customer_name AS 'Tên KH', c.phone AS 'Số điện thoại', "
                + "o.order_date AS 'Ngày tạo', v.voucher_code AS 'Mã giảm giá', "
                + "o.VAT AS 'VAT', o.payment_method AS 'Phương thức thanh toán', "
                + "o.total_price AS 'Tổng giá', o.status AS 'Trạng thái' "
                + "FROM Orders o "
                + "JOIN Users u ON o.user_id = u.user_id "
                + "JOIN Customers c ON o.customer_id = c.customer_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id";
        try (Connection connection = dbManager.getConnection(); PreparedStatement pst = connection.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String orderCode = rs.getString("Mã HĐ");
                String userCode = rs.getString("Mã NV");
                String userName = rs.getString("Tên NV");
                String customerCode = rs.getString("Mã KH");
                String customerName = rs.getString("Tên KH");
                String phone = rs.getString("Số điện thoại"); // Lấy số điện thoại khách hàng
                String orderDate = rs.getString("Ngày tạo");
                String voucherCode = rs.getString("Mã giảm giá");
                double vat = rs.getDouble("VAT");
                String paymentMethod = rs.getString("Phương thức thanh toán");
                double totalPrice = rs.getDouble("Tổng giá");
                String status = rs.getString("Trạng thái");

                Order order = new Order(orderCode, userCode, userName, customerCode, customerName, phone, orderDate,
                        voucherCode, vat, paymentMethod, totalPrice, status);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

   

}

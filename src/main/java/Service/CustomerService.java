package Service;

import dao.DatabaseConnectionManager;
import Model.Customers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HoaNguyenVan
 */
public class CustomerService {

    DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");

    public ArrayList<Customers> getAllCustomer() {
        ArrayList<Customers> listCustomer = new ArrayList<>();
        String sql = """
                      SELECT 
                      	customer_id, 
                      	customer_code, 
                      	customer_name, 
                      	phone, 
                      	point_gained, 
                      	points_used
                      FROM Customers
                      """;
        listCustomer.clear();
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql); ResultSet rs = pre.executeQuery()) {
            while (rs.next()) {
                Integer customer_id = rs.getInt(1);
                String customer_code = rs.getString(2);
                String customer_name = rs.getString(3);
                String phone = rs.getString(4);
                Integer point_gained = rs.getInt(5);
                Integer points_used = rs.getInt(6);

                Customers cus = new Customers(customer_id, customer_code, customer_name, phone, point_gained, points_used);
                listCustomer.add(cus);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return listCustomer;
    }

    public void addCus(Customers cus) {
        String sql = "INSERT INTO Customers (customer_name, phone, point_gained, points_used) VALUES (?, ?, 0, 0)";

        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, cus.getCustomer_name());
            pre.setString(2, cus.getPhone());
            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateCus(Customers cus) {
        String sql = "UPDATE Customers SET customer_name = ?, phone = ? WHERE customer_id = ?";

        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, cus.getCustomer_name());
            pre.setString(2, cus.getPhone());
            pre.setInt(3, cus.getCustomer_id());
            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadTableCus(JTable table, List<Customers> list) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        int stt = 1;
        if (!list.isEmpty()) {
            for (Customers cus : list) {
                dtm.addRow(new Object[]{
                    stt++,
                    cus.getCustomer_code(),
                    cus.getCustomer_name(),
                    cus.getPhone(),
                    cus.getPoint_gained(),
                    cus.getPoints_used(),
                    cus.getPoint_gained() - cus.getPoints_used()
                });
            }
        } else {
            dtm.addRow(new Object[]{"", "", "", "Danh sách trống", ""});
        }
    }

    public Boolean isDulicatePhoneKH(String phone) {
        for (Customers cus : getAllCustomer()) {
            if (cus.getPhone().equalsIgnoreCase(phone)) {
                return true;
            }
        }
        return false;
    }

    public Integer getCusIdByCode(String cusCode) {
        Integer cusID = null;
        for (Customers cus : getAllCustomer()) {
            if (cus.getCustomer_code().equalsIgnoreCase(cusCode)) {
                cusID = cus.getCustomer_id();
                break;
            }
        }
        return cusID;
    }

    public Customers getCustomerByCode(String cusCode) {
        for (Customers cus : getAllCustomer()) {
            if (cus.getCustomer_code().equalsIgnoreCase(cusCode)) {
                return cus;
            }
        }
        return null;
    }

    private String removeVietnameseAccents(String str) {
        str = str.replaceAll("[đĐ]", "d");
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    public ArrayList<Customers> getListSearch(String keyword, JTable table) {
        ArrayList<Customers> listSearch = new ArrayList<>();

        for (Customers cus : getAllCustomer()) {
            if (removeVietnameseAccents(cus.getPhone()).contains(removeVietnameseAccents(keyword))
                    || removeVietnameseAccents(cus.getCustomer_name()).toUpperCase().contains(removeVietnameseAccents(keyword).toUpperCase())) {
                listSearch.add(cus);
            }
        }

        // Cập nhật bảng sau khi tìm kiếm
        loadTableCuss(table, listSearch);

        return listSearch;
    }

    public void loadTableCuss(JTable table, ArrayList<Customers> list) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        int stt = 1;
        if (!list.isEmpty()) {
            for (Customers cus : list) {
                dtm.addRow(new Object[]{
                    stt++,
                    cus.getCustomer_code(),
                    cus.getCustomer_name(),
                    cus.getPhone(),
                    cus.getPoint_gained(),
                    cus.getPoints_used(),
                    cus.getPoint_gained() - cus.getPoints_used()
                });
            }
        } else {
            dtm.addRow(new Object[]{"", "", "", "Danh sách trống", ""});
        }
    }

    public Customers getCustomerIdByCode(String cusCode) {
        for (Customers cus : getAllCustomer()) {
            if (cus.getCustomer_code().equalsIgnoreCase(cusCode)) {
                return cus;
            }
        }
        return null;
    }

}

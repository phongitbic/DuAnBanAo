/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Model.Discount;
import Model.Voucher;
import View.view_TrangChu;
import com.toedter.calendar.JDateChooser;
import dao.DatabaseConnectionManager;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.w3c.dom.Text;

/**
 *
 * @author dohuu
 */
public class VoucherService {

    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Connection con = null;
    private String sql = "";
    ArrayList<Voucher> listDis = new ArrayList<>();

    public List<Voucher> searchVou(String keyword) {
        List<Voucher> listSearch = new ArrayList<>();
        keyword = removeVietnameseAccents(keyword).toUpperCase();

        for (Voucher proDe : getAll()) {
            boolean matches = false;

            // Kiểm tra từng thuộc tính của Discount
            if (removeVietnameseAccents(proDe.getDescription()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (removeVietnameseAccents(proDe.getStartDate()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (removeVietnameseAccents(proDe.getEndDate()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (String.valueOf(proDe.getDiscountValue()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (removeVietnameseAccents(proDe.getStatus()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (String.valueOf(proDe.getMaxMonye()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (String.valueOf(proDe.getMinMonye()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (String.valueOf(proDe.getQuantity()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (String.valueOf(proDe.getVoucherId()).toUpperCase().contains(keyword)) {
                matches = true;
            }

            if (matches) {
                listSearch.add(proDe);
            }
        }

        return listSearch;
    }

    private String removeVietnameseAccents(String str) {
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    public int checkThaoTacVoucher(JTable tblCoupon, JTextField txtMaGiamGia, JTextArea txtMoTa, JTextField txtCode, JTextField txtPhanTramChietKhau, JDateChooser jdc_ngay_bat_dau_voucher, JDateChooser jdc_ngay_ket_thuc_voucher, JTextField txt_tien_toi_thieu, JTextField txt_tien_toi_da, JRadioButton rdoDangKM, JRadioButton rdoDungKM, JTextField txt_so_luong) {
        int rowIndex = tblCoupon.getSelectedRow();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Lấy dữ liệu từ các thành phần giao diện người dùng
        String voucherId = txtMaGiamGia.getText().trim();
        String description = txtMoTa.getText().trim();
        String code = txtCode.getText().trim();
        String discountValue = txtPhanTramChietKhau.getText().trim();
        java.util.Date startDate = jdc_ngay_bat_dau_voucher.getDate();
        java.util.Date endDate = jdc_ngay_ket_thuc_voucher.getDate();
        String minAmount = txt_tien_toi_thieu.getText().trim();
        String maxAmount = txt_tien_toi_da.getText().trim();
        String quantity = txt_so_luong.getText().trim();
        String status = "";
        if (rdoDangKM.isSelected()) {
            status = "Hoạt động";
        } else if (rdoDungKM.isSelected()) {
            status = "Dừng hoạt động";
        }
        String ngayStart = dateFormat.format(startDate); // ngày bắt đầu
        String ngayEnd = dateFormat.format(endDate); // ngày kết thúc

        // Lấy dữ liệu từ bảng
        String tableVoucherId = tblCoupon.getValueAt(rowIndex, 0).toString().trim();
        String tableVoucherCode = tblCoupon.getValueAt(rowIndex, 1).toString().trim();
        String tableDescription = tblCoupon.getValueAt(rowIndex, 2).toString().trim();
        String tableDiscountValue = tblCoupon.getValueAt(rowIndex, 3).toString().trim();
        String tableStartDate = tblCoupon.getValueAt(rowIndex, 4).toString().trim();
        String tableEndDate = tblCoupon.getValueAt(rowIndex, 5).toString().trim();
        String tableStatus = tblCoupon.getValueAt(rowIndex, 6).toString().trim();
        String tableMinAmount = tblCoupon.getValueAt(rowIndex, 7).toString().trim();
        String tableMaxAmount = tblCoupon.getValueAt(rowIndex, 8).toString().trim();
        String tableQuantity = tblCoupon.getValueAt(rowIndex, 9).toString().trim();

        // Kiểm tra nếu không có thay đổi nào
        if (voucherId.equalsIgnoreCase(tableVoucherId)
                && description.equalsIgnoreCase(tableDescription)
                && code.equalsIgnoreCase(tableVoucherCode)
                && discountValue.equalsIgnoreCase(tableDiscountValue)
                && ngayStart.equalsIgnoreCase(tableStartDate)
                && ngayEnd.equalsIgnoreCase(tableEndDate)
                && minAmount.equalsIgnoreCase(tableMinAmount)
                && maxAmount.equalsIgnoreCase(tableMaxAmount)
                && quantity.equalsIgnoreCase(tableQuantity)
                && status.equalsIgnoreCase(tableStatus)) {
            JOptionPane.showMessageDialog(null, "Bạn hãy thao tác để lưu.");
            return 1;
        }
        return 0;
    }

    public void showVoucher(JTable tblCoupon, JTextField txtMaGiamGia, JTextArea txtMoTa, JTextField txtCode, JTextField txtGiaChietKhau, JDateChooser jdc_ngay_bat_dau_voucher, JDateChooser jdc_ngay_ket_thuc_voucher, JTextField txt_tien_toi_thieu, JTextField txt_tien_toi_da, JRadioButton rdoDangKM, JRadioButton rdoDungKM, JTextField txt_so_luong) {
        int rowIndex = tblCoupon.getSelectedRow();
        String voucher_id = tblCoupon.getValueAt(rowIndex, 0).toString();
        String voucherCode = tblCoupon.getValueAt(rowIndex, 1).toString();
        String description = tblCoupon.getValueAt(rowIndex, 2).toString();
        double discountValue = Double.parseDouble(tblCoupon.getValueAt(rowIndex, 3).toString());
        String startDate = tblCoupon.getValueAt(rowIndex, 4).toString();
        String endDate = tblCoupon.getValueAt(rowIndex, 5).toString();
        String status = tblCoupon.getValueAt(rowIndex, 6).toString();
        String tienToiThieu = tblCoupon.getValueAt(rowIndex, 7).toString();
        String tienToiDa = tblCoupon.getValueAt(rowIndex, 8).toString();
        String soluong = tblCoupon.getValueAt(rowIndex, 9).toString();
        txtMaGiamGia.setText(voucher_id);
        txtMoTa.setText(description);
        txtCode.setText(voucherCode);
        txtGiaChietKhau.setText(String.valueOf(discountValue));

        try {

            String start = tblCoupon.getValueAt(rowIndex, 4).toString();
            String end = tblCoupon.getValueAt(rowIndex, 5).toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng của chuỗi ngày từ JTable
            java.util.Date dateS = dateFormat.parse(start);
            java.util.Date dateE = dateFormat.parse(end);
            // Đặt giá trị ngày vào JDateChooser
            jdc_ngay_bat_dau_voucher.setDate(dateS);
            jdc_ngay_ket_thuc_voucher.setDate(dateE);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("looi date S");
        }
        jdc_ngay_bat_dau_voucher.setDateFormatString("yyyy-MM-dd");
        jdc_ngay_bat_dau_voucher.setDateFormatString("yyyy-MM-dd");
//            txtNgayBatDau.setText(startDate);
//            txtNgayKetThuc.setText(endDate);
        txt_tien_toi_thieu.setText(tienToiThieu);
        txt_tien_toi_da.setText(tienToiDa);
        txt_so_luong.setText(soluong);
        if (status.equalsIgnoreCase("Hoạt động")) {
            rdoDangKM.setSelected(true);
        } else {
            rdoDungKM.setSelected(true);
        }
    }

    public Voucher readFromVou(JTextArea txtMoTa, JTextField txtPhanTramChietKhau, JDateChooser jdc_ngay_bat_dau_voucher, JDateChooser jdc_ngay_ket_thuc_voucher, JTextField txt_tien_ap_dung, JTextField txt_tien_toi_da, JTextField txt_so_luong_vou, JRadioButton rdoDangKM) {
        String mota = txtMoTa.getText().trim();
        String discountValue = txtPhanTramChietKhau.getText().trim();
//        String ngayStart = txtNgayBatDau.getText().trim();
//        String ngayEnd = txtNgayKetThuc.getText().trim();
        String trangThai = rdoDangKM.isSelected() ? "Hoạt động" : "Dừng hoạt động";
        String min = txt_tien_ap_dung.getText().trim();
        String max = txt_tien_toi_da.getText().trim();
        String sl = txt_so_luong_vou.getText().trim();
        int soLuong = 0;
        double discountValueDouble = 0;
        double minMon = 0;
        double maxMon = 0;
        java.util.Date ds = null;
        java.util.Date de = null;
        Component view_TrangChu = null;
        if (discountValue.isEmpty()) {

            JOptionPane.showMessageDialog(view_TrangChu, "Giá trị chiết khấu trống");
            txtPhanTramChietKhau.requestFocus();
            txtPhanTramChietKhau.setBackground(Color.YELLOW);
            return null;
        }
        try {
            discountValueDouble = Double.parseDouble(discountValue);
            if (discountValueDouble < 0) {
                JOptionPane.showMessageDialog(view_TrangChu, "Phần trăm giảm phải lớn hơn 0%");
                txtPhanTramChietKhau.requestFocus();
                txtPhanTramChietKhau.setBackground(Color.YELLOW);
                return null;
            }
            if (discountValueDouble >= 100) {
                JOptionPane.showMessageDialog(view_TrangChu, "Không thể giảm quá 100% giá trị sản phẩm");
                txtPhanTramChietKhau.requestFocus();
                txtPhanTramChietKhau.setBackground(Color.YELLOW);
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view_TrangChu, "Giá trị chiết khấu phải là số");
            txtPhanTramChietKhau.requestFocus();
            txtPhanTramChietKhau.setBackground(Color.YELLOW);
            return null;
        }
//        if (mota.isEmpty()) {
//            JOptionPane.showMessageDialog(view_TrangChu, "Mô tả trống");
//            txtMoTa.requestFocus();
//            txtMoTa.setBackground(Color.YELLOW);
//            return null;
//        }

        ds = jdc_ngay_bat_dau_voucher.getDate();

        if (ds == null) {
            jdc_ngay_bat_dau_voucher.setBackground(Color.yellow);
            jdc_ngay_bat_dau_voucher.requestFocus();
            JOptionPane.showMessageDialog(view_TrangChu, "Ngày bắt đầu không hợp lệ (định dạng yyyy-MM-dd)");
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        de = jdc_ngay_ket_thuc_voucher.getDate();

        if (de == null) {
            jdc_ngay_ket_thuc_voucher.setBackground(Color.yellow);
            jdc_ngay_ket_thuc_voucher.requestFocus();
            JOptionPane.showMessageDialog(view_TrangChu, "Ngày kết thúc không hợp lệ (định dạng yyyy-MM-dd)");
            return null;
        }
        String ngayStart = dateFormat.format(ds);
        String ngayEnd = dateFormat1.format(de);
        LocalDate localDate = LocalDate.now();
        String today = localDate.toString();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date startDate = sdf.parse(ngayStart);
            java.util.Date endDate = sdf.parse(ngayEnd);
            java.util.Date todayDate = sdf.parse(today);
            if (todayDate.after(startDate)) {
                JOptionPane.showMessageDialog(null, "Ngày bắt đầu không được sau ngày hôm nay!");
                // jdc_ngay_bat_dau.requestFocus(); // Uncomment if you have the jdc_ngay_bat_dau component
                return null;
            }
            if (startDate.after(endDate)) {
                JOptionPane.showMessageDialog(view_TrangChu, "Ngày kết thúc  không được sau ngày bắt đầu!");
                jdc_ngay_ket_thuc_voucher.setBackground(Color.yellow);
                jdc_ngay_ket_thuc_voucher.requestFocus();

                return null;
            }
        } catch (ParseException e) {
            // Handle parsing exception (e.g., invalid date format)
            return null;
        }

        try {
            minMon = Double.parseDouble(min);
            if (minMon < 0) {
                JOptionPane.showMessageDialog(view_TrangChu, "Tiền  áp dụng không được nhỏ hơn 0");
                txt_tien_ap_dung.requestFocus();

                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view_TrangChu, "Tiền áp dụng phải là số");
            txt_tien_ap_dung.requestFocus();

            return null;
        }
//        try {
//            maxMon = Double.parseDouble(max);
//            minMon = Double.parseDouble(min);
//            if (maxMon > minMon) {
//                JOptionPane.showMessageDialog(view_TrangChu, "Tiền giảm tối đa không được lớn hơn tiền áp dụng");
//                txt_tien_toi_da.requestFocus();
//                return null;
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(view_TrangChu, "Tiền giảm tối đa phải là số");
//            txt_tien_toi_da.requestFocus();
//            return null;
//        }
        try {
            maxMon = Double.parseDouble(max);
            if (maxMon < 0) {
                JOptionPane.showMessageDialog(view_TrangChu, "Tiền giảm tối đa không được nhỏ hơn 0");
                txt_tien_toi_da.requestFocus();

                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view_TrangChu, "Tiền giảm tối đa phải là số");
            txt_tien_toi_da.requestFocus();
            return null;
        }
        try {
            soLuong = Integer.parseInt(sl);
            if (soLuong < 0) {
                JOptionPane.showMessageDialog(view_TrangChu, "Số lượng không được nhỏ hơn 0");
                txt_so_luong_vou.requestFocus();
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view_TrangChu, "Số lượng phải là số nguyên");
            txt_so_luong_vou.requestFocus();
            return null;
        }
        //thêm conster
        return new Voucher(mota, discountValueDouble, ngayStart, ngayEnd, trangThai, minMon, maxMon, soLuong);
    }

    public static boolean isValidDate(String dateStr) {
        //copy check nhập ngày đúng kiểu dữ liệu
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;

    }

    public ArrayList<Voucher> getAll() {
        try {

            String sql = "SELECT voucher_id, voucher_code, description, discount_value, start_date, end_date, status,money_applies,maximum_reduce,quantity_voucher FROM dbo.Voucher";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                int magiamgia = rs.getInt(1);
                String magiamgia_code = rs.getString(2);
                String mota = rs.getString(3);
                double giaTriChietKhau = rs.getDouble(4);
                String start = rs.getString(5);
                String end = rs.getString(6);
                String trangThai = rs.getString(7);
                double minMo = rs.getDouble(8);
                double maxMo = rs.getDouble(9);
                int soluong = rs.getInt(10);
                Voucher dis = new Voucher(magiamgia, magiamgia_code, mota, giaTriChietKhau, start, end, trangThai, minMo, maxMo, soluong);
                listDis.add(dis);
            }
            return listDis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void themVoucher(Voucher voucher) {
        String sql = "INSERT INTO Voucher (description, discount_value, start_date, end_date, status,money_applies,maximum_reduce,quantity_voucher) VALUES (?, ?, ?, ?, ?,? ,?,?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, voucher.getDescription());
            ps.setDouble(2, voucher.getDiscountValue());
            ps.setString(3, voucher.getStartDate());
            ps.setString(4, voucher.getEndDate());
            ps.setString(5, voucher.getStatus());
            ps.setDouble(6, voucher.getMinMonye());
            ps.setDouble(7, voucher.getMaxMonye());
            ps.setInt(8, voucher.getQuantity());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void capNhatVoucher(Voucher voucher, int ma) {
        String sql = "UPDATE Voucher SET description = ?, discount_value = ?, start_date = ?,"
                + " end_date = ?, status = ? ,money_applies = ?,maximum_reduce = ? , quantity_voucher = ? WHERE voucher_id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, voucher.getDescription());
            ps.setDouble(2, voucher.getDiscountValue());
            ps.setString(3, voucher.getStartDate());
            ps.setString(4, voucher.getEndDate());
            ps.setString(5, voucher.getStatus());
            ps.setDouble(6, voucher.getMinMonye());
            ps.setDouble(7, voucher.getMaxMonye());
            ps.setInt(8, voucher.getQuantity());
            ps.setInt(9, ma);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Voucher> tim(String vou) {
        try {

            String sql = "SELECT voucher_id, voucher_code, description, discount_value, start_date, end_date, status,money_applies,maximum_reduce FROM dbo.Voucher where voucher_id = ?";
            ps = con.prepareStatement(sql);
            ps.setObject(1, vou);
            rs = ps.executeQuery();
            while (rs.next()) {
                int magiamgia = rs.getInt(1);
                String magiamgia_code = rs.getString(2);
                String mota = rs.getString(3);
                double giaTriChietKhau = rs.getDouble(4);
                String start = rs.getString(5);
                String end = rs.getString(6);
                String trangThai = rs.getString(7);
                double minMo = rs.getDouble(8);
                double maxMo = rs.getDouble(9);
                int soluong = rs.getInt(10);
                Voucher dis = new Voucher(magiamgia, magiamgia_code, mota, giaTriChietKhau, start, end, trangThai, minMo, maxMo, soluong);
                listDis.add(dis);
            }
            return listDis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void voHieuHoaVoucher(int voucherId) {
        String sql = "UPDATE Voucher SET status = N'Dừng hoạt động' WHERE voucher_id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, voucherId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public void loadTableVoucher(JTable name) {
//        DefaultTableModel dtm = (DefaultTableModel) name.getModel();
//        dtm.setRowCount(0);
//        for (Voucher x : getAll()) {
//            dtm.addRow(new Object[]{
//                x.getVoucherId(),
//                x.getVoucherCode(),
//                x.getDescription(),
//                x.getDiscountValue(),
//                x.getStartDate(),
//                x.getEndDate(),
//                x.getStatus(),
//                x.getMinMonye(),
//                x.getMaxMonye(),
//                x.getQuantity()
//            });
//        }
//    }

    public void loadTableVoucherTim(JTable name, String key) {
        DefaultTableModel dtm = (DefaultTableModel) name.getModel();
        dtm.setRowCount(0);
        for (Voucher x : searchVou(key)) {
            dtm.addRow(new Object[]{
                x.getVoucherId(),
                x.getVoucherCode(),
                x.getDescription(),
                x.getDiscountValue(),
                x.getStartDate(),
                x.getEndDate(),
                x.getStatus(),
                x.getMinMonye(),
                x.getMaxMonye(),
                x.getQuantity()
            });
        }
    }
//------------------------------------------------------
public int getTotalRecords() {
    try {
        String sql = "SELECT COUNT(*) FROM dbo.Voucher";
        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0;
}

   public ArrayList<Voucher> getAll(int page, int itemsPerPage) {
    ArrayList<Voucher> listDis = new ArrayList<>();
    int offset = (page - 1) * itemsPerPage;
    try {
        String sql = "SELECT voucher_id, voucher_code, description, discount_value, start_date, end_date, status, money_applies, maximum_reduce, quantity_voucher "
                   + "FROM dbo.Voucher ORDER BY voucher_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        ps = con.prepareStatement(sql);
        ps.setInt(1, offset);
        ps.setInt(2, itemsPerPage);
        rs = ps.executeQuery();
        while (rs.next()) {
            int magiamgia = rs.getInt(1);
            String magiamgia_code = rs.getString(2);
            String mota = rs.getString(3);
            double giaTriChietKhau = rs.getDouble(4);
            String start = rs.getString(5);
            String end = rs.getString(6);
            String trangThai = rs.getString(7);
            double minMo = rs.getDouble(8);
            double maxMo = rs.getDouble(9);
            int soluong = rs.getInt(10);
            Voucher dis = new Voucher(magiamgia, magiamgia_code, mota, giaTriChietKhau, start, end, trangThai, minMo, maxMo, soluong);
            listDis.add(dis);
        }
        return listDis;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
public void loadTableVoucher(JTable name, int page, int itemsPerPage) {
    DefaultTableModel dtm = (DefaultTableModel) name.getModel();
    dtm.setRowCount(0);
    for (Voucher x : getAll(page, itemsPerPage)) {
        dtm.addRow(new Object[]{
            x.getVoucherId(), x.getVoucherCode(), x.getDescription(),
            x.getDiscountValue(), x.getStartDate(), x.getEndDate(),
            x.getStatus(), x.getMinMonye(), x.getMaxMonye(), x.getQuantity()
        });
    }
}


    // Update page number display (optional)
    // ... (update a label or UI element to show current page)


//
//--------------------------------------------------------------
public VoucherService() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
        con = dcm.getConnection();
    }

}

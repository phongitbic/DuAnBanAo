/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Model.Color;
import Model.Discount;
import Model.OriginalPrice;
import Model.ProductDetails;
import Model.Size;
import Model.Style;
import static Service.VoucherService.isValidDate;
import com.toedter.calendar.JDateChooser;

import dao.DatabaseConnectionManager;
import java.awt.Component;
import java.awt.List;
import java.sql.*;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author dohuu
 */
public class DiscountService {

    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Connection con = null;
    private String sql = "";
    ArrayList<Discount> listDis = new ArrayList<>();

    public void showBang_chi_tiet_Sp(JTable tbl_san_pham_khuyen_mai, JTextField txt_code_chi_tiet, JTextField txtMau, JTextField txtKieuDang, JTextField txtKichThuoc) {
        int row = tbl_san_pham_khuyen_mai.getSelectedRow();
        if (row == -1) {
            return;
        }
        String code = tbl_san_pham_khuyen_mai.getValueAt(row, 0).toString();
        String Mau = tbl_san_pham_khuyen_mai.getValueAt(row, 2).toString();
        String KieuDang = tbl_san_pham_khuyen_mai.getValueAt(row, 3).toString();
        String KichThuoc = tbl_san_pham_khuyen_mai.getValueAt(row, 4).toString();
        txt_code_chi_tiet.setText(code);
        txtMau.setText(Mau);
        txtKieuDang.setText(KieuDang);
        txtKichThuoc.setText(KichThuoc);

    }

    public void showDiscount(JTextField txtMa_chiet_khau, JTable tblDiscount, JDateChooser jdc_ngay_bat_dau, JDateChooser jdc_ngay_ket_thuc, JTextField txt_so_tien_chiet_khau, JTextField txt_code_chi_tiet, JTextField txtMau, JTextField txtKieuDang, JTextField txtKichThuoc) {
        int row = tblDiscount.getSelectedRow();
        if (row == -1) {
            return;
        }
        txtMa_chiet_khau.setEnabled(false);
        jdc_ngay_bat_dau.setEnabled(false);
        jdc_ngay_ket_thuc.setEnabled(false);
        txt_so_tien_chiet_khau.setEnabled(false);
        txt_code_chi_tiet.setEnabled(false);

        txtMa_chiet_khau.setText(tblDiscount.getValueAt(row, 0).toString());
        String code = tblDiscount.getValueAt(row, 1).toString();
        txt_code_chi_tiet.setText(code);
        try {

            String start = tblDiscount.getValueAt(row, 2).toString();
            String end = tblDiscount.getValueAt(row, 3).toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng của chuỗi ngày từ JTable
            java.util.Date dateS = dateFormat.parse(start);
            java.util.Date dateE = dateFormat.parse(end);
            // Đặt giá trị ngày vào JDateChooser
            jdc_ngay_bat_dau.setDate(dateS);
            jdc_ngay_ket_thuc.setDate(dateE);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("looi date S");
        }
        jdc_ngay_bat_dau.setDateFormatString("yyyy-MM-dd");
        jdc_ngay_bat_dau.setDateFormatString("yyyy-MM-dd");
        txt_so_tien_chiet_khau.setText(tblDiscount.getValueAt(row, 4).toString());
        String Mau = tblDiscount.getValueAt(row, 5).toString();
        String KieuDang = tblDiscount.getValueAt(row, 6).toString();
        String KichThuoc = tblDiscount.getValueAt(row, 7).toString();
        txtMau.setText(Mau);
        txtKieuDang.setText(KieuDang);
        txtKichThuoc.setText(KichThuoc);

    }

    public int checkLuu(JTable tblDiscount, JDateChooser jdc_ngay_bat_dau, JDateChooser jdc_ngay_ket_thuc, JTextField txt_so_tien_chiet_khau) {
        int row = tblDiscount.getSelectedRow();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Lấy dữ liệu từ các thành phần giao diện người dùng
        java.util.Date ds = jdc_ngay_bat_dau.getDate();
        java.util.Date de = jdc_ngay_ket_thuc.getDate();
        String ngayStart = dateFormat.format(ds); // ngày bắt đầu
        String ngayEnd = dateFormat.format(de);   // ngày kết thúc
        String tienGiam = txt_so_tien_chiet_khau.getText().trim(); // số tiền chiết khấu

        // Lấy dữ liệu từ bảng
        String ngayBatDau = tblDiscount.getValueAt(row, 2).toString();
        String ngayKetThuc = tblDiscount.getValueAt(row, 3).toString();
        String tien = tblDiscount.getValueAt(row, 4).toString();

        // Kiểm tra nếu không có thay đổi nào
        if (ngayEnd.equalsIgnoreCase(ngayKetThuc)
                && ngayStart.equalsIgnoreCase(ngayBatDau)
                && tienGiam.equalsIgnoreCase(tien)) {
            JOptionPane.showMessageDialog(null, "Bạn hãy thao tác để lưu.");
            return 1;
        }
        return 0;
    }

    public Discount readFromDis(JTextField txtMa_chiet_khau, JDateChooser jdc_ngay_bat_dau, JDateChooser jdc_ngay_ket_thuc, JTextField txt_so_tien_chiet_khau, JTextField txt_code_chi_tiet) {
        String maChietKhau = txtMa_chiet_khau.getText().trim();
        String soTienChietKhau = txt_so_tien_chiet_khau.getText().trim();
        String code = txt_code_chi_tiet.getText().trim();
        java.util.Date ds = null;
        java.util.Date de = null;
        Component view_TrangChu = null;
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(view_TrangChu, "Bạn chưa chọn 1 sản phẩm nào hãy chọn một sp trong bảng");
            return null;
        }
        double soTienChietKhau1 = 0;
        if (maChietKhau.isEmpty()) {
            JOptionPane.showMessageDialog(view_TrangChu, "Mã chiết khấu trống");
            txtMa_chiet_khau.requestFocus();

            return null;
        }

        ds = jdc_ngay_bat_dau.getDate();

        if (ds == null) {

            jdc_ngay_bat_dau.requestFocus();
            JOptionPane.showMessageDialog(view_TrangChu, "Ngày bắt đầu không hợp lệ (định dạng yyyy-MM-dd)");
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String ngayStart = dateFormat.format(ds);

        de = jdc_ngay_ket_thuc.getDate();

        if (de == null) {

            jdc_ngay_ket_thuc.requestFocus();
            JOptionPane.showMessageDialog(view_TrangChu, "Ngày kết thúc không hợp lệ (định dạng yyyy-MM-dd)");
            return null;
        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
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
                JOptionPane.showMessageDialog(view_TrangChu, "Ngày kết thúc không được sau ngày bắt đầu!");
                jdc_ngay_ket_thuc.requestFocus();
                return null;
            }
        } catch (ParseException e) {
            // Handle parsing exception (e.g., invalid date format)
            return null;
        }

        try {
            soTienChietKhau1 = Double.parseDouble(soTienChietKhau);
            if (soTienChietKhau1 < 0) {
                JOptionPane.showMessageDialog(view_TrangChu, "Số tiền chiết khấu không được nhỏ hơn 0");
                txt_so_tien_chiet_khau.requestFocus();

                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view_TrangChu, "Số tiền chiết khấu phải là số");
            txt_so_tien_chiet_khau.requestFocus();
            return null;
        }

        return new Discount(maChietKhau, getProductDetailsId(code), ngayStart, ngayEnd, soTienChietKhau1);

    }

    public ArrayList<Discount> getAll() {
        try {

            String sql = "	SELECT \n"
                    + "    D.discount_id, \n"
                    + "	P.productDetails_code,\n"
                    + "    D.start_date, \n"
                    + "    D.end_date, \n"
                    + "    D.discount_amount, \n"
                    + "    CL.color_name,\n"
                    + "    SL.style_name,\n"
                    + "    SI.size_name\n"
                    + "FROM \n"
                    + "    Discount D\n"
                    + "JOIN \n"
                    + "    DetailDiscount DD ON D.discount_id = DD.discount_id\n"
                    + "JOIN \n"
                    + "    ProductDetails P ON DD.productDetails_id = P.productDetails_id\n"
                    + "JOIN \n"
                    + "    Color CL ON P.color_id = CL.color_id\n"
                    + "JOIN \n"
                    + "    Styles SL ON P.style_id = SL.style_id\n"
                    + "JOIN \n"
                    + "    Sizes SI ON P.size_id = SI.size_id;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String ma_discount = rs.getString(1);
                String code = rs.getString(2);
                String start_date_dis = rs.getString(3);
                String end_date_dis = rs.getString(4);
                double discountAmount = rs.getDouble(5);
                String colorName = rs.getString(6);
                String styleName = rs.getString(7);
                String sizeName = rs.getString(8);

                Discount dis = new Discount(ma_discount, start_date_dis, end_date_dis, discountAmount, code, styleName, sizeName, colorName);
                listDis.add(dis);
            }
            return listDis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getProductDetailsId(String productCode) {
        int productId = 0;
        String sql = "SELECT productDetails_id FROM ProductDetails WHERE productDetails_code = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, productCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    productId = rs.getInt("productDetails_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productId;
    }

    public void them(Discount dis, String code, JTextField txtMa_chiet_khau, JDateChooser jdc_ngay_bat_dau, JDateChooser jdc_ngay_ket_thuc, JTextField txt_so_tien_giam, JTable tblDiscount, JTable tbl_san_pham_khuyen_mai) throws SQLException {
        ProductDetails_Service dis_v = new ProductDetails_Service();
        // Lấy giá từ ProductDetails
        double price = dis_v.getPriceById(code);
//        System.out.println(price);
        // Kiểm tra nếu discount_amount nhỏ hơn price
        if (dis.getDiscountAmount() >= price) {
            JOptionPane.showMessageDialog(null, "Số tiền chiết khấu không được cao hơn hoặc bằng giá gốc,giá gốc đang là:" + price);

            return; // Thông báo lỗi nếu discount_amount >= price
        }
        String checkDetailsSql1 = "SELECT COUNT(*) FROM Discount WHERE discount_id = ?";
        try (PreparedStatement checkDetailsPs = con.prepareStatement(checkDetailsSql1)) {
            checkDetailsPs.setObject(1, dis.getDiscountId());
            System.out.println(dis.getDiscountId());
            ResultSet rs = checkDetailsPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Mã giảm giá đã có hãy chọn lại");
                return; // Thông báo lỗi nếu ReducedDetails và DetailedType trùng
            }
        }
        String checkDetailsSql = "SELECT COUNT(*) FROM Discount WHERE productDetails_id = ?";
        try (PreparedStatement checkDetailsPs = con.prepareStatement(checkDetailsSql)) {
            checkDetailsPs.setObject(1, getProductDetailsId(code));
            System.out.println(dis.getDiscountId());
            ResultSet rs = checkDetailsPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Mã sản phẩm chi tiết đã có hãy chọn lại");
                return; // Thông báo lỗi nếu ReducedDetails và DetailedType trùng
            }
        }
        int conf = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn thêm");
        if (conf != 0) {
            return;
        }
        try (PreparedStatement psInsertDiscount = con.prepareStatement(
                "INSERT INTO Discount(discount_id, productDetails_id, start_date, end_date, discount_amount) VALUES (?, ?, ?, ?, ?)"); PreparedStatement psInsertDetailDiscount = con.prepareStatement(
                        "INSERT INTO DetailDiscount(productDetails_id, discount_id) VALUES (?, ?)"); PreparedStatement psExecProc = con.prepareStatement("exec ApplyDiscount @discount_id = ?")) {

            // Insert into Discount table
            psInsertDiscount.setObject(1, dis.getDiscountId());
            psInsertDiscount.setObject(2, dis.getProductDetailsId());
            psInsertDiscount.setObject(3, dis.getStartDate());
            psInsertDiscount.setObject(4, dis.getEndDate());
            psInsertDiscount.setObject(5, dis.getDiscountAmount());
            psInsertDiscount.executeUpdate();

            // Insert into DetailDiscount table
            psInsertDetailDiscount.setObject(1, getProductDetailsId(code));
            psInsertDetailDiscount.setObject(2, dis.getDiscountId());
            psInsertDetailDiscount.executeUpdate();

            // Execute stored procedure
            psExecProc.setObject(1, dis.getDiscountId());
            psExecProc.execute();
            JOptionPane.showMessageDialog(null, "Thêm thành công");
        } catch (SQLException e) {
            e.printStackTrace();

        }
        txtMa_chiet_khau.setEnabled(false);
        jdc_ngay_bat_dau.setEnabled(false);
        jdc_ngay_ket_thuc.setEnabled(false);
        txt_so_tien_giam.setEnabled(false);
        txtMa_chiet_khau.setBackground(java.awt.Color.WHITE);
        jdc_ngay_bat_dau.setBackground(java.awt.Color.WHITE);
        jdc_ngay_ket_thuc.setBackground(java.awt.Color.WHITE);
        jdc_ngay_ket_thuc.setBackground(java.awt.Color.WHITE);
        loadTableDisscount(tblDiscount);
        ProductDetails_Service pro = new ProductDetails_Service();
        pro.loadTableProductDetails(tbl_san_pham_khuyen_mai);
    }

    public void capNhat(Discount dis, double index, String code, JTextField txtMa_chiet_khau, JDateChooser jdc_ngay_bat_dau, JDateChooser jdc_ngay_ket_thuc, JTextField txt_so_tien_giam, JTable tblDiscount, JTable tbl_san_pham_khuyen_mai) throws SQLException {
        // Câu lệnh SQL để cập nhật bảng Discount và DetailDiscount

        ProductDetails_Service dis_v = new ProductDetails_Service();
        OriginalPriceService ops = new OriginalPriceService();
        double price1 = ops.getOriginalPriceById(getProductDetailsId(code));
        if (ops.tim(getProductDetailsId(code)) != null) {

            if (index >= price1) {
                JOptionPane.showMessageDialog(null, "Số tiền chiết khấu không được cao hơn hoặc bằng giá gốc, giá gốc đang là:" + price1);
                return;
            }
            int conf = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn sửa");
            if (conf != 0) {
                return;
            }
            sql = "UPDATE Discount SET  end_date = GETDATE() -1 where productDetails_id = ? and discount_id  = ? EXEC RevertPricesAfterDiscountEnd";
            try (
                    PreparedStatement psProc = con.prepareStatement(sql)) {
                psProc.setObject(1, getProductDetailsId(code));
                psProc.setObject(2, dis.getDiscountId());

                psProc.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return; // Thông báo lỗi nếu có vấn đề trong việc thực thi thủ tục lưu trữ
            }

            sql = "UPDATE Discount SET [start_date] = ?, end_date = ?, discount_amount = ? WHERE discount_id = ? AND productDetails_id = ?;"
                    + "exec ApplyDiscount @discount_id = ? ";

            try {
                ps = con.prepareStatement(sql);
                // Cập nhật bảng Discount
                ps.setObject(1, dis.getStartDate());
                ps.setObject(2, dis.getEndDate());
                ps.setObject(3, dis.getDiscountAmount());
                ps.setObject(4, dis.getDiscountId());
                ps.setObject(5, getProductDetailsId(code));
                ps.setObject(6, dis.getDiscountId());
                // Cập nhật bảng DetailDiscount
                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Sửa thành công");
        } else {
            double gia = dis_v.getPriceById(code);
            if (index > gia) {
                JOptionPane.showMessageDialog(null, "Số tiền chiết khấu không được cao hơn giá gốc đang là:" + gia);
                return;
            }
            int conf = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn sửa");
            if (conf != 0) {
                return;
            }
            sql = "UPDATE Discount SET [start_date] = ?, end_date = ?, discount_amount = ? WHERE discount_id = ? AND productDetails_id = ?;"
                    + "exec ApplyDiscount @discount_id = ? ";

            try {
                ps = con.prepareStatement(sql);
                // Cập nhật bảng Discount
                ps.setObject(1, dis.getStartDate());
                ps.setObject(2, dis.getEndDate());
                ps.setObject(3, dis.getDiscountAmount());
                ps.setObject(4, dis.getDiscountId());
                ps.setObject(5, getProductDetailsId(code));
                ps.setObject(6, dis.getDiscountId()); // Bạn đã truyền discount_id đúng chưa?
                // Thực thi cập nhật
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }

            JOptionPane.showMessageDialog(null, "Sửa thành công");

        }
        txtMa_chiet_khau.setEnabled(false);
        jdc_ngay_bat_dau.setEnabled(false);
        jdc_ngay_ket_thuc.setEnabled(false);
        txt_so_tien_giam.setEnabled(false);
        txtMa_chiet_khau.setBackground(java.awt.Color.WHITE);
        jdc_ngay_bat_dau.setBackground(java.awt.Color.WHITE);
        jdc_ngay_ket_thuc.setBackground(java.awt.Color.WHITE);
        jdc_ngay_ket_thuc.setBackground(java.awt.Color.WHITE);
        loadTableDisscount(tblDiscount);
        ProductDetails_Service pro = new ProductDetails_Service();
        pro.loadTableProductDetails(tbl_san_pham_khuyen_mai);

    }
//

    public ArrayList<Discount> tim(String ma_dis) {
        try {

            String sql = "SELECT D.discount_id, DD.productDetails_id,D.start_date,D.end_date,D.discount_amount FROM "
                    + "Discount  D JOIN DetailDiscount DD ON D.discount_id = DD.discount_id  where D.discount_id = ?";
            ps = con.prepareStatement(sql);
            ps.setObject(1, ma_dis);
            rs = ps.executeQuery();
            while (rs.next()) {
                String ma_discout = rs.getString(1);
                int ma_spct = rs.getInt(2);
                String start_date_dis = rs.getString(3);
                String end_date_dis = rs.getString(4);
                double discountAmount = rs.getDouble(5);
                Discount dis = new Discount(ma_discout, ma_spct, start_date_dis, end_date_dis, discountAmount);
                listDis.add(dis);
            }
            return listDis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadTableDisscount(JTable name) {
        DefaultTableModel dtm = (DefaultTableModel) name.getModel();
        dtm.setRowCount(0);
        for (Discount x : getAll()) {
            dtm.addRow(new Object[]{
                x.getDiscountId(),
                x.getProductDetails_code(),
                x.getStartDate(),
                x.getEndDate(),
                x.getDiscountAmount(),
                x.getColor(),
                x.getStyle(),
                x.getSize(),});
        }
    }

    public void loadTableTim(JTable name, String keywordUP) {
        DefaultTableModel dtm = (DefaultTableModel) name.getModel();
        dtm.setRowCount(0);
        for (Discount x : GetListSearchProductDetail(keywordUP)) {
            dtm.addRow(new Object[]{
                x.getDiscountId(),
                x.getProductDetails_code(),
                x.getStartDate(),
                x.getEndDate(),
                x.getDiscountAmount(),
                x.getColor(),
                x.getStyle(),
                x.getSize(),});
        }
    }

    public java.util.List<Discount> GetListSearchProductDetail(String keywordUP) {
        String keyword = removeVietnameseAccents(keywordUP).toUpperCase();
        java.util.List<Discount> listSearch = new ArrayList<>();

        for (Discount proDe : getAll()) {
            boolean matches = false;

            // Kiểm tra từng thuộc tính của Discount
            if (removeVietnameseAccents(proDe.getDiscountId()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (removeVietnameseAccents(proDe.getProductDetails_code()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (removeVietnameseAccents(proDe.getStartDate()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (removeVietnameseAccents(proDe.getEndDate()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (String.valueOf(proDe.getDiscountAmount()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (removeVietnameseAccents(proDe.getColor()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (removeVietnameseAccents(proDe.getStyle()).toUpperCase().contains(keyword)) {
                matches = true;
            } else if (removeVietnameseAccents(proDe.getSize()).toUpperCase().contains(keyword)) {
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

    public DiscountService() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
        con = dcm.getConnection();
    }

    public void GetListSearchProductDetail(JTable tblDiscount, String index) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

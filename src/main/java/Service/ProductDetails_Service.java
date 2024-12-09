
package Service;

import Model.Discount;
import Model.ProductDetails;
import dao.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author dohuu
 */
public class ProductDetails_Service {

    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Connection con = null;
    private String sql = "";
    ArrayList<ProductDetails> listDis = new ArrayList<>();

    public void loadTableProductDetails(JTable tbl_san_pham_khuyen_mai) {
        DefaultTableModel dtm = (DefaultTableModel) tbl_san_pham_khuyen_mai.getModel();
        dtm.setRowCount(0);
        for (ProductDetails x : getAll()) {
            dtm.addRow(new Object[]{
                x.getProductDetailsCode(),
                x.getPrice(),
                x.getColor(),
                x.getStyle(),
                x.getSize()
            });
        }

    }

    public void loadTableProductDetailsTim(JTable tbl_san_pham_khuyen_mai, String index) {
        DefaultTableModel dtm = (DefaultTableModel) tbl_san_pham_khuyen_mai.getModel();
        dtm.setRowCount(0);
        for (ProductDetails x : GetListSearchProductDetail(index)) {
            dtm.addRow(new Object[]{
                x.getProductDetailsCode(),
                x.getPrice(),
                x.getColor(),
                x.getStyle(),
                x.getSize()
            });
        }

    }

    public void timCBB(JTable tbl_san_pham_khuyen_mai, String id) {
        DefaultTableModel dtm = (DefaultTableModel) tbl_san_pham_khuyen_mai.getModel();
        dtm.setRowCount(0);
        for (ProductDetails x : tim(id)) {
            dtm.addRow(new Object[]{
                x.getProductDetailsId(),
                x.getPrice(),
                x.getColor(),
                x.getStyle(),
                x.getSize()
            });
        }

    }

    public List<ProductDetails> GetListSearchProductDetail(String keywordUP) {
        String keyword = removeVietnameseAccents(keywordUP).toUpperCase();
        List<ProductDetails> listSearch = new ArrayList<>();

        for (ProductDetails proDe : getAll()) {
            if (
                     removeVietnameseAccents(proDe.getStyle()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(proDe.getProductDetailsCode()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(proDe.getSize()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(proDe.getColor()).toUpperCase().contains(keyword)
                    || String.valueOf(proDe.getPrice()).toUpperCase().contains(keyword)) {
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

    public void loadccb_ma(JComboBox cbb) {
        cbb.removeAllItems();
        for (ProductDetails x : getAll()) {
            cbb.addItem(x.getProductDetailsId() + "");
        }
    }

    public ArrayList<ProductDetails> getAll() {
        try {

            String sql = "SELECT \n"
                    + "    p.productDetails_code, \n"
                    + "    p.price,\n"
                    + "    cl.color_name,\n"
                    + "    sl.style_name,\n"
                    + "    si.size_name\n"
                    + "FROM \n"
                    + "    ProductDetails p\n"
                    + "\n"
                    + "JOIN \n"
                    + "    Color cl ON p.color_id = cl.color_id\n"
                    + "JOIN \n"
                    + "    Styles sl ON p.style_id = sl.style_id\n"
                    + "JOIN \n"
                    + "    Sizes si ON p.size_id = si.size_id;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String ma_spct = rs.getString(1);
                double gia = rs.getDouble(2);
                String color = rs.getString(3);
                String style = rs.getString(4);
                String size = rs.getString(5);
                ProductDetails dis = new ProductDetails(ma_spct, gia,  style, size, color);
                listDis.add(dis);
            }
            return listDis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<ProductDetails> tim(String id) {
        try {
            String sql = "SELECT pro.product_code, price,cl.color_name,sl.style_name,si.size_name\n"
                    + "FROM ProductDetails p\n"
                    + "join Products Pro on p.productDetails_id = p.product_id\n"
                    + "join Color cl on p.color_id = cl.color_id\n"
                    + "join Styles sl on p.style_id = sl.style_id\n"
                    + "join Sizes si on p.size_id = si.size_id where product_code = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                String ma_spct = rs.getString(1);
                double gia = rs.getDouble(2);
                String color = rs.getString(3);
                String style = rs.getString(4);
                String size = rs.getString(5);
                ProductDetails dis = new ProductDetails(ma_spct, gia,  style, size, color);
                listDis.add(dis);
            }
            return listDis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getPriceById(String code) {
        double price = 0;
        try {
            String sql = "SELECT price FROM ProductDetails WHERE productDetails_code = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, code);
            rs = ps.executeQuery();
            if (rs.next()) {
                price = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return price;
    }

    public ProductDetails_Service() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
        con = dcm.getConnection();
    }
}

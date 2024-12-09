package Service;

import Model.Categories;
import dao.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CategoriesService {

    public ArrayList<Categories> getAllCt() throws SQLDataException {
        ArrayList<Categories> list = new ArrayList<>();
        try {
            String sql = "select category_id, category_name\n"
                    + "from Categories";
            Connection cn = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
            Statement sm = cn.createStatement();
            ResultSet rs = sm.executeQuery(sql);
            while (rs.next()) {
                Categories ct = new Categories();
                ct.setCategory_id(rs.getString("category_id"));
                ct.setCategory_name(rs.getString("category_name"));
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Integer addCt(Categories ct) throws SQLDataException {
        Integer row = null;
        try {
            String sql = "insert into Categories(category_name)\n"
                    + "values(?)";
            Connection cn = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
            PreparedStatement ps = cn.prepareStatement(sql);
           
            ps.setString(1, ct.getCategory_name());
            row = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    public Integer updateCt(Categories ct) throws SQLDataException {
        Integer row = null;
        try {
            String sql = "update Categories\n"
                    + "set category_name=?\n"
                    + "where category_id=?";
            Connection cn = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(2, ct.getCategory_id());
            ps.setString(1, ct.getCategory_name());
            row = ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    public Integer deleteCt(String maCt) throws SQLDataException {
        Integer row = null;
        try {
            String sql = "DELETE FROM ProductDetails\n"
                    + "WHERE product_id IN (SELECT product_id FROM Products WHERE category_id = ?)\n"
                    + "DELETE FROM Products\n"
                    + "WHERE category_id = ?\n"
                    + "DELETE FROM Categories\n"
                    + "WHERE category_id = ?";
            Connection cn = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, maCt);
            ps.setString(2, maCt);
            ps.setString(3, maCt);
            row = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    //code danh mục 
    public void filltoTable(JTable tblDanhSachDM) throws SQLDataException {
        ArrayList<Categories> list = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) tblDanhSachDM.getModel();
        int i = 1;
        ArrayList<Categories> listCt = getAllCt();
        model.setRowCount(0);
        list.clear();
        for (Categories ct : listCt) {
            model.addRow(new Object[]{
                i++, ct.getCategory_id(), ct.getCategory_name()
            });
            list.add(ct);
        }
    }

    public void showDetails(DefaultTableModel model, JTable tblDanhSachDM, int index, JTextField txtMaDM, JTextField txtTenDM) throws SQLDataException {
        ArrayList<Categories> listCt = getAllCt();
        if (index >= 0 && index < listCt.size()) {
            Categories ct = listCt.get(index);
            txtMaDM.setText(ct.getCategory_id());
            txtTenDM.setText(ct.getCategory_name());
            tblDanhSachDM.setRowSelectionInterval(index, index);
        }

    }

    public Categories readForm(JTextField txtMaDM, JTextField txtTenDM) {
        Categories ct = new Categories();
        ct.setCategory_id(txtMaDM.getText());
        ct.setCategory_name(txtTenDM.getText());
        return ct;
    }

public boolean check(String tenDM) {
    try {
        if (tenDM.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không để trống các trường dữ liệu");
            return false;
        }
        if (tenDM.trim().matches("[0-9]+")) {
            JOptionPane.showMessageDialog(null, "Tên danh mục không được nhập số");
            return false;
        }
        
        String[] words = tenDM.trim().split("\\s+");
        for (String word : words) {
            if (!Character.isUpperCase(word.charAt(0))) {
                JOptionPane.showMessageDialog(null, "Mỗi từ trong tên danh mục phải bắt đầu bằng chữ cái viết hoa");
                return false;
            }
        }
//        for (Categories ct : getAllCt()) {
//            if (tenDM.trim().toLowerCase().equals(ct.getCategory_name().toLowerCase())) {
//                JOptionPane.showMessageDialog(null, "Danh mục đã tồn tại");
//                return false;
//            }
//        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return true;
}

    public String addCategory(Categories ct) throws Exception {

        if (addCt(ct) != null) {
            return "Thêm thành công";
        } else {
            return "Thêm thất bại";
        }
    }

    public boolean updateCategory(Categories ct) throws SQLDataException {
        boolean tc = false;
        ArrayList<Categories> list = getAllCt();
        if (updateCt(ct) != null) {
            tc = true;
        }
        return tc;
    }
    
    // Trong lớp CategoriesService

public void saveCategory(JTable tblDanhSachDM, JTextField txtMaDM, JTextField txtTenDM, ArrayList<Categories> list) {
    int row = tblDanhSachDM.getSelectedRow();
    if (row == -1) {
        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có muốn thêm không?");
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        String tenDM= txtTenDM.getText();
        if (check(tenDM)) { 
            try {
                txtMaDM.setEnabled(false);
//                for (Categories cat : list) {
//                    if (cat.getCategory_name().trim().equalsIgnoreCase(tenDM)) {
//                        JOptionPane.showMessageDialog(null, "Tên danh mục đã tồn tại");
//                        return;
//                    }
//                }
                Categories ct = readForm(txtMaDM, txtTenDM); 
                String result = addCategory(ct); 
                JOptionPane.showMessageDialog(null, result);
                if (result.equals("Thêm thành công")) {
                    filltoTable(tblDanhSachDM); 
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } else {
        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có muốn sửa không?");
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        String tenDM=txtTenDM.getText();
        if (check(tenDM)) {
            try {
//                for (Categories cat : list) {
//                    if (cat.getCategory_name().trim().equalsIgnoreCase(tenDM)) {
//                        JOptionPane.showMessageDialog(null, "Tên danh mục đã tồn tại");
//                        return;
//                    }
//                }
                Categories ct = readForm(txtMaDM, txtTenDM);
                if (updateCategory(ct)) { 
                    JOptionPane.showMessageDialog(null, "Sửa thành công");
                    filltoTable(tblDanhSachDM);
                } else {
                    JOptionPane.showMessageDialog(null, "Sửa thất bại");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


}

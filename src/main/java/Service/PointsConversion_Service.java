package Service;

import Model.PointsConversion;
import dao.DatabaseConnectionManager;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class PointsConversion_Service {

    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Connection con = null;
    private String sql = "";
    private ArrayList<PointsConversion> listDis = new ArrayList<>();

    public PointsConversion_Service() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
        con = dcm.getConnection();
        loadAllPointsConversions(); // Load data when the service is initialized
    }
    
    public int checkLuu(JTextField txtMoney , JTextField txtMoney1){
     double tienSangDiem = Double.parseDouble(txtMoney.getText().trim());
     int diemSangTien = Integer.parseInt(txtMoney1.getText().trim());
     if(tienSangDiem == tienSangDiem() && diemSangTien == diemSangTien()){
         JOptionPane.showMessageDialog(null, "Bạn chưa thay đổi gì để lưu!");
            return 1;
     }
     return 0;
 }

    private void loadAllPointsConversions() {
        try {
            String sql = "SELECT money_into_points, points_into_money FROM PointsConversion";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                double tienSangDiem = rs.getDouble(1);
                int diemSangTien = rs.getInt(2);
                PointsConversion dis = new PointsConversion(tienSangDiem, diemSangTien);
                listDis.add(dis);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PointsConversion> getAll() {
        return listDis;
    }

    public double tienSangDiem() {
        if (!listDis.isEmpty()) {
            return listDis.get(0).getMoneyIntoPoints();
        }
        return 0; // Hoặc một giá trị mặc định nào đó
    }

    public int diemSangTien() {
        if (!listDis.isEmpty()) {
            return listDis.get(0).getPointsIntoMoney();
        }
        return 0; // Hoặc một giá trị mặc định nào đó
    }

    public void updateDiemSangTien(int newDiemSangTien) {
        try {
            String sql = "UPDATE dbo.PointsConversion SET points_into_money = ? WHERE ScorePoints_id = 1";
            ps = con.prepareStatement(sql);
            ps.setInt(1, newDiemSangTien);
            ps.executeUpdate();
            reloadPointsConversions(); // Reload data after update
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTienSangDiem(double newTienSangDiem) {
        try {
            String sql = "UPDATE dbo.PointsConversion SET money_into_points = ? WHERE ScorePoints_id = 1";
            ps = con.prepareStatement(sql);
            ps.setDouble(1, newTienSangDiem);
            ps.executeUpdate();
            reloadPointsConversions(); // Reload data after update
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reloadPointsConversions() {
        listDis.clear();
        loadAllPointsConversions();
    }
    

  public void ponts(JTextField txtMoney, JTextField txtMoney1) {
        int dtt = 0;
        double ttd = 0;
        Component view_TrangChu = null;
        try {
            ttd = Double.parseDouble(txtMoney.getText());
            if (ttd < 0) {
                JOptionPane.showMessageDialog(view_TrangChu, "Số tiền phải lớn hơn 0");
                txtMoney.requestFocus();
                txtMoney.setBackground(Color.YELLOW);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view_TrangChu, "Số tiền quy đổi giá không đúng định dạng");
            txtMoney.requestFocus();
            txtMoney.setBackground(Color.YELLOW);
            return;
        }
        try {
            dtt = Integer.parseInt(txtMoney1.getText());
            if (dtt < 0) {
                JOptionPane.showMessageDialog(view_TrangChu, "Số điểm phải lớn hơn 0");
                txtMoney1.requestFocus();
                txtMoney1.setBackground(Color.YELLOW);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view_TrangChu, "Số điểm quy đổi không đúng định dạng");
            txtMoney1.requestFocus();
            txtMoney1.setBackground(Color.YELLOW);
            return;
        }
        try {
            PointsConversion_Service psv = new PointsConversion_Service();
            dtt = Integer.parseInt(txtMoney1.getText());
            psv.updateDiemSangTien(dtt);
            psv.updateTienSangDiem(ttd);
//            JOptionPane.showMessageDialog(view_TrangChu, "Cập nhật thành công");
            txtMoney.setText(String.valueOf(ttd));
            txtMoney1.setText(String.valueOf(dtt));
            txtMoney.setBackground(Color.WHITE);
            txtMoney1.setBackground(Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

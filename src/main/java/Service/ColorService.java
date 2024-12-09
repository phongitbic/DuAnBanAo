/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;


import Model.Color;
import dao.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author DELL
 */
public class ColorService {
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Connection con = null;
    private String sql = "";

    public ArrayList<Color> getAllColors() {
        ArrayList<Color> listColors = new ArrayList<>();
        try {
            String sql = "SELECT color_id, color_name FROM Color";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String colorId = rs.getString(1);
                String colorName = rs.getString(2);
                Color cl = new Color(colorId, colorName);
                listColors.add(cl);
            }
            return listColors;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    
}

    public ColorService() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
        con = dcm.getConnection();
    }
    
}
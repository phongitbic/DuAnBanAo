/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Model.Style;
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
public class StyleService {

    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Connection con = null;
    private String sql = "";

    public ArrayList<Style> getAllStyles() {
        ArrayList<Style> listStyles = new ArrayList<>();
        try {
            String sql = "SELECT style_id, style_name, description FROM Styles";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String styleId = rs.getString(1);
                String styleName = rs.getString(2);
                String styleDescription = rs.getString(3);
                Style style = new Style(styleId, styleName, styleDescription);
                listStyles.add(style);
            }
            return listStyles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public StyleService() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
        con = dcm.getConnection();
    }
    
}

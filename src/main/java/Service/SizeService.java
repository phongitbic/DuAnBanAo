/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Model.Color;
import Model.Size;
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
public class SizeService {

    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Connection con = null;
    private String sql = "";

    public ArrayList<Size> getAllSizes() throws SQLException {
        ArrayList<Size> listSizes = new ArrayList<>();
        try {
            String sql = "SELECT size_id, size_name, size_description FROM Sizes";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String sizeId = rs.getString(1);
                String sizeName = rs.getString(2);
                String sizeDescription = rs.getString(3);
                Size size = new Size(sizeId, sizeName, sizeDescription);
                listSizes.add(size);
            }
            return listSizes;
        } catch (Exception e) {
            return null;
        }

    }

    public SizeService() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
        con = dcm.getConnection();
    }
}

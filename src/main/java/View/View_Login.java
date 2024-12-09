
package View;

import dao.DatabaseConnectionManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class View_Login extends javax.swing.JFrame {
    private int userId;  
    public View_Login() {
        initComponents();
        setLocationRelativeTo(null);
    }
      
    
    private String getRoleName(int roleId) throws SQLException {
    String roleName = null;
    DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
    try {
        Connection conn = dcm.getConnection();
        String sql = "SELECT role_name FROM Roles WHERE role_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, roleId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            roleName = rs.getString("role_name");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return roleName;
}
       
    private ResultSet dsUsers() throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService","sa","123456");
        try {
            Connection connection = dcm.getConnection();
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM Users";
            ResultSet resultset = statement.executeQuery(sql);
            return resultset;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
       public int getUserId() {
        return userId;
    }
    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtUsername = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        txtPassword = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\logo.jpg")); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\tải xuống.png")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel2)
                .addContainerGap(58, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(97, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 204, 204));

        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 255));
        jLabel3.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\49249_key_login_logout_icon.png")); // NOI18N
        jLabel3.setText("Login");

        jLabel4.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 255));
        jLabel4.setText("Login");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\8726390_user_icon.png")); // NOI18N

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\2191536_key_login_pad_secret_secure_icon.png")); // NOI18N

        btnLogin.setBackground(new java.awt.Color(255, 255, 51));
        btnLogin.setFont(new java.awt.Font("Arial", 2, 18)); // NOI18N
        btnLogin.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\3669160_ic_input_icon.png")); // NOI18N
        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPassword))
                .addGap(48, 48, 48))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtUsername)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(txtPassword))
                .addGap(33, 33, 33)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(135, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
    
    }//GEN-LAST:event_txtUsernameActionPerformed
  
    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
     String[] roles = {"Quản lý", "Nhân viên"};
    String selectedRole = showRoleSelectionDialog(roles);

    if (selectedRole == null) {
        return;
    }

    if (this.txtUsername.getText().trim().isEmpty() || this.txtPassword.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Không để trống User Name hoặc Password");
        return;
    }

    try {
        ResultSet rs = dsUsers();
        if (rs != null) {
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                int roleId = rs.getInt("role_id");
                int userId = rs.getInt("user_id");
                String role = getRoleName(roleId);
                if (txtUsername.getText().equalsIgnoreCase(username) && txtPassword.getText().equals(password) && role.equalsIgnoreCase(selectedRole)) {
                    this.userId = userId;
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công");
                    if (selectedRole.equalsIgnoreCase("Quản lý")) {
                        view_TrangChu vtt = new view_TrangChu(userId);  // Truyền userId vào constructor của view_TrangChu
                        vtt.setVisible(true);
                    } else if (selectedRole.equalsIgnoreCase("Nhân viên")) {
                        View_NhanVien vnv = new View_NhanVien(userId);
                        vnv.setVisible(true);
                    }
                    dispose();
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Sai thông tin đăng nhập");
        }
    } catch (SQLException e) {
        Logger.getLogger(View_Login.class.getName()).log(Level.SEVERE, null, e);
    }
    }//GEN-LAST:event_btnLoginActionPerformed
    private String showRoleSelectionDialog(String[] roles) {
    JDialog dialog = new JDialog(this, "Chọn vai trò", true);
    dialog.setSize(350, 250);
    dialog.setLayout(new BorderLayout());
    JPanel backgroundPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            URL imageURL = getClass().getResource("/icons/logo.jpg");
            if (imageURL != null) {
                ImageIcon backgroundImage = new ImageIcon(imageURL);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            } else {
                System.err.println("Không tìm thấy hình nền tại đường dẫn: /icons/logo.jpg");
            }
        }
    };
    backgroundPanel.setLayout(new BorderLayout());
    JLabel message = new JLabel("Đăng nhập với tư cách:");
    message.setFont(new Font("Arial", Font.BOLD, 18));
    message.setForeground(Color.WHITE); // Màu chữ trắng
    message.setHorizontalAlignment(JLabel.CENTER);
    message.setOpaque(true);
    message.setBackground(new Color(0, 128, 0));
    message.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    backgroundPanel.add(message, BorderLayout.NORTH);
    

    JComboBox<String> roleComboBox = new JComboBox<>(roles);
    roleComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
    roleComboBox.setBackground(new Color(255, 255, 255));
    roleComboBox.setForeground(new Color(60, 63, 65));
    roleComboBox.setOpaque(true);
    JPanel comboBoxPanel = new JPanel();
    comboBoxPanel.setOpaque(false);
    comboBoxPanel.add(roleComboBox);
    comboBoxPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    backgroundPanel.add(comboBoxPanel, BorderLayout.CENTER);

    JButton okButton = new JButton("OK");
    okButton.setBackground(new Color(0, 123, 255));
    okButton.setForeground(Color.WHITE);
    okButton.setFont(new Font("Arial", Font.BOLD, 14));
    okButton.setFocusPainted(false);
    okButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    okButton.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            okButton.setBackground(new Color(0, 153, 255));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            okButton.setBackground(new Color(0, 123, 255));
        }
    });
    okButton.addActionListener(e -> dialog.dispose());
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(new Color(255, 255, 0));
    buttonPanel.add(okButton);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

    dialog.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, new Color(0, 123, 255)));
    
    dialog.add(backgroundPanel);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    
    return (String) roleComboBox.getSelectedItem();
}

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(View_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(View_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(View_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(View_Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new View_Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}

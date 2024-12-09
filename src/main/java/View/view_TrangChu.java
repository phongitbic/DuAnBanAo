package View;

import Model.Categories;
import Model.Customers;
import Model.Order;
import Model.OrderProductDetails;
import Model.ProductDetails;
import Model.Products;
import Model.Size;
import Model.Style;
import Model.Suppliers;
import java.awt.Color;
import static java.awt.Color.black;
import static java.awt.Color.white;
import javax.swing.JFrame;
import Model.Users;
import Model.Users1;
import QR_CODE.QRCode;
import QR_CODE.QRCodeGenerator;
import Service.CategoriesService;
import Service.CustomerService;
import Service.DiscountService;
import Service.Excel.ExcelHoaDon;
import Service.OriginalPriceService;
import Service.PointsConversion_Service;
import Service.ProductDetails_Service;
import Service.QuanLyBanHang;
import Service.QuanLyHoaDon;
import Service.QuanLySanPhamService;
import Service.ThongKeService;
import Service.User1Service;
import static Service.User1Service.generateCode;
import Service.UserService;
import Service.VoucherService;
import Utils.ExcelExporter;
import Utils.ExcelImporter;
import Utils.QRCodeScanner;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import dao.DatabaseConnectionManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import java.io.IOException;
import java.util.Date;

public class view_TrangChu extends javax.swing.JFrame {

    //Hà
    private QuanLyHoaDon qlHoaDon = new QuanLyHoaDon();
    private QuanLyBanHang ql = new QuanLyBanHang();
    private int currentUserId;
    private String currentOrderCode;
    private QRCode qrCode;

    //Hà
    private ExcelHoaDon ex = new ExcelHoaDon();

    //Tuấn
    private int usercode;
    int index;
    //doi mk
    private boolean trangThaiMk = false;
    CategoriesService ctsv = new CategoriesService();
    User1Service us1sv = new User1Service();
    DefaultTableModel model = new DefaultTableModel();
    DefaultTableModel modelUs = new DefaultTableModel();
    ArrayList<Categories> list = new CategoriesService().getAllCt();
    ArrayList<Users1> listNv = new User1Service().getAllUser1();

    // end Tuấn
    //Đông 
    int itemsPerPagee = 5; // Số lượng mục trên mỗi trang
    int addVoucher = 0;
    int updateVoucher = 0;
    int addDis = 0;
    int updateDis = 0;
    int selectedTabIndex = 0;
    int totalRecords = 0;
    private VoucherService vou = new VoucherService();
    private DiscountService dis = new DiscountService();

    // Hòa*
    //Product
    private QuanLySanPhamService qlsp = new QuanLySanPhamService();
    private ExcelExporter ec = new ExcelExporter();
    private ExcelImporter importer = new ExcelImporter();

    private String selectedCategory = "";
    private String selectedSupplier = "";
    private String selectedStatus = "";
    private String selectedCategorys = "";
    private String selectedStyle = "";
    private String selectedSize = "";
    private String selectedColor = "";
    private String selectedQuantity = "";
    private Double[] selectedPrice = null;

    private String image;
    private QRCodeScanner qrCodeScanner;

    private boolean choice = false;
    private boolean isAddOrUpdate = false;

    private int currentPage = 1;
    private int totalPages = 0;

    private int currentPages = 1;
    private int totalPage = 0;

    private int rowsPerPage = 10;
    private boolean fillter = false;
    //End product

    //Hòa Customers
    private CustomerService qlkh = new CustomerService();
    //End

    //Phong
    ThongKeService thongKeService = new ThongKeService();

    public view_TrangChu(int userId) throws SQLDataException, SQLException {
        this.list = new CategoriesService().getAllCt();
        initComponents();
        setLocationRelativeTo(null);
        qrCode = new QRCode(camera, anhQRcode, ql, tblDSSanPham);
        camera.setPreferredSize(new Dimension(153, 106));
        cbbLocTrangThaiHoaDon.addItem("");
        cbbLocTrangThaiHoaDon.addItem("Đã thanh toán");
        cbbLocTrangThaiHoaDon.addItem("Chưa thanh toán");

        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLBH2.setVisible(true);
        this.jpViewDoiMK.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);
        this.jpQLBH.setBackground(new Color(220, 220, 220));

        //Của Hà
        this.currentUserId = userId;
        String tenNhanVien = getEmployeeNameById(this.currentUserId);
        lblNhanVienBan.setText(tenNhanVien != null ? tenNhanVien : "Không tìm thấy họ tên nhân viên");
        tblDSHD.setModel(qlHoaDon.loadOrderData());
        lblSoTrang.setText(qlHoaDon.getCurrentOrderPage() + " / " + qlHoaDon.getTotalOrderPages());

        
        ql.LoadDSSPTrongQLBH(tblDSSanPham);
        ql.loadVoucher(cbbVouchers);
//        ql.loadKhachHang(tblKhachH);
        ql.DanhSachHoaDonCho(tblHoaDonCho);

        // cập nhật giá voucher khi chọn
        cbbVouchers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbVouchersActionPerformed(evt);
            }
        });

        // cập nhật bảng spct trong hóa đơn nếu k có dòng được chọn
        tblDSHD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblDSHDFocusLost(evt);
            }
        });

        // Của Tuấn
        //danh mục
        model = (DefaultTableModel) tblDanhSachDM.getModel();
        ctsv.filltoTable(tblDanhSachDM);
        txtMaDM.setEnabled(false);
        txtTenDM.setEnabled(false);
//        ctsv.showDetails(model, tblDanhSachDM, index, txtMaDM, txtTenDM);

        //code nhân viên     
        tblNhanVien.setModel(us1sv.loadTableNV());
        lblSoTrang1.setText(us1sv.getCurrentOrderPage() + " / " + us1sv.getTotalOrderPages());

        lblMatKhau.setVisible(false);
        txtMatKhau.setVisible(false);

        // end Tuấn
        //Đông
        TabGiamGia.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Lấy chỉ số của tab hiện tại
                selectedTabIndex = TabGiamGia.getSelectedIndex();

            }
        });

        vou.loadTableVoucher(tblCoupon, currentPage, itemsPerPagee);
        dis.loadTableDisscount(tblDiscount);
        // end Dong

        //Product
        this.init();
        this.addCbbListener();
        currentPage = 1;
        updatePagination(qlsp.getAllProductDetail());

        currentPages = 1;
        updatePaginations(qlsp.getAllProduct());
        //Customers
        this.qlkh.loadTableCus(this.tblKhachH, this.qlkh.getAllCustomer());
        this.setEnableFormKH();

        //Phong
        thongKeService.LoadTongDoanhSo(lblDoanhThuTong);
        thongKeService.LocDSTheoThang(jDTTNam, jDTTThang, lblDoanhThuThang);
        thongKeService.LoadTableThongKe(tblDanhSachSPdaBan);
        thongKeService.LocDoanhSoTheoNgay(jDateDoanhThuNgay, lblDoanhThuNgay);
        thongKeService.loadTongSoLuong(lblTongSP);
        thongKeService.loadTongSoLuongDaBan(lblSPDaBan);

    }

    //            ----------------------Ngoai Ham Khoi Tao---------------------
    //Tuấn
    public Users1 getUsers() {
        Users1 us = new Users1();
        us.setHoTen(txtHoTen.getText().trim());
        us.setUsername(txtUsername.getText().trim());
        us.setEmail(txtEmail.getText().trim());
        us.setPhone(txtSdt.getText().trim());
        us.setAddress(txtDiaChi.getText().trim());
        us.setRoleId(Boolean.valueOf(txtVaiTro.getText().trim()));
        return us;
    }

    //code danh mục 
    private void filltoTable() throws SQLDataException {
        ctsv.filltoTable(tblDanhSachDM);
    }

    private void showDetails() throws SQLDataException {
        ctsv.showDetails(model, tblDanhSachDM, index, txtMaDM, txtTenDM);
    }

    private Categories readForm() {
        return ctsv.readForm(txtMaDM, txtTenDM);
    }

    private boolean check() {
        String tenDM = txtTenDM.getText();
        return ctsv.check(tenDM);
    }

    //nhanvien 
//    private void showDataNv(int index) {
//        us1sv.showDataNv(index, txtHoTen1, txtTenDangNhap, txtEmail1, txtSDT, txtDiaChi1, cbbVaiTro,
//                dateNgayTao, rdoDanglam, rdoNghilam, tblNhanVien);
//
//    }
    private void resetNv() {
        txtHoTen1.setText("");
        txtTenDangNhap.setText("");

        txtEmail1.setText("");
        txtSDT.setText("");
        txtDiaChi1.setText("");
        cbbVaiTro.setSelectedIndex(0);
        rdoDanglam.setSelected(true);

    }// end Tuấn

    //Hòa
    //PRODUCT
    private void init() {

        this.qlsp.addCbbDanhMuc(this.cbbDanhMuc);
        this.qlsp.addCbbDanhMuc(this.cbbLocTheoDM);
        this.qlsp.addCbbDanhMuc(this.cbbLoaiSPCT);
        this.qlsp.addCbbNhaCC(this.cbbNhaCC);
        this.qlsp.addCbbNhaCC(this.cbbLocNhaCC);
        this.qlsp.addCbbStyle(this.cbbStyle);
        this.qlsp.addCbbStyle(this.cbbStyles);
        this.qlsp.addCbbColor(this.cbbColor);
        this.qlsp.addCbbColor(this.cbbMauSacSPCT);
        this.qlsp.addCbbSize(this.cbbSizes);
        this.qlsp.addCbbSize(this.cbbKichThuocSPCT);
        this.qlsp.addCbbLocSoLuong(this.cbbSoLuong);
        this.qlsp.addCbbLocGiaBan(this.cbbGiaBan);
        this.qlsp.addCbbLocTrangThai(this.cbbTrangThaiSP);

        this.qlsp.loadTable(this.tblHienThiKetQua, qlsp.getAllProduct(), currentPages, rowsPerPage);
        this.qlsp.loadTableDetail(this.tblSPCT, this.qlsp.getAllProductDetail(), currentPage, rowsPerPage);
        this.qlsp.loadTableColor(tblMauSac, this.qlsp.getAllColor());
        this.qlsp.loadTableSize(tblKichThuoc, this.qlsp.getAllSize());
        this.qlsp.loadTableSupplier(tblNhaCungCap, this.qlsp.getAllSuppliers());
        this.qlsp.loadTableStyle(tblPhongCach, this.qlsp.getAllStyle());

        this.qrCodeScanner = new QRCodeScanner(this.pnWebCam, this.qlsp, this.tblSPCT);

    }

    private void displayImage(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        int labelWidth = lblImage.getWidth();
        int labelHeight = lblImage.getHeight();

        if (icon.getIconWidth() > labelWidth || icon.getIconHeight() > labelHeight) {
            Image image = icon.getImage().getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);

            icon = new ImageIcon(image);
        }

        lblImage.setIcon(icon);
    }

    private void addCbbListener() throws SQLException {

        this.setEnableFormSP();
        this.setEnableFormSPCT();
        this.setEnableFormSize(false);
        this.setEnableFormStyle(false);
        this.setEnableFormColor(false);
        this.setEnableFormSupp(false);

        this.cbbLocTheoDM.addActionListener(e -> {
            selectedCategory = (String) cbbLocTheoDM.getSelectedItem();
            filterAndLoadTable();
        });

        this.cbbLocNhaCC.addActionListener(e -> {
            selectedSupplier = (String) this.cbbLocNhaCC.getSelectedItem();
            filterAndLoadTable();
        });

        this.cbbTrangThaiSP.addActionListener(e -> {
            selectedStatus = (String) this.cbbTrangThaiSP.getSelectedItem();

            filterAndLoadTable();
        });

        this.cbbLoaiSPCT.addActionListener(e -> {
            selectedCategorys = (String) this.cbbLoaiSPCT.getSelectedItem();
            filterAndLoadTables();
        });

        this.cbbStyles.addActionListener(e -> {
            selectedStyle = (String) this.cbbStyles.getSelectedItem();

            filterAndLoadTables();
        });

        this.cbbKichThuocSPCT.addActionListener(e -> {
            selectedSize = (String) this.cbbKichThuocSPCT.getSelectedItem();
            filterAndLoadTables();
        });
        this.cbbMauSacSPCT.addActionListener(e -> {
            selectedColor = (String) this.cbbMauSacSPCT.getSelectedItem();

            filterAndLoadTables();
        });
        this.cbbSoLuong.addActionListener(e -> {
            selectedQuantity = (String) this.cbbSoLuong.getSelectedItem();

            filterAndLoadTables();
        });
        this.cbbGiaBan.addActionListener(e -> {
            String selectedPriceString = (String) this.cbbGiaBan.getSelectedItem();
            selectedPrice = qlsp.convertPrice(selectedPriceString);
            filterAndLoadTables();
        });

        this.tbpThuocTinh.addChangeListener(e -> {
            this.choice = false;
            this.isAddOrUpdate = false;
            this.setEnableFormColor(false);
            this.setEnableFormSize(false);
            this.setEnableFormStyle(false);
            this.setEnableFormSupp(false);
            this.tblMauSac.clearSelection();
            this.tblKichThuoc.clearSelection();
            this.tblNhaCungCap.clearSelection();
            this.tblPhongCach.clearSelection();
            this.setInputFields("màu sắc");
            this.setInputFields("kích thước");
            this.setInputFields("nhà cung cấp");
            this.setInputFields("phong cách");
        });

    }

    public Boolean isConfirmMessage(String message) {
        int showMessage = JOptionPane.showConfirmDialog(this, message, "Xác nhận", JOptionPane.YES_NO_OPTION);
        return showMessage == JOptionPane.YES_OPTION;
    }

    public void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isShowMessageSelected(JTable table) {
        int rowIndex = table.getSelectedRow();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trên table để tiếp tục cập nhật.");
            return false;
        }
        return true;
    }

    private void setInputFields(String objType) {
        switch (objType) {
            case "màu sắc":
                this.txtMaMau.setText("");
                this.txtTenMau.setText("");
                break;
            case "kích thước":
                this.txtMaKT.setText("");
                this.txtTenKichThuoc.setText("");
                this.txtMoTaSize.setText("");
                break;
            case "nhà cung cấp":
                this.txtNCC.setText("");
                this.txtTenNCC.setText("");
                this.txtSDTNCC.setText("");
                this.txtEmailNCC.setText("");
                this.txtDiaChiNCC.setText("");
                break;
            case "phong cách":
                this.txtMaPC.setText("");
                this.txtTenPhongCach.setText("");
                this.txtMoTaPC.setText("");
                break;
            case "sản phẩm chi tiết":
                this.txtMaSPCT1.setText("");
                this.cbbStyle.setSelectedItem("");
                this.cbbColor.setSelectedItem("");
                this.cbbSizes.setSelectedItem("");
                this.txtQuantity.setText("");
                this.txtPrice.setText("");
                this.lblImage.setIcon(null);
                this.lblImage.setText("Click để chọn ảnh!");
                break;
            case "sản phẩm":
                this.txtMaSanPham.setText("");
                this.txtTenSP.setText("");
                this.txtMotaSPP.setText("");
                this.cbbDanhMuc.setSelectedItem("");
                this.cbbNhaCC.setSelectedItem("");
                this.rdoRun.setSelected(true);
                break;
        }
    }

    private void handleAddOrUpdate(String id, Object obj, String objType, Runnable addOrUpdateFunc) {
        if (this.isConfirmMessage("Bạn có chắc chắn muốn " + (this.choice ? "thêm mới" : "cập nhật") + " " + objType + " " + id + " không?")) {
            addOrUpdateFunc.run();
            this.showMessage((this.choice ? "Thêm mới" : "Cập nhật") + " " + objType + " " + id + " thành công", "Thành công");
            setInputFields(objType);
            this.txtMaSP.setText("");
            this.image = null;
        } else {
            setInputFields(objType);
            this.image = null;
        }
    }

    private void filterAndLoadTable() {

        java.util.List<Products> filteredProducts = this.qlsp.filterProducts(selectedCategory, selectedSupplier, selectedStatus);
        this.qlsp.loadTable(tblHienThiKetQua, filteredProducts, currentPages, rowsPerPage);
        currentPages = 1;
        updatePaginations(filteredProducts);
    }

    private void filterAndLoadTables() {
        java.util.List<ProductDetails> filteredProductDetails = qlsp.filterProductDetail(
                selectedCategorys, selectedStyle, selectedSize, selectedColor,
                qlsp.convertQuantity(selectedQuantity), selectedPrice);
        this.qlsp.loadTableDetail(tblSPCT, filteredProductDetails, currentPage, rowsPerPage);
        currentPage = 1;
        updatePagination(filteredProductDetails);
    }

    private void setEnableFormSPCT() {
        this.txtMaSPCT1.setEnabled(false);
        this.cbbStyle.setEnabled(false);
        this.txtMaSP.setEnabled(false);
        this.cbbColor.setEnabled(false);
        this.cbbSizes.setEnabled(false);
        this.txtQuantity.setEnabled(false);
        this.txtPrice.setEnabled(false);
    }

    private void setEnableFormSP() {
        this.txtMaSanPham.setEnabled(false);
        this.txtTenSP.setEnabled(false);
        this.txtMotaSPP.setEnabled(false);
        this.cbbDanhMuc.setEnabled(false);
        this.cbbNhaCC.setEnabled(false);
        this.rdoRun.setEnabled(false);
        this.rdoStop.setEnabled(false);
    }

    private void setEnableFormSize(Boolean stt) {
        this.txtMaKT.setEnabled(stt);
        this.txtTenKichThuoc.setEnabled(stt);
        this.txtMoTaSize.setEnabled(stt);
    }

    private void setEnableFormStyle(Boolean stt) {
        this.txtMaPC.setEnabled(stt);
        this.txtTenPhongCach.setEnabled(stt);
        this.txtMoTaPC.setEnabled(stt);
    }

    private void setEnableFormColor(Boolean stt) {
        this.txtMaMau.setEnabled(stt);
        this.txtTenMau.setEnabled(stt);
    }

    private void setEnableFormSupp(Boolean stt) {
        this.txtNCC.setEnabled(stt);
        this.txtTenNCC.setEnabled(stt);
        this.txtSDTNCC.setEnabled(stt);
        this.txtEmailNCC.setEnabled(stt);
        this.txtDiaChiNCC.setEnabled(stt);
    }

    private void updatePagination(java.util.List<ProductDetails> allProductDetail) {
        int totalRows = allProductDetail.size();
        totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);
        if (totalPages == 0) {
            totalPages = 1;
        }
        updateTable(allProductDetail);
        lblPage.setText(currentPage + " / " + totalPages);
    }

    private void updateTable(java.util.List<ProductDetails> allProductDetatil) {
        int startRow = (currentPage - 1) * rowsPerPage;
        int endRow = Math.min(startRow + rowsPerPage, allProductDetatil.size());
        java.util.List<ProductDetails> pageProductDetail = allProductDetatil.subList(startRow, endRow);
        qlsp.loadTableDetail(tblSPCT, pageProductDetail, currentPage, rowsPerPage);
    }

    private void updatePaginations(java.util.List<Products> allProduct) {
        int totalRows = allProduct.size();
        totalPage = (int) Math.ceil((double) totalRows / rowsPerPage);
        if (totalPage == 0) {
            totalPage = 1;
        }
        updateTables(allProduct);
        lblPages.setText(currentPages + " / " + totalPage);
    }

    private void updateTables(java.util.List<Products> allProducts) {
        int startRow = (currentPages - 1) * rowsPerPage;
        int endRow = Math.min(startRow + rowsPerPage, allProducts.size());
        java.util.List<Products> pageProduct = allProducts.subList(startRow, endRow);
        qlsp.loadTable(tblHienThiKetQua, pageProduct, currentPages, rowsPerPage);
    }

    private void navigatePage(int targetPage, boolean isFiltered) {
        if (targetPage < 1 || targetPage > totalPages) {
            return;
        }
        currentPage = targetPage;
        if (!isFiltered) {
            updatePagination(qlsp.filterProductDetail(
                    selectedCategorys, selectedStyle, selectedSize, selectedColor,
                    qlsp.convertQuantity(selectedQuantity), selectedPrice));
        } else {
            java.util.List<ProductDetails> list = new ArrayList<>();
            int[] selectedRows = this.tblHienThiKetQua.getSelectedRows();
            for (int rowIndex : selectedRows) {
                String productCode = (String) this.tblHienThiKetQua.getValueAt(rowIndex, 1);
                list.addAll(this.qlsp.getListById(productCode));
            }
            updatePagination(list);
        }
    }
    //END PRODUCT

    //Customers
    private void setEnableFormKH() {
        this.txtITenKhachHang1.setEnabled(false);
        this.txtSoDiienThoai.setEnabled(false);
        this.txtDiemTichLuy.setEnabled(false);
        this.txtTinhDiem.setEnabled(false);
        this.txtDiemDaDung.setEnabled(false);
        this.txtMaKH.setEnabled(false);
    }

    private void setText() {
        this.txtITenKhachHang1.setText("");
        this.txtSoDiienThoai.setText("");
        this.txtDiemTichLuy.setText("");
        this.txtTinhDiem.setText("");
        this.txtDiemDaDung.setText("");
        this.txtMaKH.setText("");
    }
    //End

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        jpContainer = new javax.swing.JPanel();
        MenuHeThong = new javax.swing.JPanel();
        jpQLBH = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jpQLSP = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jpQLDM = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jpQLKM = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jpQLKH = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jpTK = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jpQLTK = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        btnDangXuat = new javax.swing.JButton();
        lblLogo = new javax.swing.JLabel();
        jpQLHD = new javax.swing.JPanel();
        jLabel91 = new javax.swing.JLabel();
        ViewHeThong = new javax.swing.JPanel();
        jpViewQLHD = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        BangHoaDon = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblDSHD = new javax.swing.JTable();
        jLabel96 = new javax.swing.JLabel();
        txtLocHoaDonTheoMaHD = new javax.swing.JTextField();
        cbbLocTrangThaiHoaDon = new javax.swing.JComboBox<>();
        jLabel146 = new javax.swing.JLabel();
        jLabel147 = new javax.swing.JLabel();
        dateTu = new com.toedter.calendar.JDateChooser();
        dateDen = new com.toedter.calendar.JDateChooser();
        jLabel148 = new javax.swing.JLabel();
        jLabel149 = new javax.swing.JLabel();
        btnXuatExcelHoaDon = new javax.swing.JButton();
        btnLocTheoKhoang = new javax.swing.JButton();
        SanPhamTrongHoaDon = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        tblSanPhamTrongHoaDon = new javax.swing.JTable();
        jLabel92 = new javax.swing.JLabel();
        btnLui = new javax.swing.JButton();
        btnTien = new javax.swing.JButton();
        lblSoTrang = new javax.swing.JLabel();
        btnVeTrangDau = new javax.swing.JButton();
        btnVeTrangCuoi = new javax.swing.JButton();
        jpViewQLBH2 = new javax.swing.JPanel();
        jp_V_ThemMoiHD = new javax.swing.JPanel();
        jp_V_HoaDon = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblDSSPtrongHD = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        txtTenKhachHang = new javax.swing.JTextField();
        txtSDTKhachHang = new javax.swing.JTextField();
        cbbVouchers = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtSoDiemDaDung = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        lblNhanVienBan = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        lblTongTienThanhToan = new javax.swing.JLabel();
        btnThemKHtuHD = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        lblVAT = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        lblDiemConLai = new javax.swing.JLabel();
        rdoTienMat = new javax.swing.JRadioButton();
        rdoChuyenKhoan = new javax.swing.JRadioButton();
        btnLuuTruHD = new javax.swing.JButton();
        btnThanhToan = new javax.swing.JButton();
        btnXoaKhoiGioHang = new javax.swing.JButton();
        btnCapNhatGioHang = new javax.swing.JButton();
        txtNhapSoLuong1 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        btnKhachLe = new javax.swing.JButton();
        jLabel90 = new javax.swing.JLabel();
        lblTienTruocKhiGiam = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblDSSanPham = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        txtTenSanPham = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        txtNhapSoLuong = new javax.swing.JTextField();
        btnThemSPVaoHD = new javax.swing.JButton();
        jLabel95 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel144 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtMaSPCT = new javax.swing.JTextField();
        btnLamMoiTTSP = new javax.swing.JButton();
        txtMauSaccc = new javax.swing.JTextField();
        txtLoaiSPpp = new javax.swing.JTextField();
        txtKichThuoccc = new javax.swing.JTextField();
        txtPhongCachhh = new javax.swing.JTextField();
        camera = new javax.swing.JPanel();
        anhQRcode = new javax.swing.JLabel();
        btnQuetMaQRCodee = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblHoaDonCho = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        btnXoaHoaDonCho = new javax.swing.JButton();
        txtMaCTSP = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        btnTiepTucThanhToan = new javax.swing.JButton();
        jpViewQLDM = new javax.swing.JPanel();
        jLabel73 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDanhSachDM = new javax.swing.JTable();
        jLabel75 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnThemMoiDM = new javax.swing.JButton();
        btnSuaDM = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtMaDM = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        txtTenDM = new javax.swing.JTextField();
        btnLuu = new javax.swing.JButton();
        jpViewQLTK = new javax.swing.JPanel();
        tabQLNV = new javax.swing.JTabbedPane();
        jpThongTinCN = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtHoTen = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtSdt = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtDiaChi = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtVaiTro = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btnCapNhatThongTinCN = new javax.swing.JButton();
        btnDoiMatKhau = new javax.swing.JButton();
        jpQLNV = new javax.swing.JPanel();
        ThongTinNhanVien = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        txtHoTen1 = new javax.swing.JTextField();
        txtDiaChi1 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        txtEmail1 = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        txtTenDangNhap = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        rdoDanglam = new javax.swing.JRadioButton();
        rdoNghilam = new javax.swing.JRadioButton();
        jLabel71 = new javax.swing.JLabel();
        cbbVaiTro = new javax.swing.JComboBox<>();
        dateNgayTao = new com.toedter.calendar.JDateChooser();
        lblMatKhau = new javax.swing.JLabel();
        txtMatKhau = new javax.swing.JPasswordField();
        txtMaNhanVienn = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        DanhSachNhanVien = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblNhanVien = new javax.swing.JTable();
        CRUD_NhanVien = new javax.swing.JPanel();
        btnThemNhanVien = new javax.swing.JButton();
        btnCapNhatNhanVien = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel88 = new javax.swing.JLabel();
        txtHoTen2 = new javax.swing.JTextField();
        lblSoTrang1 = new javax.swing.JLabel();
        btnTienTrang = new javax.swing.JButton();
        btnLuiLai = new javax.swing.JButton();
        btnVeCuoii = new javax.swing.JButton();
        btnVeDauu = new javax.swing.JButton();
        jpViewDoiMK = new javax.swing.JPanel();
        formDoiMK = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        txtUsername1 = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        btnXacNhanDMK = new javax.swing.JButton();
        jLabel61 = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        txtNewPassword = new javax.swing.JPasswordField();
        txtPassword = new javax.swing.JPasswordField();
        btnQuaylai = new javax.swing.JButton();
        btnEyeMK = new javax.swing.JButton();
        btnEyeMKMoi = new javax.swing.JButton();
        btnEyeXacNhan = new javax.swing.JButton();
        jpViewQLSPP = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel161 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel162 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        jLabel163 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtTenSP = new javax.swing.JTextField();
        txtMaSanPham = new javax.swing.JTextField();
        rdoRun = new javax.swing.JRadioButton();
        rdoStop = new javax.swing.JRadioButton();
        jLabel109 = new javax.swing.JLabel();
        btnAddProduct = new javax.swing.JButton();
        btnUpdateProduct = new javax.swing.JButton();
        cbbDanhMuc = new javax.swing.JComboBox<>();
        cbbNhaCC = new javax.swing.JComboBox<>();
        jScrollPane15 = new javax.swing.JScrollPane();
        txtMotaSPP = new javax.swing.JTextArea();
        btnLuu2 = new javax.swing.JButton();
        btnAddProductDetails = new javax.swing.JButton();
        btnNhapTuExcel = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane13 = new javax.swing.JScrollPane();
        tblHienThiKetQua = new javax.swing.JTable();
        cbbLocTheoDM = new javax.swing.JComboBox<>();
        jLabel164 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        txtTimKiemTheoMa = new javax.swing.JTextField();
        jLabel165 = new javax.swing.JLabel();
        cbbTrangThaiSP = new javax.swing.JComboBox<>();
        jLabel110 = new javax.swing.JLabel();
        btnHoanTac1 = new javax.swing.JButton();
        cbbLocNhaCC = new javax.swing.JComboBox<>();
        btnExcel = new javax.swing.JButton();
        btnPreviousAll = new javax.swing.JButton();
        btnPreviou = new javax.swing.JButton();
        btnNextt = new javax.swing.JButton();
        btnNexttALL = new javax.swing.JButton();
        lblPages = new javax.swing.JLabel();
        jpViewQLSPP2 = new javax.swing.JPanel();
        jLabel111 = new javax.swing.JLabel();
        btnBackProducts = new javax.swing.JButton();
        jPanel26 = new javax.swing.JPanel();
        jLabel93 = new javax.swing.JLabel();
        txtMaSPCT1 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        cbbColor = new javax.swing.JComboBox<>();
        jLabel119 = new javax.swing.JLabel();
        cbbSizes = new javax.swing.JComboBox<>();
        jLabel120 = new javax.swing.JLabel();
        cbbStyle = new javax.swing.JComboBox<>();
        jLabel121 = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        jLabel122 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        btnAddChiTietSP = new javax.swing.JButton();
        btnCapNhat = new javax.swing.JButton();
        btnLuuSPCT = new javax.swing.JButton();
        lblImage = new javax.swing.JLabel();
        pnWebCam = new javax.swing.JLabel();
        txtMaSP = new javax.swing.JTextField();
        btnNhapSPCTExcel = new javax.swing.JButton();
        jPanel31 = new javax.swing.JPanel();
        jLabel112 = new javax.swing.JLabel();
        txtSearhProductDetail = new javax.swing.JTextField();
        cbbMauSacSPCT = new javax.swing.JComboBox<>();
        jLabel116 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jLabel115 = new javax.swing.JLabel();
        cbbKichThuocSPCT = new javax.swing.JComboBox<>();
        cbbLoaiSPCT = new javax.swing.JComboBox<>();
        jLabel117 = new javax.swing.JLabel();
        cbbStyles = new javax.swing.JComboBox<>();
        btnXemDSSPCT = new javax.swing.JButton();
        btnQRcode = new javax.swing.JButton();
        jScrollPane16 = new javax.swing.JScrollPane();
        tblSPCT = new javax.swing.JTable();
        jLabel168 = new javax.swing.JLabel();
        jLabel169 = new javax.swing.JLabel();
        cbbSoLuong = new javax.swing.JComboBox<>();
        cbbGiaBan = new javax.swing.JComboBox<>();
        btnExcelProductDetail = new javax.swing.JButton();
        btnDowloadQr = new javax.swing.JButton();
        btnPreviouAll = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnNextAll = new javax.swing.JButton();
        lblPage = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        btnThemMoiThuocTinh = new javax.swing.JButton();
        jpViewQLSPP3 = new javax.swing.JPanel();
        jLabel124 = new javax.swing.JLabel();
        tbpThuocTinh = new javax.swing.JTabbedPane();
        jPanel13 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane17 = new javax.swing.JScrollPane();
        tblMauSac = new javax.swing.JTable();
        jLabel125 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel126 = new javax.swing.JLabel();
        jLabel127 = new javax.swing.JLabel();
        jLabel128 = new javax.swing.JLabel();
        txtMaMau = new javax.swing.JTextField();
        txtTenMau = new javax.swing.JTextField();
        btnThemMoi = new javax.swing.JButton();
        btnCapNhatMS = new javax.swing.JButton();
        btnLuuColor = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane18 = new javax.swing.JScrollPane();
        tblKichThuoc = new javax.swing.JTable();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jLabel129 = new javax.swing.JLabel();
        jLabel130 = new javax.swing.JLabel();
        jLabel131 = new javax.swing.JLabel();
        txtMaKT = new javax.swing.JTextField();
        txtTenKichThuoc = new javax.swing.JTextField();
        btnThemMoiKT = new javax.swing.JButton();
        btnCapNhatKT = new javax.swing.JButton();
        btnLuuSize = new javax.swing.JButton();
        jLabel166 = new javax.swing.JLabel();
        jScrollPane21 = new javax.swing.JScrollPane();
        txtMoTaSize = new javax.swing.JTextArea();
        jLabel132 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jLabel133 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jLabel134 = new javax.swing.JLabel();
        jLabel135 = new javax.swing.JLabel();
        jLabel136 = new javax.swing.JLabel();
        txtNCC = new javax.swing.JTextField();
        txtTenNCC = new javax.swing.JTextField();
        btnThemNCC = new javax.swing.JButton();
        btnCapNhatNCC = new javax.swing.JButton();
        btnLuuSupp = new javax.swing.JButton();
        jLabel137 = new javax.swing.JLabel();
        txtSDTNCC = new javax.swing.JTextField();
        jLabel138 = new javax.swing.JLabel();
        txtEmailNCC = new javax.swing.JTextField();
        jLabel139 = new javax.swing.JLabel();
        txtDiaChiNCC = new javax.swing.JTextField();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblNhaCungCap = new javax.swing.JTable();
        jPanel20 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jPanel29 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jScrollPane19 = new javax.swing.JScrollPane();
        tblPhongCach = new javax.swing.JTable();
        jPanel32 = new javax.swing.JPanel();
        jLabel141 = new javax.swing.JLabel();
        jLabel142 = new javax.swing.JLabel();
        jLabel143 = new javax.swing.JLabel();
        txtMaPC = new javax.swing.JTextField();
        txtTenPhongCach = new javax.swing.JTextField();
        btnThemPC = new javax.swing.JButton();
        btnCapNhatPC = new javax.swing.JButton();
        btnLuuStyle = new javax.swing.JButton();
        jLabel123 = new javax.swing.JLabel();
        jScrollPane22 = new javax.swing.JScrollPane();
        txtMoTaPC = new javax.swing.JTextArea();
        jLabel140 = new javax.swing.JLabel();
        btnBackkkkkk = new javax.swing.JButton();
        jpViewQLKH = new javax.swing.JPanel();
        jLabel170 = new javax.swing.JLabel();
        DanhSachKhachHang1 = new javax.swing.JPanel();
        jScrollPane23 = new javax.swing.JScrollPane();
        tblKhachH = new javax.swing.JTable();
        jLabel171 = new javax.swing.JLabel();
        txtITenKhachHang1 = new javax.swing.JTextField();
        txtSoDiienThoai = new javax.swing.JTextField();
        jLabel172 = new javax.swing.JLabel();
        jLabel173 = new javax.swing.JLabel();
        txtDiemTichLuy = new javax.swing.JTextField();
        jLabel174 = new javax.swing.JLabel();
        txtTinhDiem = new javax.swing.JTextField();
        jLabel175 = new javax.swing.JLabel();
        txtDiemDaDung = new javax.swing.JTextField();
        btnThemMoiKH1 = new javax.swing.JButton();
        btnLamMoiKH1 = new javax.swing.JButton();
        btnSuaKhachHang1 = new javax.swing.JButton();
        jLabel176 = new javax.swing.JLabel();
        txtMaKH = new javax.swing.JTextField();
        txtSearchKH = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jpViewQLKM = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        TabGiamGia = new javax.swing.JTabbedPane();
        Coupon = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblCoupon = new javax.swing.JTable();
        btnAddCou = new javax.swing.JButton();
        btnEditCou = new javax.swing.JButton();
        jLabel98 = new javax.swing.JLabel();
        txtMaGiamGia = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
        txtPhanTramChietKhau = new javax.swing.JTextField();
        jLabel101 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        txtMoTa = new javax.swing.JTextArea();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        rdoDangKM = new javax.swing.JRadioButton();
        rdoDungKM = new javax.swing.JRadioButton();
        btnVoHH = new javax.swing.JButton();
        btnLuu3 = new javax.swing.JButton();
        txt_tien_toi_da = new javax.swing.JTextField();
        txt_tien_ap_dung = new javax.swing.JTextField();
        jLabel107 = new javax.swing.JLabel();
        jLabel167 = new javax.swing.JLabel();
        jLabel177 = new javax.swing.JLabel();
        txt_so_luong_vou = new javax.swing.JTextField();
        jdc_ngay_bat_dau_voucher = new com.toedter.calendar.JDateChooser();
        jdc_ngay_ket_thuc_voucher = new com.toedter.calendar.JDateChooser();
        jLabel178 = new javax.swing.JLabel();
        capNhatTichDiem = new javax.swing.JPanel();
        jLabel77 = new javax.swing.JLabel();
        txtMoney = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        txtPoint = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        txtPoint1 = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        txtMoney1 = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        btnLuuThayDoi = new javax.swing.JButton();
        btnResetPoint = new javax.swing.JButton();
        jLabel84 = new javax.swing.JLabel();
        txt_tim_Voucher = new javax.swing.JTextField();
        btnLuiVoucher = new javax.swing.JButton();
        btnTienVoucher = new javax.swing.JButton();
        txt_phan_trangVoucher = new javax.swing.JLabel();
        btnTienCuoiVoucher1 = new javax.swing.JButton();
        btnLuiCuoiVoucher1 = new javax.swing.JButton();
        jLabel86 = new javax.swing.JLabel();
        Discount = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tblDiscount = new javax.swing.JTable();
        btnAddDis = new javax.swing.JButton();
        btnEditDis = new javax.swing.JButton();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        txtMa_chiet_khau = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        btnLamMoi2 = new javax.swing.JButton();
        jLabel179 = new javax.swing.JLabel();
        txt_so_tien_giam = new javax.swing.JTextField();
        jdc_ngay_bat_dau = new com.toedter.calendar.JDateChooser();
        jdc_ngay_ket_thuc = new com.toedter.calendar.JDateChooser();
        jScrollPane24 = new javax.swing.JScrollPane();
        tbl_san_pham_khuyen_mai = new javax.swing.JTable();
        txt_tim_sp = new javax.swing.JTextField();
        jLabel180 = new javax.swing.JLabel();
        txt_tim_chuong_trình = new javax.swing.JTextField();
        jLabel181 = new javax.swing.JLabel();
        jLabel182 = new javax.swing.JLabel();
        txt_code_chi_tiet = new javax.swing.JTextField();
        jLabel183 = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        jLabel152 = new javax.swing.JLabel();
        txtMau = new javax.swing.JTextField();
        txtKieuDang = new javax.swing.JTextField();
        txtKichThuoc = new javax.swing.JTextField();
        jpViewTK = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        ThongKeSP = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        txtTimKiemSP = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jTuNgay = new com.toedter.calendar.JDateChooser();
        jDenNgay = new com.toedter.calendar.JDateChooser();
        btnLocKhoangSanPham = new javax.swing.JButton();
        jLabel150 = new javax.swing.JLabel();
        tongSP = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        lblTongSP = new javax.swing.JLabel();
        spDaBan = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        lblSPDaBan = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblDanhSachSPdaBan = new javax.swing.JTable();
        jLabel47 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        lblDoanhThuThang = new javax.swing.JLabel();
        jDTTThang = new com.toedter.calendar.JMonthChooser();
        jDTTNam = new com.toedter.calendar.JYearChooser();
        jPanel7 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        lblDoanhThuNgay = new javax.swing.JLabel();
        jDateDoanhThuNgay = new com.toedter.calendar.JDateChooser();
        jPanel5 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        lblDoanhThuTong = new javax.swing.JLabel();
        TuLocTheoKhoangUpdate = new com.toedter.calendar.JDateChooser();
        DenLocTheoKhoangUpdate = new com.toedter.calendar.JDateChooser();
        btnLocDoanhSoUpdate = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        cbbTOP = new javax.swing.JComboBox<>();
        btnLamMoiThongKe = new javax.swing.JButton();
        tongSP1 = new javax.swing.JPanel();
        jLabel153 = new javax.swing.JLabel();
        lblTongSP1 = new javax.swing.JLabel();
        spDaBan1 = new javax.swing.JPanel();
        jLabel154 = new javax.swing.JLabel();
        lblSPDaBan1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jpContainer.setBackground(new java.awt.Color(255, 255, 255));
        jpContainer.setPreferredSize(new java.awt.Dimension(1250, 600));
        jpContainer.setRequestFocusEnabled(false);

        MenuHeThong.setBackground(new java.awt.Color(255, 255, 255));
        MenuHeThong.setForeground(new java.awt.Color(255, 255, 255));

        jpQLBH.setBackground(new java.awt.Color(255, 102, 0));
        jpQLBH.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpQLBH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpQLBHMouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Bán hàng");

        javax.swing.GroupLayout jpQLBHLayout = new javax.swing.GroupLayout(jpQLBH);
        jpQLBH.setLayout(jpQLBHLayout);
        jpQLBHLayout.setHorizontalGroup(
            jpQLBHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpQLBHLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        jpQLBHLayout.setVerticalGroup(
            jpQLBHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpQLBHLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpQLSP.setBackground(new java.awt.Color(255, 102, 0));
        jpQLSP.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpQLSP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpQLSPMouseClicked(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Sản phẩm");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout jpQLSPLayout = new javax.swing.GroupLayout(jpQLSP);
        jpQLSP.setLayout(jpQLSPLayout);
        jpQLSPLayout.setHorizontalGroup(
            jpQLSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpQLSPLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(57, 57, 57))
        );
        jpQLSPLayout.setVerticalGroup(
            jpQLSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        jpQLDM.setBackground(new java.awt.Color(255, 102, 0));
        jpQLDM.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpQLDM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpQLDMMouseClicked(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Danh mục");

        javax.swing.GroupLayout jpQLDMLayout = new javax.swing.GroupLayout(jpQLDM);
        jpQLDM.setLayout(jpQLDMLayout);
        jpQLDMLayout.setHorizontalGroup(
            jpQLDMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpQLDMLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        jpQLDMLayout.setVerticalGroup(
            jpQLDMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        jpQLKM.setBackground(new java.awt.Color(255, 102, 0));
        jpQLKM.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpQLKM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpQLKMMouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Khuyến mãi");

        javax.swing.GroupLayout jpQLKMLayout = new javax.swing.GroupLayout(jpQLKM);
        jpQLKM.setLayout(jpQLKMLayout);
        jpQLKMLayout.setHorizontalGroup(
            jpQLKMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpQLKMLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(47, 47, 47))
        );
        jpQLKMLayout.setVerticalGroup(
            jpQLKMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        jpQLKH.setBackground(new java.awt.Color(255, 102, 0));
        jpQLKH.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpQLKH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpQLKHMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Khách hàng");

        javax.swing.GroupLayout jpQLKHLayout = new javax.swing.GroupLayout(jpQLKH);
        jpQLKH.setLayout(jpQLKHLayout);
        jpQLKHLayout.setHorizontalGroup(
            jpQLKHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpQLKHLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        jpQLKHLayout.setVerticalGroup(
            jpQLKHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
        );

        jpTK.setBackground(new java.awt.Color(255, 102, 0));
        jpTK.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpTK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpTKMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Thống kê");

        javax.swing.GroupLayout jpTKLayout = new javax.swing.GroupLayout(jpTK);
        jpTK.setLayout(jpTKLayout);
        jpTKLayout.setHorizontalGroup(
            jpTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpTKLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpTKLayout.setVerticalGroup(
            jpTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
        );

        jpQLTK.setBackground(new java.awt.Color(255, 102, 0));
        jpQLTK.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpQLTK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpQLTKMouseClicked(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(76, 252, 57));
        jLabel7.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Tài khoản");

        javax.swing.GroupLayout jpQLTKLayout = new javax.swing.GroupLayout(jpQLTK);
        jpQLTK.setLayout(jpQLTKLayout);
        jpQLTKLayout.setHorizontalGroup(
            jpQLTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpQLTKLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpQLTKLayout.setVerticalGroup(
            jpQLTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
        );

        btnDangXuat.setBackground(new java.awt.Color(255, 0, 0));
        btnDangXuat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDangXuat.setForeground(new java.awt.Color(255, 255, 255));
        btnDangXuat.setText("Đăng xuất");
        btnDangXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDangXuatActionPerformed(evt);
            }
        });

        lblLogo.setBackground(new java.awt.Color(255, 255, 255));
        lblLogo.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\logo.jpg")); // NOI18N

        jpQLHD.setBackground(new java.awt.Color(255, 102, 0));
        jpQLHD.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpQLHD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpQLHDMouseClicked(evt);
            }
        });

        jLabel91.setBackground(new java.awt.Color(255, 255, 255));
        jLabel91.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel91.setText("Hóa đơn");

        javax.swing.GroupLayout jpQLHDLayout = new javax.swing.GroupLayout(jpQLHD);
        jpQLHD.setLayout(jpQLHDLayout);
        jpQLHDLayout.setHorizontalGroup(
            jpQLHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpQLHDLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jLabel91)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpQLHDLayout.setVerticalGroup(
            jpQLHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpQLHDLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel91, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout MenuHeThongLayout = new javax.swing.GroupLayout(MenuHeThong);
        MenuHeThong.setLayout(MenuHeThongLayout);
        MenuHeThongLayout.setHorizontalGroup(
            MenuHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpQLBH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpQLSP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpQLDM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpQLKM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpQLKH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpTK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpQLTK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jpQLHD, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MenuHeThongLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDangXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
            .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MenuHeThongLayout.setVerticalGroup(
            MenuHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MenuHeThongLayout.createSequentialGroup()
                .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpQLBH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpQLHD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpQLSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpQLDM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpQLKM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpQLKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpQLTK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(btnDangXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
        );

        ViewHeThong.setPreferredSize(new java.awt.Dimension(948, 600));

        jpViewQLHD.setBackground(new java.awt.Color(255, 255, 255));
        jpViewQLHD.setPreferredSize(new java.awt.Dimension(948, 600));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(51, 51, 255));
        jLabel20.setText("DANH SÁCH HÓA ĐƠN");

        BangHoaDon.setBackground(new java.awt.Color(255, 255, 255));

        tblDSHD.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã HĐ", "Mã NV", "Tên NV", "Mã KH", "Tên KH", "SĐT KH", "Ngày tạo", "Mã giảm giá", "VAT", "Phương thức thanh toán", "Tổng giá", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDSHD.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDSHD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDSHDMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblDSHD);

        jLabel96.setText("Tìm kiếm:");

        txtLocHoaDonTheoMaHD.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtLocHoaDonTheoMaHD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLocHoaDonTheoMaHDKeyReleased(evt);
            }
        });

        cbbLocTrangThaiHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbLocTrangThaiHoaDonActionPerformed(evt);
            }
        });

        jLabel146.setText("Trạng thái");

        jLabel147.setText("Lọc theo khoảng :");

        dateTu.setDateFormatString("yyyy-MM-dd");

        dateDen.setDateFormatString("yyyy-MM-dd");

        jLabel148.setText("Đến :");

        jLabel149.setText("Từ :");

        btnXuatExcelHoaDon.setBackground(new java.awt.Color(51, 102, 255));
        btnXuatExcelHoaDon.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXuatExcelHoaDon.setForeground(new java.awt.Color(255, 255, 255));
        btnXuatExcelHoaDon.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\xuatEX.png")); // NOI18N
        btnXuatExcelHoaDon.setText("Xuất Excel");
        btnXuatExcelHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatExcelHoaDonActionPerformed(evt);
            }
        });

        btnLocTheoKhoang.setBackground(new java.awt.Color(51, 102, 255));
        btnLocTheoKhoang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLocTheoKhoang.setForeground(new java.awt.Color(255, 255, 255));
        btnLocTheoKhoang.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\loc.png")); // NOI18N
        btnLocTheoKhoang.setText("Lọc");
        btnLocTheoKhoang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocTheoKhoangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout BangHoaDonLayout = new javax.swing.GroupLayout(BangHoaDon);
        BangHoaDon.setLayout(BangHoaDonLayout);
        BangHoaDonLayout.setHorizontalGroup(
            BangHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BangHoaDonLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(BangHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(BangHoaDonLayout.createSequentialGroup()
                        .addComponent(jLabel96)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLocHoaDonTheoMaHD, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel146)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbbLocTrangThaiHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel147)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel149))
                    .addComponent(jLabel148))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(BangHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateTu, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateDen, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(BangHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnXuatExcelHoaDon)
                    .addComponent(btnLocTheoKhoang, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BangHoaDonLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 977, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        BangHoaDonLayout.setVerticalGroup(
            BangHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BangHoaDonLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(BangHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(BangHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(dateTu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(BangHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel96)
                            .addComponent(txtLocHoaDonTheoMaHD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbbLocTrangThaiHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel146)
                            .addComponent(jLabel147)
                            .addComponent(jLabel149)))
                    .addComponent(btnLocTheoKhoang, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(BangHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateDen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel148)
                    .addComponent(btnXuatExcelHoaDon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        SanPhamTrongHoaDon.setBackground(new java.awt.Color(255, 255, 255));

        tblSanPhamTrongHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã SPCT", "Tên SP", "Loại SP", "Màu sắc", "Kích thước", "Phong cách", "Số lượng", "Đơn giá"
            }
        ));
        jScrollPane14.setViewportView(tblSanPhamTrongHoaDon);

        javax.swing.GroupLayout SanPhamTrongHoaDonLayout = new javax.swing.GroupLayout(SanPhamTrongHoaDon);
        SanPhamTrongHoaDon.setLayout(SanPhamTrongHoaDonLayout);
        SanPhamTrongHoaDonLayout.setHorizontalGroup(
            SanPhamTrongHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SanPhamTrongHoaDonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 977, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        SanPhamTrongHoaDonLayout.setVerticalGroup(
            SanPhamTrongHoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SanPhamTrongHoaDonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel92.setBackground(new java.awt.Color(255, 255, 255));
        jLabel92.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel92.setForeground(new java.awt.Color(51, 51, 255));
        jLabel92.setText("Sản phẩm trong hóa đơn");

        btnLui.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veTruoc.png")); // NOI18N
        btnLui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuiActionPerformed(evt);
            }
        });

        btnTien.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veSau.png")); // NOI18N
        btnTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTienActionPerformed(evt);
            }
        });

        lblSoTrang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSoTrang.setText("0/0");

        btnVeTrangDau.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veDau.png")); // NOI18N
        btnVeTrangDau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVeTrangDauActionPerformed(evt);
            }
        });

        btnVeTrangCuoi.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veCuoi.png")); // NOI18N
        btnVeTrangCuoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVeTrangCuoiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpViewQLHDLayout = new javax.swing.GroupLayout(jpViewQLHD);
        jpViewQLHD.setLayout(jpViewQLHDLayout);
        jpViewQLHDLayout.setHorizontalGroup(
            jpViewQLHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpViewQLHDLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jpViewQLHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(SanPhamTrongHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BangHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 1002, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpViewQLHDLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel92)))
                .addGap(15, 15, 15))
            .addGroup(jpViewQLHDLayout.createSequentialGroup()
                .addGroup(jpViewQLHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpViewQLHDLayout.createSequentialGroup()
                        .addGap(349, 349, 349)
                        .addComponent(jLabel20))
                    .addGroup(jpViewQLHDLayout.createSequentialGroup()
                        .addGap(359, 359, 359)
                        .addComponent(btnVeTrangDau, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLui, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblSoTrang)
                        .addGap(18, 18, 18)
                        .addComponent(btnTien, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnVeTrangCuoi, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpViewQLHDLayout.setVerticalGroup(
            jpViewQLHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLHDLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BangHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpViewQLHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnLui)
                    .addComponent(btnTien, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnVeTrangCuoi)
                    .addComponent(lblSoTrang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVeTrangDau, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel92)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(SanPhamTrongHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpViewQLBH2.setBackground(new java.awt.Color(255, 255, 255));
        jpViewQLBH2.setOpaque(false);

        jp_V_ThemMoiHD.setBackground(new java.awt.Color(255, 255, 255));

        jp_V_HoaDon.setBackground(new java.awt.Color(255, 255, 255));
        jp_V_HoaDon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 255, 51), 2));

        jLabel22.setText("Tên khách hàng:");

        jLabel23.setText("SĐT:");

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setText("Giỏ hàng:");

        tblDSSPtrongHD.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã SPCT", "Tên SP", "Loại SP", "Màu", "Size", "Style", "SL", "Giá"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDSSPtrongHD.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDSSPtrongHD.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblDSSPtrongHD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDSSPtrongHDMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblDSSPtrongHD);

        jLabel25.setText("Voucher:");

        txtTenKhachHang.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtSDTKhachHang.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtSDTKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSDTKhachHangActionPerformed(evt);
            }
        });

        cbbVouchers.setBackground(new java.awt.Color(255, 204, 255));

        jLabel26.setText("Phương thức thanh toán:");

        jLabel27.setText("Điểm dùng");

        txtSoDiemDaDung.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtSoDiemDaDung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoDiemDaDungActionPerformed(evt);
            }
        });
        txtSoDiemDaDung.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSoDiemDaDungKeyReleased(evt);
            }
        });

        jLabel28.setText("Nhân viên giao dịch:");

        lblNhanVienBan.setText("Dương Minh Hà");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel32.setText("Tổng tiền:");

        lblTongTienThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongTienThanhToan.setText("0 VNĐ");

        btnThemKHtuHD.setBackground(new java.awt.Color(51, 153, 255));
        btnThemKHtuHD.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemKHtuHD.setForeground(new java.awt.Color(255, 255, 255));
        btnThemKHtuHD.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add.png")); // NOI18N
        btnThemKHtuHD.setText("Thêm KH");
        btnThemKHtuHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemKHtuHDActionPerformed(evt);
            }
        });

        jLabel30.setText("VAT");

        lblVAT.setText("8%");

        jLabel31.setText("Điểm còn");

        lblDiemConLai.setText("0");

        buttonGroup2.add(rdoTienMat);
        rdoTienMat.setSelected(true);
        rdoTienMat.setText("Tiền mặt");

        buttonGroup2.add(rdoChuyenKhoan);
        rdoChuyenKhoan.setText("Chuyển khoản");

        btnLuuTruHD.setBackground(new java.awt.Color(255, 51, 51));
        btnLuuTruHD.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuuTruHD.setForeground(new java.awt.Color(255, 255, 255));
        btnLuuTruHD.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru.png")); // NOI18N
        btnLuuTruHD.setText("Lưu trữ HĐ");
        btnLuuTruHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuTruHDActionPerformed(evt);
            }
        });

        btnThanhToan.setBackground(new java.awt.Color(51, 153, 255));
        btnThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThanhToan.setForeground(new java.awt.Color(255, 255, 255));
        btnThanhToan.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\thanhToan.png")); // NOI18N
        btnThanhToan.setText("Thanh Toán");
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });

        btnXoaKhoiGioHang.setBackground(new java.awt.Color(255, 51, 51));
        btnXoaKhoiGioHang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXoaKhoiGioHang.setForeground(new java.awt.Color(255, 255, 255));
        btnXoaKhoiGioHang.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\xoa.png")); // NOI18N
        btnXoaKhoiGioHang.setText("Xóa");
        btnXoaKhoiGioHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaKhoiGioHangActionPerformed(evt);
            }
        });

        btnCapNhatGioHang.setBackground(new java.awt.Color(51, 153, 255));
        btnCapNhatGioHang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCapNhatGioHang.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhatGioHang.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\sua.png")); // NOI18N
        btnCapNhatGioHang.setText("Cập nhật");
        btnCapNhatGioHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatGioHangActionPerformed(evt);
            }
        });

        txtNhapSoLuong1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel15.setText("số lượng");

        btnKhachLe.setBackground(new java.awt.Color(51, 153, 255));
        btnKhachLe.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnKhachLe.setForeground(new java.awt.Color(255, 255, 255));
        btnKhachLe.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\Khachle.png")); // NOI18N
        btnKhachLe.setText("Khách lẻ");
        btnKhachLe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKhachLeActionPerformed(evt);
            }
        });

        jLabel90.setText("Tiền trước khi giảm:");

        lblTienTruocKhiGiam.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTienTruocKhiGiam.setText("0 VNĐ");

        javax.swing.GroupLayout jp_V_HoaDonLayout = new javax.swing.GroupLayout(jp_V_HoaDon);
        jp_V_HoaDon.setLayout(jp_V_HoaDonLayout);
        jp_V_HoaDonLayout.setHorizontalGroup(
            jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_V_HoaDonLayout.createSequentialGroup()
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                        .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                                    .addGap(45, 45, 45)
                                    .addComponent(jLabel15))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_V_HoaDonLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel23)))
                            .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel22)))
                        .addGap(33, 33, 33)
                        .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNhapSoLuong1)
                            .addComponent(txtSDTKhachHang)
                            .addComponent(txtTenKhachHang, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(24, 24, 24))
                    .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCapNhatGioHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnXoaKhoiGioHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnKhachLe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThemKHtuHD, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(12, 12, 12))
            .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel30)
                    .addComponent(jLabel25)
                    .addComponent(jLabel31)
                    .addComponent(btnLuuTruHD, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32)
                    .addComponent(jLabel28)
                    .addComponent(jLabel90))
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_V_HoaDonLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49))
                    .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_V_HoaDonLayout.createSequentialGroup()
                                .addComponent(lblVAT)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                                .addComponent(lblNhanVienBan)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblTienTruocKhiGiam, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblTongTienThanhToan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                                        .addComponent(jLabel27)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtSoDiemDaDung, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(cbbVouchers, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jp_V_HoaDonLayout.createSequentialGroup()
                                            .addComponent(rdoTienMat, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(rdoChuyenKhoan, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(lblDiemConLai, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        jp_V_HoaDonLayout.setVerticalGroup(
            jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_V_HoaDonLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTenKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnKhachLe))
                .addGap(6, 6, 6)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtSDTKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThemKHtuHD))
                .addGap(25, 25, 25)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24)
                    .addComponent(btnCapNhatGioHang))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtNhapSoLuong1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoaKhoiGioHang))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(cbbVouchers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(rdoTienMat)
                    .addComponent(rdoChuyenKhoan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(lblDiemConLai)
                    .addComponent(jLabel27)
                    .addComponent(txtSoDiemDaDung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(lblVAT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(lblNhanVienBan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTienTruocKhiGiam)
                    .addComponent(jLabel90))
                .addGap(18, 18, 18)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTongTienThanhToan)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jp_V_HoaDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThanhToan)
                    .addComponent(btnLuuTruHD))
                .addGap(9, 9, 9))
        );

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(0, 0, 255));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("THÊM MỚI HÓA ĐƠN");

        jScrollPane6.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 255, 51), 2));

        tblDSSanPham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã SPCT", "Tên SP", "Loại SP", "Màu sắc", "Kích thước", "Phong cách", "Số lượng", "Giá bán"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDSSanPham.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDSSanPham.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblDSSanPham.setGridColor(new java.awt.Color(255, 255, 153));
        tblDSSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDSSanPhamMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tblDSSanPham);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 255, 0), 2));

        jLabel35.setText("Tên sản phẩm");

        txtTenSanPham.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtTenSanPham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenSanPhamActionPerformed(evt);
            }
        });

        jLabel36.setText("Nhập số lượng");

        txtNhapSoLuong.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        btnThemSPVaoHD.setBackground(new java.awt.Color(51, 153, 255));
        btnThemSPVaoHD.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemSPVaoHD.setForeground(new java.awt.Color(255, 255, 255));
        btnThemSPVaoHD.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnThemSPVaoHD.setText("Thêm");
        btnThemSPVaoHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemSPVaoHDActionPerformed(evt);
            }
        });

        jLabel95.setText("Loại sản phẩm");

        jLabel37.setText("Màu sắc");

        jLabel97.setText("Kích thước");

        jLabel144.setText("Phong cách");

        jLabel29.setText("Mã SPCT");

        txtMaSPCT.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        btnLamMoiTTSP.setBackground(new java.awt.Color(51, 153, 255));
        btnLamMoiTTSP.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLamMoiTTSP.setForeground(new java.awt.Color(255, 255, 255));
        btnLamMoiTTSP.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\reset.png")); // NOI18N
        btnLamMoiTTSP.setMaximumSize(new java.awt.Dimension(115, 31));
        btnLamMoiTTSP.setMinimumSize(new java.awt.Dimension(115, 31));
        btnLamMoiTTSP.setPreferredSize(new java.awt.Dimension(115, 31));
        btnLamMoiTTSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiTTSPActionPerformed(evt);
            }
        });

        txtMauSaccc.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtLoaiSPpp.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtKichThuoccc.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtPhongCachhh.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        camera.setBackground(new java.awt.Color(255, 255, 255));

        anhQRcode.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\QR.png")); // NOI18N

        javax.swing.GroupLayout cameraLayout = new javax.swing.GroupLayout(camera);
        camera.setLayout(cameraLayout);
        cameraLayout.setHorizontalGroup(
            cameraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cameraLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(anhQRcode)
                .addGap(21, 21, 21))
        );
        cameraLayout.setVerticalGroup(
            cameraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(anhQRcode, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, Short.MAX_VALUE)
        );

        btnQuetMaQRCodee.setBackground(new java.awt.Color(51, 153, 255));
        btnQuetMaQRCodee.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQuetMaQRCodee.setForeground(new java.awt.Color(255, 255, 255));
        btnQuetMaQRCodee.setText("Quét QR");
        btnQuetMaQRCodee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuetMaQRCodeeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel95)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel37))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtMauSaccc, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                            .addComponent(txtLoaiSPpp)
                            .addComponent(txtMaSPCT)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNhapSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(31, 31, 31)
                                    .addComponent(jLabel144))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel97)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel35)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPhongCachhh, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtKichThuoccc, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTenSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(camera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnThemSPVaoHD)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLamMoiTTSP, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                        .addComponent(btnQuetMaQRCodee, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29)
                            .addComponent(txtMaSPCT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35)
                            .addComponent(txtTenSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37)
                            .addComponent(jLabel97)
                            .addComponent(txtMauSaccc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtKichThuoccc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel95)
                            .addComponent(jLabel144)
                            .addComponent(txtLoaiSPpp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhongCachhh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(camera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLamMoiTTSP, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtNhapSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel36)
                                .addComponent(btnThemSPVaoHD, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnQuetMaQRCodee, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(51, 51, 255));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Hóa đơn");

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(51, 51, 255));
        jLabel33.setText("Danh sách sản phẩm");

        jScrollPane7.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 255, 51), 2));

        tblHoaDonCho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã HD", "Ngày tạo", "Tên KH", "Số điện thoại", "Mã SPCT", "Số lượng", "Tổng tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHoaDonCho.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblHoaDonCho.setGridColor(new java.awt.Color(255, 255, 255));
        tblHoaDonCho.setShowGrid(false);
        tblHoaDonCho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHoaDonChoMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(tblHoaDonCho);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(51, 51, 255));
        jLabel11.setText("Danh sách HDC");

        btnXoaHoaDonCho.setBackground(new java.awt.Color(255, 51, 51));
        btnXoaHoaDonCho.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXoaHoaDonCho.setForeground(new java.awt.Color(255, 255, 255));
        btnXoaHoaDonCho.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\xoa.png")); // NOI18N
        btnXoaHoaDonCho.setText("Xóa");
        btnXoaHoaDonCho.setPreferredSize(new java.awt.Dimension(80, 31));
        btnXoaHoaDonCho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaHoaDonChoActionPerformed(evt);
            }
        });

        txtMaCTSP.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtMaCTSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaCTSPActionPerformed(evt);
            }
        });
        txtMaCTSP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMaCTSPKeyReleased(evt);
            }
        });

        jLabel39.setText("Mã SPCT");

        btnTiepTucThanhToan.setBackground(new java.awt.Color(51, 153, 255));
        btnTiepTucThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTiepTucThanhToan.setForeground(new java.awt.Color(255, 255, 255));
        btnTiepTucThanhToan.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\thanhtoan1.png")); // NOI18N
        btnTiepTucThanhToan.setText("Tiếp tục");
        btnTiepTucThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTiepTucThanhToanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jp_V_ThemMoiHDLayout = new javax.swing.GroupLayout(jp_V_ThemMoiHD);
        jp_V_ThemMoiHD.setLayout(jp_V_ThemMoiHDLayout);
        jp_V_ThemMoiHDLayout.setHorizontalGroup(
            jp_V_ThemMoiHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_V_ThemMoiHDLayout.createSequentialGroup()
                .addGap(318, 318, 318)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(132, 132, 132)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(158, Short.MAX_VALUE))
            .addGroup(jp_V_ThemMoiHDLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jp_V_ThemMoiHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jp_V_ThemMoiHDLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtMaCTSP, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jp_V_ThemMoiHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane6)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel33)
                        .addGroup(jp_V_ThemMoiHDLayout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addGap(59, 59, 59)
                            .addComponent(btnXoaHoaDonCho, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnTiepTucThanhToan))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jp_V_HoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jp_V_ThemMoiHDLayout.setVerticalGroup(
            jp_V_ThemMoiHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jp_V_ThemMoiHDLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jp_V_ThemMoiHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jp_V_ThemMoiHDLayout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jp_V_ThemMoiHDLayout.createSequentialGroup()
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jp_V_ThemMoiHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jp_V_ThemMoiHDLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jp_V_ThemMoiHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaCTSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jp_V_ThemMoiHDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(btnXoaHoaDonCho, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTiepTucThanhToan))
                        .addGap(17, 17, 17)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jp_V_HoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout jpViewQLBH2Layout = new javax.swing.GroupLayout(jpViewQLBH2);
        jpViewQLBH2.setLayout(jpViewQLBH2Layout);
        jpViewQLBH2Layout.setHorizontalGroup(
            jpViewQLBH2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1045, Short.MAX_VALUE)
            .addGroup(jpViewQLBH2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jp_V_ThemMoiHD, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpViewQLBH2Layout.setVerticalGroup(
            jpViewQLBH2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 757, Short.MAX_VALUE)
            .addGroup(jpViewQLBH2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jp_V_ThemMoiHD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpViewQLDM.setBackground(new java.awt.Color(255, 255, 255));
        jpViewQLDM.setPreferredSize(new java.awt.Dimension(1139, 703));

        jLabel73.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(255, 0, 0));
        jLabel73.setText("Thông tin danh mục");

        tblDanhSachDM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "STT", "Mã danh mục", "Tên danh mục"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSachDM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDanhSachDMMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblDanhSachDM);

        jLabel75.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel75.setForeground(new java.awt.Color(255, 0, 0));
        jLabel75.setText("Danh sách danh mục hiện tại:");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        btnThemMoiDM.setBackground(new java.awt.Color(51, 102, 255));
        btnThemMoiDM.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemMoiDM.setForeground(new java.awt.Color(255, 255, 255));
        btnThemMoiDM.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnThemMoiDM.setText("Thêm");
        btnThemMoiDM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemMoiDMActionPerformed(evt);
            }
        });

        btnSuaDM.setBackground(new java.awt.Color(51, 102, 255));
        btnSuaDM.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSuaDM.setForeground(new java.awt.Color(255, 255, 255));
        btnSuaDM.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnSuaDM.setText("Sửa");
        btnSuaDM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaDMActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Mã danh mục");

        txtMaDM.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtMaDM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaDMActionPerformed(evt);
            }
        });

        jLabel74.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel74.setText("Tên danh mục");

        txtTenDM.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtTenDM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenDMActionPerformed(evt);
            }
        });

        btnLuu.setBackground(new java.awt.Color(51, 102, 255));
        btnLuu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuu.setForeground(new java.awt.Color(255, 255, 255));
        btnLuu.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLuu.setText("Lưu");
        btnLuu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLuuMouseClicked(evt);
            }
        });
        btnLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel74))
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTenDM, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaDM, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnThemMoiDM, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)
                        .addComponent(btnSuaDM, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(98, 98, 98))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(158, 158, 158)
                        .addComponent(btnLuu, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtMaDM, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnThemMoiDM)
                            .addComponent(btnSuaDM))))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel74)
                            .addComponent(txtTenDM, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnLuu)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jpViewQLDMLayout = new javax.swing.GroupLayout(jpViewQLDM);
        jpViewQLDM.setLayout(jpViewQLDMLayout);
        jpViewQLDMLayout.setHorizontalGroup(
            jpViewQLDMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLDMLayout.createSequentialGroup()
                .addGroup(jpViewQLDMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpViewQLDMLayout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addGroup(jpViewQLDMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel75)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 848, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jpViewQLDMLayout.createSequentialGroup()
                        .addGap(348, 348, 348)
                        .addComponent(jLabel73)))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        jpViewQLDMLayout.setVerticalGroup(
            jpViewQLDMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLDMLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel73)
                .addGap(30, 30, 30)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
                .addComponent(jLabel75)
                .addGap(29, 29, 29)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );

        jpViewQLTK.setBackground(new java.awt.Color(255, 255, 255));
        jpViewQLTK.setPreferredSize(new java.awt.Dimension(948, 600));

        tabQLNV.setBackground(new java.awt.Color(204, 255, 204));
        tabQLNV.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabQLNVMouseClicked(evt);
            }
        });

        jpThongTinCN.setBackground(new java.awt.Color(255, 255, 255));

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(102, 102, 102));
        jLabel21.setText("Thông tin cá nhân");

        jLabel12.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 255));
        jLabel12.setText("Họ và tên");

        txtHoTen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHoTenActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 51, 255));
        jLabel13.setText("Username");

        jLabel16.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 255));
        jLabel16.setText("Email");

        jLabel17.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 51, 255));
        jLabel17.setText("Số điện thoại");

        jLabel18.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(51, 51, 255));
        jLabel18.setText("Địa chỉ");

        jLabel19.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 255));
        jLabel19.setText("Chức vụ");

        jPanel1.setBackground(new java.awt.Color(255, 249, 255));

        btnCapNhatThongTinCN.setBackground(new java.awt.Color(255, 255, 51));
        btnCapNhatThongTinCN.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnCapNhatThongTinCN.setForeground(new java.awt.Color(51, 51, 255));
        btnCapNhatThongTinCN.setText("Cập nhật thông tin");
        btnCapNhatThongTinCN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatThongTinCNActionPerformed(evt);
            }
        });

        btnDoiMatKhau.setBackground(new java.awt.Color(255, 255, 51));
        btnDoiMatKhau.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        btnDoiMatKhau.setForeground(new java.awt.Color(51, 51, 255));
        btnDoiMatKhau.setText("Đổi mật khẩu");
        btnDoiMatKhau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoiMatKhauActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCapNhatThongTinCN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDoiMatKhau, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(btnCapNhatThongTinCN, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(btnDoiMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpThongTinCNLayout = new javax.swing.GroupLayout(jpThongTinCN);
        jpThongTinCN.setLayout(jpThongTinCNLayout);
        jpThongTinCNLayout.setHorizontalGroup(
            jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpThongTinCNLayout.createSequentialGroup()
                .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpThongTinCNLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19)
                            .addComponent(jLabel17)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpThongTinCNLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpThongTinCNLayout.createSequentialGroup()
                        .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtHoTen)
                            .addComponent(txtUsername)
                            .addComponent(txtEmail)
                            .addComponent(txtSdt)
                            .addComponent(txtDiaChi)
                            .addComponent(txtVaiTro))
                        .addGap(75, 75, 75))
                    .addGroup(jpThongTinCNLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpThongTinCNLayout.setVerticalGroup(
            jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpThongTinCNLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSdt, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(18, 18, 18)
                .addGroup(jpThongTinCNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addContainerGap(191, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpThongTinCNLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabQLNV.addTab("Thông tin tài khoản", jpThongTinCN);

        jpQLNV.setBackground(new java.awt.Color(255, 255, 255));
        jpQLNV.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jpQLNVMouseClicked(evt);
            }
        });

        ThongTinNhanVien.setBackground(new java.awt.Color(255, 255, 255));
        ThongTinNhanVien.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));

        jLabel63.setText("Họ và tên");

        txtHoTen1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtDiaChi1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel64.setText("Địa chỉ");

        jLabel65.setText("Email");

        txtEmail1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel66.setText("Số điện thoại");

        txtSDT.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel67.setText("Tên đăng nhập");

        txtTenDangNhap.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtTenDangNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenDangNhapActionPerformed(evt);
            }
        });

        jLabel69.setText("Ngày tạo");

        jLabel70.setText("Trạng thái");

        buttonGroup4.add(rdoDanglam);
        rdoDanglam.setSelected(true);
        rdoDanglam.setText("Đang làm");

        buttonGroup4.add(rdoNghilam);
        rdoNghilam.setText("Nghỉ làm");

        jLabel71.setText("Chức vụ");

        cbbVaiTro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Quản lý", "Nhân viên" }));
        cbbVaiTro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbVaiTroActionPerformed(evt);
            }
        });

        dateNgayTao.setDateFormatString("yyyy-MM-dd");

        lblMatKhau.setText("Mật khẩu");

        txtMatKhau.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtMatKhau.setEnabled(false);

        txtMaNhanVienn.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel42.setText("Mã NV");

        javax.swing.GroupLayout ThongTinNhanVienLayout = new javax.swing.GroupLayout(ThongTinNhanVien);
        ThongTinNhanVien.setLayout(ThongTinNhanVienLayout);
        ThongTinNhanVienLayout.setHorizontalGroup(
            ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ThongTinNhanVienLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblMatKhau)
                    .addComponent(jLabel70)
                    .addComponent(jLabel65)
                    .addComponent(jLabel64)
                    .addComponent(jLabel63)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(ThongTinNhanVienLayout.createSequentialGroup()
                        .addComponent(txtHoTen1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel67))
                    .addGroup(ThongTinNhanVienLayout.createSequentialGroup()
                        .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ThongTinNhanVienLayout.createSequentialGroup()
                                .addComponent(rdoDanglam)
                                .addGap(18, 18, 18)
                                .addComponent(rdoNghilam))
                            .addComponent(txtEmail1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDiaChi1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMaNhanVienn, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(84, 84, 84)
                        .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ThongTinNhanVienLayout.createSequentialGroup()
                                .addGap(88, 88, 88)
                                .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel69)
                                    .addComponent(jLabel66)))
                            .addComponent(jLabel71, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(dateNgayTao, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbbVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtTenDangNhap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(98, Short.MAX_VALUE))
        );
        ThongTinNhanVienLayout.setVerticalGroup(
            ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ThongTinNhanVienLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel66)
                    .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42)
                    .addComponent(txtMaNhanVienn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(txtTenDangNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHoTen1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63))
                .addGap(18, 18, 18)
                .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel71)
                    .addComponent(cbbVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiaChi1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64))
                .addGap(19, 19, 19)
                .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel69)
                        .addComponent(dateNgayTao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtEmail1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel65)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMatKhau)
                    .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ThongTinNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(rdoDanglam)
                    .addComponent(rdoNghilam))
                .addGap(17, 17, 17))
        );

        jLabel62.setBackground(new java.awt.Color(255, 0, 0));
        jLabel62.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(255, 0, 0));
        jLabel62.setText("Thông tin nhân viên");

        jLabel72.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(255, 0, 0));
        jLabel72.setText("Danh sách nhân viên");

        DanhSachNhanVien.setBackground(new java.awt.Color(255, 255, 255));
        DanhSachNhanVien.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));

        tblNhanVien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Người dùng", "Họ tên", "Tên đăng nhập", "Email", "SĐT", "Địa chỉ", "Chức vụ", "Ngày tạo", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNhanVienMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblNhanVien);

        javax.swing.GroupLayout DanhSachNhanVienLayout = new javax.swing.GroupLayout(DanhSachNhanVien);
        DanhSachNhanVien.setLayout(DanhSachNhanVienLayout);
        DanhSachNhanVienLayout.setHorizontalGroup(
            DanhSachNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DanhSachNhanVienLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 992, Short.MAX_VALUE)
                .addContainerGap())
        );
        DanhSachNhanVienLayout.setVerticalGroup(
            DanhSachNhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DanhSachNhanVienLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        CRUD_NhanVien.setBackground(new java.awt.Color(255, 255, 255));
        CRUD_NhanVien.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));

        btnThemNhanVien.setBackground(new java.awt.Color(51, 102, 255));
        btnThemNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemNhanVien.setForeground(new java.awt.Color(255, 255, 255));
        btnThemNhanVien.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnThemNhanVien.setText("Thêm mới");
        btnThemNhanVien.setBorder(null);
        btnThemNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemNhanVienActionPerformed(evt);
            }
        });

        btnCapNhatNhanVien.setBackground(new java.awt.Color(51, 102, 255));
        btnCapNhatNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCapNhatNhanVien.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhatNhanVien.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnCapNhatNhanVien.setText("Cập nhật");
        btnCapNhatNhanVien.setBorder(null);
        btnCapNhatNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatNhanVienActionPerformed(evt);
            }
        });

        btnLamMoi.setBackground(new java.awt.Color(51, 102, 255));
        btnLamMoi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLamMoi.setForeground(new java.awt.Color(255, 255, 255));
        btnLamMoi.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\lammoiiii.png")); // NOI18N
        btnLamMoi.setText("Làm mới");
        btnLamMoi.setBorder(null);
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        btnSave.setBackground(new java.awt.Color(51, 102, 255));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnSave.setText("Lưu");
        btnSave.setBorder(null);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout CRUD_NhanVienLayout = new javax.swing.GroupLayout(CRUD_NhanVien);
        CRUD_NhanVien.setLayout(CRUD_NhanVienLayout);
        CRUD_NhanVienLayout.setHorizontalGroup(
            CRUD_NhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CRUD_NhanVienLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(CRUD_NhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCapNhatNhanVien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnThemNhanVien, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        CRUD_NhanVienLayout.setVerticalGroup(
            CRUD_NhanVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CRUD_NhanVienLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(btnThemNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCapNhatNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        jLabel88.setText("Tìm kiếm");

        txtHoTen2.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtHoTen2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtHoTen2KeyReleased(evt);
            }
        });

        lblSoTrang1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSoTrang1.setText("0/1");

        btnTienTrang.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veSau.png")); // NOI18N
        btnTienTrang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTienTrangActionPerformed(evt);
            }
        });

        btnLuiLai.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veTruoc.png")); // NOI18N
        btnLuiLai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuiLaiActionPerformed(evt);
            }
        });

        btnVeCuoii.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veCuoi.png")); // NOI18N
        btnVeCuoii.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVeCuoiiActionPerformed(evt);
            }
        });

        btnVeDauu.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veDau.png")); // NOI18N
        btnVeDauu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVeDauuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpQLNVLayout = new javax.swing.GroupLayout(jpQLNV);
        jpQLNV.setLayout(jpQLNVLayout);
        jpQLNVLayout.setHorizontalGroup(
            jpQLNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpQLNVLayout.createSequentialGroup()
                .addGroup(jpQLNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpQLNVLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jpQLNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel72)
                            .addComponent(DanhSachNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jpQLNVLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jpQLNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel62)
                            .addGroup(jpQLNVLayout.createSequentialGroup()
                                .addComponent(ThongTinNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(CRUD_NhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jpQLNVLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel88)
                        .addGap(18, 18, 18)
                        .addComponent(txtHoTen2, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpQLNVLayout.createSequentialGroup()
                        .addGap(319, 319, 319)
                        .addComponent(btnVeDauu, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLuiLai, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(lblSoTrang1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnTienTrang, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnVeCuoii, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpQLNVLayout.setVerticalGroup(
            jpQLNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpQLNVLayout.createSequentialGroup()
                .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jpQLNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(CRUD_NhanVien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ThongTinNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 230, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel72)
                .addGap(18, 18, 18)
                .addGroup(jpQLNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel88)
                    .addComponent(txtHoTen2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(DanhSachNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jpQLNVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLuiLai, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVeCuoii, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVeDauu, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnTienTrang, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblSoTrang1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        tabQLNV.addTab("Quản lý nhân viên", jpQLNV);

        javax.swing.GroupLayout jpViewQLTKLayout = new javax.swing.GroupLayout(jpViewQLTK);
        jpViewQLTK.setLayout(jpViewQLTKLayout);
        jpViewQLTKLayout.setHorizontalGroup(
            jpViewQLTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabQLNV)
        );
        jpViewQLTKLayout.setVerticalGroup(
            jpViewQLTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabQLNV)
        );

        jpViewDoiMK.setBackground(new java.awt.Color(255, 255, 255));
        jpViewDoiMK.setPreferredSize(new java.awt.Dimension(948, 600));

        formDoiMK.setBackground(new java.awt.Color(255, 255, 255));
        formDoiMK.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 3));

        jLabel57.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel57.setText("Username");

        jLabel58.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel58.setText("Password");

        jLabel59.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel59.setText("NewPassword");

        jLabel60.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel60.setText(" ConfirmPassword");

        btnXacNhanDMK.setBackground(new java.awt.Color(51, 102, 255));
        btnXacNhanDMK.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXacNhanDMK.setForeground(new java.awt.Color(255, 255, 255));
        btnXacNhanDMK.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnXacNhanDMK.setText("Lưu");
        btnXacNhanDMK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXacNhanDMKActionPerformed(evt);
            }
        });

        jLabel61.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(0, 0, 255));
        jLabel61.setText("Đổi mật khẩu");

        btnQuaylai.setBackground(new java.awt.Color(51, 102, 255));
        btnQuaylai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQuaylai.setForeground(new java.awt.Color(255, 255, 255));
        btnQuaylai.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\troLai.png")); // NOI18N
        btnQuaylai.setText("Quay lại");
        btnQuaylai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnQuaylaiMouseClicked(evt);
            }
        });
        btnQuaylai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuaylaiActionPerformed(evt);
            }
        });

        btnEyeMK.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\Eye.png")); // NOI18N
        btnEyeMK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEyeMKActionPerformed(evt);
            }
        });

        btnEyeMKMoi.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\Eye.png")); // NOI18N
        btnEyeMKMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEyeMKMoiActionPerformed(evt);
            }
        });

        btnEyeXacNhan.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\Eye.png")); // NOI18N
        btnEyeXacNhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEyeXacNhanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout formDoiMKLayout = new javax.swing.GroupLayout(formDoiMK);
        formDoiMK.setLayout(formDoiMKLayout);
        formDoiMKLayout.setHorizontalGroup(
            formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formDoiMKLayout.createSequentialGroup()
                .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formDoiMKLayout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addComponent(btnXacNhanDMK, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80)
                        .addComponent(btnQuaylai)
                        .addGap(0, 35, Short.MAX_VALUE))
                    .addGroup(formDoiMKLayout.createSequentialGroup()
                        .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel59)
                            .addComponent(jLabel60)
                            .addComponent(jLabel58)
                            .addComponent(jLabel57))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtConfirmPassword)
                            .addComponent(txtNewPassword)
                            .addComponent(txtPassword)
                            .addComponent(txtUsername1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEyeMK, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEyeMKMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEyeXacNhan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(58, 58, 58))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formDoiMKLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel61)
                .addGap(121, 121, 121))
        );
        formDoiMKLayout.setVerticalGroup(
            formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formDoiMKLayout.createSequentialGroup()
                .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formDoiMKLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel61)
                        .addGap(62, 62, 62)
                        .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtUsername1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(formDoiMKLayout.createSequentialGroup()
                                .addGap(67, 67, 67)
                                .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(30, 30, 30)
                        .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formDoiMKLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEyeMK, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)
                        .addComponent(btnEyeMKMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)))
                .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(formDoiMKLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(btnEyeXacNhan, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(59, 59, 59)
                .addGroup(formDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnQuaylai, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXacNhanDMK, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(74, 74, 74))
        );

        javax.swing.GroupLayout jpViewDoiMKLayout = new javax.swing.GroupLayout(jpViewDoiMK);
        jpViewDoiMK.setLayout(jpViewDoiMKLayout);
        jpViewDoiMKLayout.setHorizontalGroup(
            jpViewDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewDoiMKLayout.createSequentialGroup()
                .addGap(228, 228, 228)
                .addComponent(formDoiMK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(290, Short.MAX_VALUE))
        );
        jpViewDoiMKLayout.setVerticalGroup(
            jpViewDoiMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpViewDoiMKLayout.createSequentialGroup()
                .addContainerGap(127, Short.MAX_VALUE)
                .addComponent(formDoiMK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82))
        );

        jpViewQLSPP.setBackground(new java.awt.Color(255, 255, 255));

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(51, 102, 255));
        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setText("QUẢN LÝ SẢN PHẨM");

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel161.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel161.setForeground(new java.awt.Color(51, 102, 255));
        jLabel161.setText("Danh sách sản phẩm");

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 4, true));

        jLabel162.setText("Mã Sản phẩm:");

        jLabel94.setText("Tên sản phẩm:");

        jLabel108.setText("Danh mục:");

        jLabel163.setText("Nhà CC:");

        jLabel8.setText("Trạng thái");

        txtTenSP.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtMaSanPham.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        rdoRun.setSelected(true);
        rdoRun.setText("Đang kinh doanh");

        rdoStop.setText("Ngừng kinh doanh");

        jLabel109.setText("Mô tả:");

        btnAddProduct.setBackground(new java.awt.Color(51, 102, 255));
        btnAddProduct.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddProduct.setForeground(new java.awt.Color(255, 255, 255));
        btnAddProduct.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnAddProduct.setText("Thêm");
        btnAddProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddProductActionPerformed(evt);
            }
        });

        btnUpdateProduct.setBackground(new java.awt.Color(51, 102, 255));
        btnUpdateProduct.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnUpdateProduct.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdateProduct.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnUpdateProduct.setText("Cập nhật");
        btnUpdateProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateProductActionPerformed(evt);
            }
        });

        cbbDanhMuc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbbNhaCC.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtMotaSPP.setColumns(20);
        txtMotaSPP.setRows(5);
        jScrollPane15.setViewportView(txtMotaSPP);

        btnLuu2.setBackground(new java.awt.Color(51, 102, 255));
        btnLuu2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuu2.setForeground(new java.awt.Color(255, 255, 255));
        btnLuu2.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLuu2.setText("Lưu ");
        btnLuu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuu2ActionPerformed(evt);
            }
        });

        btnAddProductDetails.setBackground(new java.awt.Color(51, 102, 255));
        btnAddProductDetails.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddProductDetails.setForeground(new java.awt.Color(255, 255, 255));
        btnAddProductDetails.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\xemCTSP.png")); // NOI18N
        btnAddProductDetails.setText("Chi tiết SP");
        btnAddProductDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddProductDetailsActionPerformed(evt);
            }
        });

        btnNhapTuExcel.setBackground(new java.awt.Color(51, 102, 255));
        btnNhapTuExcel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNhapTuExcel.setForeground(new java.awt.Color(255, 255, 255));
        btnNhapTuExcel.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\xuatEX.png")); // NOI18N
        btnNhapTuExcel.setText("Nhập SP");
        btnNhapTuExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNhapTuExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel162, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel94))
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTenSP, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMaSanPham, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                        .addComponent(rdoRun)
                        .addGap(18, 18, 18)
                        .addComponent(rdoStop, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)))
                .addGap(60, 60, 60)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel108, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel163)
                    .addComponent(jLabel109, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbbDanhMuc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbbNhaCC, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addGap(69, 69, 69)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnNhapTuExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLuu2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddProductDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel108)
                            .addComponent(cbbDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel162))
                        .addGap(46, 46, 46)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel94)
                            .addComponent(jLabel163)
                            .addComponent(txtTenSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbbNhaCC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAddProduct)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNhapTuExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnUpdateProduct)))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel109)
                        .addGap(35, 35, 35))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(btnLuu2)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(rdoRun)
                                    .addComponent(rdoStop))
                                .addGap(17, 17, 17))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAddProductDetails)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 4, true));

        tblHienThiKetQua.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã SP", "Tên SP", "Danh mục", "Số lượng", "Mô tả", "Nhà cung cấp", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHienThiKetQua.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHienThiKetQuaMouseClicked(evt);
            }
        });
        jScrollPane13.setViewportView(tblHienThiKetQua);

        cbbLocTheoDM.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel164.setText("Danh mục:");

        jLabel87.setText("Tìm kiếm:");

        txtTimKiemTheoMa.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtTimKiemTheoMa.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtTimKiemTheoMaCaretUpdate(evt);
            }
        });
        txtTimKiemTheoMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimKiemTheoMaActionPerformed(evt);
            }
        });
        txtTimKiemTheoMa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTimKiemTheoMaKeyPressed(evt);
            }
        });

        jLabel165.setText("Trạng thái:");

        cbbTrangThaiSP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Đang kinh doanh", "Ngừng kinh doanh" }));

        jLabel110.setText("Nhà CC:");

        btnHoanTac1.setBackground(new java.awt.Color(51, 102, 255));
        btnHoanTac1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHoanTac1.setForeground(new java.awt.Color(255, 255, 255));
        btnHoanTac1.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\xemCTSP.png")); // NOI18N
        btnHoanTac1.setText("Xem tất cả");
        btnHoanTac1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoanTac1ActionPerformed(evt);
            }
        });

        cbbLocNhaCC.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnExcel.setBackground(new java.awt.Color(51, 102, 255));
        btnExcel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExcel.setForeground(new java.awt.Color(255, 255, 255));
        btnExcel.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\xuatEX.png")); // NOI18N
        btnExcel.setText("Xuất Excel");
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });

        btnPreviousAll.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veDau.png")); // NOI18N
        btnPreviousAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousAllActionPerformed(evt);
            }
        });

        btnPreviou.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veTruoc.png")); // NOI18N
        btnPreviou.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviouActionPerformed(evt);
            }
        });

        btnNextt.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veSau.png")); // NOI18N
        btnNextt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNexttActionPerformed(evt);
            }
        });

        btnNexttALL.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veCuoi.png")); // NOI18N
        btnNexttALL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNexttALLActionPerformed(evt);
            }
        });

        lblPages.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPages.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPages.setText("1/2");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPane13)
                        .addContainerGap())
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel87)
                                .addGap(23, 23, 23)
                                .addComponent(txtTimKiemTheoMa))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel164)
                                .addGap(18, 18, 18)
                                .addComponent(cbbLocTheoDM, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel165)
                        .addGap(18, 18, 18)
                        .addComponent(cbbTrangThaiSP, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jLabel110)
                        .addGap(18, 18, 18)
                        .addComponent(cbbLocNhaCC, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnHoanTac1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(btnExcel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(28, 28, 28))))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(326, 326, 326)
                .addComponent(btnPreviousAll, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnPreviou, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPages, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnNextt, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnNexttALL, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel87)
                        .addComponent(txtTimKiemTheoMa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnExcel))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbbTrangThaiSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel110)
                    .addComponent(btnHoanTac1)
                    .addComponent(jLabel164)
                    .addComponent(jLabel165)
                    .addComponent(cbbLocTheoDM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbbLocNhaCC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNextt, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblPages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPreviou, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnNexttALL, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnPreviousAll))
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel161)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel161)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpViewQLSPPLayout = new javax.swing.GroupLayout(jpViewQLSPP);
        jpViewQLSPP.setLayout(jpViewQLSPPLayout);
        jpViewQLSPPLayout.setHorizontalGroup(
            jpViewQLSPPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLSPPLayout.createSequentialGroup()
                .addGap(336, 336, 336)
                .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(323, 323, 323))
            .addGroup(jpViewQLSPPLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpViewQLSPPLayout.setVerticalGroup(
            jpViewQLSPPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLSPPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpViewQLSPP2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel111.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel111.setForeground(new java.awt.Color(51, 102, 255));
        jLabel111.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel111.setText("QUẢN LÝ CHI TIẾT SẢN PHẨM");

        btnBackProducts.setBackground(new java.awt.Color(51, 102, 255));
        btnBackProducts.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBackProducts.setForeground(new java.awt.Color(255, 255, 255));
        btnBackProducts.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\troLai.png")); // NOI18N
        btnBackProducts.setText("Quay lại");
        btnBackProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackProductsActionPerformed(evt);
            }
        });

        jPanel26.setBackground(new java.awt.Color(255, 255, 255));
        jPanel26.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 4, true));

        jLabel93.setText("Mã SPCT:");

        txtMaSPCT1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel54.setText("Mã SP:");

        jLabel118.setText("Màu sắc:");

        cbbColor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel119.setText("Kích thước:");

        cbbSizes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel120.setText("Phong cách:");

        cbbStyle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel121.setText("Số lượng:");

        txtQuantity.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel122.setText("Giá:");

        txtPrice.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        btnAddChiTietSP.setBackground(new java.awt.Color(51, 102, 255));
        btnAddChiTietSP.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddChiTietSP.setForeground(new java.awt.Color(255, 255, 255));
        btnAddChiTietSP.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnAddChiTietSP.setText("Thêm");
        btnAddChiTietSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddChiTietSPActionPerformed(evt);
            }
        });

        btnCapNhat.setBackground(new java.awt.Color(51, 102, 255));
        btnCapNhat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCapNhat.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhat.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnCapNhat.setText("Cập nhật");
        btnCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatActionPerformed(evt);
            }
        });

        btnLuuSPCT.setBackground(new java.awt.Color(51, 102, 255));
        btnLuuSPCT.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuuSPCT.setForeground(new java.awt.Color(255, 255, 255));
        btnLuuSPCT.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLuuSPCT.setText("Lưu");
        btnLuuSPCT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuSPCTActionPerformed(evt);
            }
        });

        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText("Ảnh");
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });

        pnWebCam.setBackground(new java.awt.Color(255, 255, 255));
        pnWebCam.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnWebCam.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\QRcodenho.png")); // NOI18N
        pnWebCam.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 51, 51), 1, true));

        txtMaSP.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        btnNhapSPCTExcel.setBackground(new java.awt.Color(51, 102, 255));
        btnNhapSPCTExcel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNhapSPCTExcel.setForeground(new java.awt.Color(255, 255, 255));
        btnNhapSPCTExcel.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\xuatEX.png")); // NOI18N
        btnNhapSPCTExcel.setText("Nhập SPCT");
        btnNhapSPCTExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNhapSPCTExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel54)
                            .addComponent(jLabel118)
                            .addComponent(jLabel93))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtMaSP, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbbColor, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMaSPCT1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel26Layout.createSequentialGroup()
                                .addComponent(jLabel120)
                                .addGap(0, 22, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel121, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel122, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbbStyle, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel119)
                        .addGap(18, 18, 18)
                        .addComponent(cbbSizes, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnWebCam, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnNhapSPCTExcel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCapNhat, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLuuSPCT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddChiTietSP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddChiTietSP)
                            .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel93)
                                .addComponent(txtMaSPCT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel120)
                                .addComponent(cbbStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel26Layout.createSequentialGroup()
                                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel121)
                                    .addComponent(txtMaSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel54))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel118)
                                        .addComponent(cbbColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel122))
                                        .addGap(3, 3, 3)))
                                .addGap(22, 22, 22)
                                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel119)
                                    .addComponent(cbbSizes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 11, Short.MAX_VALUE))
                            .addGroup(jPanel26Layout.createSequentialGroup()
                                .addComponent(btnNhapSPCTExcel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCapNhat)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLuuSPCT))))
                    .addComponent(pnWebCam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)))
                .addContainerGap())
        );

        jPanel31.setBackground(new java.awt.Color(255, 255, 255));
        jPanel31.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 4, true));

        jLabel112.setText("Tìm kiếm:");

        txtSearhProductDetail.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtSearhProductDetail.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSearhProductDetailCaretUpdate(evt);
            }
        });

        cbbMauSacSPCT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel116.setText("Màu sắc:");

        jLabel114.setText("Loại SP:");

        jLabel115.setText("Kích thước:");

        cbbKichThuocSPCT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbbLoaiSPCT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel117.setText("Phong cách:");

        cbbStyles.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnXemDSSPCT.setBackground(new java.awt.Color(51, 102, 255));
        btnXemDSSPCT.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXemDSSPCT.setForeground(new java.awt.Color(255, 255, 255));
        btnXemDSSPCT.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\xemCTSP.png")); // NOI18N
        btnXemDSSPCT.setText("Xem tất cả ");
        btnXemDSSPCT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXemDSSPCTActionPerformed(evt);
            }
        });

        btnQRcode.setBackground(new java.awt.Color(51, 102, 255));
        btnQRcode.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQRcode.setForeground(new java.awt.Color(255, 255, 255));
        btnQRcode.setText("Quét QR");
        btnQRcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQRcodeActionPerformed(evt);
            }
        });

        tblSPCT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã SPCT", "Mã SP", "Tên SP", "Danh mục", "Màu sắc", "Kích thước", "Image_Url", "Phong cách", "Số lượng", "Giá"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSPCT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSPCTMouseClicked(evt);
            }
        });
        jScrollPane16.setViewportView(tblSPCT);

        jLabel168.setText("Số lượng:");

        jLabel169.setText("Giá bán:");

        cbbSoLuong.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbbSoLuong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbSoLuongActionPerformed(evt);
            }
        });

        cbbGiaBan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnExcelProductDetail.setBackground(new java.awt.Color(51, 102, 255));
        btnExcelProductDetail.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExcelProductDetail.setForeground(new java.awt.Color(255, 255, 255));
        btnExcelProductDetail.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\xuatEX.png")); // NOI18N
        btnExcelProductDetail.setText("Xuất Excel");
        btnExcelProductDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelProductDetailActionPerformed(evt);
            }
        });

        btnDowloadQr.setBackground(new java.awt.Color(51, 102, 255));
        btnDowloadQr.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDowloadQr.setForeground(new java.awt.Color(255, 255, 255));
        btnDowloadQr.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru.png")); // NOI18N
        btnDowloadQr.setText("QR Code");
        btnDowloadQr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDowloadQrActionPerformed(evt);
            }
        });

        btnPreviouAll.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veDau.png")); // NOI18N
        btnPreviouAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviouAllActionPerformed(evt);
            }
        });

        btnPrevious.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veTruoc.png")); // NOI18N
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });

        btnNext.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veSau.png")); // NOI18N
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnNextAll.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veCuoi.png")); // NOI18N
        btnNextAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextAllActionPerformed(evt);
            }
        });

        lblPage.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPage.setText("0/7");

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGap(346, 346, 346)
                .addComponent(btnPreviouAll, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPage, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnNextAll, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 980, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 15, Short.MAX_VALUE))
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel117)
                            .addComponent(jLabel114, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel112, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cbbLoaiSPCT, javax.swing.GroupLayout.Alignment.LEADING, 0, 152, Short.MAX_VALUE)
                            .addComponent(txtSearhProductDetail, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbbStyles, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel31Layout.createSequentialGroup()
                                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel31Layout.createSequentialGroup()
                                        .addGap(43, 43, 43)
                                        .addComponent(jLabel115))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel31Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel116)))
                                .addGap(25, 25, 25)
                                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cbbKichThuocSPCT, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbbMauSacSPCT, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(47, 47, 47)
                                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel168, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel169, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cbbSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbbGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel31Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(btnQRcode, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnExcelProductDetail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                .addComponent(btnDowloadQr, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnXemDSSPCT))
                        .addGap(14, 14, 14))))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel112)
                        .addComponent(txtSearhProductDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnQRcode, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDowloadQr)))
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel114)
                            .addComponent(cbbLoaiSPCT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel115)
                            .addComponent(cbbKichThuocSPCT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbbSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel168)
                            .addComponent(btnExcelProductDetail))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbbMauSacSPCT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel116)
                            .addComponent(cbbStyles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel117)
                            .addComponent(jLabel169)
                            .addComponent(cbbGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(btnXemDSSPCT)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPreviouAll)
                    .addComponent(btnPrevious)
                    .addComponent(lblPage, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNext)
                    .addComponent(btnNextAll))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel52.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(51, 102, 255));
        jLabel52.setText("Danh sách sản phẩm chi tiết:");

        btnThemMoiThuocTinh.setBackground(new java.awt.Color(51, 102, 255));
        btnThemMoiThuocTinh.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemMoiThuocTinh.setForeground(new java.awt.Color(255, 255, 255));
        btnThemMoiThuocTinh.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnThemMoiThuocTinh.setText("Thuộc tính");
        btnThemMoiThuocTinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemMoiThuocTinhActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpViewQLSPP2Layout = new javax.swing.GroupLayout(jpViewQLSPP2);
        jpViewQLSPP2.setLayout(jpViewQLSPP2Layout);
        jpViewQLSPP2Layout.setHorizontalGroup(
            jpViewQLSPP2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLSPP2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpViewQLSPP2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jpViewQLSPP2Layout.createSequentialGroup()
                        .addComponent(btnBackProducts)
                        .addGap(287, 287, 287)
                        .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpViewQLSPP2Layout.createSequentialGroup()
                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnThemMoiThuocTinh)
                        .addGap(21, 21, 21))
                    .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jpViewQLSPP2Layout.setVerticalGroup(
            jpViewQLSPP2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLSPP2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpViewQLSPP2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBackProducts)
                    .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpViewQLSPP2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(btnThemMoiThuocTinh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpViewQLSPP3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel124.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel124.setForeground(new java.awt.Color(0, 102, 255));
        jLabel124.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel124.setText("QUẢN LÝ THUỘC TÍNH");

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));

        tblMauSac.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã màu", "Tên màu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMauSac.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMauSacMouseClicked(evt);
            }
        });
        jScrollPane17.setViewportView(tblMauSac);

        jLabel125.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel125.setForeground(new java.awt.Color(255, 102, 102));
        jLabel125.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel125.setText("Quản lý màu sắc");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel125, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addComponent(jScrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel125, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84)
                .addComponent(jScrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));
        jPanel18.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));

        jLabel126.setText("Mã màu:");

        jLabel127.setText("Tên màu sắc:");

        jLabel128.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel128.setForeground(new java.awt.Color(0, 51, 255));
        jLabel128.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel128.setText("Thêm mới màu sắc");

        txtMaMau.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtTenMau.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        btnThemMoi.setBackground(new java.awt.Color(51, 102, 255));
        btnThemMoi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemMoi.setForeground(new java.awt.Color(255, 255, 255));
        btnThemMoi.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnThemMoi.setText("Thêm");
        btnThemMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemMoiActionPerformed(evt);
            }
        });

        btnCapNhatMS.setBackground(new java.awt.Color(51, 102, 255));
        btnCapNhatMS.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCapNhatMS.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhatMS.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnCapNhatMS.setText("Cập nhật");
        btnCapNhatMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatMSActionPerformed(evt);
            }
        });

        btnLuuColor.setBackground(new java.awt.Color(51, 102, 255));
        btnLuuColor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuuColor.setForeground(new java.awt.Color(255, 255, 255));
        btnLuuColor.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLuuColor.setText("Lưu");
        btnLuuColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuColorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jLabel126)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtMaMau, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addComponent(jLabel127)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtTenMau))))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jLabel128, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLuuColor, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCapNhatMS, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThemMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(72, 72, 72))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel128)
                .addGap(49, 49, 49)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel126))
                .addGap(46, 46, 46)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel127)
                    .addComponent(txtTenMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addComponent(btnThemMoi)
                .addGap(33, 33, 33)
                .addComponent(btnCapNhatMS)
                .addGap(33, 33, 33)
                .addComponent(btnLuuColor)
                .addContainerGap(59, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(34, 34, 34))
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(138, 138, 138)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tbpThuocTinh.addTab("Màu sắc", jPanel13);

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));

        tblKichThuoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã kích thước", "Tên kích thước", "Mô tả"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblKichThuoc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKichThuocMouseClicked(evt);
            }
        });
        jScrollPane18.setViewportView(tblKichThuoc);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(jScrollPane18, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        jPanel22.setBackground(new java.awt.Color(255, 255, 255));

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));

        jLabel129.setText("Mã KT:");

        jLabel130.setText("Tên KT");

        jLabel131.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel131.setForeground(new java.awt.Color(0, 51, 255));
        jLabel131.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel131.setText("Thêm mới kích thước");

        txtMaKT.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtTenKichThuoc.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        btnThemMoiKT.setBackground(new java.awt.Color(51, 102, 255));
        btnThemMoiKT.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemMoiKT.setForeground(new java.awt.Color(255, 255, 255));
        btnThemMoiKT.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnThemMoiKT.setText("Thêm");
        btnThemMoiKT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemMoiKTActionPerformed(evt);
            }
        });

        btnCapNhatKT.setBackground(new java.awt.Color(51, 102, 255));
        btnCapNhatKT.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCapNhatKT.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhatKT.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnCapNhatKT.setText("Cập nhật");
        btnCapNhatKT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatKTActionPerformed(evt);
            }
        });

        btnLuuSize.setBackground(new java.awt.Color(51, 102, 255));
        btnLuuSize.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuuSize.setForeground(new java.awt.Color(255, 255, 255));
        btnLuuSize.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLuuSize.setText("Lưu");
        btnLuuSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuSizeActionPerformed(evt);
            }
        });

        jLabel166.setText("Mô tả:");

        txtMoTaSize.setColumns(20);
        txtMoTaSize.setRows(5);
        jScrollPane21.setViewportView(txtMoTaSize);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel129, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel166, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel131, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(76, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtTenKichThuoc, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                                    .addComponent(txtMaKT)
                                    .addComponent(jScrollPane21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addGap(38, 38, 38))))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jLabel130)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnCapNhatKT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnThemMoiKT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnLuuSize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(88, 88, 88))))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel131)
                .addGap(18, 18, 18)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel129)
                    .addComponent(txtMaKT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel130)
                    .addComponent(txtTenKichThuoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel166)
                    .addComponent(jScrollPane21, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(btnThemMoiKT)
                .addGap(32, 32, 32)
                .addComponent(btnCapNhatKT)
                .addGap(33, 33, 33)
                .addComponent(btnLuuSize)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                .addGap(0, 23, Short.MAX_VALUE)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel132.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel132.setForeground(new java.awt.Color(255, 102, 102));
        jLabel132.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel132.setText("Quản lý kích thước");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(417, 417, 417)
                .addComponent(jLabel132)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel132)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(91, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tbpThuocTinh.addTab("Kích thước", jPanel14);

        jPanel25.setBackground(new java.awt.Color(255, 255, 255));

        jLabel133.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel133.setForeground(new java.awt.Color(255, 102, 102));
        jLabel133.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel133.setText("Quản lý nhà cung cấp");

        jPanel28.setBackground(new java.awt.Color(255, 255, 255));
        jPanel28.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));

        jLabel134.setText("Mã NCC:");

        jLabel135.setText("Tên NCC:");

        jLabel136.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel136.setForeground(new java.awt.Color(0, 51, 255));
        jLabel136.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel136.setText("Thêm mới nhà cung cấp");

        txtNCC.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtTenNCC.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        btnThemNCC.setBackground(new java.awt.Color(51, 102, 255));
        btnThemNCC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemNCC.setForeground(new java.awt.Color(255, 255, 255));
        btnThemNCC.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnThemNCC.setText("Thêm");
        btnThemNCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemNCCActionPerformed(evt);
            }
        });

        btnCapNhatNCC.setBackground(new java.awt.Color(51, 102, 255));
        btnCapNhatNCC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCapNhatNCC.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhatNCC.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnCapNhatNCC.setText("Cập nhật");
        btnCapNhatNCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatNCCActionPerformed(evt);
            }
        });

        btnLuuSupp.setBackground(new java.awt.Color(0, 102, 255));
        btnLuuSupp.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuuSupp.setForeground(new java.awt.Color(255, 255, 255));
        btnLuuSupp.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLuuSupp.setText("Lưu");
        btnLuuSupp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuSuppActionPerformed(evt);
            }
        });

        jLabel137.setText("SĐT:");

        txtSDTNCC.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel138.setText("Email:");

        txtEmailNCC.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel139.setText("Địa chỉ:");

        txtDiaChiNCC.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel28Layout.createSequentialGroup()
                                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel135)
                                    .addComponent(jLabel137)
                                    .addComponent(jLabel138)
                                    .addComponent(jLabel139))
                                .addGap(32, 32, 32)
                                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtDiaChiNCC, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                    .addComponent(txtEmailNCC)
                                    .addComponent(txtTenNCC)
                                    .addComponent(txtSDTNCC)))
                            .addComponent(jLabel134, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel28Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(txtNCC, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel28Layout.createSequentialGroup()
                            .addGap(63, 63, 63)
                            .addComponent(jLabel136))))
                .addGap(0, 64, Short.MAX_VALUE))
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnThemNCC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCapNhatNCC, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addComponent(btnLuuSupp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel136)
                .addGap(34, 34, 34)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel134)
                    .addComponent(txtNCC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel135)
                    .addComponent(txtTenNCC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSDTNCC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel137))
                .addGap(18, 18, 18)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel138)
                    .addComponent(txtEmailNCC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel139)
                    .addComponent(txtDiaChiNCC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(btnThemNCC)
                .addGap(30, 30, 30)
                .addComponent(btnCapNhatNCC)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLuuSupp)
                .addGap(15, 15, 15))
        );

        tblNhaCungCap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã NCC", "Tên NCC", "SĐT", "Email", "ĐỊa chỉ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblNhaCungCap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNhaCungCapMouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(tblNhaCungCap);

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(379, 379, 379)
                .addComponent(jLabel133)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel133)
                .addGap(87, 87, 87)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE))
                .addGap(0, 94, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tbpThuocTinh.addTab("Nhà cung cấp", jPanel15);

        jPanel20.setPreferredSize(new java.awt.Dimension(949, 600));

        jPanel29.setBackground(new java.awt.Color(255, 255, 255));

        jPanel30.setBackground(new java.awt.Color(255, 255, 255));

        tblPhongCach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã PC", "Tên PC", "Mô tả"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPhongCach.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPhongCachMouseClicked(evt);
            }
        });
        jScrollPane19.setViewportView(tblPhongCach);

        jPanel32.setBackground(new java.awt.Color(255, 255, 255));
        jPanel32.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));

        jLabel141.setText("Mã PC:");

        jLabel142.setText("Tên PC:");

        jLabel143.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel143.setForeground(new java.awt.Color(0, 51, 255));
        jLabel143.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel143.setText("Thêm mới phong cách");

        txtMaPC.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtTenPhongCach.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        btnThemPC.setBackground(new java.awt.Color(51, 102, 255));
        btnThemPC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemPC.setForeground(new java.awt.Color(255, 255, 255));
        btnThemPC.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnThemPC.setText("Thêm");
        btnThemPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemPCActionPerformed(evt);
            }
        });

        btnCapNhatPC.setBackground(new java.awt.Color(51, 102, 255));
        btnCapNhatPC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCapNhatPC.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhatPC.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnCapNhatPC.setText("Cập nhật");
        btnCapNhatPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatPCActionPerformed(evt);
            }
        });

        btnLuuStyle.setBackground(new java.awt.Color(51, 102, 255));
        btnLuuStyle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuuStyle.setForeground(new java.awt.Color(255, 255, 255));
        btnLuuStyle.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLuuStyle.setText("Lưu");
        btnLuuStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuStyleActionPerformed(evt);
            }
        });

        jLabel123.setText("Mô tả;");

        txtMoTaPC.setColumns(20);
        txtMoTaPC.setRows(5);
        jScrollPane22.setViewportView(txtMoTaPC);

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel32Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel32Layout.createSequentialGroup()
                        .addComponent(jLabel123, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(jScrollPane22, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel32Layout.createSequentialGroup()
                            .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel142, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel141, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(42, 42, 42)
                            .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtMaPC, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                .addComponent(txtTenPhongCach)))
                        .addGroup(jPanel32Layout.createSequentialGroup()
                            .addGap(63, 63, 63)
                            .addComponent(jLabel143, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnCapNhatPC, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnThemPC, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLuuStyle, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)))
                .addGap(55, 55, 55))
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel143)
                .addGap(36, 36, 36)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel141)
                    .addComponent(txtMaPC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel142)
                    .addComponent(txtTenPhongCach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel123)
                    .addComponent(jScrollPane22, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnThemPC)
                .addGap(25, 25, 25)
                .addComponent(btnCapNhatPC)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(btnLuuStyle)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jScrollPane19, javax.swing.GroupLayout.PREFERRED_SIZE, 635, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane19, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(118, Short.MAX_VALUE))
        );

        jLabel140.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel140.setForeground(new java.awt.Color(255, 102, 102));
        jLabel140.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel140.setText("Quản lý phong cách");

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(390, 390, 390)
                .addComponent(jLabel140)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel29Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel140)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tbpThuocTinh.addTab("Phong cách", jPanel20);

        btnBackkkkkk.setBackground(new java.awt.Color(51, 102, 255));
        btnBackkkkkk.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBackkkkkk.setForeground(new java.awt.Color(255, 255, 255));
        btnBackkkkkk.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\troLai.png")); // NOI18N
        btnBackkkkkk.setText("Quay lại");
        btnBackkkkkk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackkkkkkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpViewQLSPP3Layout = new javax.swing.GroupLayout(jpViewQLSPP3);
        jpViewQLSPP3.setLayout(jpViewQLSPP3Layout);
        jpViewQLSPP3Layout.setHorizontalGroup(
            jpViewQLSPP3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLSPP3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpViewQLSPP3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpViewQLSPP3Layout.createSequentialGroup()
                        .addComponent(btnBackkkkkk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel124, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(316, 316, 316))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpViewQLSPP3Layout.createSequentialGroup()
                        .addComponent(tbpThuocTinh)
                        .addContainerGap())))
        );
        jpViewQLSPP3Layout.setVerticalGroup(
            jpViewQLSPP3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLSPP3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpViewQLSPP3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel124, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBackkkkkk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbpThuocTinh))
        );

        jpViewQLKH.setBackground(new java.awt.Color(255, 255, 255));
        jpViewQLKH.setPreferredSize(new java.awt.Dimension(948, 600));

        jLabel170.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel170.setText("Quản Lý Khách Hàng");

        DanhSachKhachHang1.setBackground(new java.awt.Color(255, 255, 255));
        DanhSachKhachHang1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách khách hàng", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        tblKhachH.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã khách hàng", "Tên khách hàng", "SĐT", "Điểm tích lũy", "Điểm đã dùng", "Điểm còn lại"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblKhachH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKhachHMouseClicked(evt);
            }
        });
        jScrollPane23.setViewportView(tblKhachH);

        jLabel171.setText("Tên khách hàng");

        txtITenKhachHang1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtSoDiienThoai.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtSoDiienThoai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSoDiienThoaiKeyReleased(evt);
            }
        });

        jLabel172.setText("SĐT");

        jLabel173.setText("Điểm tích lũy");

        txtDiemTichLuy.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel174.setText("Điểm đã dùng");

        txtTinhDiem.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        jLabel175.setText("Điểm còn lại");

        txtDiemDaDung.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        btnThemMoiKH1.setBackground(new java.awt.Color(51, 102, 255));
        btnThemMoiKH1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThemMoiKH1.setForeground(new java.awt.Color(255, 255, 255));
        btnThemMoiKH1.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add.png")); // NOI18N
        btnThemMoiKH1.setText("Thêm");
        btnThemMoiKH1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemMoiKH1ActionPerformed(evt);
            }
        });

        btnLamMoiKH1.setBackground(new java.awt.Color(51, 102, 255));
        btnLamMoiKH1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLamMoiKH1.setForeground(new java.awt.Color(255, 255, 255));
        btnLamMoiKH1.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLamMoiKH1.setText("Lưu");
        btnLamMoiKH1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiKH1ActionPerformed(evt);
            }
        });

        btnSuaKhachHang1.setBackground(new java.awt.Color(51, 102, 255));
        btnSuaKhachHang1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSuaKhachHang1.setForeground(new java.awt.Color(255, 255, 255));
        btnSuaKhachHang1.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnSuaKhachHang1.setText("Cập nhật");
        btnSuaKhachHang1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaKhachHang1ActionPerformed(evt);
            }
        });

        jLabel176.setText("Mã khách hàng");

        txtMaKH.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));

        txtSearchKH.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtSearchKH.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtSearchKHCaretUpdate(evt);
            }
        });

        jLabel68.setText("Tìm kiếm:");

        javax.swing.GroupLayout DanhSachKhachHang1Layout = new javax.swing.GroupLayout(DanhSachKhachHang1);
        DanhSachKhachHang1.setLayout(DanhSachKhachHang1Layout);
        DanhSachKhachHang1Layout.setHorizontalGroup(
            DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DanhSachKhachHang1Layout.createSequentialGroup()
                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DanhSachKhachHang1Layout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel172)
                            .addComponent(jLabel171)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DanhSachKhachHang1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel174, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel175, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel176, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel173, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel68, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(36, 36, 36)
                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DanhSachKhachHang1Layout.createSequentialGroup()
                        .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(DanhSachKhachHang1Layout.createSequentialGroup()
                                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtITenKhachHang1, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMaKH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnThemMoiKH1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(DanhSachKhachHang1Layout.createSequentialGroup()
                                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtSoDiienThoai)
                                    .addComponent(txtDiemTichLuy)
                                    .addComponent(txtTinhDiem, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                    .addComponent(txtDiemDaDung, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnLamMoiKH1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnSuaKhachHang1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(139, 139, 139))
                    .addGroup(DanhSachKhachHang1Layout.createSequentialGroup()
                        .addComponent(txtSearchKH, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(DanhSachKhachHang1Layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jScrollPane23, javax.swing.GroupLayout.PREFERRED_SIZE, 731, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(95, Short.MAX_VALUE))
        );
        DanhSachKhachHang1Layout.setVerticalGroup(
            DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DanhSachKhachHang1Layout.createSequentialGroup()
                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DanhSachKhachHang1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel176)
                            .addComponent(txtMaKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(DanhSachKhachHang1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(btnThemMoiKH1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtITenKhachHang1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel171))
                .addGap(15, 15, 15)
                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSoDiienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel172)
                    .addComponent(btnSuaKhachHang1))
                .addGap(14, 14, 14)
                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDiemTichLuy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel173))
                .addGap(10, 10, 10)
                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTinhDiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel174)
                    .addComponent(btnLamMoiKH1))
                .addGap(18, 18, 18)
                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel175)
                    .addComponent(txtDiemDaDung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(DanhSachKhachHang1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68))
                .addGap(36, 36, 36)
                .addComponent(jScrollPane23, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout jpViewQLKHLayout = new javax.swing.GroupLayout(jpViewQLKH);
        jpViewQLKH.setLayout(jpViewQLKHLayout);
        jpViewQLKHLayout.setHorizontalGroup(
            jpViewQLKHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLKHLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpViewQLKHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpViewQLKHLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel170)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(DanhSachKhachHang1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpViewQLKHLayout.setVerticalGroup(
            jpViewQLKHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpViewQLKHLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel170)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DanhSachKhachHang1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpViewQLKM.setBackground(new java.awt.Color(255, 255, 255));
        jpViewQLKM.setPreferredSize(new java.awt.Dimension(948, 600));

        jLabel76.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel76.setText("Chương trình khuyến mãi");

        TabGiamGia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabGiamGiaMouseClicked(evt);
            }
        });

        Coupon.setBackground(new java.awt.Color(255, 255, 255));
        Coupon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        tblCoupon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã giảm giá", "Code", "Mô tả", "Giá trị chiết khấu", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái", "Tiền áp dụng", "Tiền giảm tối đa", "Số lượng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCoupon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCouponMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(tblCoupon);

        btnAddCou.setBackground(new java.awt.Color(51, 102, 255));
        btnAddCou.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddCou.setForeground(new java.awt.Color(255, 255, 255));
        btnAddCou.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnAddCou.setText("Thêm");
        btnAddCou.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddCouMouseClicked(evt);
            }
        });
        btnAddCou.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCouActionPerformed(evt);
            }
        });

        btnEditCou.setBackground(new java.awt.Color(51, 102, 255));
        btnEditCou.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEditCou.setForeground(new java.awt.Color(255, 255, 255));
        btnEditCou.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnEditCou.setText("Cập nhật");
        btnEditCou.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEditCouMouseClicked(evt);
            }
        });
        btnEditCou.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditCouActionPerformed(evt);
            }
        });

        jLabel98.setText("Mã giảm giá");

        txtMaGiamGia.setForeground(new java.awt.Color(51, 255, 204));
        txtMaGiamGia.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 102, 255)));

        jLabel99.setText("code");

        txtCode.setForeground(new java.awt.Color(51, 255, 204));
        txtCode.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 102, 255)));

        jLabel100.setText("Phần trăm giảm giá");

        txtPhanTramChietKhau.setForeground(new java.awt.Color(255, 102, 102));
        txtPhanTramChietKhau.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 102, 255)));
        txtPhanTramChietKhau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhanTramChietKhauActionPerformed(evt);
            }
        });

        jLabel101.setText("Mô tả");

        txtMoTa.setColumns(20);
        txtMoTa.setForeground(new java.awt.Color(255, 0, 255));
        txtMoTa.setRows(5);
        jScrollPane11.setViewportView(txtMoTa);

        jLabel102.setText("Ngày bắt đầu");

        jLabel103.setText("Ngày kết thúc");

        jLabel104.setText("Trạng thái");

        rdoDangKM.setSelected(true);
        rdoDangKM.setText("Đang khuyến mãi");

        rdoDungKM.setText("Dừng khuyến mãi");

        btnVoHH.setBackground(new java.awt.Color(255, 102, 102));
        btnVoHH.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnVoHH.setForeground(new java.awt.Color(255, 255, 255));
        btnVoHH.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\voHieuHoa.png")); // NOI18N
        btnVoHH.setText("Vô hiệu hóa");
        btnVoHH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnVoHHMouseClicked(evt);
            }
        });

        btnLuu3.setBackground(new java.awt.Color(0, 102, 255));
        btnLuu3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuu3.setForeground(new java.awt.Color(255, 255, 255));
        btnLuu3.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLuu3.setText("Lưu");
        btnLuu3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLuu3MouseClicked(evt);
            }
        });
        btnLuu3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuu3ActionPerformed(evt);
            }
        });

        txt_tien_toi_da.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 102, 255)));
        txt_tien_toi_da.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_tien_toi_daActionPerformed(evt);
            }
        });

        txt_tien_ap_dung.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 102, 255)));

        jLabel107.setText("Tiền giảm tối đa");

        jLabel167.setText("Tiền áp dụng:");

        jLabel177.setText("Số lượng:");

        txt_so_luong_vou.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 102, 255)));
        txt_so_luong_vou.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_so_luong_vouActionPerformed(evt);
            }
        });

        jdc_ngay_bat_dau_voucher.setDateFormatString("yyyy-MM-dd");

        jdc_ngay_ket_thuc_voucher.setDateFormatString("yyyy-MM-dd");

        jLabel178.setForeground(new java.awt.Color(255, 0, 0));
        jLabel178.setText("%");

        capNhatTichDiem.setBackground(new java.awt.Color(255, 255, 255));
        capNhatTichDiem.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cập nhật tích điểm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jLabel77.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel77.setText("Tỉ lệ quy đổi điểm thưởng");

        jLabel78.setText("VND");

        jLabel79.setText("=");

        jLabel80.setText("Thanh toán bằng điểm");

        jLabel81.setText("Điểm");

        jLabel82.setText("=");

        jLabel83.setText("VND");

        btnLuuThayDoi.setBackground(new java.awt.Color(51, 102, 255));
        btnLuuThayDoi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuuThayDoi.setForeground(new java.awt.Color(255, 255, 255));
        btnLuuThayDoi.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLuuThayDoi.setText("Lưu thay đổi");
        btnLuuThayDoi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLuuThayDoiMouseClicked(evt);
            }
        });

        btnResetPoint.setBackground(new java.awt.Color(51, 102, 255));
        btnResetPoint.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnResetPoint.setForeground(new java.awt.Color(255, 255, 255));
        btnResetPoint.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\lammoiiii.png")); // NOI18N
        btnResetPoint.setText("Làm mới");
        btnResetPoint.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnResetPointMouseClicked(evt);
            }
        });

        jLabel84.setText("Điểm");

        javax.swing.GroupLayout capNhatTichDiemLayout = new javax.swing.GroupLayout(capNhatTichDiem);
        capNhatTichDiem.setLayout(capNhatTichDiemLayout);
        capNhatTichDiemLayout.setHorizontalGroup(
            capNhatTichDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(capNhatTichDiemLayout.createSequentialGroup()
                .addGroup(capNhatTichDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(capNhatTichDiemLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel80))
                    .addGroup(capNhatTichDiemLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(capNhatTichDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(capNhatTichDiemLayout.createSequentialGroup()
                                .addComponent(txtMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel78)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel79)
                                .addGap(18, 18, 18)
                                .addComponent(txtPoint, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel84))))
                    .addGroup(capNhatTichDiemLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(capNhatTichDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(capNhatTichDiemLayout.createSequentialGroup()
                                .addComponent(txtMoney1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel81)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnLuuThayDoi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(capNhatTichDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(capNhatTichDiemLayout.createSequentialGroup()
                                .addComponent(txtPoint1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnResetPoint))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        capNhatTichDiemLayout.setVerticalGroup(
            capNhatTichDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(capNhatTichDiemLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel77)
                .addGap(30, 30, 30)
                .addGroup(capNhatTichDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel78)
                    .addComponent(jLabel79)
                    .addComponent(txtPoint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84))
                .addGap(36, 36, 36)
                .addComponent(jLabel80)
                .addGap(29, 29, 29)
                .addGroup(capNhatTichDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPoint1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel81)
                    .addComponent(jLabel82)
                    .addComponent(jLabel83)
                    .addComponent(txtMoney1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(62, 62, 62)
                .addGroup(capNhatTichDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnResetPoint)
                    .addComponent(btnLuuThayDoi))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        txt_tim_Voucher.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 102, 255)));
        txt_tim_Voucher.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txt_tim_VoucherCaretUpdate(evt);
            }
        });

        btnLuiVoucher.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLuiVoucher.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veTruoc.png")); // NOI18N
        btnLuiVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuiVoucherActionPerformed(evt);
            }
        });

        btnTienVoucher.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTienVoucher.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veSau.png")); // NOI18N
        btnTienVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTienVoucherActionPerformed(evt);
            }
        });

        txt_phan_trangVoucher.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_phan_trangVoucher.setText("1");

        btnTienCuoiVoucher1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTienCuoiVoucher1.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veCuoi.png")); // NOI18N
        btnTienCuoiVoucher1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTienCuoiVoucher1ActionPerformed(evt);
            }
        });

        btnLuiCuoiVoucher1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuiCuoiVoucher1.setForeground(new java.awt.Color(255, 255, 255));
        btnLuiCuoiVoucher1.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\veDau.png")); // NOI18N
        btnLuiCuoiVoucher1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuiCuoiVoucher1ActionPerformed(evt);
            }
        });

        jLabel86.setText("Tìm kiếm:");

        javax.swing.GroupLayout CouponLayout = new javax.swing.GroupLayout(Coupon);
        Coupon.setLayout(CouponLayout);
        CouponLayout.setHorizontalGroup(
            CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CouponLayout.createSequentialGroup()
                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(CouponLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CouponLayout.createSequentialGroup()
                                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel98)
                                    .addComponent(jLabel99)
                                    .addComponent(jLabel100, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(CouponLayout.createSequentialGroup()
                                        .addComponent(txtPhanTramChietKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel178))
                                    .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtCode, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                                        .addComponent(txtMaGiamGia)))
                                .addGap(79, 79, 79))
                            .addGroup(CouponLayout.createSequentialGroup()
                                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel101)
                                    .addComponent(jLabel104))
                                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(CouponLayout.createSequentialGroup()
                                        .addGap(79, 79, 79)
                                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                    .addGroup(CouponLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(CouponLayout.createSequentialGroup()
                                                .addComponent(jLabel86)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txt_tim_Voucher, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(CouponLayout.createSequentialGroup()
                                                .addComponent(rdoDangKM)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(rdoDungKM)))))
                                .addGap(40, 40, 40)))
                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel177)
                                .addComponent(jLabel107)
                                .addComponent(jLabel102)
                                .addComponent(jLabel167))
                            .addGroup(CouponLayout.createSequentialGroup()
                                .addComponent(jLabel103)
                                .addGap(15, 15, 15)))
                        .addGap(36, 36, 36)
                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jdc_ngay_bat_dau_voucher, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txt_so_luong_vou, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                                    .addComponent(txt_tien_ap_dung, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_tien_toi_da, javax.swing.GroupLayout.Alignment.LEADING)))
                            .addComponent(jdc_ngay_ket_thuc_voucher, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnAddCou, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEditCou, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                .addComponent(btnLuu3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnVoHH))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(capNhatTichDiem, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CouponLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnLuiCuoiVoucher1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLuiVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(txt_phan_trangVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnTienVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTienCuoiVoucher1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(377, 377, 377))
        );
        CouponLayout.setVerticalGroup(
            CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CouponLayout.createSequentialGroup()
                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CouponLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(capNhatTichDiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(CouponLayout.createSequentialGroup()
                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(CouponLayout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtMaGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel98)
                                    .addComponent(jLabel167)
                                    .addComponent(txt_tien_ap_dung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(CouponLayout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(btnAddCou)))
                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(CouponLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel99))
                                    .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txt_tien_toi_da, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel107)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel178)
                                        .addComponent(txt_so_luong_vou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel177))
                                    .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtPhanTramChietKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel100)))
                                .addGap(21, 21, 21)
                                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel101)
                                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(CouponLayout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jdc_ngay_bat_dau_voucher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel102))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel103, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jdc_ngay_ket_thuc_voucher, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(CouponLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(btnEditCou)
                                .addGap(26, 26, 26)
                                .addComponent(btnLuu3)
                                .addGap(25, 25, 25)
                                .addComponent(btnVoHH)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdoDangKM)
                            .addComponent(rdoDungKM)
                            .addComponent(jLabel104))
                        .addGap(41, 41, 41)
                        .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel86)
                            .addComponent(txt_tim_Voucher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)))
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(CouponLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTienVoucher)
                    .addComponent(btnTienCuoiVoucher1)
                    .addComponent(btnLuiVoucher)
                    .addComponent(btnLuiCuoiVoucher1)
                    .addComponent(txt_phan_trangVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
        );

        TabGiamGia.addTab("Voucher giảm giá", Coupon);

        Discount.setBackground(new java.awt.Color(255, 255, 255));
        Discount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Discount.setPreferredSize(new java.awt.Dimension(600, 505));

        tblDiscount.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã chiết khấu", "Code chi tiết", "Ngày bắt đầu", "Ngày kết thúc", "Số tiền chiết khấu", "Màu", "Kiểu dáng", "Kích thước"
            }
        ));
        tblDiscount.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDiscountMouseClicked(evt);
            }
        });
        jScrollPane10.setViewportView(tblDiscount);

        btnAddDis.setBackground(new java.awt.Color(51, 102, 255));
        btnAddDis.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddDis.setForeground(new java.awt.Color(255, 255, 255));
        btnAddDis.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\add1.png")); // NOI18N
        btnAddDis.setText("Thêm");
        btnAddDis.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddDisMouseClicked(evt);
            }
        });

        btnEditDis.setBackground(new java.awt.Color(51, 102, 255));
        btnEditDis.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEditDis.setForeground(new java.awt.Color(255, 255, 255));
        btnEditDis.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\penSua.png")); // NOI18N
        btnEditDis.setText("Cập nhật");
        btnEditDis.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEditDisMouseClicked(evt);
            }
        });
        btnEditDis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditDisActionPerformed(evt);
            }
        });

        jLabel105.setText("Mã chiết khấu");

        jLabel106.setText("Mã sp ct");

        txtMa_chiet_khau.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(51, 204, 0)));
        txtMa_chiet_khau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMa_chiet_khauActionPerformed(evt);
            }
        });

        jLabel10.setText("Ngày bắt đầu");

        jLabel85.setText("Ngày kết thúc");

        btnLamMoi2.setBackground(new java.awt.Color(51, 102, 255));
        btnLamMoi2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLamMoi2.setForeground(new java.awt.Color(255, 255, 255));
        btnLamMoi2.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\luuTru1.png")); // NOI18N
        btnLamMoi2.setText("Lưu");
        btnLamMoi2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLamMoi2MouseClicked(evt);
            }
        });

        jLabel179.setText("Số tiền giảm");

        txt_so_tien_giam.setForeground(new java.awt.Color(255, 102, 255));
        txt_so_tien_giam.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 255, 0)));

        jdc_ngay_bat_dau.setDateFormatString("yyyy-MM-dd");

        jdc_ngay_ket_thuc.setDateFormatString("yyyy-MM-dd");

        tbl_san_pham_khuyen_mai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã giảm giá chi tiết", "Giá", "Màu", "Kiểu dáng", "Kích thước"
            }
        ));
        tbl_san_pham_khuyen_mai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_san_pham_khuyen_maiMouseClicked(evt);
            }
        });
        jScrollPane24.setViewportView(tbl_san_pham_khuyen_mai);

        txt_tim_sp.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txt_tim_spCaretUpdate(evt);
            }
        });
        txt_tim_sp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_tim_spKeyTyped(evt);
            }
        });

        jLabel180.setText("Tìm SP");

        txt_tim_chuong_trình.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txt_tim_chuong_trìnhCaretUpdate(evt);
            }
        });

        jLabel182.setBackground(new java.awt.Color(153, 51, 255));
        jLabel182.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel182.setForeground(new java.awt.Color(255, 0, 255));
        jLabel182.setText("Bảng chương trình khuyến mãi ");

        txt_code_chi_tiet.setForeground(new java.awt.Color(255, 102, 255));
        txt_code_chi_tiet.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 255, 0)));

        jLabel183.setText("Màu");

        jLabel151.setText("Kiểu dáng");

        jLabel152.setText("Kích thước");

        javax.swing.GroupLayout DiscountLayout = new javax.swing.GroupLayout(Discount);
        Discount.setLayout(DiscountLayout);
        DiscountLayout.setHorizontalGroup(
            DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DiscountLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DiscountLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel182, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(txt_tim_chuong_trình, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jLabel181, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(DiscountLayout.createSequentialGroup()
                        .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane10)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DiscountLayout.createSequentialGroup()
                                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(DiscountLayout.createSequentialGroup()
                                        .addComponent(jLabel105)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtMa_chiet_khau, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(DiscountLayout.createSequentialGroup()
                                        .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel180)
                                            .addComponent(jLabel183, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txt_tim_sp, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                            .addComponent(txtMau))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel179)
                                    .addComponent(jLabel151, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(35, 35, 35)
                                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_so_tien_giam, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                                    .addComponent(txtKieuDang))
                                .addGap(30, 30, 30)
                                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel152, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_code_chi_tiet, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtKichThuoc, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 22, Short.MAX_VALUE)
                                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel85)
                                    .addComponent(jLabel10))
                                .addGap(34, 34, 34)
                                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jdc_ngay_bat_dau, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                                    .addComponent(jdc_ngay_ket_thuc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnLamMoi2, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                    .addComponent(btnAddDis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnEditDis, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                                .addGap(16, 16, 16))
                            .addComponent(jScrollPane24))
                        .addContainerGap())))
        );
        DiscountLayout.setVerticalGroup(
            DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DiscountLayout.createSequentialGroup()
                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DiscountLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtMa_chiet_khau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_so_tien_giam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel179)
                                .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_code_chi_tiet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10))
                            .addComponent(jLabel105)
                            .addComponent(jdc_ngay_bat_dau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(DiscountLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAddDis)))
                .addGap(21, 21, 21)
                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel183)
                    .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel151)
                        .addComponent(txtKieuDang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel152)
                        .addComponent(txtKichThuoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel85))
                    .addComponent(jdc_ngay_ket_thuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditDis))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DiscountLayout.createSequentialGroup()
                        .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_tim_sp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel180, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DiscountLayout.createSequentialGroup()
                        .addComponent(btnLamMoi2)
                        .addGap(18, 18, 18)))
                .addComponent(jScrollPane24, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel181)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, DiscountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_tim_chuong_trình)
                        .addComponent(jLabel182, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        TabGiamGia.addTab("Giảm giá trực tiếp", Discount);

        javax.swing.GroupLayout jpViewQLKMLayout = new javax.swing.GroupLayout(jpViewQLKM);
        jpViewQLKM.setLayout(jpViewQLKMLayout);
        jpViewQLKMLayout.setHorizontalGroup(
            jpViewQLKMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpViewQLKMLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel76)
                .addGap(384, 384, 384))
            .addComponent(TabGiamGia)
        );
        jpViewQLKMLayout.setVerticalGroup(
            jpViewQLKMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpViewQLKMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel76)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TabGiamGia)
                .addContainerGap())
        );

        jpViewTK.setBackground(new java.awt.Color(255, 255, 255));
        jpViewTK.setPreferredSize(new java.awt.Dimension(948, 600));

        jLabel14.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 51, 51));
        jLabel14.setText("Thống kê sản phẩm");

        ThongKeSP.setBackground(new java.awt.Color(255, 255, 255));
        ThongKeSP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel40.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel40.setText("Tìm Kiếm:");

        txtTimKiemSP.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txtTimKiemSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimKiemSPActionPerformed(evt);
            }
        });
        txtTimKiemSP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTimKiemSPKeyReleased(evt);
            }
        });

        jLabel41.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel41.setText("Lọc theo khoảng:");

        jLabel43.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel43.setText("Đến:");

        jTuNgay.setDateFormatString("yyyy-MM-dd");
        jTuNgay.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTuNgayPropertyChange(evt);
            }
        });

        jDenNgay.setDateFormatString("yyyy-MM-dd");
        jDenNgay.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDenNgayPropertyChange(evt);
            }
        });

        btnLocKhoangSanPham.setBackground(new java.awt.Color(51, 102, 255));
        btnLocKhoangSanPham.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLocKhoangSanPham.setForeground(new java.awt.Color(255, 255, 255));
        btnLocKhoangSanPham.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\loc.png")); // NOI18N
        btnLocKhoangSanPham.setText("Lọc");
        btnLocKhoangSanPham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocKhoangSanPhamActionPerformed(evt);
            }
        });

        jLabel150.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel150.setText("Từ:");

        tongSP.setBackground(new java.awt.Color(102, 204, 255));

        jLabel44.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel44.setText("Tổng số sản phẩmtồn kho:");

        lblTongSP.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblTongSP.setText("0 SP");

        javax.swing.GroupLayout tongSPLayout = new javax.swing.GroupLayout(tongSP);
        tongSP.setLayout(tongSPLayout);
        tongSPLayout.setHorizontalGroup(
            tongSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tongSPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tongSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTongSP, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tongSPLayout.setVerticalGroup(
            tongSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tongSPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(26, 26, 26)
                .addComponent(lblTongSP, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        spDaBan.setBackground(new java.awt.Color(255, 204, 204));

        jLabel45.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel45.setText("Tổng số sản phẩm đã bán:");

        lblSPDaBan.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblSPDaBan.setText("0 SP");

        javax.swing.GroupLayout spDaBanLayout = new javax.swing.GroupLayout(spDaBan);
        spDaBan.setLayout(spDaBanLayout);
        spDaBanLayout.setHorizontalGroup(
            spDaBanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spDaBanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(spDaBanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45)
                    .addComponent(lblSPDaBan, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        spDaBanLayout.setVerticalGroup(
            spDaBanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, spDaBanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblSPDaBan, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        jButton1.setBackground(new java.awt.Color(51, 102, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Nhập lại");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ThongKeSPLayout = new javax.swing.GroupLayout(ThongKeSP);
        ThongKeSP.setLayout(ThongKeSPLayout);
        ThongKeSPLayout.setHorizontalGroup(
            ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ThongKeSPLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel41)
                    .addGroup(ThongKeSPLayout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTimKiemSP, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ThongKeSPLayout.createSequentialGroup()
                        .addGroup(ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel150)
                            .addComponent(jLabel43))
                        .addGap(18, 18, 18)
                        .addGroup(ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jDenNgay, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(jTuNgay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnLocKhoangSanPham, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tongSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(spDaBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );
        ThongKeSPLayout.setVerticalGroup(
            ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ThongKeSPLayout.createSequentialGroup()
                .addGroup(ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ThongKeSPLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel40)
                            .addComponent(txtTimKiemSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addGroup(ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnLocKhoangSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, 23, Short.MAX_VALUE)
                            .addComponent(jTuNgay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel150, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                        .addGap(14, 14, 14)
                        .addGroup(ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(ThongKeSPLayout.createSequentialGroup()
                                .addGroup(ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton1)
                                    .addComponent(jDenNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 6, Short.MAX_VALUE))))
                    .addGroup(ThongKeSPLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(ThongKeSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spDaBan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tongSP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        jLabel46.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(255, 51, 51));
        jLabel46.setText("Danh sách sản phẩm đã bán");

        tblDanhSachSPdaBan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã sản phẩm", "Tên sản phẩm", "Danh mục", "Số lượng", "Tổng tiền", "Nhà cung cấp"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSachSPdaBan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDanhSachSPdaBanMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(tblDanhSachSPdaBan);

        jLabel47.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(255, 51, 51));
        jLabel47.setText("Thống kê doanh thu");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel48.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel48.setText("Lọc theo khoảng:");

        jLabel49.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel49.setText("Từ:");

        jLabel50.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel50.setText("Đến:");

        jPanel6.setBackground(new java.awt.Color(0, 0, 255));

        jLabel53.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(255, 255, 255));
        jLabel53.setText("Doanh Thu Tháng");

        lblDoanhThuThang.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblDoanhThuThang.setForeground(new java.awt.Color(255, 255, 255));
        lblDoanhThuThang.setText("0 VNĐ");

        jDTTThang.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDTTThangPropertyChange(evt);
            }
        });

        jDTTNam.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDTTNamPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDoanhThuThang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel53)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jDTTThang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                .addComponent(jDTTNam, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel53)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDTTNam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDTTThang, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDoanhThuThang, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel7.setBackground(new java.awt.Color(204, 0, 204));

        jLabel55.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(255, 255, 255));
        jLabel55.setText("Doanh thu ngày");

        lblDoanhThuNgay.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblDoanhThuNgay.setForeground(new java.awt.Color(255, 255, 255));
        lblDoanhThuNgay.setText("0 VNĐ");

        jDateDoanhThuNgay.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateDoanhThuNgayPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDoanhThuNgay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateDoanhThuNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel55))
                        .addGap(0, 77, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel55)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDateDoanhThuNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDoanhThuNgay, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(102, 102, 102));

        jLabel51.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(255, 255, 255));
        jLabel51.setText("Doanh Thu tổng");

        lblDoanhThuTong.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblDoanhThuTong.setForeground(new java.awt.Color(255, 255, 255));
        lblDoanhThuTong.setText("0 VNĐ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDoanhThuTong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(120, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addComponent(lblDoanhThuTong, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        TuLocTheoKhoangUpdate.setDateFormatString("yyyy-MM-dd");
        TuLocTheoKhoangUpdate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                TuLocTheoKhoangUpdatePropertyChange(evt);
            }
        });

        DenLocTheoKhoangUpdate.setDateFormatString("yyyy-MM-dd");
        DenLocTheoKhoangUpdate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                DenLocTheoKhoangUpdatePropertyChange(evt);
            }
        });

        btnLocDoanhSoUpdate.setBackground(new java.awt.Color(51, 102, 255));
        btnLocDoanhSoUpdate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLocDoanhSoUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnLocDoanhSoUpdate.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\loc.png")); // NOI18N
        btnLocDoanhSoUpdate.setText("Lọc");
        btnLocDoanhSoUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocDoanhSoUpdateActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(51, 102, 255));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Nhập lại");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel48)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel50)
                            .addComponent(jLabel49))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DenLocTheoKhoangUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(TuLocTheoKhoangUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(26, 26, 26)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLocDoanhSoUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(TuLocTheoKhoangUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLocDoanhSoUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel50)
                        .addGap(18, 18, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 2, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton2)
                            .addComponent(DenLocTheoKhoangUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );

        cbbTOP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Top 10 sản phẩm bán nhiều nhất", "Top 10 sản phẩm bán ít nhất" }));
        cbbTOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbTOPActionPerformed(evt);
            }
        });

        btnLamMoiThongKe.setBackground(new java.awt.Color(51, 102, 255));
        btnLamMoiThongKe.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLamMoiThongKe.setForeground(new java.awt.Color(255, 255, 255));
        btnLamMoiThongKe.setIcon(new javax.swing.ImageIcon("C:\\Users\\DƯƠNG MINH HÀ\\Desktop\\anhDuAn1\\HoanTac.png")); // NOI18N
        btnLamMoiThongKe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiThongKeActionPerformed(evt);
            }
        });

        tongSP1.setBackground(new java.awt.Color(102, 204, 255));

        jLabel153.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel153.setText("Tổng số lượng sản phẩm:");

        lblTongSP1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblTongSP1.setText("0 Sản phẩm");

        javax.swing.GroupLayout tongSP1Layout = new javax.swing.GroupLayout(tongSP1);
        tongSP1.setLayout(tongSP1Layout);
        tongSP1Layout.setHorizontalGroup(
            tongSP1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tongSP1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel153)
                .addGap(14, 14, 14))
            .addGroup(tongSP1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTongSP1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tongSP1Layout.setVerticalGroup(
            tongSP1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tongSP1Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabel153)
                .addGap(8, 8, 8)
                .addComponent(lblTongSP1)
                .addContainerGap())
        );

        spDaBan1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel154.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel154.setText("Tổng số lượng sản phẩm bán:");

        lblSPDaBan1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblSPDaBan1.setText("0 Sản phẩm");

        javax.swing.GroupLayout spDaBan1Layout = new javax.swing.GroupLayout(spDaBan1);
        spDaBan1.setLayout(spDaBan1Layout);
        spDaBan1Layout.setHorizontalGroup(
            spDaBan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spDaBan1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(spDaBan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(spDaBan1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel154))
                    .addComponent(lblSPDaBan1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        spDaBan1Layout.setVerticalGroup(
            spDaBan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spDaBan1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel154)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSPDaBan1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout jpViewTKLayout = new javax.swing.GroupLayout(jpViewTK);
        jpViewTK.setLayout(jpViewTKLayout);
        jpViewTKLayout.setHorizontalGroup(
            jpViewTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpViewTKLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpViewTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jpViewTKLayout.createSequentialGroup()
                        .addComponent(tongSP1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spDaBan1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpViewTKLayout.createSequentialGroup()
                        .addComponent(cbbTOP, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(btnLamMoiThongKe, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ThongKeSP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane8)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(145, 145, 145))
            .addGroup(jpViewTKLayout.createSequentialGroup()
                .addGroup(jpViewTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpViewTKLayout.createSequentialGroup()
                        .addGap(371, 371, 371)
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpViewTKLayout.createSequentialGroup()
                        .addGap(334, 334, 334)
                        .addComponent(jLabel46))
                    .addGroup(jpViewTKLayout.createSequentialGroup()
                        .addGap(371, 371, 371)
                        .addComponent(jLabel14)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpViewTKLayout.setVerticalGroup(
            jpViewTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpViewTKLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ThongKeSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpViewTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLamMoiThongKe, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbbTOP))
                .addGap(7, 7, 7)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpViewTKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spDaBan1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tongSP1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout ViewHeThongLayout = new javax.swing.GroupLayout(ViewHeThong);
        ViewHeThong.setLayout(ViewHeThongLayout);
        ViewHeThongLayout.setHorizontalGroup(
            ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLHD, javax.swing.GroupLayout.DEFAULT_SIZE, 1046, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLBH2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLDM, javax.swing.GroupLayout.DEFAULT_SIZE, 1046, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLTK, javax.swing.GroupLayout.DEFAULT_SIZE, 1046, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewDoiMK, javax.swing.GroupLayout.DEFAULT_SIZE, 1046, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLSPP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLSPP2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLSPP3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLKH, javax.swing.GroupLayout.DEFAULT_SIZE, 1046, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLKM, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1046, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewTK, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1046, Short.MAX_VALUE))
        );
        ViewHeThongLayout.setVerticalGroup(
            ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 757, Short.MAX_VALUE)
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLHD, javax.swing.GroupLayout.PREFERRED_SIZE, 757, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLBH2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLDM, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLTK, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewDoiMK, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLSPP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLSPP2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLSPP3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLKH, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewQLKM, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE))
            .addGroup(ViewHeThongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jpViewTK, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 757, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpContainerLayout = new javax.swing.GroupLayout(jpContainer);
        jpContainer.setLayout(jpContainerLayout);
        jpContainerLayout.setHorizontalGroup(
            jpContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpContainerLayout.createSequentialGroup()
                .addComponent(MenuHeThong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ViewHeThong, javax.swing.GroupLayout.PREFERRED_SIZE, 1029, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpContainerLayout.setVerticalGroup(
            jpContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MenuHeThong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(ViewHeThong, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpContainer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jpQLBHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpQLBHMouseClicked
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLBH2.setVisible(true);
        this.jpViewDoiMK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);
        this.jpQLBH.setBackground(new Color(220, 220, 220));
        this.jpQLDM.setBackground(new Color(255, 102, 0));
        this.jpQLKH.setBackground(new Color(255, 102, 0));
        this.jpQLKM.setBackground(new Color(255, 102, 0));
        this.jpQLSP.setBackground(new Color(255, 102, 0));
        this.jpQLTK.setBackground(new Color(255, 102, 0));
        this.jpTK.setBackground(new Color(255, 102, 0));
        this.jpQLHD.setBackground(new Color(255, 102, 0));

//        cbbVouchers.removeAllItems();
//        ql.loadVoucher(cbbVouchers);
        ql.LoadDSSPTrongQLBH(tblDSSanPham);

    }//GEN-LAST:event_jpQLBHMouseClicked

    private void jpQLSPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpQLSPMouseClicked
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(true);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLBH2.setVisible(false);
        this.jpViewDoiMK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);
        this.jpQLBH.setBackground(new Color(255, 102, 0));
        this.jpQLDM.setBackground(new Color(255, 102, 0));
        this.jpQLKH.setBackground(new Color(255, 102, 0));
        this.jpQLKM.setBackground(new Color(255, 102, 0));
        this.jpQLSP.setBackground(new Color(220, 220, 220));
        this.jpQLTK.setBackground(new Color(255, 102, 0));
        this.jpTK.setBackground(new Color(255, 102, 0));
        this.jpQLHD.setBackground(new Color(255, 102, 0));

        // Hòa
        if (this.jpViewQLSPP.isVisible()) {
            this.setEnableFormSP();
            this.setInputFields("sản phẩm");
            this.cbbLocTheoDM.setSelectedItem("");
            this.cbbTrangThaiSP.setSelectedItem("");
            this.cbbLocNhaCC.setSelectedItem("");
            currentPage = 1;
            updatePagination(qlsp.getAllProductDetail());

            currentPages = 1;
            updatePaginations(qlsp.getAllProduct());
        }

    }//GEN-LAST:event_jpQLSPMouseClicked

    private void jpQLDMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpQLDMMouseClicked
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(true);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLBH2.setVisible(false);
        this.jpViewDoiMK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);
        this.jpQLBH.setBackground(new Color(255, 102, 0));
        this.jpQLDM.setBackground(new Color(220, 220, 220));
        this.jpQLKH.setBackground(new Color(255, 102, 0));
        this.jpQLKM.setBackground(new Color(255, 102, 0));
        this.jpQLSP.setBackground(new Color(255, 102, 0));
        this.jpQLTK.setBackground(new Color(255, 102, 0));
        this.jpTK.setBackground(new Color(255, 102, 0));
        this.jpQLHD.setBackground(new Color(255, 102, 0));
    }//GEN-LAST:event_jpQLDMMouseClicked

    private void jpQLKHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpQLKHMouseClicked
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(true);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLBH2.setVisible(false);
        this.jpViewDoiMK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);
        this.jpQLBH.setBackground(new Color(255, 102, 0));
        this.jpQLDM.setBackground(new Color(255, 102, 0));
        this.jpQLKH.setBackground(new Color(220, 220, 220));
        this.jpQLKM.setBackground(new Color(255, 102, 0));
        this.jpQLSP.setBackground(new Color(255, 102, 0));
        this.jpQLTK.setBackground(new Color(255, 102, 0));
        this.jpTK.setBackground(new Color(255, 102, 0));
        this.jpQLHD.setBackground(new Color(255, 102, 0));

        //Hòa
        if (this.jpViewQLKH.isVisible()) {
            this.setEnableFormKH();
            this.txtMaKH.setText("");
            this.txtITenKhachHang1.setText("");
            this.txtSoDiienThoai.setText("");
            this.txtDiemTichLuy.setText("");
            this.txtTinhDiem.setText("");
            this.txtDiemDaDung.setText("");
            this.tblKhachH.clearSelection();
        }
    }//GEN-LAST:event_jpQLKHMouseClicked

    private void jpTKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpTKMouseClicked
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(true);
        this.jpViewQLBH2.setVisible(false);
        this.jpViewDoiMK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);
        this.jpQLBH.setBackground(new Color(255, 102, 0));
        this.jpQLDM.setBackground(new Color(255, 102, 0));
        this.jpQLKH.setBackground(new Color(255, 102, 0));
        this.jpQLKM.setBackground(new Color(255, 102, 0));
        this.jpQLSP.setBackground(new Color(255, 102, 0));
        this.jpQLTK.setBackground(new Color(255, 102, 0));
        this.jpTK.setBackground(new Color(220, 220, 220));
        this.jpQLHD.setBackground(new Color(255, 102, 0));
        tblDSHD.setModel(qlHoaDon.loadOrderData());
        ql.LoadDSSPTrongQLBH(tblDSSanPham);
    }//GEN-LAST:event_jpTKMouseClicked

    private void jpQLTKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpQLTKMouseClicked
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(true);
        this.jpViewTK.setVisible(false);
        this.jpViewQLBH2.setVisible(false);
        this.jpViewDoiMK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);
        this.jpQLBH.setBackground(new Color(255, 102, 0));
        this.jpQLDM.setBackground(new Color(255, 102, 0));
        this.jpQLKH.setBackground(new Color(255, 102, 0));
        this.jpQLKM.setBackground(new Color(255, 102, 0));
        this.jpQLSP.setBackground(new Color(255, 102, 0));
        this.jpQLTK.setBackground(new Color(220, 220, 220));
        this.jpTK.setBackground(new Color(255, 102, 0));
        this.jpQLHD.setBackground(new Color(255, 102, 0));

        ql.loadUserInfo(currentUserId, txtHoTen, txtUsername, txtEmail, txtSdt, txtDiaChi, txtVaiTro);

        // fix 04/08
        txtHoTen.setEnabled(false);
        txtUsername.setEnabled(false);
        txtEmail.setEnabled(false);
        txtSdt.setEnabled(false);
        txtDiaChi.setEnabled(false);
        txtVaiTro.setEnabled(false);

    }//GEN-LAST:event_jpQLTKMouseClicked

    private void jpQLKMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpQLKMMouseClicked
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(true);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLBH2.setVisible(false);
        this.jpViewDoiMK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);
        this.jpQLBH.setBackground(new Color(255, 102, 0));
        this.jpQLDM.setBackground(new Color(255, 102, 0));
        this.jpQLKH.setBackground(new Color(255, 102, 0));
        this.jpQLKM.setBackground(new Color(220, 220, 220));
        this.jpQLSP.setBackground(new Color(255, 102, 0));
        this.jpQLTK.setBackground(new Color(255, 102, 0));
        this.jpTK.setBackground(new Color(255, 102, 0));
        this.jpQLHD.setBackground(new Color(255, 102, 0));

        try {

            jdc_ngay_bat_dau_voucher.setDate(null);
            jdc_ngay_ket_thuc_voucher.setDate(null);
            txt_so_luong_vou.setText("");
            txtMaGiamGia.setText("");
            txtCode.setText("");
            txtMoTa.setText("");
            txtPhanTramChietKhau.setText("");
            txtMa_chiet_khau.setText("");
            txt_tien_toi_da.setText("");
            txt_tien_ap_dung.setText("");
            txt_so_tien_giam.setText("");
            ProductDetails_Service pro = new ProductDetails_Service();
            PointsConversion_Service psv = new PointsConversion_Service();
            DiscountService dis = new DiscountService();
            VoucherService vou = new VoucherService();
            ProductDetails x = null;
            txtPoint1.setEditable(false);
            txtCode.setEditable(false);
            txtMaGiamGia.setEnabled(false);
            txtMoTa.setEnabled(false);
            txtCode.setEnabled(false);
            txtPhanTramChietKhau.setEnabled(false);
            jdc_ngay_bat_dau_voucher.setEnabled(false);
            jdc_ngay_ket_thuc_voucher.setEnabled(false);
            txt_tien_ap_dung.setEnabled(false);
            txt_tien_toi_da.setEnabled(false);
            txt_so_luong_vou.setEnabled(false);
            txt_so_tien_giam.setEnabled(false);
            txtMa_chiet_khau.setEnabled(false);
            txtPoint.setText(String.valueOf(1));
            int diemSangTien = (int) psv.diemSangTien();
            double tienSangDiem = psv.tienSangDiem();
            txtMoney1.setText(String.valueOf(diemSangTien));
            txtMoney.setText(String.valueOf(tienSangDiem));
            txtPoint1.setText(String.valueOf(1000));
            totalRecords = vou.getTotalRecords();
            totalPages = (int) Math.ceil((double) totalRecords / itemsPerPagee);
            txt_phan_trangVoucher.setText("1" + "/" + totalPages);
            vou.loadTableVoucher(tblCoupon, currentPage, itemsPerPagee);
            pro.loadTableProductDetails(tbl_san_pham_khuyen_mai);
            dis.loadTableDisscount(tblDiscount);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }//GEN-LAST:event_jpQLKMMouseClicked

    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangXuatActionPerformed
        View_Login loginView = new View_Login();
        loginView.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnDangXuatActionPerformed

    private void txtTenSanPhamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenSanPhamActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenSanPhamActionPerformed

    private int getCustomerId(String tenKhachHang, String soDienThoai) throws SQLException {
        // Kiểm tra định dạng số điện thoại
        if (!soDienThoai.matches("^0[0-9]{9}$")) {
            throw new SQLException("Số điện thoại không hợp lệ! Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số.");
        }
        String queryCheck = "SELECT customer_id FROM Customers WHERE customer_name = ? AND phone = ?";
        String queryInsert = "INSERT INTO Customers (customer_name, phone, point_gained, points_used) VALUES (?, ?, 0, 0)";

        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstCheck = connection.prepareStatement(queryCheck); PreparedStatement pstInsert = connection.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS)) {

            pstCheck.setString(1, tenKhachHang);
            pstCheck.setString(2, soDienThoai);

            try (ResultSet rs = pstCheck.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    pstInsert.setString(1, tenKhachHang);
                    pstInsert.setString(2, soDienThoai);
                    pstInsert.executeUpdate();

                    try (ResultSet generatedKeys = pstInsert.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Thêm khách hàng thất bại, không có ID được tạo.");
                        }
                    }
                }
            }
        }
    }

    private int getProductDetailsId(String productDetailsCode) throws SQLException {
        String query = "SELECT productDetails_id FROM ProductDetails WHERE productDetails_code = ?";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {

            pst.setString(1, productDetailsCode);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("productDetails_id");
                } else {
                    throw new SQLException("Không tìm thấy chi tiết sản phẩm cho mã sản phẩm chi tiết: " + productDetailsCode);
                }
            }
        }
    }

    private void resetInvoiceData() {
        txtNhapSoLuong1.setText("");
        txtTenKhachHang.setText("");
        txtSDTKhachHang.setText("");
        txtSoDiemDaDung.setText("");
        lblTienTruocKhiGiam.setText("0");
        DefaultTableModel model = (DefaultTableModel) tblDSSPtrongHD.getModel();
        model.setRowCount(0);
        lblTongTienThanhToan.setText("0.00 VNĐ");
        lblDiemConLai.setText("");
//    ql.DanhSachHD(tblDSHD);
        tblDSHD.setModel(qlHoaDon.loadOrderData());
        cbbVouchers.setSelectedIndex(0);
    }

    private void xoaHDCkhiThanhToan() {
        int selectedRow = tblHoaDonCho.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) tblHoaDonCho.getModel();
            model.removeRow(selectedRow); // Xóa dòng được chọn
        }
    }

    private double getVoucherDiscountPercent(String voucherCode) throws SQLException {
        String query = "SELECT discount_value FROM Voucher WHERE voucher_code = ?";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {

            pst.setString(1, voucherCode);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("discount_value");
                } else {
                    throw new SQLException("Không tìm thấy voucher cho mã: " + voucherCode);
                }
            }
        }
    }

    private String getEmployeeNameById(int userId) {
        String hoTen = null;
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstGetEmployeeName = connection.prepareStatement("SELECT hoTen FROM Users WHERE user_id = ?")) {
            pstGetEmployeeName.setInt(1, userId);
            ResultSet rs = pstGetEmployeeName.executeQuery();
            if (rs.next()) {
                hoTen = rs.getString("hoTen");
            } else {
                System.out.println("No employee name found for userId: " + userId);  // Debug line
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy họ tên nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return hoTen;
    }

    private void insertOrderDetails(long maHoaDon) throws SQLException {
        String queryInsertOrderDetails = "INSERT INTO OrderDetails (order_id, quantity, unit_price, productDetails_id) VALUES (?, ?, ?, ?)";
        String queryUpdateProductDetails = "UPDATE ProductDetails SET quantity = quantity - ? WHERE productDetails_id = ?";

        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstInsertOrderDetails = connection.prepareStatement(queryInsertOrderDetails); PreparedStatement pstUpdateProductDetails = connection.prepareStatement(queryUpdateProductDetails)) {

            for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
                try {
                    String productDetailsCode = tblDSSPtrongHD.getValueAt(i, 1).toString();
                    int productDetailsId = getProductDetailsId(productDetailsCode);
                    int soLuong = Integer.parseInt(tblDSSPtrongHD.getValueAt(i, 7).toString());
                    double gia = Double.parseDouble(tblDSSPtrongHD.getValueAt(i, 8).toString().replace(" VNĐ", "").replace(",", ".")); // Giả định cột giá bán là cột thứ 7

                    pstInsertOrderDetails.setLong(1, maHoaDon);
                    pstInsertOrderDetails.setInt(2, soLuong);
                    pstInsertOrderDetails.setDouble(3, gia);
                    pstInsertOrderDetails.setInt(4, productDetailsId);
                    pstInsertOrderDetails.addBatch();

                    pstUpdateProductDetails.setInt(1, soLuong);
                    pstUpdateProductDetails.setInt(2, productDetailsId);
                    pstUpdateProductDetails.addBatch();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi định dạng số tại dòng " + (i + 1) + ": " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

            pstInsertOrderDetails.executeBatch();
            pstUpdateProductDetails.executeBatch();
        }
    }
    private String lastProcessedOrderCode;

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        try {
            int selectedRow = tblHoaDonCho.getSelectedRow();

            String tenKhachHang = txtTenKhachHang.getText().trim();
            String soDienThoai = txtSDTKhachHang.getText().trim();
            int diemDaDung = Integer.parseInt(txtSoDiemDaDung.getText().trim().isEmpty() ? "0" : txtSoDiemDaDung.getText().trim());
            String phuongThucThanhToan = rdoTienMat.isSelected() ? "Tiền mặt" : "Chuyển khoản";
            String voucherCode = (String) cbbVouchers.getSelectedItem();

            double voucherDiscountPercent = 0;
            double maximumReduce = 0;
            double moneyApplies = 0;

            if (voucherCode != null && !voucherCode.isEmpty()) {
                voucherDiscountPercent = getVoucherDiscountPercent(voucherCode);
                String query = "SELECT money_applies, maximum_reduce FROM Voucher WHERE voucher_code = ?";
                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setString(1, voucherCode);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        moneyApplies = rs.getDouble("money_applies");
                        maximumReduce = rs.getDouble("maximum_reduce");
                    }
                }
            }

            double tongTienChuaVAT = 0;
            double VAT = 0.08;
            for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
                int soLuong = Integer.parseInt(tblDSSPtrongHD.getValueAt(i, 7).toString());
                String giaBanString = tblDSSPtrongHD.getValueAt(i, 8).toString().replace(" VNĐ", "").replace(",", ".");
                double giaBan = Double.parseDouble(giaBanString);
                tongTienChuaVAT += soLuong * giaBan;
            }

            double tienVAT = tongTienChuaVAT * VAT;
            double tongTienCoVAT = tongTienChuaVAT + tienVAT;

            double tongTienSauVoucher = tongTienCoVAT;

            if (tongTienCoVAT >= moneyApplies) {
                double discountAmount = tongTienCoVAT * (voucherDiscountPercent / 100);
                if (discountAmount > maximumReduce) {
                    discountAmount = maximumReduce;
                }
                tongTienSauVoucher -= discountAmount;
            } else {
                voucherCode = null;
            }

            double pointsIntoMoney = 0;
            String queryPointsConversion = "SELECT points_into_money FROM PointsConversion";
            try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstPointsConversion = connection.prepareStatement(queryPointsConversion); ResultSet rsPointsConversion = pstPointsConversion.executeQuery()) {
                if (rsPointsConversion.next()) {
                    pointsIntoMoney = rsPointsConversion.getDouble("points_into_money");
                }
            }

            int customerId = getCustomerId(tenKhachHang, soDienThoai);
            int pointGained = 0;
            int pointsUsed = 0;
            String queryCustomerPoints = "SELECT point_gained, points_used FROM Customers WHERE customer_id = ?";
            try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstCustomerPoints = connection.prepareStatement(queryCustomerPoints)) {
                pstCustomerPoints.setInt(1, customerId);
                ResultSet rsCustomerPoints = pstCustomerPoints.executeQuery();
                if (rsCustomerPoints.next()) {
                    pointGained = rsCustomerPoints.getInt("point_gained");
                    pointsUsed = rsCustomerPoints.getInt("points_used");
                }
            }
            int pointsRemaining = pointGained - pointsUsed;

            if (diemDaDung > pointsRemaining) {
                JOptionPane.showMessageDialog(null, "Số điểm nhập vào vượt quá số điểm còn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double discountFromPoints = diemDaDung / pointsIntoMoney * 1000;
            tongTienSauVoucher -= discountFromPoints;

            lblTongTienThanhToan.setText(String.format("%.2f VNĐ", tongTienSauVoucher));

            if (tenKhachHang.isEmpty() || soDienThoai.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (tblDSSPtrongHD.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Giỏ hàng chưa có sản phẩm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String tenNhanVien = getEmployeeNameById(this.currentUserId);
            lblNhanVienBan.setText(tenNhanVien != null ? tenNhanVien : "Không tìm thấy tên nhân viên");

            // nếu có hóa đơn chờ đã chọn và bấm thanh toán thì update stt thành đã thanh toán
            if (selectedRow != -1) {
                String maHoaDon = tblHoaDonCho.getValueAt(selectedRow, 1).toString();

                String checkInvoiceQuery = "SELECT status FROM Orders WHERE order_code = ?";
                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstCheckInvoice = connection.prepareStatement(checkInvoiceQuery)) {

                    pstCheckInvoice.setString(1, maHoaDon);
                    ResultSet rs = pstCheckInvoice.executeQuery();

                    if (rs.next()) {
                        String currentStatus = rs.getString("status");

                        if ("Chưa thanh toán".equals(currentStatus)) {
                            capNhatSoLuongKho(connection, maHoaDon);

                            String updateInvoiceQuery = "UPDATE Orders SET status = N'Đã thanh toán', total_price = ?, payment_method = ?, voucher_id = (SELECT voucher_id FROM Voucher WHERE voucher_code = ?) WHERE order_code = ?";
                            try (PreparedStatement pstUpdateInvoice = connection.prepareStatement(updateInvoiceQuery)) {
                                pstUpdateInvoice.setDouble(1, tongTienSauVoucher);
                                pstUpdateInvoice.setString(2, phuongThucThanhToan);
                                if (voucherCode != null) {
                                    pstUpdateInvoice.setString(3, voucherCode);
                                } else {
                                    pstUpdateInvoice.setNull(3, Types.VARCHAR);
                                }
                                pstUpdateInvoice.setString(4, maHoaDon);
                                pstUpdateInvoice.executeUpdate();
                            }

                            CapNhatHDCT(connection, maHoaDon);

                            if (voucherCode != null && !voucherCode.isEmpty()) {
                                String updateVoucherQuery = "UPDATE Voucher SET quantity_voucher = quantity_voucher - 1 WHERE voucher_code = ?";
                                try (PreparedStatement pstUpdateVoucher = connection.prepareStatement(updateVoucherQuery)) {
                                    pstUpdateVoucher.setString(1, voucherCode);
                                    pstUpdateVoucher.executeUpdate();
                                }
                            }

                            if (!guestPhoneNumbers.contains(soDienThoai)) {
                                // Tính điểm tích lũy
                                double moneyIntoPoints = 0;
                                String queryPointsConversio = "SELECT money_into_points FROM PointsConversion";
                                try (PreparedStatement pstPointsConversion = connection.prepareStatement(queryPointsConversio); ResultSet rsPointsConversion = pstPointsConversion.executeQuery()) {
                                    if (rsPointsConversion.next()) {
                                        moneyIntoPoints = rsPointsConversion.getDouble("money_into_points");
                                    }
                                }

                                int newPoints = (int) (tongTienCoVAT / moneyIntoPoints);

                                String updateCustomerPointsQuery = "UPDATE Customers SET point_gained = point_gained + ?, points_used = points_used + ? WHERE customer_id = ?";
                                try (PreparedStatement pstUpdateCustomerPoints = connection.prepareStatement(updateCustomerPointsQuery)) {
                                    pstUpdateCustomerPoints.setInt(1, newPoints);
                                    pstUpdateCustomerPoints.setInt(2, diemDaDung);
                                    pstUpdateCustomerPoints.setInt(3, customerId);
                                    pstUpdateCustomerPoints.executeUpdate();
                                }
                            }

                            resetInvoiceData();

                            JOptionPane.showMessageDialog(null, "Hóa đơn đã được cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            xoaHDCkhiThanhToan();
                            tiepTucThanhToan = false;
                            tblDSHD.setModel(qlHoaDon.loadOrderData());
                            this.qlkh.loadTableCus(this.tblKhachH, this.qlkh.getAllCustomer());
                            lastProcessedOrderCode = maHoaDon;
                            xuatPDF();
                        } else {
                            JOptionPane.showMessageDialog(null, "Hóa đơn đã được thanh toán trước đó!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi kiểm tra hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                String queryHoaDon = "INSERT INTO Orders (user_id, order_date, status, customer_id, voucher_id, VAT, payment_method, total_price, last_update_date_point) VALUES (?, ?, ?, ?, (SELECT voucher_id FROM Voucher WHERE voucher_code = ?), ?, ?, ?, ?)";

                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstHoaDon = connection.prepareStatement(queryHoaDon, Statement.RETURN_GENERATED_KEYS)) {

                    pstHoaDon.setInt(1, this.currentUserId);
                    pstHoaDon.setDate(2, new java.sql.Date(System.currentTimeMillis()));
                    pstHoaDon.setString(3, "Đã thanh toán");
                    pstHoaDon.setInt(4, customerId);
                    if (voucherCode != null) {
                        pstHoaDon.setString(5, voucherCode);
                    } else {
                        pstHoaDon.setNull(5, Types.VARCHAR);
                    }
                    pstHoaDon.setDouble(6, VAT * 100);
                    pstHoaDon.setString(7, phuongThucThanhToan);
                    pstHoaDon.setDouble(8, tongTienSauVoucher);
                    pstHoaDon.setDate(9, new java.sql.Date(System.currentTimeMillis()));
                    pstHoaDon.executeUpdate();

                    try (ResultSet generatedKeys = pstHoaDon.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            long maHoaDonMoi = generatedKeys.getLong(1);
                            insertOrderDetails(maHoaDonMoi); // Chèn chi tiết đơn hàng mới
                        } else {
                            throw new SQLException("Tạo hóa đơn thất bại, không có ID được tạo.");
                        }
                    }

                    if (voucherCode != null && !voucherCode.isEmpty()) {
                        String updateVoucherQuery = "UPDATE Voucher SET quantity_voucher = quantity_voucher - 1 WHERE voucher_code = ?";
                        try (PreparedStatement pstUpdateVoucher = connection.prepareStatement(updateVoucherQuery)) {
                            pstUpdateVoucher.setString(1, voucherCode);
                            pstUpdateVoucher.executeUpdate();
                        }
                    }

                    if (!guestPhoneNumbers.contains(soDienThoai)) {
                        double moneyIntoPoints = 0;
                        String queryPointsConver = "SELECT money_into_points FROM PointsConversion";
                        try (PreparedStatement pstPointsConversion = connection.prepareStatement(queryPointsConver); ResultSet rsPointsConversion = pstPointsConversion.executeQuery()) {
                            if (rsPointsConversion.next()) {
                                moneyIntoPoints = rsPointsConversion.getDouble("money_into_points");
                            }
                        }

                        int newPoints = (int) (tongTienCoVAT / moneyIntoPoints);

                        String updateCustomerPointsQuery = "UPDATE Customers SET point_gained = point_gained + ?, points_used = points_used + ? WHERE customer_id = ?";
                        try (PreparedStatement pstUpdateCustomerPoints = connection.prepareStatement(updateCustomerPointsQuery)) {
                            pstUpdateCustomerPoints.setInt(1, newPoints);
                            pstUpdateCustomerPoints.setInt(2, diemDaDung);
                            pstUpdateCustomerPoints.setInt(3, customerId);
                            pstUpdateCustomerPoints.executeUpdate();
                        }
                    }

                    resetInvoiceData();

                    JOptionPane.showMessageDialog(null, "Hóa đơn đã được thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    tiepTucThanhToan = false;
                    tblDSHD.setModel(qlHoaDon.loadOrderData());
                    this.qlkh.loadTableCus(this.tblKhachH, this.qlkh.getAllCustomer());
                    ql.LoadDSSPTrongQLBH(tblDSSanPham);
                    lastProcessedOrderCode = generatedOrderCode();
                    xuatPDF();
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi tạo hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số điểm hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnThanhToanActionPerformed
     

       ArrayList<String> guestPhoneNumbers = new ArrayList<>(Arrays.asList(
            "0000000000", "0000000001", "0000000002", "0000000003", "0000000004",
            "0000000005", "0000000006", "0000000007", "0000000008", "0000000009"
    ));
       
    private String generatedOrderCode() {
        String query = "SELECT TOP 1 order_code FROM Orders ORDER BY order_id DESC";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getString("order_code");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Order getOrderByOrderCode(String orderCode) {
        String query = "SELECT o.order_code, o.order_date, o.status, o.payment_method, o.total_price, o.VAT, "
                + "c.customer_name, c.phone, v.voucher_code, u.hoTen "
                + "FROM Orders o "
                + "LEFT JOIN Customers c ON o.customer_id = c.customer_id "
                + "LEFT JOIN Voucher v ON o.voucher_id = v.voucher_id "
                + "LEFT JOIN Users u ON o.user_id = u.user_id "
                + "WHERE o.order_code = ?";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, orderCode);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Tạo đối tượng Order từ kết quả truy vấn
                Order order = new Order();
                order.setOrderCode(rs.getString("order_code"));
                order.setOrderDate(rs.getString("order_date")); // Sử dụng Date để lưu trữ ngày
                order.setStatus(rs.getString("status"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setVat(rs.getDouble("VAT"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setPhone(rs.getString("phone"));
                order.setVoucherCode(rs.getString("voucher_code"));
                order.setUserName(rs.getString("hoTen")); // Lấy tên nhân viên
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void xuatPDF() {
        if (lastProcessedOrderCode == null) {
            JOptionPane.showMessageDialog(this, "Không có hóa đơn nào để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Order order = getOrderByOrderCode(lastProcessedOrderCode); // Lấy hóa đơn theo mã

        if (order == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userHome = System.getProperty("user.home");
        String desktopPath = userHome + "/Desktop/xuatPDF/";
        File directory = new File(desktopPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String dest = desktopPath + "hoadon_" + order.getOrderCode() + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            String fontPath = "C:/Windows/Fonts/times.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
            document.add(new Paragraph("Hóa đơn: " + order.getOrderCode())
                    .setFont(font)
                    .setFontSize(16));

            document.add(new Paragraph("Tên Nhân viên: " + order.getUserName())
                    .setFont(font)
                    .setFontSize(14));

            document.add(new Paragraph("Tên khách hàng: " + order.getCustomerName())
                    .setFont(font)
                    .setFontSize(14));

            document.add(new Paragraph("Số điện thoại: " + order.getPhone())
                    .setFont(font)
                    .setFontSize(14));

            document.add(new Paragraph("Ngày tạo: " + order.getOrderDate())
                    .setFont(font)
                    .setFontSize(14));

            document.add(new Paragraph("Mã giảm giá: " + (order.getVoucherCode() != null ? order.getVoucherCode() : "Không có"))
                    .setFont(font)
                    .setFontSize(14));

            document.add(new Paragraph("VAT: " + order.getVat())
                    .setFont(font)
                    .setFontSize(14));

            document.add(new Paragraph("Phương thức thanh toán: " + order.getPaymentMethod())
                    .setFont(font)
                    .setFontSize(14));

            document.add(new Paragraph("Tổng giá: " + order.getTotalPrice())
                    .setFont(font)
                    .setFontSize(16)
                    .setBold());

            document.add(new Paragraph("Trạng thái: " + order.getStatus())
                    .setFont(font)
                    .setFontSize(14));
            document.close();

            JOptionPane.showMessageDialog(this, "Xuất PDF thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF: " + e.getMessage(), "Thông báo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inSertHoaDonChiTiet(long orderId) throws SQLException {
        String getProductIdQuery = "SELECT productDetails_id FROM ProductDetails WHERE productDetails_code = ?";
        String insertOrderDetailsQuery = "INSERT INTO OrderDetails (order_id, productDetails_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstGetProductId = connection.prepareStatement(getProductIdQuery); PreparedStatement pstInsertOrderDetails = connection.prepareStatement(insertOrderDetailsQuery)) {

            for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
                String productDetailsCode = tblDSSPtrongHD.getValueAt(i, 1).toString();
                int quantity = Integer.parseInt(tblDSSPtrongHD.getValueAt(i, 7).toString());
                String priceString = tblDSSPtrongHD.getValueAt(i, 8).toString().replace(" VNĐ", "").replace(",", ".");
                double price = Double.parseDouble(priceString);

                pstGetProductId.setString(1, productDetailsCode);
                ResultSet rs = pstGetProductId.executeQuery();
                if (rs.next()) {
                    int productDetailsId = rs.getInt("productDetails_id");
                    // them tt chi tiết đơn hàng vào csdl
                    pstInsertOrderDetails.setLong(1, orderId);
                    pstInsertOrderDetails.setInt(2, productDetailsId);
                    pstInsertOrderDetails.setInt(3, quantity);
                    pstInsertOrderDetails.setDouble(4, price);
                    pstInsertOrderDetails.addBatch();
                } else {
                    System.err.println("Không tìm thấy productDetails_id cho mã sản phẩm: " + productDetailsCode);
                }
            }

            // tien hành chen
            pstInsertOrderDetails.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Đã xảy ra lỗi khi chèn chi tiết hóa đơn: " + e.getMessage());
        }
    }

    private void capNhatSoLuongKho(Connection connection, String maHoaDon) throws SQLException {
        // Lấy số lượng sản phẩm cũ từ hóa đơn
        String getOrderDetailsQuery = "SELECT productDetails_id, quantity FROM OrderDetails WHERE order_id = (SELECT order_id FROM Orders WHERE order_code = ?)";
        Map<Integer, Integer> oldQuantities = new HashMap<>();

        try (PreparedStatement pstGetOrderDetails = connection.prepareStatement(getOrderDetailsQuery)) {
            pstGetOrderDetails.setString(1, maHoaDon);
            ResultSet rs = pstGetOrderDetails.executeQuery();
            while (rs.next()) {
                int productDetailsId = rs.getInt("productDetails_id");
                int quantity = rs.getInt("quantity");
                oldQuantities.put(productDetailsId, quantity);
            }
        }

        // Tạo ánh xạ từ productDetails_code đến productDetails_id
        String getProductIdQuery = "SELECT productDetails_id FROM ProductDetails WHERE productDetails_code = ?";
        Map<String, Integer> productCodeToId = new HashMap<>();

        for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
            String productDetailsCode = tblDSSPtrongHD.getValueAt(i, 1).toString();
            if (!productCodeToId.containsKey(productDetailsCode)) {
                try (PreparedStatement pstGetProductId = connection.prepareStatement(getProductIdQuery)) {
                    pstGetProductId.setString(1, productDetailsCode);
                    ResultSet rs = pstGetProductId.executeQuery();
                    if (rs.next()) {
                        int productDetailsId = rs.getInt("productDetails_id");
                        productCodeToId.put(productDetailsCode, productDetailsId);
                    }
                }
            }
        }

        //cap nhật sl sp trong hdlt
        String updateInventoryQuery = "UPDATE ProductDetails SET quantity = quantity + ? WHERE productDetails_id = ?";
        try (PreparedStatement pstUpdateInventory = connection.prepareStatement(updateInventoryQuery)) {
            for (Map.Entry<Integer, Integer> entry : oldQuantities.entrySet()) {
                int productDetailsId = entry.getKey();
                int oldQuantity = entry.getValue();
                String productDetailsCode = null;
                for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
                    String code = tblDSSPtrongHD.getValueAt(i, 1).toString();
                    if (productCodeToId.containsKey(code) && productCodeToId.get(code) == productDetailsId) {
                        productDetailsCode = code;
                        break;
                    }
                }

                //k thấy trong giỏ hàng thì cập nhật vào kho
                if (productDetailsCode == null) {
                    pstUpdateInventory.setInt(1, oldQuantity);
                    pstUpdateInventory.setInt(2, productDetailsId);
                    pstUpdateInventory.executeUpdate();
                }
            }

            // câp nhật sl kho cho sp vừa thêm
            for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
                String productDetailsCode = tblDSSPtrongHD.getValueAt(i, 1).toString();
                String newQuantityStr = tblDSSPtrongHD.getValueAt(i, 7).toString();

                try {
                    int newQuantity = Integer.parseInt(newQuantityStr);
                    int productDetailsId = productCodeToId.getOrDefault(productDetailsCode, -1);

                    if (productDetailsId != -1) {
                        int oldQuantity = oldQuantities.getOrDefault(productDetailsId, 0);
                        int quantityDifference = oldQuantity - newQuantity;

                        if (quantityDifference != 0) {
                            pstUpdateInventory.setInt(1, quantityDifference); // Gia tăng nếu quantityDifference > 0, giảm nếu < 0
                            pstUpdateInventory.setInt(2, productDetailsId);
                            pstUpdateInventory.executeUpdate();
                        }
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Có lỗi trong dữ liệu sản phẩm: " + e.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void CapNhatHDCT(Connection connection, String maHoaDon) throws SQLException {
        String updateOrderDetailsQuery = "DELETE FROM OrderDetails WHERE order_id = (SELECT order_id FROM Orders WHERE order_code = ?)";
        try (PreparedStatement pstUpdateOrderDetails = connection.prepareStatement(updateOrderDetailsQuery)) {
            pstUpdateOrderDetails.setString(1, maHoaDon);
            pstUpdateOrderDetails.executeUpdate();

            inSertHoaDonChiTiet(getOrderIdFromOrderCode(connection, maHoaDon));
        }

    }

    private long getOrderIdFromOrderCode(Connection connection, String maHoaDon) throws SQLException {
        String query = "SELECT order_id FROM Orders WHERE order_code = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, maHoaDon);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getLong("order_id");
            } else {
                throw new SQLException("Không tìm thấy mã đơn hàng.");
            }
        }
    }

    private void cbbVouchersActionPerformed(java.awt.event.ActionEvent evt) {
        CapNhatGiaKhiChonVoucher();
    }

    private void CapNhatGiaKhiChonVoucher() {
        try {
            String voucherCode = (String) cbbVouchers.getSelectedItem();
            double voucherDiscountPercent = 0;
            double maximumReduce = 0;
            double moneyApplies = 0;

            if (voucherCode != null && !voucherCode.isEmpty()) {
                voucherDiscountPercent = getVoucherDiscountPercent(voucherCode);
                String query = "SELECT money_applies, maximum_reduce FROM Voucher WHERE voucher_code = ?";
                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setString(1, voucherCode);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        moneyApplies = rs.getDouble("money_applies");
                        maximumReduce = rs.getDouble("maximum_reduce");
                    }
                }
            }

            double tongTienChuaVAT = 0;
            double VAT = 0.08;

            for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
                int soLuong = Integer.parseInt(tblDSSPtrongHD.getValueAt(i, 7).toString());
                String giaBanString = tblDSSPtrongHD.getValueAt(i, 8).toString().replace(" VNĐ", "").replace(",", ".");
                double giaBan = Double.parseDouble(giaBanString);
                tongTienChuaVAT += soLuong * giaBan;
            }

            double tienVAT = tongTienChuaVAT * VAT;
            double tongTienCoVAT = tongTienChuaVAT + tienVAT;

            double tongTienSauVoucher = tongTienCoVAT;
            if (tongTienCoVAT >= moneyApplies) {
                double discountAmount = tongTienCoVAT * (voucherDiscountPercent / 100);
                // Giảm tối đa theo cột maximum_reduce
                if (discountAmount > maximumReduce) {
                    discountAmount = maximumReduce;
                }
                tongTienSauVoucher -= discountAmount;
            }

            lblTongTienThanhToan.setText(String.format("%.2f VNĐ", tongTienSauVoucher));
        } catch (SQLException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi khi tính tổng tiền: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

 
    private void tblDSSanPhamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDSSanPhamMouseClicked
        int selectedRow = tblDSSanPham.getSelectedRow();
        if (selectedRow >= 0) {

            String stt = tblDSSanPham.getValueAt(selectedRow, 0).toString();
            String maSPCT = tblDSSanPham.getValueAt(selectedRow, 1).toString();
            String tenSanPham = tblDSSanPham.getValueAt(selectedRow, 2).toString();
            String loaiCuaSP = tblDSSanPham.getValueAt(selectedRow, 3).toString();
            String mauSac = tblDSSanPham.getValueAt(selectedRow, 4).toString();
            String kichThuoc = tblDSSanPham.getValueAt(selectedRow, 5).toString();
            String phongCach = tblDSSanPham.getValueAt(selectedRow, 6).toString();
            String nhapSoLuong = tblDSSanPham.getValueAt(selectedRow, 7).toString();

            txtMaSPCT.setText(maSPCT);
            txtTenSanPham.setText(tenSanPham);
            txtLoaiSPpp.setText(loaiCuaSP);
            txtMauSaccc.setText(mauSac);
            txtKichThuoccc.setText(kichThuoc);
            txtPhongCachhh.setText(phongCach);
            txtMaSPCT.setEditable(false);
            txtTenSanPham.setEditable(false);
            txtLoaiSPpp.setEditable(false);
            txtMauSaccc.setEditable(false);
            txtKichThuoccc.setEditable(false);
            txtPhongCachhh.setEditable(false);
        }
    }//GEN-LAST:event_tblDSSanPhamMouseClicked
    private String maSP;
    private String tenSP;
    private String loaiSP;
    private String mauSac;
    private String kichThuoc;
    private String phongCach;
    private double giaBan;
    private ArrayList<Object[]> danhSachSanPhamDaChon = new ArrayList<>();
    private void btnThemSPVaoHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemSPVaoHDActionPerformed
        try {
            int soLuongNhapVao;
            try {
                soLuongNhapVao = Integer.parseInt(txtNhapSoLuong.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (soLuongNhapVao <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int selectedRow = tblDSSanPham.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Chọn một sản phẩm từ danh sách", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int soLuongHienCo;
            double giaBan;
            try {
                soLuongHienCo = Integer.parseInt(tblDSSanPham.getValueAt(selectedRow, 7).toString());
                giaBan = Double.parseDouble(tblDSSanPham.getValueAt(selectedRow, 8).toString());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Dữ liệu trong bảng không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (soLuongNhapVao > soLuongHienCo) {
                JOptionPane.showMessageDialog(this, "Số lượng nhập vào vượt quá số lượng hiện có", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String maSPCT = tblDSSanPham.getValueAt(selectedRow, 1).toString();
            String tenSP = tblDSSanPham.getValueAt(selectedRow, 2).toString();
            String loaiSP = tblDSSanPham.getValueAt(selectedRow, 3).toString();
            String mauSac = tblDSSanPham.getValueAt(selectedRow, 4).toString();
            String kichThuoc = tblDSSanPham.getValueAt(selectedRow, 5).toString();
            String phongCach = tblDSSanPham.getValueAt(selectedRow, 6).toString();

            DefaultTableModel modelTrongHD = (DefaultTableModel) tblDSSPtrongHD.getModel();
            boolean found = false;
            int stt = modelTrongHD.getRowCount() + 1;

            for (int i = 0; i < modelTrongHD.getRowCount(); i++) {
                if (modelTrongHD.getValueAt(i, 1).toString().equals(maSPCT)) { // Kiểm tra mã SPCT
                    int soLuongHienCoTrongHD = Integer.parseInt(modelTrongHD.getValueAt(i, 7).toString());
                    int soLuongMoiTrongHD = soLuongHienCoTrongHD + soLuongNhapVao;
                    modelTrongHD.setValueAt(soLuongMoiTrongHD, i, 7); // Cập nhật lại số lượng trong giỏ hàng
                    found = true;
                    break;
                }
            }

            if (!found) {
                modelTrongHD.addRow(new Object[]{stt, maSPCT, tenSP, loaiSP, mauSac, kichThuoc, phongCach, soLuongNhapVao, giaBan});
            }

            int soLuongMoi = soLuongHienCo - soLuongNhapVao;
            tblDSSanPham.setValueAt(soLuongMoi, selectedRow, 7); // Cập nhật lại cột số lượng
            danhSachSanPhamDaChon.add(new Object[]{stt, maSPCT, tenSP, loaiSP, mauSac, kichThuoc, phongCach, soLuongNhapVao, giaBan});
            tinhTongTien();
            CapNhatGiaKhiChonVoucher();
            txtNhapSoLuong.setText("");
            lamMoi();

        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnThemSPVaoHDActionPerformed
    private void tinhTongTien() {
        double tongTienChuaVAT = 0;
        double VAT = 0.08;
        for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
            int soLuong = Integer.parseInt(tblDSSPtrongHD.getValueAt(i, 7).toString());
            String giaBanString = tblDSSPtrongHD.getValueAt(i, 8).toString().replace(" VNĐ", "").replace(",", ".");
            double giaBan = Double.parseDouble(giaBanString);
            tongTienChuaVAT += soLuong * giaBan;
        }

        double tienVAT = tongTienChuaVAT * VAT;
        double tongTienCoVAT = tongTienChuaVAT + tienVAT;
        double tongTienSauDiem = tongTienCoVAT;
        try {
            String diemDaDungText = txtSoDiemDaDung.getText().trim();
            if (!diemDaDungText.isEmpty()) {
                int diemDaDung = Integer.parseInt(diemDaDungText);
                double pointsIntoMoney = 0;
                String queryPointsConversion = "SELECT points_into_money FROM PointsConversion";
                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstPointsConversion = connection.prepareStatement(queryPointsConversion); ResultSet rsPointsConversion = pstPointsConversion.executeQuery()) {
                    if (rsPointsConversion.next()) {
                        pointsIntoMoney = rsPointsConversion.getDouble("points_into_money");
                    }
                }
                int pointsRemaining = Integer.parseInt(lblDiemConLai.getText().replace(" điểm", "").trim());
                if (diemDaDung > pointsRemaining) {
                    JOptionPane.showMessageDialog(null, "Số điểm nhập vào vượt quá số điểm còn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    txtSoDiemDaDung.setText("");
                    return;
                }
                double moneyIsReduced = diemDaDung / pointsIntoMoney * 1000;
                tongTienSauDiem -= moneyIsReduced;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số điểm hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSoDiemDaDung.setText("");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi tính tổng tiền: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String voucherCode = (String) cbbVouchers.getSelectedItem();
            double voucherDiscountPercent = 0;
            double maximumReduce = 0;
            double moneyApplies = 0;

            if (voucherCode != null && !voucherCode.isEmpty()) {
                voucherDiscountPercent = getVoucherDiscountPercent(voucherCode);
                String query = "SELECT money_applies, maximum_reduce FROM Voucher WHERE voucher_code = ?";
                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setString(1, voucherCode);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        moneyApplies = rs.getDouble("money_applies");
                        maximumReduce = rs.getDouble("maximum_reduce");
                    }
                }
            }

            double tongTienSauVoucher = tongTienSauDiem;

            if (tongTienSauDiem >= moneyApplies) {
                double discountAmount = tongTienSauDiem * (voucherDiscountPercent / 100);
                // giảm tối đa dựa vào maximum_reduce
                if (discountAmount > maximumReduce) {
                    discountAmount = maximumReduce;
                }
                tongTienSauVoucher -= discountAmount;
            }

            lblTienTruocKhiGiam.setText(String.format("%.2f VNĐ", tongTienCoVAT));
            lblTongTienThanhToan.setText(String.format("%.2f VNĐ", tongTienSauVoucher));
        } catch (SQLException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi khi tính tổng tiền: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void btnCapNhatGioHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatGioHangActionPerformed

        int selectedRowTrongHD = tblDSSPtrongHD.getSelectedRow();
        if (selectedRowTrongHD != -1) {
            String giaTriNhapVao = txtNhapSoLuong1.getText().trim();
            System.out.println("Giá trị nhập vào: " + giaTriNhapVao);

            int soLuongMoi;
            try {
                soLuongMoi = Integer.parseInt(giaTriNhapVao);
                if (soLuongMoi <= 0) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Lỗi số lượng nhập vào: " + giaTriNhapVao);
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DefaultTableModel modelTrongHD = (DefaultTableModel) tblDSSPtrongHD.getModel();
            int soLuongCu = Integer.parseInt(modelTrongHD.getValueAt(selectedRowTrongHD, 7).toString().trim()); // Số lượng cũ ở cột thứ 7

            int soLuongChenhLech = soLuongMoi - soLuongCu;

            String maSPCT = modelTrongHD.getValueAt(selectedRowTrongHD, 1).toString().trim(); // Mã SPCT ở cột thứ 1

            DefaultTableModel modelDanhSachSP = (DefaultTableModel) tblDSSanPham.getModel();
            for (int i = 0; i < modelDanhSachSP.getRowCount(); i++) {
                String maSPCTTrongDanhSach = modelDanhSachSP.getValueAt(i, 1).toString().trim(); // Mã SPCT ở cột thứ 1
                if (maSPCT.equals(maSPCTTrongDanhSach)) {
                    try {
                        int soLuongHienCo = Integer.parseInt(modelDanhSachSP.getValueAt(i, 7).toString().trim()); // Số lượng hiện có ở cột thứ 7
                        int soLuongMoiTrongDanhSach = soLuongHienCo - soLuongChenhLech;
                        if (soLuongMoiTrongDanhSach < 0) {
                            JOptionPane.showMessageDialog(this, "Số lượng nhập vào vượt quá số lượng hiện có", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        modelDanhSachSP.setValueAt(soLuongMoiTrongDanhSach, i, 7);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Lỗi dữ liệu trong bảng danh sách sản phẩm: " + modelDanhSachSP.getValueAt(i, 7));
                        JOptionPane.showMessageDialog(this, "Dữ liệu trong bảng danh sách sản phẩm không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            modelTrongHD.setValueAt(soLuongMoi, selectedRowTrongHD, 7);

            tinhTongTien();
            CapNhatGiaKhiChonVoucher();

            txtNhapSoLuong1.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Chọn một sản phẩm từ giỏ hàng để cập nhật", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_btnCapNhatGioHangActionPerformed

    private void tblDSSPtrongHDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDSSPtrongHDMouseClicked
        int selectedRow = tblDSSPtrongHD.getSelectedRow();
        if (selectedRow != -1) {
            int soLuong = Integer.parseInt(tblDSSPtrongHD.getValueAt(selectedRow, 7).toString());
            txtNhapSoLuong1.setText(String.valueOf(soLuong));
        }

    }//GEN-LAST:event_tblDSSPtrongHDMouseClicked

    private void btnThemKHtuHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemKHtuHDActionPerformed
        String tenKH = txtTenKhachHang.getText().trim();
        String sdtKH = txtSDTKhachHang.getText().trim();

        if (!tenKH.isEmpty() && !sdtKH.isEmpty()) {
            try {
                ql.themKhachHang(tenKH, sdtKH); 
                this.qlkh.loadTableCus(this.tblKhachH, this.qlkh.getAllCustomer());
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng: " + e.getMessage(), "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnThemKHtuHDActionPerformed


    private void btnXoaKhoiGioHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaKhoiGioHangActionPerformed
        int selectedRow = tblDSSPtrongHD.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel modelTrongHD = (DefaultTableModel) tblDSSPtrongHD.getModel();
            String maSPCT = modelTrongHD.getValueAt(selectedRow, 1).toString();
            int soLuongXoa = Integer.parseInt(modelTrongHD.getValueAt(selectedRow, 7).toString());

            DefaultTableModel modelDanhSachSP = (DefaultTableModel) tblDSSanPham.getModel();
            for (int i = 0; i < modelDanhSachSP.getRowCount(); i++) {
                String maSPCTTrongDanhSach = modelDanhSachSP.getValueAt(i, 1).toString();
                if (maSPCT.equals(maSPCTTrongDanhSach)) {
                    int soLuongHienCo = Integer.parseInt(modelDanhSachSP.getValueAt(i, 7).toString());
                    int soLuongMoi = soLuongHienCo + soLuongXoa;
                    modelDanhSachSP.setValueAt(soLuongMoi, i, 7);
                    break;
                }
            }
            modelTrongHD.removeRow(selectedRow);
            tinhTongTien();
            CapNhatGiaKhiChonVoucher();
            txtLoaiSPpp.setText("");
            txtMauSaccc.setText("");
            txtKichThuoccc.setText("");
            txtPhongCachhh.setText("");
            txtNhapSoLuong1.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Chọn một sản phẩm từ giỏ hàng để xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnXoaKhoiGioHangActionPerformed

    private void jpQLHDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpQLHDMouseClicked
        this.jpViewQLHD.setVisible(true);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLBH2.setVisible(false);

        this.jpViewDoiMK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);

        this.jpQLBH.setBackground(new Color(255, 102, 0));
        this.jpQLHD.setBackground(new Color(220, 220, 220));
        this.jpQLDM.setBackground(new Color(255, 102, 0));
        this.jpQLKH.setBackground(new Color(255, 102, 0));
        this.jpQLKM.setBackground(new Color(255, 102, 0));
        this.jpQLSP.setBackground(new Color(255, 102, 0));
        this.jpQLTK.setBackground(new Color(255, 102, 0));
        this.jpTK.setBackground(new Color(255, 102, 0));

    }//GEN-LAST:event_jpQLHDMouseClicked

    private void txtSDTKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSDTKhachHangActionPerformed
        String soDienThoai = txtSDTKhachHang.getText().trim();

        boolean found = false;
        for (int i = 0; i < tblKhachH.getRowCount(); i++) {
            String sdtTrongBang = tblKhachH.getValueAt(i, 3).toString();
            if (sdtTrongBang.equals(soDienThoai)) {
                String tenKhachHang = tblKhachH.getValueAt(i, 2).toString(); // Cột tên khách hàng
                int diemTichLuy = Integer.parseInt(tblKhachH.getValueAt(i, 4).toString()); // Cột điểm tích lũy
                int diemDaDung = Integer.parseInt(tblKhachH.getValueAt(i, 5).toString()); // Cột điểm đã dùng
                int diemConLai = diemTichLuy - diemDaDung; // Tính điểm còn lại

                txtTenKhachHang.setText(tenKhachHang);
                lblDiemConLai.setText(String.valueOf(diemConLai));

                found = true;
                break;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(null, "Số điện thoại này chưa có tài khoản", "Thông báo", JOptionPane.ERROR_MESSAGE);

            lblDiemConLai.setText("0");
        }
        tblHoaDonCho.clearSelection();
    }//GEN-LAST:event_txtSDTKhachHangActionPerformed

    private void btnLuuTruHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuTruHDActionPerformed

        try {
            String tenKhachHang = txtTenKhachHang.getText().trim();
            String soDienThoai = txtSDTKhachHang.getText().trim();
            int diemDaDung = Integer.parseInt(txtSoDiemDaDung.getText().trim().isEmpty() ? "0" : txtSoDiemDaDung.getText().trim());
            String phuongThucThanhToan = rdoTienMat.isSelected() ? "Tiền mặt" : "Chuyển khoản";
            String voucherCode = (String) cbbVouchers.getSelectedItem(); // Lấy mã voucher từ combobox

            double voucherDiscountPercent = 0;
            double maximumReduce = 0;
            double moneyApplies = 0;

            if (voucherCode != null && !voucherCode.isEmpty()) {
                voucherDiscountPercent = getVoucherDiscountPercent(voucherCode);
                String query = "SELECT money_applies, maximum_reduce, quantity_voucher FROM Voucher WHERE voucher_code = ?";
                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setString(1, voucherCode);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        moneyApplies = rs.getDouble("money_applies");
                        maximumReduce = rs.getDouble("maximum_reduce");
                    }
                }
            }
            double tongTienChuaVAT = 0;
            double VAT = 0.08;

            for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
                int soLuong = Integer.parseInt(tblDSSPtrongHD.getValueAt(i, 7).toString());
                String giaBanString = tblDSSPtrongHD.getValueAt(i, 8).toString().replace(" VNĐ", "").replace(",", ".");
                double giaBan = Double.parseDouble(giaBanString);
                tongTienChuaVAT += soLuong * giaBan;
            }

            double tienVAT = tongTienChuaVAT * VAT;
            double tongTienCoVAT = tongTienChuaVAT + tienVAT;
            double tongTienSauVoucher = tongTienCoVAT;

            if (tongTienCoVAT >= moneyApplies) {
                double discountAmount = tongTienCoVAT * (voucherDiscountPercent / 100);
                if (discountAmount > maximumReduce) {
                    discountAmount = maximumReduce;
                }
                tongTienSauVoucher -= discountAmount;
            } else {
                voucherCode = null;
            }

            lblTienTruocKhiGiam.setText(String.format("%.2f VNĐ", tongTienCoVAT));
            lblTongTienThanhToan.setText(String.format("%.2f VNĐ", tongTienSauVoucher));

            if (tenKhachHang.isEmpty() || soDienThoai.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tblDSSPtrongHD.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Giỏ hàng chưa có sản phẩm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (tblHoaDonCho.getSelectedRowCount() == 0) {
                boolean soDienThoaiTonTai = false;
                for (int i = 0; i < tblHoaDonCho.getRowCount(); i++) {
                    String soDienThoaiTrongHDCho = tblHoaDonCho.getValueAt(i, 4).toString(); // Cột thứ 5 là số điện thoại
                    if (soDienThoaiTrongHDCho.equals(soDienThoai)) {
                        soDienThoaiTonTai = true;
                        break;
                    }
                }

                if (tblHoaDonCho.getRowCount() >= 10) {
                    JOptionPane.showMessageDialog(null, "Không thể thêm đối tượng mới. Số lượng đối tượng đã đạt giới hạn tối đa (10).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (soDienThoaiTonTai) {
                    JOptionPane.showMessageDialog(null, "Số điện thoại này đã tồn tại trong hóa đơn chờ khác rồi.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            String tenNhanVien = getEmployeeNameById(this.currentUserId);
            lblNhanVienBan.setText(tenNhanVien != null ? tenNhanVien : "Không tìm thấy tên nhân viên");

            long selectedOrderId = -1;
            String maHoaDon = "";
            try {
                selectedOrderId = getSelectedOrderId();
                maHoaDon = tblHoaDonCho.getValueAt(tblHoaDonCho.getSelectedRow(), 1).toString();
            } catch (SQLException e) {
                selectedOrderId = -1;
            }

            String queryHoaDon;

            if (selectedOrderId != -1) {
                queryHoaDon = "UPDATE Orders SET customer_id = ?, voucher_id = (SELECT voucher_id FROM Voucher WHERE voucher_code = ?), VAT = ?, payment_method = ?, total_price = ?, last_update_date_point = ? WHERE order_id = ?";
            } else {
                queryHoaDon = "INSERT INTO Orders (user_id, order_date, status, customer_id, voucher_id, VAT, payment_method, total_price, last_update_date_point) VALUES (?, ?, ?, ?, (SELECT voucher_id FROM Voucher WHERE voucher_code = ?), ?, ?, ?, ?)";
            }

            try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstHoaDon = connection.prepareStatement(queryHoaDon, Statement.RETURN_GENERATED_KEYS)) {

                int customerId = getCustomerId(tenKhachHang, soDienThoai);

                if (selectedOrderId != -1) {
                    capNhatSoLuongKho(connection, maHoaDon);
                    pstHoaDon.setInt(1, customerId);
                    pstHoaDon.setString(2, voucherCode);
                    pstHoaDon.setDouble(3, VAT * 100);
                    pstHoaDon.setString(4, phuongThucThanhToan);
                    pstHoaDon.setDouble(5, tongTienSauVoucher);
                    pstHoaDon.setDate(6, new java.sql.Date(System.currentTimeMillis()));
                    pstHoaDon.setLong(7, selectedOrderId);
                } else {
                    pstHoaDon.setInt(1, this.currentUserId);
                    pstHoaDon.setDate(2, new java.sql.Date(System.currentTimeMillis()));
                    pstHoaDon.setString(3, "Chưa thanh toán");
                    pstHoaDon.setInt(4, customerId);
                    pstHoaDon.setString(5, voucherCode);
                    pstHoaDon.setDouble(6, VAT * 100);
                    pstHoaDon.setString(7, phuongThucThanhToan);
                    pstHoaDon.setDouble(8, tongTienSauVoucher);
                    pstHoaDon.setDate(9, new java.sql.Date(System.currentTimeMillis()));
                }

                int affectedRows = pstHoaDon.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Lưu trữ hóa đơn thất bại, không có hàng nào bị ảnh hưởng.");
                }

                if (selectedOrderId != -1) {
                    CapNhatHDCT(connection, maHoaDon);
                } else {
                    try (ResultSet generatedKeys = pstHoaDon.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            long maHoaDonMoi = generatedKeys.getLong(1);
                            insertOrderDetails(maHoaDonMoi); // Thêm chi tiết đơn hàng
                        } else {
                            throw new SQLException("Lưu trữ hóa đơn thất bại, không có ID được lấy.");
                        }
                    }
                }

                JOptionPane.showMessageDialog(null, "Lưu trữ hóa đơn thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                resetInvoiceData();

                tblDSHD.setModel(qlHoaDon.loadOrderData());
                ql.LoadDSSPTrongQLBH(tblDSSanPham);
                ql.DanhSachHoaDonCho(tblHoaDonCho);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lưu trữ hóa đơn thất bại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            tiepTucThanhToan = false;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi định dạng số: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLuuTruHDActionPerformed

    private long getSelectedOrderId() throws SQLException {
        int selectedRow = tblHoaDonCho.getSelectedRow();
        if (selectedRow == -1) {
            throw new SQLException("Chưa chọn hóa đơn nào.");
        }
        String orderCode = tblHoaDonCho.getValueAt(selectedRow, 1).toString();

        // Kết nối với cơ sở dữ liệu để lấy order_id
        String query = "SELECT order_id FROM Orders WHERE order_code = ?";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, orderCode);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getLong("order_id");
            } else {
                throw new SQLException("Không tìm thấy hóa đơn.");
            }
        }
    }
    private void tblHoaDonChoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHoaDonChoMouseClicked

    }//GEN-LAST:event_tblHoaDonChoMouseClicked

    private void btnXoaHoaDonChoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaHoaDonChoActionPerformed
        int selectedRow = tblHoaDonCho.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa hóa đơn này không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String orderCode = tblHoaDonCho.getValueAt(selectedRow, 1).toString();
                int hoaDonId = 0;

                String getOrderId = "SELECT order_id FROM Orders WHERE RIGHT('HD' + RIGHT('000' + CAST(order_id AS VARCHAR(3)), 3), LEN(?)) = ?";
                String deleteOrderDetail = "DELETE FROM OrderDetails WHERE order_id = ?";
                String deleteOrder = "DELETE FROM Orders WHERE order_id = ?";
                String updateProductDetail = "UPDATE ProductDetails SET quantity = quantity + ? WHERE productDetails_id = ?";
                String selectOrderDetail = "SELECT productDetails_id, quantity FROM OrderDetails WHERE order_id = ?";

                Connection connection = null;
                try {
                    connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection();
                    connection.setAutoCommit(false);
                    try (PreparedStatement pre = connection.prepareStatement(getOrderId)) {
                        pre.setString(1, orderCode);
                        pre.setString(2, orderCode);
                        try (ResultSet rs = pre.executeQuery()) {
                            if (rs.next()) {
                                hoaDonId = rs.getInt("order_id");
                            } else {
                                JOptionPane.showMessageDialog(null, "Không tìm thấy hóa đơn với mã: " + orderCode, "Thông báo", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }

                    try (PreparedStatement pre = connection.prepareStatement(selectOrderDetail)) {
                        pre.setInt(1, hoaDonId);
                        try (ResultSet rs = pre.executeQuery()) {
                            while (rs.next()) {
                                int productDetailsId = rs.getInt("productDetails_id");
                                int quantity = rs.getInt("quantity");
                                try (PreparedStatement prepare = connection.prepareStatement(updateProductDetail)) {
                                    prepare.setInt(1, quantity);
                                    prepare.setInt(2, productDetailsId);
                                    prepare.executeUpdate();
                                }
                            }
                        }
                    }

                    try (PreparedStatement pre = connection.prepareStatement(deleteOrderDetail)) {
                        pre.setInt(1, hoaDonId);
                        pre.executeUpdate();
                    }

                    try (PreparedStatement pre = connection.prepareStatement(deleteOrder)) {
                        pre.setInt(1, hoaDonId);
                        pre.executeUpdate();
                    }

                    connection.commit();

                    if (tiepTucThanhToan) {
                        DefaultTableModel model = (DefaultTableModel) tblDSSPtrongHD.getModel();
                        model.setRowCount(0);
                        lblTongTienThanhToan.setText("0 VNĐ");
                        tiepTucThanhToan = false;
                    }

                    JOptionPane.showMessageDialog(null, "Xóa hóa đơn thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    ql.DanhSachHoaDonCho(tblHoaDonCho); // Cập nhật bảng hóa đơn chờ
                    tblDSHD.setModel(qlHoaDon.loadOrderData());
                    ql.LoadDSSPTrongQLBH(tblDSSanPham);
                    txtTenKhachHang.setText("");
                    txtSDTKhachHang.setText("");
                    lblTienTruocKhiGiam.setText("0");
                    lblDiemConLai.setText("0");

                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi khi xóa hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    // Rollback giao dịch nếu có lỗi
                    if (connection != null) {
                        try {
                            connection.rollback();
                        } catch (SQLException rollbackEx) {
                            rollbackEx.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Lỗi khi rollback giao dịch: " + rollbackEx.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException closeEx) {
                            closeEx.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Lỗi khi đóng kết nối: " + closeEx.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn hóa đơn để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnXoaHoaDonChoActionPerformed

    private void txtMaCTSPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMaCTSPKeyReleased
        String input = txtMaCTSP.getText().trim();
        DefaultTableModel model = (DefaultTableModel) tblDSSanPham.getModel();
        model.setRowCount(0);

        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection()) {
            String query = "SELECT d.productDetails_code AS 'Mã SPCT', p.product_name AS 'Tên SP', c.category_name AS 'Loại SP', "
                    + "cl.color_name AS 'Màu sắc', s.size_name AS 'Kích thước', st.style_name AS 'Phong cách', "
                    + "d.quantity AS 'Số lượng', d.price AS 'Giá bán' "
                    + "FROM Products p "
                    + "JOIN Categories c ON p.category_id = c.category_id "
                    + "JOIN ProductDetails d ON p.product_id = d.product_id "
                    + "JOIN Color cl ON d.color_id = cl.color_id "
                    + "JOIN Styles st ON d.style_id = st.style_id "
                    + "JOIN Sizes s ON d.size_id = s.size_id "
                    + "WHERE d.productDetails_code LIKE ?";

            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1, input + "%");

                ResultSet rs = pst.executeQuery();
                int stt = 1;
                while (rs.next()) {

                    String maSPCT = rs.getString("Mã SPCT");
                    String tenSP = rs.getString("Tên SP");
                    String loaiSP = rs.getString("Loại SP");
                    String mauSac = rs.getString("Màu sắc");
                    String kichThuoc = rs.getString("Kích thước");
                    String phongCach = rs.getString("Phong cách");
                    int soLuong = rs.getInt("Số lượng");
                    double giaBan = rs.getDouble("Giá bán");

                    // Thêm dòng dữ liệu vào bảng
                    model.addRow(new Object[]{stt, maSPCT, tenSP, loaiSP, mauSac, kichThuoc, phongCach, soLuong, giaBan});
                    stt++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_txtMaCTSPKeyReleased
    private void lamMoi() {
        txtMaSPCT.setText("");
        txtTenSanPham.setText("");
        txtNhapSoLuong.setText("");
        txtMauSaccc.setText("");
        txtPhongCachhh.setText("");
        txtKichThuoccc.setText("");
        txtLoaiSPpp.setText("");
    }
    private void btnLamMoiTTSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiTTSPActionPerformed
        this.lamMoi();
    }//GEN-LAST:event_btnLamMoiTTSPActionPerformed
    private boolean tiepTucThanhToan = false;
    private int dongDaChonTruocDo = -1;

    private void btnTiepTucThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTiepTucThanhToanActionPerformed
        int selectedRow = tblHoaDonCho.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một hóa đơn để tiếp tục thanh toán.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ql.LoadDSSPTrongQLBH(tblDSSanPham);
        String maHoaDon = tblHoaDonCho.getValueAt(selectedRow, 1).toString();

        if (tiepTucThanhToan && maHoaDon.equals(tblHoaDonCho.getValueAt(dongDaChonTruocDo, 1).toString())) {
            JOptionPane.showMessageDialog(null, "Hóa đơn này đang trong quá trình thanh toán. Vui lòng thanh toán cho xong.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dongDaChonTruocDo = selectedRow;

        try {
            if (tblDSSPtrongHD.getRowCount() > 0) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Trong giỏ hàng đang có sản phẩm. Bạn có muốn lưu trữ hóa đơn không?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    themHDCvaCapNhatHDC();
                    tblHoaDonCho.setRowSelectionInterval(selectedRow, selectedRow);
                } else {
                    ql.LoadDSSPTrongQLBH(tblDSSanPham);
                }
            }

            String queryHoaDon = "SELECT * FROM Orders WHERE order_code = ?";
            try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstHoaDon = connection.prepareStatement(queryHoaDon)) {
                pstHoaDon.setString(1, maHoaDon);
                try (ResultSet rsHoaDon = pstHoaDon.executeQuery()) {
                    if (rsHoaDon.next()) {
                        // Lấy thông tin khách hàng
                        int customerId = rsHoaDon.getInt("customer_id");
                        String queryCustomer = "SELECT * FROM Customers WHERE customer_id = ?";
                        try (PreparedStatement pstCustomer = connection.prepareStatement(queryCustomer)) {
                            pstCustomer.setInt(1, customerId);
                            try (ResultSet rsCustomer = pstCustomer.executeQuery()) {
                                if (rsCustomer.next()) {
                                    txtTenKhachHang.setText(rsCustomer.getString("customer_name"));
                                    txtSDTKhachHang.setText(rsCustomer.getString("phone"));
                                    int pointGained = rsCustomer.getInt("point_gained");
                                    int pointsUsed = rsCustomer.getInt("points_used");
                                    lblDiemConLai.setText(String.valueOf(pointGained - pointsUsed)); // Tính điểm còn lại
                                }
                            }
                        }

                        String queryOrderDetails = "SELECT od.*, pd.productDetails_code, p.product_name, c.category_name, co.color_name, s.size_name, st.style_name "
                                + "FROM OrderDetails od "
                                + "JOIN ProductDetails pd ON od.productDetails_id = pd.productDetails_id "
                                + "JOIN Products p ON pd.product_id = p.product_id "
                                + "JOIN Categories c ON p.category_id = c.category_id "
                                + "JOIN Color co ON pd.color_id = co.color_id "
                                + "JOIN Sizes s ON pd.size_id = s.size_id "
                                + "JOIN Styles st ON pd.style_id = st.style_id "
                                + "WHERE od.order_id = ?";
                        try (PreparedStatement pstOrderDetails = connection.prepareStatement(queryOrderDetails)) {
                            pstOrderDetails.setInt(1, rsHoaDon.getInt("order_id"));
                            try (ResultSet rsOrderDetails = pstOrderDetails.executeQuery()) {
                                DefaultTableModel model = (DefaultTableModel) tblDSSPtrongHD.getModel();
                                model.setRowCount(0);
                                double tongTienChuaVAT = 0;
                                double VAT = 0.08;
                                int stt = 1;
                                while (rsOrderDetails.next()) {
                                    // them sp vao tblDSSPtrongHD
                                    int soLuong = rsOrderDetails.getInt("quantity");
                                    double giaBan = rsOrderDetails.getDouble("unit_price");
                                    tongTienChuaVAT += soLuong * giaBan;

                                    Object[] row = new Object[]{
                                        stt++,
                                        rsOrderDetails.getString("productDetails_code"),
                                        rsOrderDetails.getString("product_name"),
                                        rsOrderDetails.getString("category_name"),
                                        rsOrderDetails.getString("color_name"),
                                        rsOrderDetails.getString("size_name"),
                                        rsOrderDetails.getString("style_name"),
                                        soLuong,
                                        String.format("%.2f VNĐ", giaBan)
                                    };
                                    model.addRow(row);
                                }

                                double tienVAT = tongTienChuaVAT * VAT;
                                double tongTienCoVAT = tongTienChuaVAT + tienVAT;
                                double tongTienDaLuu = rsHoaDon.getDouble("total_price");
                                lblTienTruocKhiGiam.setText(String.format("%.2f VNĐ", tongTienCoVAT));
                                lblTongTienThanhToan.setText(String.format("%.2f VNĐ", tongTienDaLuu));
                            }
                        }

                        String paymentMethod = rsHoaDon.getString("payment_method");
                        if (paymentMethod.equals("Tiền mặt")) {
                            rdoTienMat.setSelected(true);
                        } else {
                            rdoChuyenKhoan.setSelected(true);
                        }
                    }
                }
            }
            tiepTucThanhToan = true;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy thông tin hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnTiepTucThanhToanActionPerformed
    private void capNhatSoLuong(String maHoaDon) throws SQLException {
        // lấy sl sp cũ từ hd
        String getOrderDetailsQuery = "SELECT productDetails_id, quantity FROM OrderDetails WHERE order_id = (SELECT order_id FROM Orders WHERE order_code = ?)";
        Map<Integer, Integer> oldQuantities = new HashMap<>();

        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstGetOrderDetails = connection.prepareStatement(getOrderDetailsQuery)) {
            pstGetOrderDetails.setString(1, maHoaDon);
            ResultSet rs = pstGetOrderDetails.executeQuery();
            while (rs.next()) {
                int productDetailsId = rs.getInt("productDetails_id");
                int quantity = rs.getInt("quantity");
                oldQuantities.put(productDetailsId, quantity);
            }
        }

        String getProductIdQuery = "SELECT productDetails_id FROM ProductDetails WHERE productDetails_code = ?";
        Map<String, Integer> productCodeToId = new HashMap<>();

        for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
            String productDetailsCode = tblDSSPtrongHD.getValueAt(i, 1).toString();
            if (!productCodeToId.containsKey(productDetailsCode)) {
                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstGetProductId = connection.prepareStatement(getProductIdQuery)) {
                    pstGetProductId.setString(1, productDetailsCode);
                    ResultSet rs = pstGetProductId.executeQuery();
                    if (rs.next()) {
                        int productDetailsId = rs.getInt("productDetails_id");
                        productCodeToId.put(productDetailsCode, productDetailsId);
                    }
                }
            }
        }

        String updateInventoryQuery = "UPDATE ProductDetails SET quantity = quantity + ? WHERE productDetails_id = ?";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstUpdateInventory = connection.prepareStatement(updateInventoryQuery)) {
            for (Map.Entry<Integer, Integer> entry : oldQuantities.entrySet()) {
                int productDetailsId = entry.getKey();
                int oldQuantity = entry.getValue();

                String productDetailsCode = null;
                for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
                    String code = tblDSSPtrongHD.getValueAt(i, 1).toString();
                    if (productCodeToId.containsKey(code) && productCodeToId.get(code) == productDetailsId) {
                        productDetailsCode = code;
                        break;
                    }
                }
                // nếu không tìm thấy trong giỏ cộng lại vào kho
                if (productDetailsCode == null) {
                    pstUpdateInventory.setInt(1, oldQuantity);
                    pstUpdateInventory.setInt(2, productDetailsId);
                    pstUpdateInventory.executeUpdate();
                }
            }

            for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
                String productDetailsCode = tblDSSPtrongHD.getValueAt(i, 1).toString();
                String newQuantityStr = tblDSSPtrongHD.getValueAt(i, 7).toString();

                try {
                    int newQuantity = Integer.parseInt(newQuantityStr);
                    int productDetailsId = productCodeToId.getOrDefault(productDetailsCode, -1);

                    if (productDetailsId != -1) {
                        int oldQuantity = oldQuantities.getOrDefault(productDetailsId, 0);
                        int quantityDifference = oldQuantity - newQuantity;

                        if (quantityDifference != 0) {
                            pstUpdateInventory.setInt(1, quantityDifference);
                            pstUpdateInventory.setInt(2, productDetailsId);
                            pstUpdateInventory.executeUpdate();
                        }
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Có lỗi trong dữ liệu sản phẩm: " + e.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void CapNhatHDCTcuaHDC(String maHoaDon) throws SQLException {
        // Cập nhật chi tiết hóa đơn
        String updateOrderDetailsQuery = "DELETE FROM OrderDetails WHERE order_id = (SELECT order_id FROM Orders WHERE order_code = ?)";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pstUpdateOrderDetails = connection.prepareStatement(updateOrderDetailsQuery)) {
            pstUpdateOrderDetails.setString(1, maHoaDon);
            pstUpdateOrderDetails.executeUpdate();

            inSertHoaDonChiTiet(getOrderIdFromOrderCode(connection, maHoaDon));
        }

    }

    private void themHDCvaCapNhatHDC() {
        try {
            String tenKhachHang = txtTenKhachHang.getText().trim();
            String soDienThoai = txtSDTKhachHang.getText().trim();
            int diemDaDung = Integer.parseInt(txtSoDiemDaDung.getText().trim().isEmpty() ? "0" : txtSoDiemDaDung.getText().trim());
            String phuongThucThanhToan = rdoTienMat.isSelected() ? "Tiền mặt" : "Chuyển khoản";
            String voucherCode = (String) cbbVouchers.getSelectedItem();

            boolean hoaDonChoTonTai = false;
            String maHoaDonCho = null;
            for (int i = 0; i < tblHoaDonCho.getRowCount(); i++) {
                String sdtKhachHangTrongHoaDonCho = tblHoaDonCho.getValueAt(i, 4).toString();
                if (soDienThoai.equals(sdtKhachHangTrongHoaDonCho)) {
                    hoaDonChoTonTai = true;
                    maHoaDonCho = tblHoaDonCho.getValueAt(i, 1).toString(); // Lấy mã hóa đơn chờ
                    break;
                }
            }
            double voucherDiscountPercent = 0;
            double maximumReduce = 0;
            double moneyApplies = 0;

            if (voucherCode != null && !voucherCode.isEmpty()) {
                voucherDiscountPercent = getVoucherDiscountPercent(voucherCode);

                String query = "SELECT money_applies, maximum_reduce FROM Voucher WHERE voucher_code = ?";
                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setString(1, voucherCode);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        moneyApplies = rs.getDouble("money_applies");
                        maximumReduce = rs.getDouble("maximum_reduce");
                    }
                }
            }
            double tongTienChuaVAT = 0;
            double VAT = 0.08;

            for (int i = 0; i < tblDSSPtrongHD.getRowCount(); i++) {
                int soLuong = Integer.parseInt(tblDSSPtrongHD.getValueAt(i, 7).toString());
                String giaBanString = tblDSSPtrongHD.getValueAt(i, 8).toString().replace(" VNĐ", "").replace(",", ".");
                double giaBan = Double.parseDouble(giaBanString);
                tongTienChuaVAT += soLuong * giaBan;
            }

            double tienVAT = tongTienChuaVAT * VAT;
            double tongTienCoVAT = tongTienChuaVAT + tienVAT;
            double tongTienSauVoucher = tongTienCoVAT;

            if (tongTienCoVAT >= moneyApplies) {
                double discountAmount = tongTienCoVAT * (voucherDiscountPercent / 100);
                // Giảm tối đa theo cột maximum_reduce
                if (discountAmount > maximumReduce) {
                    discountAmount = maximumReduce;
                }
                tongTienSauVoucher -= discountAmount;
            } else {
                voucherCode = null;
            }

            lblTongTienThanhToan.setText(String.format("%.2f VNĐ", tongTienSauVoucher));

            if (tenKhachHang.isEmpty() || soDienThoai.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

            if (tblDSSPtrongHD.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Giỏ hàng chưa có sản phẩm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String tenNhanVien = getEmployeeNameById(this.currentUserId);
            lblNhanVienBan.setText(tenNhanVien != null ? tenNhanVien : "Không tìm thấy tên nhân viên");

            if (hoaDonChoTonTai) {
                // Cập nhật hóa đơn chờ
                capNhatSoLuong(maHoaDonCho);
                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection()) {
                    String queryUpdateHoaDonCho = "UPDATE Orders SET total_price = ?, last_update_date_point = ? WHERE order_code = ?";
                    try (PreparedStatement pstUpdate = connection.prepareStatement(queryUpdateHoaDonCho)) {
                        pstUpdate.setDouble(1, tongTienSauVoucher);
                        pstUpdate.setDate(2, new java.sql.Date(System.currentTimeMillis()));
                        pstUpdate.setString(3, maHoaDonCho);
                        pstUpdate.executeUpdate();
                    }

                    CapNhatHDCTcuaHDC(maHoaDonCho);

                    JOptionPane.showMessageDialog(null, "Cập nhật hóa đơn chờ thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Cập nhật hóa đơn chờ thất bại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Tạo hóa đơn mới
                try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection()) {
                    String queryHoaDon = "INSERT INTO Orders (user_id, order_date, status, customer_id, voucher_id, VAT, payment_method, total_price, last_update_date_point) VALUES (?, ?, ?, ?, (SELECT voucher_id FROM Voucher WHERE voucher_code = ?), ?, ?, ?, ?)";
                    try (PreparedStatement pstHoaDon = connection.prepareStatement(queryHoaDon, Statement.RETURN_GENERATED_KEYS)) {
                        int customerId = getCustomerId(tenKhachHang, soDienThoai);

                        pstHoaDon.setInt(1, this.currentUserId);
                        pstHoaDon.setDate(2, new java.sql.Date(System.currentTimeMillis()));
                        pstHoaDon.setString(3, "Chưa thanh toán");
                        pstHoaDon.setInt(4, customerId);
                        pstHoaDon.setString(5, voucherCode);
                        pstHoaDon.setDouble(6, VAT * 100);
                        pstHoaDon.setString(7, phuongThucThanhToan);
                        pstHoaDon.setDouble(8, tongTienSauVoucher);
                        pstHoaDon.setDate(9, new java.sql.Date(System.currentTimeMillis()));

                        int affectedRows = pstHoaDon.executeUpdate();
                        if (affectedRows == 0) {
                            throw new SQLException("Lưu trữ hóa đơn thất bại, không có hàng nào bị ảnh hưởng.");
                        }

                        try (ResultSet generatedKeys = pstHoaDon.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                long maHoaDonMoi = generatedKeys.getLong(1);
                                insertOrderDetails(maHoaDonMoi); // Thêm chi tiết đơn hàng
                            } else {
                                throw new SQLException("Lưu trữ hóa đơn thất bại, không có ID được lấy.");
                            }
                        }

                        JOptionPane.showMessageDialog(null, "Lưu trữ hóa đơn thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lưu trữ hóa đơn thất bại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

            resetInvoiceData();
            tblDSHD.setModel(qlHoaDon.loadOrderData());
            ql.LoadDSSPTrongQLBH(tblDSSanPham);
            ql.DanhSachHoaDonCho(tblHoaDonCho);
            tiepTucThanhToan = false;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi định dạng số: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void txtMaCTSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaCTSPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaCTSPActionPerformed

    private void btnKhachLeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKhachLeActionPerformed
        for (int i = 0; i < tblKhachH.getRowCount(); i++) {
            String sdtTrongBang = tblKhachH.getValueAt(i, 3).toString();
            if (sdtTrongBang.equals("0000000000")) {
                String tenKhachHang = tblKhachH.getValueAt(i, 2).toString();
                txtTenKhachHang.setText(tenKhachHang);
                txtSDTKhachHang.setText(sdtTrongBang);
                break;
            }
        }
        tblHoaDonCho.clearSelection();
    }//GEN-LAST:event_btnKhachLeActionPerformed

    private void btnQuetMaQRCodeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuetMaQRCodeeActionPerformed
        qrCode.toggleScanning();

        qrCode.setOnScanResultListener((productDetailCode) -> {
            // Tìm dòng sản phẩm trong bảng tblDSSanPham theo productDetailCode
            DefaultTableModel modelSanPham = (DefaultTableModel) tblDSSanPham.getModel();
            int selectedRow = -1;
            for (int i = 0; i < modelSanPham.getRowCount(); i++) {
                if (modelSanPham.getValueAt(i, 1).toString().equals(productDetailCode)) {
                    selectedRow = i;
                    break;
                }
            }

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với mã QR đã quét", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int soLuongNhapVao = 1; // Số lượng mặc định là 1

                // Kiểm tra số lượng hiện có
                int soLuongHienCo = Integer.parseInt(tblDSSanPham.getValueAt(selectedRow, 7).toString()); // Cột số lượng ở vị trí thứ 7
                double giaBan = Double.parseDouble(tblDSSanPham.getValueAt(selectedRow, 8).toString()); // Cột giá bán ở vị trí thứ 8

                if (soLuongNhapVao > soLuongHienCo) {
                    JOptionPane.showMessageDialog(this, "Số lượng nhập vào vượt quá số lượng hiện có", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Lấy thông tin sản phẩm từ bảng tblDSSanPham
                String tenSP = tblDSSanPham.getValueAt(selectedRow, 2).toString();
                String loaiSP = tblDSSanPham.getValueAt(selectedRow, 3).toString();
                String mauSac = tblDSSanPham.getValueAt(selectedRow, 4).toString();
                String kichThuoc = tblDSSanPham.getValueAt(selectedRow, 5).toString();
                String phongCach = tblDSSanPham.getValueAt(selectedRow, 6).toString();

                // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
                DefaultTableModel modelTrongHD = (DefaultTableModel) tblDSSPtrongHD.getModel();
                boolean found = false;
                int stt = modelTrongHD.getRowCount() + 1; // STT là số thứ tự của hàng mới

                for (int i = 0; i < modelTrongHD.getRowCount(); i++) {
                    if (modelTrongHD.getValueAt(i, 1).toString().equals(productDetailCode)) { // Kiểm tra mã SPCT
                        int soLuongHienCoTrongHD = Integer.parseInt(modelTrongHD.getValueAt(i, 7).toString());
                        int soLuongMoiTrongHD = soLuongHienCoTrongHD + soLuongNhapVao;
                        modelTrongHD.setValueAt(soLuongMoiTrongHD, i, 7); // Cập nhật lại số lượng trong giỏ hàng
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Thêm sản phẩm mới vào giỏ hàng nếu chưa có
                    modelTrongHD.addRow(new Object[]{stt, productDetailCode, tenSP, loaiSP, mauSac, kichThuoc, phongCach, soLuongNhapVao, giaBan});
                }

                // Cập nhật số lượng sản phẩm trong bảng tblDSSanPham
                int soLuongMoi = soLuongHienCo - soLuongNhapVao;
                tblDSSanPham.setValueAt(soLuongMoi, selectedRow, 7); // Cập nhật lại cột số lượng
                danhSachSanPhamDaChon.add(new Object[]{stt, productDetailCode, tenSP, loaiSP, mauSac, kichThuoc, phongCach, soLuongNhapVao, giaBan});
                tinhTongTien();
                CapNhatGiaKhiChonVoucher();
                txtNhapSoLuong.setText("");
                lamMoi();

            } catch (NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Dữ liệu trong bảng không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

    }//GEN-LAST:event_btnQuetMaQRCodeeActionPerformed

    private int findOrderIdByCode(String orderCode) {
        // Tìm mã đơn hàng từ mã hóa đơn
        int orderId = 0;
        String query = "SELECT order_id FROM Orders WHERE order_code = ?";
        try (Connection connection = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456").getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {

            pst.setString(1, orderCode);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    orderId = rs.getInt("order_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderId;
    }

    private void tblDSHDFocusLost(java.awt.event.FocusEvent evt) {
        // Nếu bảng tblDSHD bị mất focus, xóa dòng được chọn
        tblDSHD.clearSelection();
        // Xóa dữ liệu trong tblSanPhamTrongHoaDon
        DefaultTableModel model = (DefaultTableModel) tblSanPhamTrongHoaDon.getModel();
        model.setRowCount(0);
    }

    private void tblDSHDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDSHDMouseClicked
        int row = tblDSHD.getSelectedRow();
        if (row >= 0) {
            // Lấy mã đơn hàng từ bảng tblDSHD
            String orderCode = (String) tblDSHD.getValueAt(row, 1);

            // Tìm mã đơn hàng từ mã hóa đơn
            int orderId = findOrderIdByCode(orderCode);

            // Lấy danh sách chi tiết sản phẩm cho đơn hàng
            ArrayList<OrderProductDetails> productDetails = (ArrayList<OrderProductDetails>) qlHoaDon.getOrderDetails(orderId);

            DefaultTableModel model = (DefaultTableModel) tblSanPhamTrongHoaDon.getModel();
            model.setRowCount(0); // Xóa dữ liệu cũ
            int stt = 1;
            for (OrderProductDetails detail : productDetails) {
                model.addRow(new Object[]{
                    stt,
                    detail.getProductDetailCode(),
                    detail.getProductName(),
                    detail.getProductType(),
                    detail.getColor(),
                    detail.getSize(),
                    detail.getStyle(),
                    detail.getQuantity(),
                    detail.getUnitPrice()
                });
                stt++;
            }
        }
    }//GEN-LAST:event_tblDSHDMouseClicked


    private void cbbLocTrangThaiHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbLocTrangThaiHoaDonActionPerformed
        String selectedStatus = (String) cbbLocTrangThaiHoaDon.getSelectedItem();
        tblDSHD.setModel(qlHoaDon.loadOrderDataByStatus(selectedStatus));
    }//GEN-LAST:event_cbbLocTrangThaiHoaDonActionPerformed

    private void txtLocHoaDonTheoMaHDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLocHoaDonTheoMaHDKeyReleased
//        String orderCode = txtLocHoaDonTheoMaHD.getText();
//        tblDSHD.setModel(qlHoaDon.loadOrderDataByOrderCode(orderCode));
        String searchText = txtLocHoaDonTheoMaHD.getText();
        tblDSHD.setModel(qlHoaDon.loadOrderDataBySearch(searchText));
    }//GEN-LAST:event_txtLocHoaDonTheoMaHDKeyReleased

    private void btnXuatExcelHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatExcelHoaDonActionPerformed
//    ArrayList<Order> dsOrder = (ArrayList<Order>) qlHoaDon.getAllOrders();
//    JFileChooser fileChooser = new JFileChooser();   
//    // Đặt kiểu chọn là chỉ chọn file
//    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);    
//    // Đặt tiêu đề cho hộp thoại
//    fileChooser.setDialogTitle("Chọn nơi lưu tệp");
//    // Đặt bộ lọc để chỉ hiển thị các file có đuôi .xlsx
//    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
//    // Hiển thị hộp thoại lưu và lấy kết quả
//    int userSelection = fileChooser.showSaveDialog(this);    
//    if (userSelection == JFileChooser.APPROVE_OPTION) {
//        // Lấy file đã chọn
//        File fileToSave = fileChooser.getSelectedFile();      
//        // Đảm bảo file có đuôi .xlsx
//        String fileName = fileToSave.getAbsolutePath();
//        if (!fileName.endsWith(".xlsx")) {
//            fileName += ".xlsx";
//        }
//        // Xuất file Excel
//        ex.xuatHoaDon(dsOrder, fileName);
//        JOptionPane.showMessageDialog(this, "Xuất danh sách hóa đơn ra tệp " + fileName + " thành công.");
//    }     

        int rowCount = tblDSHD.getRowCount();
        ArrayList<Order> dsOrder = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng ngày tháng bạn muốn

        for (int i = 0; i < rowCount; i++) {
            Order order = new Order();
            order.setOrderCode(tblDSHD.getValueAt(i, 1).toString());
            order.setUserCode(tblDSHD.getValueAt(i, 2).toString());
            order.setUserName(tblDSHD.getValueAt(i, 3).toString());
            order.setCustomerCode(tblDSHD.getValueAt(i, 4).toString());
            order.setCustomerName(tblDSHD.getValueAt(i, 5).toString());
            order.setPhone(tblDSHD.getValueAt(i, 6).toString());

            // Lấy giá trị ngày và chuyển đổi thành java.util.Date
            String dateString = tblDSHD.getValueAt(i, 7).toString();
            try {
                java.util.Date orderDate = dateFormat.parse(dateString);
                // Chuyển đổi thành String nếu cần
                String formattedDate = dateFormat.format(orderDate);
                order.setOrderDate(formattedDate); // Nếu setOrderDate yêu cầu String
            } catch (ParseException e) {
                e.printStackTrace();
                // Xử lý lỗi nếu không thể chuyển đổi ngày
            }

            // Kiểm tra voucher và gán giá trị
            Object voucherValue = tblDSHD.getValueAt(i, 8);
            order.setVoucherCode(voucherValue != null ? voucherValue.toString() : ""); // Gán chuỗi trống nếu voucher là null

            order.setVat(Double.parseDouble(tblDSHD.getValueAt(i, 9).toString()));
            order.setPaymentMethod(tblDSHD.getValueAt(i, 10).toString());
            order.setTotalPrice(Double.parseDouble(tblDSHD.getValueAt(i, 11).toString()));
            order.setStatus(tblDSHD.getValueAt(i, 12).toString());

            dsOrder.add(order);
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Chọn nơi lưu tệp");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getAbsolutePath();
            if (!fileName.endsWith(".xlsx")) {
                fileName += ".xlsx";
            }

            ex.xuatHoaDon(dsOrder, fileName);
            JOptionPane.showMessageDialog(this, "Xuất danh sách hóa đơn ra tệp " + fileName + " thành công.");
        }
    }//GEN-LAST:event_btnXuatExcelHoaDonActionPerformed

    private void btnLocTheoKhoangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocTheoKhoangActionPerformed
        java.util.Date startDate = dateTu.getDate();
        java.util.Date endDate = dateDen.getDate();
        if (startDate != null && endDate != null) {
            if (!endDate.before(startDate)) {
                tblDSHD.setModel(qlHoaDon.loadOrderDataByDateRange(startDate, endDate));
            } else {
                JOptionPane.showMessageDialog(this, "Ngày Đến phải lớn hơn hoặc bằng Ngày Từ.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoảng ngày hợp lệ.");
        }
    }//GEN-LAST:event_btnLocTheoKhoangActionPerformed

    private void tblDanhSachDMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachDMMouseClicked
        // TODO add your handling code here:
        txtMaDM.setEnabled(false);
        txtTenDM.setEnabled(false);
        index = tblDanhSachDM.getSelectedRow();
        try {
            showDetails();
        } catch (SQLDataException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tblDanhSachDMMouseClicked

    private void btnThemMoiDMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemMoiDMActionPerformed
        // TODO add your handling code here:
        txtMaDM.setText("");
        txtTenDM.setText("");
        txtMaDM.setEnabled(false);
        txtTenDM.setEnabled(true);
        try {
            filltoTable();

        } catch (SQLDataException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnThemMoiDMActionPerformed

    private void btnSuaDMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaDMActionPerformed

        int row = tblDanhSachDM.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Bạn phải chọn một danh mục cần sửa");
            txtTenDM.setEnabled(false);
            return;
        }
        txtTenDM.setEnabled(true);
    }//GEN-LAST:event_btnSuaDMActionPerformed

    private void txtMaDMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaDMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaDMActionPerformed

    private void txtTenDMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenDMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenDMActionPerformed

    private void btnLuuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLuuMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLuuMouseClicked

    private void btnLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuActionPerformed
        ctsv.saveCategory(tblDanhSachDM, txtMaDM, txtTenDM, list);
    }//GEN-LAST:event_btnLuuActionPerformed

    private void txtHoTenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHoTenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHoTenActionPerformed

    private void btnCapNhatThongTinCNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatThongTinCNActionPerformed

        String hoTen = this.txtHoTen.getText();
        String userName = this.txtUsername.getText();
        String email = this.txtEmail.getText();
        String sdt = this.txtSdt.getText();
        String diaChi = this.txtDiaChi.getText();
        String vaiTro = txtVaiTro.getText();
        int idUser = this.currentUserId;
        // Mở form cập nhật thông tin người dùng
        new FormUpdateThongTin(this, true, idUser, hoTen, userName, email, sdt, diaChi, vaiTro).setVisible(true);
        try {
            DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");
            Connection connection = dcm.getConnection();
            String sql = "SELECT hoTen, username, email, phone, [address], role_id FROM Users WHERE user_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtHoTen.setText(rs.getString("hoTen")); // Hiển thị tên người dùng
                txtUsername.setText(rs.getString("username"));
                txtEmail.setText(rs.getString("email"));
                txtSdt.setText(rs.getString("phone"));
                txtDiaChi.setText(rs.getString("address"));

                int roleId = rs.getInt("role_id");
                String roleName = ql.getRoleName(roleId); // Lấy tên vai trò từ role_id
                txtVaiTro.setText(roleName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnCapNhatThongTinCNActionPerformed

    private void btnDoiMatKhauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoiMatKhauActionPerformed
        try {

            this.jpViewQLHD.setVisible(false);
            this.jpViewQLDM.setVisible(false);
            this.jpViewQLKH.setVisible(false);
            this.jpViewQLKM.setVisible(false);
            this.jpViewQLSPP.setVisible(false);
            this.jpViewQLTK.setVisible(false);
            this.jpViewTK.setVisible(false);
            this.jpViewQLBH2.setVisible(false);
            this.jpViewDoiMK.setVisible(true);
            this.jpViewQLSPP2.setVisible(false);
            this.jpViewQLSPP3.setVisible(false);

            // code them
            Users1 us1 = us1sv.getUser1(this.currentUserId);
            txtUsername1.setText(us1.getUsername());
            txtUsername1.setEnabled(false);

        } catch (SQLDataException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnDoiMatKhauActionPerformed

    private void txtTenDangNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenDangNhapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenDangNhapActionPerformed

    private void cbbVaiTroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbVaiTroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbbVaiTroActionPerformed

    private void btnThemNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemNhanVienActionPerformed
        txtHoTen1.setText("");
        txtDiaChi1.setText("");
        txtEmail1.setText("");
        txtSDT.setText("");
        txtTenDangNhap.setText("");
        dateNgayTao.setDate(null);
        cbbVaiTro.setSelectedItem(0);
        rdoDanglam.setSelected(true);
//        txtTenDangNhap.setEnabled(true);
        txtHoTen1.setEnabled(true);
        txtDiaChi1.setEnabled(true);
        txtEmail1.setEnabled(true);
        txtSDT.setEnabled(true);
        txtMatKhau.setText(generateCode());
        System.out.println(txtMatKhau.getText());
        cbbVaiTro.setEnabled(true);
        txtTenDangNhap.setEnabled(true);
        dateNgayTao.setEnabled(true);

        rdoDanglam.setEnabled(false);
        rdoNghilam.setEnabled(false);
        txtMaNhanVienn.setText("");
        txtMaNhanVienn.setEnabled(false);

        tblNhanVien.clearSelection();
    }//GEN-LAST:event_btnThemNhanVienActionPerformed

    private void btnCapNhatNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatNhanVienActionPerformed

        int row = tblNhanVien.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Bạn phải chọn một nhân viên cần sửa");

            txtTenDangNhap.setEnabled(false);
            txtHoTen1.setEnabled(true);
            txtDiaChi1.setEnabled(true);
            txtSDT.setEnabled(true);
            txtEmail1.setEnabled(true);
            return;
        }
        txtTenDangNhap.setEnabled(false);
        dateNgayTao.setEnabled(false);
        txtHoTen1.setEnabled(true);
        txtDiaChi1.setEnabled(true);
        txtSDT.setEnabled(true);
        txtEmail1.setEnabled(true);
        rdoDanglam.setEnabled(true);
        rdoNghilam.setEnabled(true);
        cbbVaiTro.setEnabled(true);
        txtMaNhanVienn.setEnabled(false);
    }//GEN-LAST:event_btnCapNhatNhanVienActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        resetNv();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        String maNhanVien = txtMaNhanVienn.getText();
        try {
            us1sv.saveNV(txtHoTen1, txtTenDangNhap, txtMatKhau, txtEmail1, txtSDT, txtDiaChi1, cbbVaiTro, dateNgayTao, rdoDanglam, rdoNghilam, tblNhanVien, maNhanVien);
        } catch (SQLDataException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void jpQLNVMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpQLNVMouseClicked

    }//GEN-LAST:event_jpQLNVMouseClicked

    private void tabQLNVMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabQLNVMouseClicked

    }//GEN-LAST:event_tabQLNVMouseClicked
    private void loadDatatoTableTimKiem(ArrayList<Users1> allUser1) {
        DefaultTableModel dtm = (DefaultTableModel) tblNhanVien.getModel();
        dtm.setRowCount(0);
        for (Users1 us1 : allUser1) {
            dtm.addRow(new Object[]{
                us1.getUsersCode(), us1.getHoTen(), us1.getUsername(), us1.getEmail(), us1.getPhone(),
                us1.getAddress(), us1.isRoleId() ? "Quản lý" : "Nhân viên", us1.getCreatedDate(), us1.isStatus() ? "Đang làm" : "Nghỉ làm"
            });
        }
    }

    private void btnXacNhanDMKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXacNhanDMKActionPerformed
        // code thêm 
        String userName = txtUsername1.getText();
        String passWord = txtPassword.getText();
        String newPass = txtNewPassword.getText();
        String confirmPass = txtConfirmPassword.getText();
        if (us1sv.validateDoiMk(txtUsername1, txtPassword, txtNewPassword, txtConfirmPassword) == true) {
            if (!newPass.equals(confirmPass)) {
                this.jpViewQLHD.setVisible(false);
                this.jpViewQLDM.setVisible(false);
                this.jpViewQLKH.setVisible(false);
                this.jpViewQLKM.setVisible(false);
                this.jpViewQLSPP.setVisible(false);
                this.jpViewQLTK.setVisible(false);
                this.jpViewTK.setVisible(false);
                this.jpViewQLBH2.setVisible(false);
                this.jpViewDoiMK.setVisible(true);
                this.jpViewQLSPP2.setVisible(false);
                this.jpViewQLSPP3.setVisible(false);
                JOptionPane.showMessageDialog(this, "Mật khẩu mới không khớp");

                return;
            }
            boolean kq = us1sv.doiMk(userName, passWord, newPass);
            if (kq) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công");
                this.jpViewQLHD.setVisible(false);
                this.jpViewQLDM.setVisible(false);
                this.jpViewQLKH.setVisible(false);
                this.jpViewQLKM.setVisible(false);
                this.jpViewQLSPP.setVisible(false);
                this.jpViewQLTK.setVisible(true);
                this.jpViewTK.setVisible(false);
                this.jpViewQLBH2.setVisible(false);
                this.jpViewDoiMK.setVisible(false);
                this.jpViewQLSPP2.setVisible(false);
                this.jpViewQLSPP3.setVisible(false);
                txtPassword.setText("");
                txtNewPassword.setText("");
                txtConfirmPassword.setText("");

            } else {
                this.jpViewQLHD.setVisible(false);
                this.jpViewQLDM.setVisible(false);
                this.jpViewQLKH.setVisible(false);
                this.jpViewQLKM.setVisible(false);
                this.jpViewQLSPP.setVisible(false);
                this.jpViewQLTK.setVisible(false);
                this.jpViewTK.setVisible(false);
                this.jpViewQLBH2.setVisible(false);
                this.jpViewDoiMK.setVisible(true);
                this.jpViewQLSPP2.setVisible(false);
                this.jpViewQLSPP3.setVisible(false);
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại");

            }
        }
    }//GEN-LAST:event_btnXacNhanDMKActionPerformed

    private void btnQuaylaiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnQuaylaiMouseClicked
        // TODO add your handling code here:
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(true);
        this.jpViewTK.setVisible(false);
        this.jpViewQLBH2.setVisible(false);
        this.jpViewDoiMK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);

        this.jpQLBH.setBackground(new Color(255, 102, 0));
        this.jpQLDM.setBackground(new Color(255, 102, 0));
        this.jpQLKH.setBackground(new Color(255, 102, 0));
        this.jpQLKM.setBackground(new Color(255, 102, 0));
        this.jpQLSP.setBackground(new Color(255, 102, 0));
        this.jpQLTK.setBackground(new Color(220, 220, 220));
        this.jpTK.setBackground(new Color(255, 102, 0));

    }//GEN-LAST:event_btnQuaylaiMouseClicked

    private void btnQuaylaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuaylaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnQuaylaiActionPerformed

    private void btnEyeMKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEyeMKActionPerformed
        // TODO add your handling code here:
        if (trangThaiMk) {
            txtPassword.setEchoChar('•');  // Ẩn mật khẩu
        } else {
            txtPassword.setEchoChar((char) 0);  // Hiển thị mật khẩu
        }
        trangThaiMk = !trangThaiMk;
    }//GEN-LAST:event_btnEyeMKActionPerformed

    private void btnEyeMKMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEyeMKMoiActionPerformed
        // TODO add your handling code here:
        if (trangThaiMk) {
            txtNewPassword.setEchoChar('•');  // Ẩn mật khẩu
        } else {
            txtNewPassword.setEchoChar((char) 0);  // Hiển thị mật khẩu
        }
        trangThaiMk = !trangThaiMk;
    }//GEN-LAST:event_btnEyeMKMoiActionPerformed

    private void btnEyeXacNhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEyeXacNhanActionPerformed
        // TODO add your handling code here:
        if (trangThaiMk) {
            txtConfirmPassword.setEchoChar('•');  // Ẩn mật khẩu
        } else {
            txtConfirmPassword.setEchoChar((char) 0);  // Hiển thị mật khẩu
        }
        trangThaiMk = !trangThaiMk;
    }//GEN-LAST:event_btnEyeXacNhanActionPerformed

    private void btnAddProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddProductActionPerformed
        this.tblHienThiKetQua.clearSelection();
        this.txtMaSanPham.setEnabled(false);
        this.txtTenSP.setEnabled(true);
        this.txtMotaSPP.setEnabled(true);
        this.cbbDanhMuc.setEnabled(true);
        this.cbbNhaCC.setEnabled(true);
        this.rdoRun.setEnabled(true);
        this.rdoStop.setEnabled(true);

        this.setInputFields("sản phẩm");

        this.choice = true;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnAddProductActionPerformed

    private void btnUpdateProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateProductActionPerformed

        if (!this.isShowMessageSelected(tblHienThiKetQua)) {
            return;
        }
        this.txtMaSanPham.setEnabled(false);
        this.txtTenSP.setEnabled(true);
        this.txtMotaSPP.setEnabled(true);
        this.cbbDanhMuc.setEnabled(true);
        this.cbbNhaCC.setEnabled(true);
        this.rdoRun.setEnabled(true);
        this.rdoStop.setEnabled(true);

        this.choice = false;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnUpdateProductActionPerformed

    private void btnLuu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuu2ActionPerformed
        if (!this.isAddOrUpdate) {
            return;
        }

        String productCode = this.txtMaSanPham.getText().trim();
        String productName = this.txtTenSP.getText().trim();
        String description = this.txtMotaSPP.getText().trim();
        String categoryName = (String) this.cbbDanhMuc.getSelectedItem();
        String supplierName = (String) this.cbbNhaCC.getSelectedItem();
        Boolean status = this.rdoRun.isSelected();

        String validationError = this.qlsp.validateProductInput(productCode, productName, description, categoryName, supplierName, status, this.choice);

        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError);
            return;
        }

        Categories category = new Categories(this.qlsp.getCategoryIdByName(categoryName), categoryName);
        Suppliers supplier = new Suppliers(this.qlsp.getSupplierIdByName(supplierName), supplierName);

        Products newProduct = new Products(productCode, productName, category, description, supplier, status);

        if (this.choice) { // Thêm mới
            handleAddOrUpdate(productCode, newProduct, "sản phẩm", () -> this.qlsp.addNewProduct(newProduct));
            currentPages = totalPage;
        } else { // Cập nhật
            Products currentProduct = this.qlsp.getProductByCode(productCode);

            // Kiểm tra xem dữ liệu có thay đổi không
            if (currentProduct != null
                    && (!productName.equalsIgnoreCase(currentProduct.getProduct_name())
                    || !description.equalsIgnoreCase(currentProduct.getDescription())
                    || !categoryName.equalsIgnoreCase(currentProduct.getCategory().getCategory_name())
                    || !supplierName.equalsIgnoreCase(currentProduct.getSupplier().getSupplier_name())
                    || !status.equals(currentProduct.getStatus()))) {

                if (!productName.equalsIgnoreCase(currentProduct.getProduct_name()) && this.qlsp.isDulicateProductByName(productName)) {
                    JOptionPane.showMessageDialog(this, "Tên sán phẩm đã tồn tại.");
                    return;
                }

                handleAddOrUpdate(productCode, newProduct, "sản phẩm", () -> this.qlsp.updateProduct(newProduct));
            }
        }

        // Tải lại bảng và đặt lại trạng thái form
        this.qlsp.loadTable(tblHienThiKetQua, this.qlsp.getAllProduct(), currentPages, rowsPerPage);
        updatePaginations(qlsp.getAllProduct());
        this.setEnableFormSP();
        this.isAddOrUpdate = false;
        setInputFields("sản phẩm");
    }//GEN-LAST:event_btnLuu2ActionPerformed

    private void btnAddProductDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddProductDetailsActionPerformed
        this.jpViewQLBH2.setVisible(false);
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLSPP2.setVisible(true);
        this.jpViewQLSPP3.setVisible(false);
        this.jpViewDoiMK.setVisible(false);

        this.isAddOrUpdate = false;
        this.setEnableFormSP();
        this.setInputFields("sản phẩm chi tiết");
        this.setInputFields("sản phẩm");

        int[] selectedRows = this.tblHienThiKetQua.getSelectedRows();

        if (selectedRows.length == 0) {
            this.qlsp.loadTableDetail(tblSPCT, this.qlsp.getAllProductDetail(), currentPage, rowsPerPage);
            currentPage = 1;
            updatePagination(qlsp.getAllProductDetail());
            this.fillter = false;
        } else {
            ArrayList<ProductDetails> list = new ArrayList<>();
            for (int rowIndex : selectedRows) {
                String productCode = (String) this.tblHienThiKetQua.getValueAt(rowIndex, 1);
                list.addAll(this.qlsp.getListById(productCode));
                this.fillter = true;
                this.txtMaSP.setText(productCode);
            }
            this.qlsp.loadTableDetail(tblSPCT, list, currentPage, rowsPerPage);
            currentPage = 1;
            updatePagination(list);
        }
    }//GEN-LAST:event_btnAddProductDetailsActionPerformed

    private void tblHienThiKetQuaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHienThiKetQuaMouseClicked
        this.setEnableFormSP();
        int rowIndex = this.tblHienThiKetQua.getSelectedRow();
        if (rowIndex == -1 || rowIndex >= this.tblHienThiKetQua.getRowCount()) {
            return;
        }

        String proCode = (String) this.tblHienThiKetQua.getValueAt(rowIndex, 1);
        Products pro = this.qlsp.getProductByCode(proCode);
        this.txtMaSanPham.setText(pro.getProduct_code());
        this.txtTenSP.setText(pro.getProduct_name());
        this.txtMotaSPP.setText(pro.getDescription());
        this.cbbDanhMuc.setSelectedItem(pro.getCategory().getCategory_name());
        this.cbbNhaCC.setSelectedItem(pro.getSupplier().getSupplier_name());
        this.rdoRun.setSelected(pro.getStatus());
        this.rdoStop.setSelected(!pro.getStatus());

        this.isAddOrUpdate = false;
    }//GEN-LAST:event_tblHienThiKetQuaMouseClicked

    private void txtTimKiemTheoMaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtTimKiemTheoMaCaretUpdate
        String keyword = this.txtTimKiemTheoMa.getText().trim().toUpperCase();
        this.qlsp.loadTable(this.tblHienThiKetQua, this.qlsp.GetListSearch(keyword), currentPages, rowsPerPage);
    }//GEN-LAST:event_txtTimKiemTheoMaCaretUpdate

    private void txtTimKiemTheoMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimKiemTheoMaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTimKiemTheoMaActionPerformed

    private void txtTimKiemTheoMaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTimKiemTheoMaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTimKiemTheoMaKeyPressed

    private void btnHoanTac1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoanTac1ActionPerformed
        this.cbbLocTheoDM.setSelectedItem("");
        this.cbbTrangThaiSP.setSelectedItem("");
        this.cbbLocNhaCC.setSelectedItem("");
        this.qlsp.loadTable(this.tblHienThiKetQua, this.qlsp.getAllProduct(), currentPages, rowsPerPage);
    }//GEN-LAST:event_btnHoanTac1ActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        ArrayList<Products> productList = (ArrayList<Products>) qlsp.getAllProduct();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu tệp");

        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel files", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            ec.exportProductListToExcel(productList, filePath);
            JOptionPane.showMessageDialog(this, "Xuất danh sách sản phẩm ra tệp " + filePath + " thành công.");
        }
    }//GEN-LAST:event_btnExcelActionPerformed

    private void btnPreviousAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousAllActionPerformed
        currentPages = 1;
        updatePaginations(qlsp.filterProducts(selectedCategory, selectedSupplier, selectedStatus));
    }//GEN-LAST:event_btnPreviousAllActionPerformed

    private void btnPreviouActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviouActionPerformed
        if (currentPages > 1) {
            currentPages--;
            updatePaginations(qlsp.filterProducts(selectedCategory, selectedSupplier, selectedStatus));
        }
    }//GEN-LAST:event_btnPreviouActionPerformed

    private void btnNexttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNexttActionPerformed
        if (currentPages < totalPage) {
            currentPages++;
            updatePaginations(qlsp.filterProducts(selectedCategory, selectedSupplier, selectedStatus));
        }
    }//GEN-LAST:event_btnNexttActionPerformed

    private void btnNexttALLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNexttALLActionPerformed
        currentPages = totalPage;
        updatePaginations(qlsp.filterProducts(selectedCategory, selectedSupplier, selectedStatus));
    }//GEN-LAST:event_btnNexttALLActionPerformed

    private void btnBackProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackProductsActionPerformed
        this.jpViewQLBH2.setVisible(false);
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(true);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(false);
        this.jpViewDoiMK.setVisible(false);

        this.isAddOrUpdate = false;
        this.setEnableFormSPCT();
        this.qlsp.loadTable(tblHienThiKetQua, this.qlsp.getAllProduct(), currentPages, rowsPerPage);
        currentPages = 1;
        updatePaginations(qlsp.getAllProduct());
        txtMaSP.setText("");
        this.qlsp.addCbbNhaCC(cbbNhaCC);
        this.qlsp.addCbbNhaCC(cbbLocNhaCC);
    }//GEN-LAST:event_btnBackProductsActionPerformed

    private void btnAddChiTietSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddChiTietSPActionPerformed
        this.tblSPCT.clearSelection();
        this.txtMaSPCT1.setEnabled(false);
        this.cbbStyle.setEnabled(true);
        this.txtMaSP.setEnabled(true);
        this.cbbColor.setEnabled(true);
        this.cbbSizes.setEnabled(true);
        this.txtQuantity.setEnabled(true);
        this.txtPrice.setEnabled(true);
        this.lblImage.setEnabled(true);
        this.image = null;

        this.setInputFields("sản phẩm chi tiết");

        this.choice = true;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnAddChiTietSPActionPerformed

    private void btnCapNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatActionPerformed

        if (!this.isShowMessageSelected(tblSPCT)) {
            return;
        }

        this.txtMaSPCT1.setEnabled(false);
        this.cbbStyle.setEnabled(true);
        this.txtMaSP.setEnabled(true);
        this.cbbColor.setEnabled(true);
        this.cbbSizes.setEnabled(true);
        this.txtQuantity.setEnabled(true);
        this.txtPrice.setEnabled(true);
        this.lblImage.setEnabled(true);

        this.isAddOrUpdate = true;
        this.choice = false;
    }//GEN-LAST:event_btnCapNhatActionPerformed

    private void btnThemMoiThuocTinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemMoiThuocTinhActionPerformed
        this.jpViewQLBH2.setVisible(false);
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLSPP2.setVisible(false);
        this.jpViewQLSPP3.setVisible(true);
        this.jpViewDoiMK.setVisible(false);

        this.isAddOrUpdate = false;
        this.setEnableFormSPCT();
    }//GEN-LAST:event_btnThemMoiThuocTinhActionPerformed

    private void btnLuuSPCTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuSPCTActionPerformed
        if (!this.isAddOrUpdate) {
            return;
        }

        String productCode = this.txtMaSP.getText().trim().toUpperCase();
        String productDetailCode = this.txtMaSPCT1.getText();
        String colorName = (String) this.cbbColor.getSelectedItem();
        String styleName = (String) this.cbbStyle.getSelectedItem();
        String sizeName = (String) this.cbbSizes.getSelectedItem();
        String priceText = this.txtPrice.getText().trim();
        String quantityText = this.txtQuantity.getText().trim();

        if (!this.choice) {
            String existingImage = this.qlsp.getProductDetailByCode(productDetailCode).getImage();
            if (image == null || image.trim().isEmpty()) {
                image = existingImage;
            }
        }

        String validationError = this.qlsp.validateProductDetailInput(productCode, colorName, styleName,
                sizeName, priceText, quantityText, image, this.choice);

        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError);
            return;
        }

        String folderPath = "QRCodeImages";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            Integer productId = this.qlsp.getProductIdByCode(productCode);
            String colorId = this.qlsp.getColorIdByName(colorName);
            String styleId = this.qlsp.getStyleIdByName(styleName);
            String sizeId = this.qlsp.getSizeIdByName(sizeName);

            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            ProductDetails productDetail;

            if (this.choice && this.isAddOrUpdate) {
                // Kiểm tra trùng lặp khi thêm mới
                ProductDetails existingDetail = this.qlsp.getProductDetailByAttributes(productId, styleId, sizeId, colorId);
                if (existingDetail != null) {
                    JOptionPane.showMessageDialog(this, "Chi tiết sản phẩm đã tồn tại.");
                    return;
                }

                productDetail = new ProductDetails(productId, styleId, sizeId, colorId, price, quantity, image);
                handleAddOrUpdate(productCode, productDetail, "sản phẩm chi tiết", () -> this.qlsp.addProductDetail(productDetail));
                currentPage = totalPages;
                for (ProductDetails proDetail : qlsp.getAllProductDetail()) {
                    String filePath = folderPath + "/QRCode_" + proDetail.getProductDetail_code() + ".png";
                    QRCodeGenerator.generateQRCode(proDetail.getProductDetail_code(), filePath);
                }

            } else {
                boolean hasChanged = false;
                Integer productDetailId = this.qlsp.getProductDetailIdByCode(productDetailCode);
                ProductDetails oldProDe = qlsp.getProductDetailByCode(productDetailCode);
                if (oldProDe.getProduct_id() != productId
                        || !oldProDe.getStyle().equalsIgnoreCase(styleName)
                        || !oldProDe.getSize().equalsIgnoreCase(sizeName)
                        || !oldProDe.getColor().equalsIgnoreCase(colorName)
                        || oldProDe.getPrice() != price
                        || oldProDe.getQuantity() != quantity
                        || !oldProDe.getImage().equalsIgnoreCase(image)) {

                    if (oldProDe.getProduct_id() != productId
                            || !oldProDe.getStyle().equalsIgnoreCase(styleName)
                            || !oldProDe.getSize().equalsIgnoreCase(sizeName)
                            || !oldProDe.getColor().equalsIgnoreCase(colorName)) {

                        hasChanged = true;
                    }

                    if (hasChanged) {
                        // Kiểm tra trùng lặp khi cập nhật
                        ProductDetails existingDetail = this.qlsp.getProductDetailByAttributes(productId, styleId, sizeId, colorId);
                        if (existingDetail != null) {
                            JOptionPane.showMessageDialog(this, "Chi tiết sản phẩm đã tồn tại.");
                            return;
                        }
                    }
                    productDetail = new ProductDetails(productDetailId, productId, styleId, sizeId, colorId, price, quantity, image);
                    handleAddOrUpdate(productDetailCode, productDetail, "sản phẩm chi tiết", () -> this.qlsp.updateProductDetail(productDetail));
                    currentPage = 1;
                }
            }

            this.qlsp.loadTableDetail(tblSPCT, this.qlsp.getAllProductDetail(), currentPage, rowsPerPage);
            updatePagination(qlsp.getAllProductDetail());
            this.setEnableFormSPCT();
            this.setInputFields("sản phẩm chi tiết");
            this.txtMaSP.setText("");
            this.isAddOrUpdate = false;
            image = null;

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý dữ liệu sản phẩm chi tiết.");
        }

    }//GEN-LAST:event_btnLuuSPCTActionPerformed

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseClicked
        if (!this.isAddOrUpdate) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String imagePath = file.getAbsolutePath();

            displayImage(imagePath);
            this.image = imagePath;
        }
    }//GEN-LAST:event_lblImageMouseClicked

    private void txtSearhProductDetailCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSearhProductDetailCaretUpdate
        String keyword = this.txtSearhProductDetail.getText();
        this.qlsp.loadTableDetail(tblSPCT, this.qlsp.GetListSearchProductDetail(keyword), currentPage, rowsPerPage);
    }//GEN-LAST:event_txtSearhProductDetailCaretUpdate

    private void btnXemDSSPCTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemDSSPCTActionPerformed

        this.qlsp.loadTableDetail(tblSPCT, this.qlsp.getAllProductDetail(), currentPage, rowsPerPage);
        this.cbbLoaiSPCT.setSelectedItem("");
        this.cbbStyles.setSelectedItem("");
        this.cbbKichThuocSPCT.setSelectedItem("");
        this.cbbMauSacSPCT.setSelectedItem("");
        this.cbbSoLuong.setSelectedItem("");
        this.cbbGiaBan.setSelectedItem("");
    }//GEN-LAST:event_btnXemDSSPCTActionPerformed

    private void btnQRcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQRcodeActionPerformed
        qrCodeScanner.toggleScanning();
    }//GEN-LAST:event_btnQRcodeActionPerformed

    private void tblSPCTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSPCTMouseClicked
        this.setEnableFormSPCT();
        int rowIndex = this.tblSPCT.getSelectedRow();
        if (rowIndex == -1 || rowIndex >= this.tblSPCT.getRowCount()) {
            return;
        }

        String proDeCode = (String) this.tblSPCT.getValueAt(rowIndex, 1);

        ProductDetails proDe = this.qlsp.getProductDetailByCode(proDeCode);
        this.txtMaSPCT1.setText(proDe.getProductDetail_code());
        this.cbbStyle.setSelectedItem(proDe.getStyle());
        this.txtMaSP.setText(proDe.getProduct_code());
        this.cbbColor.setSelectedItem(proDe.getColor());
        this.cbbSizes.setSelectedItem(proDe.getSize());
        this.txtQuantity.setText(String.valueOf(proDe.getQuantity()));
        this.txtPrice.setText(String.valueOf(proDe.getPrice()));

        String imagePath = proDe.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            displayImage(imagePath);
        } else {
            lblImage.setIcon(null);
            lblImage.setText("Không có hình ảnh");
        }
        this.isAddOrUpdate = false;
    }//GEN-LAST:event_tblSPCTMouseClicked

    private void cbbSoLuongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbSoLuongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbbSoLuongActionPerformed

    private void btnExcelProductDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelProductDetailActionPerformed
        ArrayList<ProductDetails> productDetailList = (ArrayList<ProductDetails>) qlsp.getAllProductDetail();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu tệp");

        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel files", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            ec.exportProductDetailListToExcel(productDetailList, filePath);
            JOptionPane.showMessageDialog(this, "Xuất danh sách sản phẩm chi tiết ra tệp " + filePath + " thành công.");
        }
    }//GEN-LAST:event_btnExcelProductDetailActionPerformed

    private void btnDowloadQrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDowloadQrActionPerformed
        int rowIndex = this.tblSPCT.getSelectedRow();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 sản phẩm để thực hiện dowload QR_CODE.");
            return;
        }

        String productDetailCode = (String) this.tblSPCT.getValueAt(rowIndex, 1);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            String filePath = selectedFolder.getAbsolutePath() + "/QRCode_" + productDetailCode + ".png";

            try {
                QRCodeGenerator.generateQRCode(productDetailCode, filePath);
                JOptionPane.showMessageDialog(this, "Mã QR đã được lưu thành công tại: " + filePath);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo mã QR.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Bạn đã hủy chọn thư mục lưu.");
        }
    }//GEN-LAST:event_btnDowloadQrActionPerformed

    private void btnPreviouAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviouAllActionPerformed
        navigatePage(1, fillter);
    }//GEN-LAST:event_btnPreviouAllActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        if (currentPage > 1) {
            navigatePage(currentPage - 1, fillter);
        }
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        if (currentPage < totalPages) {
            navigatePage(currentPage + 1, fillter);
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnNextAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextAllActionPerformed
        navigatePage(totalPages, fillter);
    }//GEN-LAST:event_btnNextAllActionPerformed

    private void tblMauSacMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMauSacMouseClicked
        this.setEnableFormColor(false);
        int rowIndex = this.tblMauSac.getSelectedRow();
        if (rowIndex == -1) {
            return;
        }

        String colorId = (String) this.tblMauSac.getValueAt(rowIndex, 1);
        Model.Color color = qlsp.getColorById(colorId);
        this.txtMaMau.setText(color.getColor_id());
        this.txtTenMau.setText(color.getColor_name());
    }//GEN-LAST:event_tblMauSacMouseClicked

    private void btnThemMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemMoiActionPerformed
        this.tblMauSac.clearSelection();
        this.setEnableFormColor(true);
        this.setInputFields("màu sắc");
        this.choice = true;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnThemMoiActionPerformed

    private void btnCapNhatMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatMSActionPerformed
        if (!this.isShowMessageSelected(tblMauSac)) {
            return;
        }
        this.setEnableFormColor(true);
        this.txtMaMau.setEnabled(false);
        this.choice = false;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnCapNhatMSActionPerformed

    private void btnLuuColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuColorActionPerformed
        if (!this.isAddOrUpdate) {
            return;
        }

        String colorId = this.txtMaMau.getText().trim();
        String colorName = this.txtTenMau.getText().trim();

        String validationError = this.qlsp.validateColorInput(colorId, colorName, this.choice);

        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError);
            return;
        }

        Model.Color newColor = new Model.Color(colorId, colorName);

        if (this.choice) { // Thêm mới
            handleAddOrUpdate(colorId, newColor, "màu sắc", () -> this.qlsp.addColor(newColor));
        } else { // Cập nhật
            Model.Color currentColor = this.qlsp.getColorById(colorId);

            // Kiểm tra xem dữ liệu có thay đổi không
            if (currentColor != null && !colorName.equalsIgnoreCase(currentColor.getColor_name())) {

                if (!colorName.equalsIgnoreCase(currentColor.getColor_name()) && this.qlsp.isDuplicateColorByName(colorName)) {
                    JOptionPane.showMessageDialog(this, "Tên màu sắc đã tồn tại.");
                    return;
                }

                handleAddOrUpdate(colorId, newColor, "màu sắc", () -> this.qlsp.updateColor(newColor));
            }
        }

        // Tải lại bảng và đặt lại trạng thái form
        this.qlsp.loadTableColor(tblMauSac, this.qlsp.getAllColor());
        this.setEnableFormColor(false);
        this.setInputFields("màu sắc");
        this.isAddOrUpdate = false;
    }//GEN-LAST:event_btnLuuColorActionPerformed

    private void tblKichThuocMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKichThuocMouseClicked
        this.setEnableFormSize(false);
        int rowIndex = this.tblKichThuoc.getSelectedRow();
        if (rowIndex == -1) {
            return;
        }

        String sizeId = (String) this.tblKichThuoc.getValueAt(rowIndex, 1);

        Size size = qlsp.getSizeById(sizeId);
        this.txtMaKT.setText(size.getSize_id());
        this.txtTenKichThuoc.setText(size.getSize_name());
        this.txtMoTaSize.setText(size.getSize_description());
    }//GEN-LAST:event_tblKichThuocMouseClicked

    private void btnThemMoiKTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemMoiKTActionPerformed
        this.tblKichThuoc.clearSelection();
        this.setEnableFormSize(true);
        this.setInputFields("kích thước");
        this.choice = true;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnThemMoiKTActionPerformed

    private void btnCapNhatKTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatKTActionPerformed
        if (!this.isShowMessageSelected(tblKichThuoc)) {
            return;
        }
        this.setEnableFormSize(true);
        this.txtMaKT.setEnabled(false);
        this.choice = false;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnCapNhatKTActionPerformed

    private void btnLuuSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuSizeActionPerformed
        if (!this.isAddOrUpdate) {
            return;
        }

        String sizeId = this.txtMaKT.getText().trim();
        String sizeName = this.txtTenKichThuoc.getText().trim();
        String sizeDescription = this.txtMoTaSize.getText().trim();

        String validationError = this.qlsp.validateSizeInput(sizeId, sizeName, this.choice);

        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError);
            return;
        }

        Size newSize = new Size(sizeId, sizeName, sizeDescription);

        if (this.choice) { // Thêm mới
            handleAddOrUpdate(sizeId, newSize, "kích thước", () -> this.qlsp.addSize(newSize));
        } else { // Cập nhật
            Size currentSize = this.qlsp.getSizeById(sizeId);

            // Kiểm tra xem dữ liệu có thay đổi không
            if (currentSize != null
                    && (!sizeName.equalsIgnoreCase(currentSize.getSize_name())
                    || !sizeDescription.equalsIgnoreCase(currentSize.getSize_description()))) {

                if (!sizeName.equalsIgnoreCase(currentSize.getSize_name()) && this.qlsp.isDuplicateSizeByName(sizeName)) {
                    JOptionPane.showMessageDialog(this, "Tên kích thước đã tồn tại");
                    return;
                }

                handleAddOrUpdate(sizeId, newSize, "kích thước", () -> this.qlsp.updateSize(newSize));
            }
        }

        // Tải lại bảng và đặt lại trạng thái form
        this.qlsp.loadTableSize(tblKichThuoc, this.qlsp.getAllSize());
        this.setEnableFormSize(false);
        this.setInputFields("kích thước");
        this.isAddOrUpdate = false;
    }//GEN-LAST:event_btnLuuSizeActionPerformed

    private void btnThemNCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemNCCActionPerformed
        this.tblNhaCungCap.clearSelection();
        this.setInputFields("nhà cung cấp");
        this.setEnableFormSupp(true);
        this.choice = true;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnThemNCCActionPerformed

    private void btnCapNhatNCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatNCCActionPerformed
        if (!this.isShowMessageSelected(tblNhaCungCap)) {
            return;
        }
        this.setEnableFormSupp(true);
        this.txtNCC.setEnabled(false);
        this.choice = false;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnCapNhatNCCActionPerformed

    private void btnLuuSuppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuSuppActionPerformed
        if (!this.isAddOrUpdate) {
            return;
        }

        String supplierId = this.txtNCC.getText().trim();
        String supplierName = this.txtTenNCC.getText().trim();
        String phone = this.txtSDTNCC.getText().trim();
        String email = this.txtEmailNCC.getText().trim();
        String address = this.txtDiaChiNCC.getText().trim();

        String validationError = this.qlsp.validateSupplierInput(supplierId, supplierName, phone, email, address, this.choice);

        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError);
            return;
        }

        Suppliers newSupplier = new Suppliers(supplierId, supplierName, phone, email, address);

        if (this.choice) { // Thêm mới
            handleAddOrUpdate(supplierId, newSupplier, "nhà cung cấp", () -> this.qlsp.addSupplier(newSupplier));
        } else { // Cập nhật
            Suppliers currentSupplier = this.qlsp.getSupplierById(supplierId);

            // Kiểm tra xem dữ liệu có thay đổi không
            if (!supplierName.equalsIgnoreCase(currentSupplier.getSupplier_name())
                    || !phone.equalsIgnoreCase(currentSupplier.getPhone())
                    || !email.equalsIgnoreCase(currentSupplier.getEmail())
                    || !address.equalsIgnoreCase(currentSupplier.getAddress())) {

                // Kiểm tra trùng lặp chỉ khi có thay đổi 
                if (!supplierName.equalsIgnoreCase(currentSupplier.getPhone()) && this.qlsp.isDuplicateSupplierByName(supplierName)) {
                    JOptionPane.showMessageDialog(this, "Tên nhà cung cấp đã tồn tại.");
                    return;
                }

                if (!phone.equalsIgnoreCase(currentSupplier.getPhone()) && this.qlsp.isDuplicateSupplierByPhone(phone)) {
                    JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại.");
                    return;
                }

                if (!email.equalsIgnoreCase(currentSupplier.getEmail()) && this.qlsp.isDuplicateSupplierByEmail(email)) {
                    JOptionPane.showMessageDialog(this, "Email này đã tồn tại.");
                    return;
                }

                handleAddOrUpdate(supplierId, newSupplier, "nhà cung cấp", () -> this.qlsp.updateSupplier(newSupplier));
            }
        }

        // Tải lại bảng và đặt lại trạng thái form
        this.qlsp.loadTableSupplier(tblNhaCungCap, this.qlsp.getAllSuppliers());
        this.setEnableFormSupp(false);
        this.setInputFields("nhà cung cấp");
        this.isAddOrUpdate = false;
    }//GEN-LAST:event_btnLuuSuppActionPerformed

    private void tblNhaCungCapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNhaCungCapMouseClicked
        this.setEnableFormSupp(false);
        int rowIndex = this.tblNhaCungCap.getSelectedRow();
        if (rowIndex == -1) {
            return;
        }

        String suppId = (String) this.tblNhaCungCap.getValueAt(rowIndex, 1);

        Suppliers supp = qlsp.getSupplierById(suppId);
        this.txtNCC.setText(supp.getSupplier_id());
        this.txtTenNCC.setText(supp.getSupplier_name());
        this.txtSDTNCC.setText(supp.getPhone());
        this.txtEmailNCC.setText(supp.getEmail());
        this.txtDiaChiNCC.setText(supp.getAddress());
    }//GEN-LAST:event_tblNhaCungCapMouseClicked

    private void tblPhongCachMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPhongCachMouseClicked
        this.setEnableFormStyle(false);
        int rowIndex = this.tblPhongCach.getSelectedRow();
        if (rowIndex == -1) {
            return;
        }

        String styleId = (String) this.tblPhongCach.getValueAt(rowIndex, 1);

        Style style = qlsp.getStyleById(styleId);
        this.txtMaPC.setText(style.getStyle_id());
        this.txtTenPhongCach.setText(style.getStyle_name());
        this.txtMoTaPC.setText(style.getStyle_description());
    }//GEN-LAST:event_tblPhongCachMouseClicked

    private void btnThemPCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemPCActionPerformed
        this.tblPhongCach.clearSelection();
        this.setEnableFormStyle(true);
        this.setInputFields("phong cách");
        this.choice = true;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnThemPCActionPerformed

    private void btnCapNhatPCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatPCActionPerformed
        if (!this.isShowMessageSelected(tblPhongCach)) {
            return;
        }
        this.setEnableFormStyle(true);
        this.txtMaPC.setEnabled(false);
        this.choice = false;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnCapNhatPCActionPerformed

    private void btnLuuStyleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuStyleActionPerformed
        if (!this.isAddOrUpdate) {
            return;
        }
        String styleId = this.txtMaPC.getText().trim();
        String styleName = this.txtTenPhongCach.getText().trim();
        String description = this.txtMoTaPC.getText().trim();

        String validationError = this.qlsp.validateStyleInput(styleId, styleName, this.choice);

        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError);
            return;
        }

        Style style = new Style(styleId, styleName, description);

        if (this.choice && this.isAddOrUpdate) {
            handleAddOrUpdate(styleId, style, "phong cách", () -> this.qlsp.addStyle(style));
        } else {
            // Lấy phong cách hiện tại từ cơ sở dữ liệu
            Style currentStyle = this.qlsp.getStyleById(styleId);

            // Kiểm tra xem dữ liệu có thay đổi không
            if (!styleName.equalsIgnoreCase(currentStyle.getStyle_name())
                    || !description.equalsIgnoreCase(currentStyle.getStyle_description())) {
                if (!styleName.equalsIgnoreCase(currentStyle.getStyle_name()) && this.qlsp.isDuplicateStyleByName(styleName)) {
                    JOptionPane.showMessageDialog(this, "Tên phong cách đã tồn tại.");
                    return;
                }

                handleAddOrUpdate(styleId, style, "phong cách", () -> this.qlsp.updateStyle(style));
            }
        }

        // Tải lại bảng và đặt lại trạng thái form
        this.qlsp.loadTableStyle(tblPhongCach, this.qlsp.getAllStyle());
        this.setEnableFormStyle(false);
        this.setInputFields("phong cách");
        this.isAddOrUpdate = false;
    }//GEN-LAST:event_btnLuuStyleActionPerformed

    private void btnBackkkkkkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackkkkkkActionPerformed
        this.jpViewQLBH2.setVisible(false);
        this.jpViewQLHD.setVisible(false);
        this.jpViewQLDM.setVisible(false);
        this.jpViewQLKH.setVisible(false);
        this.jpViewQLKM.setVisible(false);
        this.jpViewQLSPP.setVisible(false);
        this.jpViewQLTK.setVisible(false);
        this.jpViewTK.setVisible(false);
        this.jpViewQLSPP2.setVisible(true);
        this.jpViewQLSPP3.setVisible(false);
        this.jpViewDoiMK.setVisible(false);

        this.setInputFields("màu sắc");
        this.setInputFields("kích thước");
        this.setInputFields("nhà cung cấp");
        this.setInputFields("phong cách");
        this.setEnableFormColor(false);
        this.setEnableFormSize(false);
        this.setEnableFormStyle(false);
        this.setEnableFormSupp(false);
        this.isAddOrUpdate = false;
        this.qlsp.addCbbColor(cbbColor);
        this.qlsp.addCbbColor(cbbMauSacSPCT);
        this.qlsp.addCbbSize(cbbSizes);
        this.qlsp.addCbbSize(cbbKichThuocSPCT);
        this.qlsp.addCbbStyle(cbbStyles);
        this.qlsp.addCbbStyle(cbbStyle);
    }//GEN-LAST:event_btnBackkkkkkActionPerformed

    private void tblKhachHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKhachHMouseClicked

        Integer rowIndex = this.tblKhachH.getSelectedRow();
        if (rowIndex == -1) {
            return;
        }

        String cusCode = (String) this.tblKhachH.getValueAt(rowIndex, 1);

        Customers cus = this.qlkh.getCustomerByCode(cusCode);
        this.txtMaKH.setText(cus.getCustomer_code());
        this.txtITenKhachHang1.setText(cus.getCustomer_name());
        this.txtSoDiienThoai.setText(cus.getPhone());
        this.txtDiemTichLuy.setText(String.valueOf(cus.getPoint_gained()));
        this.txtDiemDaDung.setText(String.valueOf(cus.getPoints_used()));
        this.txtTinhDiem.setText(String.valueOf(cus.getPoint_gained() - cus.getPoints_used()));

        this.setEnableFormKH();
        this.isAddOrUpdate = false;

    }//GEN-LAST:event_tblKhachHMouseClicked

    private void btnThemMoiKH1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemMoiKH1ActionPerformed
        this.tblKhachH.clearSelection();
        this.setText();
        this.txtITenKhachHang1.setEnabled(true);
        this.txtSoDiienThoai.setEnabled(true);

        this.choice = true;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnThemMoiKH1ActionPerformed

    private void btnLamMoiKH1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiKH1ActionPerformed
        if (!this.isAddOrUpdate) {
            return;
        }

        String cusName = this.txtITenKhachHang1.getText().trim();
        String cusPhone = this.txtSoDiienThoai.getText().trim();
        Integer cusID = this.qlkh.getCusIdByCode(this.txtMaKH.getText());

        if (cusName == null || cusName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng.");
            return;
        }
        if (cusName.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được chứa số.");
            return;
        }

        if (cusPhone == null || cusPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại khách hàng.");
            return;
        }
        String phonePattern = "^(03|07|08|09)[0-9]{8}$";
        if (!cusPhone.matches(phonePattern)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không đúng định dạng.");
            return;
        }

        if (this.choice && this.isAddOrUpdate) {
            if (this.qlkh.isDulicatePhoneKH(cusPhone)) {
                JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn thêm khách hàng mới?",
                    "Xác nhận thêm",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Customers cus = new Customers(cusName, cusPhone);
                this.qlkh.addCus(cus);
                JOptionPane.showMessageDialog(this, "Thêm khách hàng mới thành công.");
            }

        } else if (!this.choice && this.isAddOrUpdate) {
            Integer rowIndex = this.tblKhachH.getSelectedRow();
            if (rowIndex == -1) {
                return;
            }
            Customers oldCus = qlkh.getCustomerByCode(this.txtMaKH.getText());
            if (!oldCus.getCustomer_name().equalsIgnoreCase(cusName) || !oldCus.getPhone().equalsIgnoreCase(cusPhone)) {

                String oldPhone = (String) this.tblKhachH.getValueAt(rowIndex, 3);
                if (!cusPhone.equals(oldPhone)) {
                    if (this.qlkh.isDulicatePhoneKH(cusPhone)) {
                        JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại.");
                        return;
                    }
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn cập nhật thông tin khách hàng?",
                        "Xác nhận cập nhật",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Customers cus = new Customers(cusID, cusName, cusPhone);
                    this.qlkh.updateCus(cus);
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin khách hàng thành công.");
                }
            }

        }

        this.qlkh.loadTableCus(tblKhachH, this.qlkh.getAllCustomer());
        this.setEnableFormKH();
        this.isAddOrUpdate = false;
        this.setText();
    }//GEN-LAST:event_btnLamMoiKH1ActionPerformed

    private void btnSuaKhachHang1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaKhachHang1ActionPerformed
        Integer rowIndex = this.tblKhachH.getSelectedRow();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng 1 dòng trên table để cập nhật.");
            return;
        }

        this.txtITenKhachHang1.setEnabled(true);
        this.txtSoDiienThoai.setEnabled(true);

        this.choice = false;
        this.isAddOrUpdate = true;
    }//GEN-LAST:event_btnSuaKhachHang1ActionPerformed

    private void txtSoDiienThoaiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSoDiienThoaiKeyReleased

    }//GEN-LAST:event_txtSoDiienThoaiKeyReleased


    private void txtSearchKHCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtSearchKHCaretUpdate
        String keyword = txtSearchKH.getText();
        this.txtSearchKH.getText().trim();
        this.qlkh.getListSearch(keyword, tblKhachH);
    }//GEN-LAST:event_txtSearchKHCaretUpdate

    private void txtSoDiemDaDungKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSoDiemDaDungKeyReleased

        tinhTongTien();

    }//GEN-LAST:event_txtSoDiemDaDungKeyReleased

    private void txtSoDiemDaDungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoDiemDaDungActionPerformed

    }//GEN-LAST:event_txtSoDiemDaDungActionPerformed

    private void btnLuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuiActionPerformed
        qlHoaDon.previousPage();
        tblDSHD.setModel(qlHoaDon.loadOrderData());
        lblSoTrang.setText(qlHoaDon.getCurrentOrderPage() + " / " + qlHoaDon.getTotalOrderPages());

    }//GEN-LAST:event_btnLuiActionPerformed

    private void btnTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTienActionPerformed
        qlHoaDon.nextPage();
        tblDSHD.setModel(qlHoaDon.loadOrderData());
        lblSoTrang.setText(qlHoaDon.getCurrentOrderPage() + " / " + qlHoaDon.getTotalOrderPages());
    }//GEN-LAST:event_btnTienActionPerformed

    private void btnVeTrangDauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVeTrangDauActionPerformed
        qlHoaDon.setCurrentOrderPage(1); // Đặt trang hiện tại về 1
        tblDSHD.setModel(qlHoaDon.loadOrderData()); // Tải dữ liệu cho trang đầu
        lblSoTrang.setText(qlHoaDon.getCurrentOrderPage() + " / " + qlHoaDon.getTotalOrderPages()); // Cập nhật lblSoTrang
    }//GEN-LAST:event_btnVeTrangDauActionPerformed

    private void btnVeTrangCuoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVeTrangCuoiActionPerformed
        qlHoaDon.setCurrentOrderPage(qlHoaDon.getTotalOrderPages()); // Đặt trang hiện tại về trang cuối
        tblDSHD.setModel(qlHoaDon.loadOrderData()); // Tải dữ liệu cho trang cuối
        lblSoTrang.setText(qlHoaDon.getCurrentOrderPage() + " / " + qlHoaDon.getTotalOrderPages()); // Cập nhật lblSoTrang
    }//GEN-LAST:event_btnVeTrangCuoiActionPerformed

    private void tblNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNhanVienMouseClicked
        try {
            int dong = tblNhanVien.getSelectedRow();
            if (dong == -1) {
                return;
            }

            String maNhanVien = (String) tblNhanVien.getValueAt(dong, 0);
            us1sv.showDataNv(maNhanVien, txtMaNhanVienn, txtHoTen1, txtTenDangNhap, txtEmail1, txtSDT, txtDiaChi1, cbbVaiTro, dateNgayTao, rdoDanglam, rdoNghilam, tblNhanVien);

            txtHoTen1.setEnabled(false);
            txtDiaChi1.setEnabled(false);
            txtEmail1.setEnabled(false);
            txtSDT.setEnabled(false);
            txtTenDangNhap.setEnabled(false);
            dateNgayTao.setEnabled(false);
            cbbVaiTro.setEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_tblNhanVienMouseClicked

    private void txtHoTen2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHoTen2KeyReleased
        try {
            String hoTenCt = txtHoTen2.getText().trim();
            User1Service us1sv = new User1Service();

            this.loadDatatoTableTimKiem(us1sv.TimKiemUser1(hoTenCt));
            txtDiaChi1.setText("");
            txtEmail1.setText("");
            txtSDT.setText("");
            txtTenDangNhap.setText("");

            rdoDanglam.setSelected(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_txtHoTen2KeyReleased

    private void tblCouponMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCouponMouseClicked
        //tạo và copy
        //----------------------------------------------
        //MouseClicked của bảng Voucher giảm giá
        updateVoucher = 1;
        try {
            VoucherService vou = new VoucherService();
            vou.showVoucher(tblCoupon, txtMaGiamGia, txtMoTa, txtCode, txtPhanTramChietKhau, jdc_ngay_bat_dau_voucher, jdc_ngay_ket_thuc_voucher, txt_tien_ap_dung, txt_tien_toi_da, rdoDangKM, rdoDungKM, txt_so_luong_vou);
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtMaGiamGia.setEnabled(false);
        txtMoTa.setEnabled(false);
        txtCode.setEnabled(false);
        txtPhanTramChietKhau.setEnabled(false);
        jdc_ngay_bat_dau_voucher.setEnabled(false);
        jdc_ngay_ket_thuc_voucher.setEnabled(false);
        txt_tien_ap_dung.setEnabled(false);
        txt_tien_toi_da.setEnabled(false);
        txt_so_luong_vou.setEnabled(false);
        rdoDangKM.setEnabled(false);
        rdoDungKM.setEnabled(false);
        //-----------------------------------------------
    }//GEN-LAST:event_tblCouponMouseClicked

    private void btnAddCouMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddCouMouseClicked
        //copy-----------------------------------------------------------
        //nút thêm voucher
        addVoucher = 1;
        updateVoucher = 0;
        txtMaGiamGia.setText("");
        txtCode.setText("");
        txtMoTa.setText("");
        txtPhanTramChietKhau.setText("");
        txt_so_luong_vou.setText("");
        jdc_ngay_bat_dau_voucher.setDate(null);
        jdc_ngay_ket_thuc_voucher.setDate(null);
        txtMoTa.setBackground(Color.WHITE);
        txtPhanTramChietKhau.setBackground(Color.WHITE);
        jdc_ngay_bat_dau_voucher.setBackground(Color.WHITE);
        jdc_ngay_ket_thuc_voucher.setBackground(Color.WHITE);
        txt_tien_toi_da.setBackground(Color.WHITE);
        txt_tien_ap_dung.setBackground(Color.WHITE);
        txt_tien_ap_dung.setText("");
        txt_tien_toi_da.setText("");
        try {
            VoucherService vou = new VoucherService();
            vou.loadTableVoucher(tblCoupon, currentPage, itemsPerPagee);

        } catch (Exception e) {
        }
        txtMaGiamGia.setEnabled(false);
        txtMoTa.setEnabled(true);
        txtCode.setEnabled(false);
        txtPhanTramChietKhau.setEnabled(true);
        jdc_ngay_bat_dau_voucher.setEnabled(true);
        jdc_ngay_ket_thuc_voucher.setEnabled(true);
        txt_tien_ap_dung.setEnabled(true);
        txt_tien_toi_da.setEnabled(true);
        txt_so_luong_vou.setEnabled(true);
        rdoDangKM.setEnabled(true);
        rdoDungKM.setEnabled(true);
        //------------------------------------------------
    }//GEN-LAST:event_btnAddCouMouseClicked

    private void btnAddCouActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCouActionPerformed

    }//GEN-LAST:event_btnAddCouActionPerformed

    private void btnEditCouMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditCouMouseClicked
        // tạo và copy----------------------------------------------------------------------
        //--------------------Nút cập nhật bên voucher
        addVoucher = 0;
        updateVoucher = 1;
        int row = tblCoupon.getSelectedRow();
        if (row == -1) {
            updateVoucher = 0;
            JOptionPane.showMessageDialog(this, "Hãy chọn 1 chương trình để cập nhật");
            return;
        }
        txtMaGiamGia.setEnabled(false);
        txtMoTa.setEnabled(true);
        txtCode.setEnabled(false);
        txtPhanTramChietKhau.setEnabled(true);
        jdc_ngay_bat_dau_voucher.setEnabled(true);
        jdc_ngay_ket_thuc_voucher.setEnabled(true);
        txt_tien_ap_dung.setEnabled(true);
        txt_tien_toi_da.setEnabled(true);
        txt_so_luong_vou.setEnabled(true);
        rdoDangKM.setEnabled(true);
        rdoDungKM.setEnabled(true);
        //-----------------------------------------------------------------------------------------------
    }//GEN-LAST:event_btnEditCouMouseClicked

    private void btnEditCouActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditCouActionPerformed

    }//GEN-LAST:event_btnEditCouActionPerformed

    private void txtPhanTramChietKhauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhanTramChietKhauActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhanTramChietKhauActionPerformed

    private void btnVoHHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVoHHMouseClicked
        //tạo và copy--------------------------------------------------------------
        //nút vô hiệu hóa
        int row = tblCoupon.getSelectedRow(); // Phương thức để lấy ID của voucher được chọn
        String ma_Voucher = tblCoupon.getValueAt(row, 0).toString();
        int ma = 0;
        try {
            VoucherService vou = new VoucherService();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một voucher để vô hiệu hóa");

            } else {
                ma = Integer.parseInt(ma_Voucher);
                vou.voHieuHoaVoucher(ma);
                vou.loadTableVoucher(tblCoupon, currentPage, itemsPerPagee);
                JOptionPane.showMessageDialog(this, " Vô hiệu hóa thành công");

            }
        } catch (Exception e) {
            e.printStackTrace();

            return;
        }
        //-----------------------------------------------------------------------------------------
    }//GEN-LAST:event_btnVoHHMouseClicked

    private void btnLuu3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLuu3MouseClicked
        // TODO add your handling code here:
        //tạo và copy-------------Nút lưu Voucher--------------------------------
        int i = tblCoupon.getSelectedRow();

        if (i == -1) {
            if (addVoucher == 0) {
                return;
            }

            String mota = txtMoTa.getText().trim();
            String discountValue = txtPhanTramChietKhau.getText().trim();
            String trangThai = rdoDangKM.isSelected() ? "Hoạt động" : "Dừng hoạt động";
            String min = txt_tien_ap_dung.getText().trim();
            String max = txt_tien_toi_da.getText().trim();
            String sl = txt_so_luong_vou.getText().trim();

            try {
                VoucherService vou = new VoucherService();
                if (vou.readFromVou(txtMoTa, txtPhanTramChietKhau, jdc_ngay_bat_dau_voucher, jdc_ngay_ket_thuc_voucher, txt_tien_ap_dung, txt_tien_toi_da, txt_so_luong_vou, rdoDangKM) == null) {
                    return;
                }
                int conf = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thêm ?");
                if (conf != 0) {
                    return;
                }
                vou.themVoucher(vou.readFromVou(txtMoTa, txtPhanTramChietKhau, jdc_ngay_bat_dau_voucher, jdc_ngay_ket_thuc_voucher, txt_tien_ap_dung, txt_tien_toi_da, txt_so_luong_vou, rdoDangKM));

                JOptionPane.showMessageDialog(this, "Thêm thành công");
                cbbVouchers.removeAllItems();
                ql.loadVoucher(cbbVouchers);
                txtPhanTramChietKhau.setText("");
                tblCoupon.clearSelection();
                vou.loadTableVoucher(tblCoupon, currentPage, itemsPerPagee);

                rdoDangKM.setEnabled(false);
                rdoDungKM.setEnabled(false);
                jdc_ngay_bat_dau_voucher.setDate(null);
                jdc_ngay_ket_thuc_voucher.setDate(null);
                txt_tien_toi_da.setText("");
                txt_tien_ap_dung.setText("");
                txt_so_luong_vou.setText("");
                txtMaGiamGia.setText("");
                txtCode.setText("");
                txtMoTa.setText("");
                jdc_ngay_bat_dau_voucher.setEnabled(false);
                jdc_ngay_ket_thuc_voucher.setEnabled(false);
                txt_tien_toi_da.setEnabled(false);
                txt_tien_ap_dung.setEnabled(false);
                txt_so_luong_vou.setEnabled(false);
                txtMaGiamGia.setEnabled(false);
                txtCode.setEnabled(false);
                txtMoTa.setEnabled(false);
                txtPhanTramChietKhau.setBackground(Color.WHITE);
                jdc_ngay_bat_dau_voucher.setBackground(Color.WHITE);
                jdc_ngay_ket_thuc_voucher.setBackground(Color.WHITE);
                txtMoTa.setBackground(Color.WHITE);
                txt_tien_toi_da.setBackground(Color.WHITE);
                txt_tien_ap_dung.setBackground(Color.WHITE);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            if (updateVoucher == 0) {
                return;
            }
            String ma = tblCoupon.getValueAt(i, 0).toString();
            int ma_Voucher = 0;

            try {
                VoucherService vou = new VoucherService();
                if (vou.checkThaoTacVoucher(tblCoupon, txtMaGiamGia, txtMoTa, txtCode, txtPhanTramChietKhau, jdc_ngay_bat_dau_voucher, jdc_ngay_ket_thuc_voucher, txt_tien_ap_dung, txt_tien_toi_da, rdoDangKM, rdoDungKM, txt_so_luong_vou) == 1) {
                    return;
                }
                if (vou.readFromVou(txtMoTa, txtPhanTramChietKhau, jdc_ngay_bat_dau_voucher, jdc_ngay_ket_thuc_voucher, txt_tien_ap_dung, txt_tien_toi_da, txt_so_luong_vou, rdoDangKM) == null) {
                    return;
                }

                int conf = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn cập nhật ?");
                if (conf != 0) {
                    return;
                }
                ma_Voucher = Integer.parseInt(ma);
                vou.capNhatVoucher(vou.readFromVou(txtMoTa, txtPhanTramChietKhau, jdc_ngay_bat_dau_voucher, jdc_ngay_ket_thuc_voucher, txt_tien_ap_dung, txt_tien_toi_da, txt_so_luong_vou, rdoDangKM), ma_Voucher);
                vou.loadTableVoucher(tblCoupon, currentPage, itemsPerPagee);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công");
                txtPhanTramChietKhau.setText("");
                tblCoupon.clearSelection();

                cbbVouchers.removeAllItems();
                ql.loadVoucher(cbbVouchers);

                rdoDangKM.setEnabled(false);
                rdoDungKM.setEnabled(false);
                jdc_ngay_bat_dau_voucher.setDate(null);
                jdc_ngay_ket_thuc_voucher.setDate(null);
                txt_tien_toi_da.setText("");
                txt_tien_ap_dung.setText("");
                txt_so_luong_vou.setText("");
                txtMaGiamGia.setText("");
                txtCode.setText("");
                txtMoTa.setText("");
                jdc_ngay_bat_dau_voucher.setEnabled(false);
                jdc_ngay_ket_thuc_voucher.setEnabled(false);
                txt_tien_toi_da.setEnabled(false);
                txt_tien_ap_dung.setEnabled(false);
                txt_so_luong_vou.setEnabled(false);
                txtMaGiamGia.setEnabled(false);
                txtCode.setEnabled(false);
                txtMoTa.setEnabled(false);
                txtPhanTramChietKhau.setBackground(Color.WHITE);
                jdc_ngay_bat_dau_voucher.setBackground(Color.WHITE);
                jdc_ngay_ket_thuc_voucher.setBackground(Color.WHITE);
                txtMoTa.setBackground(Color.WHITE);
                txt_tien_toi_da.setBackground(Color.WHITE);
                txt_tien_ap_dung.setBackground(Color.WHITE);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        //-------------------------------------------------------------------------------------------------------------------
    }//GEN-LAST:event_btnLuu3MouseClicked

    private void btnLuu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuu3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLuu3ActionPerformed

    private void txt_tien_toi_daActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_tien_toi_daActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_tien_toi_daActionPerformed

    private void txt_so_luong_vouActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_so_luong_vouActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_so_luong_vouActionPerformed

    private void btnLuuThayDoiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLuuThayDoiMouseClicked
        //them và copy----------------------
        //nút lưu điểm quy đổi
        try {
            PointsConversion_Service p = new PointsConversion_Service();
            if (p.checkLuu(txtMoney, txtMoney1) == 1) {
                return;
            }
            p.ponts(txtMoney, txtMoney1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //-----------------------
    }//GEN-LAST:event_btnLuuThayDoiMouseClicked

    private void btnResetPointMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnResetPointMouseClicked
        // thêm và copy----------------------------------------------------
        //---------------nút làm mới điểm quy đổi
        try {
            PointsConversion_Service psv = new PointsConversion_Service();
            txtPoint.setText(String.valueOf(1));
            double tienSangDiem = psv.tienSangDiem();
            double diemSangTien = psv.diemSangTien();
            txtMoney1.setText(String.valueOf(diemSangTien));
            txtMoney.setText(String.valueOf(tienSangDiem));
            System.out.println(diemSangTien + tienSangDiem);
            txtPoint1.setText(String.valueOf(1000));
            txtMoney.setBackground(Color.WHITE);
            txtMoney1.setBackground(Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //----------------------------------------------------------------------
    }//GEN-LAST:event_btnResetPointMouseClicked

    private void txt_tim_VoucherCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txt_tim_VoucherCaretUpdate
        // TODO add your handling code here:
        try {
            VoucherService vou = new VoucherService();
            String key = txt_tim_Voucher.getText().trim();
            vou.loadTableVoucherTim(tblCoupon, key);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_txt_tim_VoucherCaretUpdate

    private void btnLuiVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuiVoucherActionPerformed
        // TODO add your handling code here:
        if (currentPage > 1) {
            currentPage--;
            //txt_phan_trangVoucher.setText(String.valueOf(currentPage));
            txt_phan_trangVoucher.setText(currentPage + "/" + totalPages);
        }
        try {
            VoucherService vou = new VoucherService();
            vou.loadTableVoucher(tblCoupon, currentPage, itemsPerPagee);;
        } catch (Exception e) {
        }
    }//GEN-LAST:event_btnLuiVoucherActionPerformed

    private void btnTienVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTienVoucherActionPerformed
        // TODO add your handling code here:

        // txt_phan_trangVoucher.setText(String.valueOf(currentPage));
        if (currentPage < totalPages) {
            currentPage++;
            try {
                VoucherService vou = new VoucherService();
                txt_phan_trangVoucher.setText(currentPage + "/" + totalPages);
                vou.loadTableVoucher(tblCoupon, currentPage, itemsPerPagee);;
            } catch (Exception e) {
            }
        }

    }//GEN-LAST:event_btnTienVoucherActionPerformed

    private void btnTienCuoiVoucher1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTienCuoiVoucher1ActionPerformed
        // TODO add your handling code here:
        if (currentPage < totalPages) {
            currentPage = totalPages;
            try {
                VoucherService vou = new VoucherService();
                txt_phan_trangVoucher.setText(totalPages + "/" + totalPages);
                vou.loadTableVoucher(tblCoupon, totalPages, itemsPerPagee);;
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_btnTienCuoiVoucher1ActionPerformed

    private void btnLuiCuoiVoucher1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuiCuoiVoucher1ActionPerformed
        // TODO add your handling code here:
        if (currentPage > 1) {
            currentPage = 1;
            //txt_phan_trangVoucher.setText(String.valueOf(currentPage));
            txt_phan_trangVoucher.setText(currentPage + "/" + totalPages);
        }
        try {
            VoucherService vou = new VoucherService();
            vou.loadTableVoucher(tblCoupon, 1, itemsPerPagee);;
        } catch (Exception e) {
        }
    }//GEN-LAST:event_btnLuiCuoiVoucher1ActionPerformed

    private void tblDiscountMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDiscountMouseClicked
        //tạo và copy đây-----------------------------
        updateDis = 1;
        try {
            DiscountService dis = new DiscountService();
            dis.showDiscount(txtMa_chiet_khau, tblDiscount, jdc_ngay_bat_dau, jdc_ngay_ket_thuc, txt_so_tien_giam, txt_code_chi_tiet, txtMau, txtKieuDang, txtKichThuoc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tbl_san_pham_khuyen_mai.clearSelection();
        txt_tim_sp.setText("");
        txtMau.setEnabled(false);
        txtKichThuoc.setEnabled(false);
        txtKieuDang.setEnabled(false);
        //---------------------------------------------------------
    }//GEN-LAST:event_tblDiscountMouseClicked

    private void btnAddDisMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddDisMouseClicked
        //tạo và copy
        //--------------------------------------------------
        //nút thêm giảm giá trực tiếp
        //        tbl_san_pham_khuyen_mai.clearSelection();
        addDis = 1;
        updateDis = 0;
        tblDiscount.clearSelection();
        txtMa_chiet_khau.setText("");
        jdc_ngay_bat_dau.setDate(null);
        jdc_ngay_ket_thuc.setDate(null);
        txt_so_tien_giam.setText("");
        txtMau.setEnabled(false);
        txtKichThuoc.setEnabled(false);
        txtKieuDang.setEnabled(false);
        txtMa_chiet_khau.setBackground(Color.WHITE);
        jdc_ngay_bat_dau.setBackground(Color.WHITE);
        jdc_ngay_ket_thuc.setBackground(Color.WHITE);
        txt_so_tien_giam.setBackground(Color.WHITE);
        try {
            DiscountService dis = new DiscountService();
            dis.loadTableDisscount(tblDiscount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        jdc_ngay_bat_dau.setEnabled(true);
        jdc_ngay_ket_thuc.setEnabled(true);
        txt_so_tien_giam.setEnabled(true);
        txtMa_chiet_khau.setEnabled(true);

        //-----------------------------------------
    }//GEN-LAST:event_btnAddDisMouseClicked

    private void btnEditDisMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditDisMouseClicked
        //        // thêm và copy-------------------------------------------------
        //--------------nút cập nhật bảng giảm giá trự tiếp

        updateDis = 1;
        addDis = 0;
        int row = tblDiscount.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Hãy chọn 1 chương trình để cập nhật");
            updateDis = 0;
            return;

        }
        tbl_san_pham_khuyen_mai.clearSelection();
        txtMa_chiet_khau.setEnabled(false);
        jdc_ngay_bat_dau.setEnabled(true);
        jdc_ngay_ket_thuc.setEnabled(true);
        txt_so_tien_giam.setEnabled(true);
        //----------------------------------------------------------------------------
    }//GEN-LAST:event_btnEditDisMouseClicked

    private void btnEditDisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditDisActionPerformed

    }//GEN-LAST:event_btnEditDisActionPerformed

    private void txtMa_chiet_khauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMa_chiet_khauActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMa_chiet_khauActionPerformed

    private void btnLamMoi2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLamMoi2MouseClicked
        //tạo và copy đây---------------------------------------------------------
        //nút lưu giảm giá trực tiếp

        int i = tblDiscount.getSelectedRow();

        //nếu bảng không click => thêm
        if (i == -1) {
            if (addDis == 0) {
                return;
            }
            try {
                DiscountService dis = new DiscountService();
                ProductDetails_Service pro = new ProductDetails_Service();
                if (dis.readFromDis(txtMa_chiet_khau, jdc_ngay_bat_dau, jdc_ngay_ket_thuc, txt_so_tien_giam, txt_code_chi_tiet) == null) {
                    return;
                }

                String maChietKhau = txtMa_chiet_khau.getText().trim();
                if (!dis.tim(maChietKhau).isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Mã chiết khấu đã tồn tại không thể thêm");
                    txtMa_chiet_khau.requestFocus();
                    txtMa_chiet_khau.setBackground(Color.YELLOW);
                    return;
                } else {
                    String code = txt_code_chi_tiet.getText().trim();
                    dis.them(dis.readFromDis(txtMa_chiet_khau, jdc_ngay_bat_dau, jdc_ngay_ket_thuc, txt_so_tien_giam, txt_code_chi_tiet), code, txtMa_chiet_khau, jdc_ngay_bat_dau, jdc_ngay_ket_thuc, txt_so_tien_giam, tblDiscount, tbl_san_pham_khuyen_mai);
                    //                    dis.loadTableDisscount(tblDiscount);
                    //                    pro.loadTableProductDetails(tbl_san_pham_khuyen_mai);

                }

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        } else {
            if (updateDis == 0) {
                return;
            }
            try {
                DiscountService dis = new DiscountService();
                OriginalPriceService op = new OriginalPriceService();

                String maChietKhau = tblDiscount.getValueAt(i, 0).toString();
                if (dis.checkLuu(tblDiscount, jdc_ngay_bat_dau, jdc_ngay_ket_thuc, txt_so_tien_giam) == 1) {
                    return;
                }
                if (dis.readFromDis(txtMa_chiet_khau, jdc_ngay_bat_dau, jdc_ngay_ket_thuc, txt_so_tien_giam, txt_code_chi_tiet) == null) {
                    return;
                }
                //
                DiscountService ds = new DiscountService();
                ProductDetails_Service pro = new ProductDetails_Service();
                OriginalPriceService ops = new OriginalPriceService();
                double index = Double.parseDouble(txt_so_tien_giam.getText());
                String code = txt_code_chi_tiet.getText().trim();
                dis.capNhat(dis.readFromDis(txtMa_chiet_khau, jdc_ngay_bat_dau, jdc_ngay_ket_thuc, txt_so_tien_giam, txt_code_chi_tiet), index, code, txtMa_chiet_khau, jdc_ngay_bat_dau, jdc_ngay_ket_thuc, txt_so_tien_giam, tblDiscount, tbl_san_pham_khuyen_mai);
                //                dis.loadTableDisscount(tblDiscount);
                //                pro.loadTableProductDetails(tbl_san_pham_khuyen_mai);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        //--------------------------------------------------------------
    }//GEN-LAST:event_btnLamMoi2MouseClicked

    private void tbl_san_pham_khuyen_maiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_san_pham_khuyen_maiMouseClicked
        // TODO add your handling code here:
        //------------------------MouseClicked của bảng tbl_san_pham_khuyen_mai
        tblDiscount.clearSelection();

        DiscountService dis;
        try {
            dis = new DiscountService();
            dis.showBang_chi_tiet_Sp(tbl_san_pham_khuyen_mai, txt_code_chi_tiet, txtMau, txtKieuDang, txtKichThuoc);
            //            System.out.println("Hết ngáo");
        } catch (SQLException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("lỗi");
        }
        txt_so_tien_giam.setText("");
        txtMa_chiet_khau.setText("");
        jdc_ngay_bat_dau.setDate(null);
        jdc_ngay_ket_thuc.setDate(null);
        txtMau.setEnabled(false);
        txtKichThuoc.setEnabled(false);
        txtKieuDang.setEnabled(false);
        //---------------------------------------------------------------------------------
    }//GEN-LAST:event_tbl_san_pham_khuyen_maiMouseClicked

    private void txt_tim_spCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txt_tim_spCaretUpdate
        // TODO add your handling code here:
        //------tạo event CaretUpdate cho txt_tim_sp
        try {
            ProductDetails_Service pro = new ProductDetails_Service();
            String index = txt_tim_sp.getText().trim();
            pro.loadTableProductDetailsTim(tbl_san_pham_khuyen_mai, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //--------------------------------------------
    }//GEN-LAST:event_txt_tim_spCaretUpdate

    private void txt_tim_spKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tim_spKeyTyped
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_tim_spKeyTyped

    private void txt_tim_chuong_trìnhCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txt_tim_chuong_trìnhCaretUpdate
        // TODO add your handling code here:
        //--tạo CaretUpdate tim_chuong_trình----------------------
        try {
            DiscountService dis = new DiscountService();
            String index = txt_tim_chuong_trình.getText().trim();
            dis.loadTableTim(tblDiscount, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //-----------------------------------------------------
    }//GEN-LAST:event_txt_tim_chuong_trìnhCaretUpdate

    private void TabGiamGiaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabGiamGiaMouseClicked
        // TODO add your handling code here:
        //---------------------------------------tạoTabGiamGiaMouseClicked----------------------
        if (selectedTabIndex == 0) {
            jdc_ngay_bat_dau.setEnabled(false);
            jdc_ngay_ket_thuc.setEnabled(false);
            txtMa_chiet_khau.setEnabled(false);
            txt_so_tien_giam.setEnabled(false);
            jdc_ngay_bat_dau.setDate(null);
            jdc_ngay_ket_thuc.setDate(null);
            txtMa_chiet_khau.setText("");
            txt_so_tien_giam.setText("");
            txt_tim_sp.setText("");
            txt_tim_chuong_trình.setText("");
            txtMau.setText("");
            txtKichThuoc.setText("");
            txtKieuDang.setText("");
            txt_code_chi_tiet.setText("");
            tblDiscount.clearSelection();
            try {
                ProductDetails_Service pro = new ProductDetails_Service();
                DiscountService dis = new DiscountService();
                pro.loadTableProductDetails(tbl_san_pham_khuyen_mai);
                dis.loadTableDisscount(tblDiscount);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(selectedTabIndex);
            return;

        } else if (selectedTabIndex == 1) {
            txtPhanTramChietKhau.setText("");
            tblCoupon.clearSelection();

            rdoDangKM.setEnabled(false);
            rdoDungKM.setEnabled(false);
            jdc_ngay_bat_dau_voucher.setDate(null);
            jdc_ngay_ket_thuc_voucher.setDate(null);
            txt_tien_toi_da.setText("");
            txt_tien_ap_dung.setText("");
            txt_so_luong_vou.setText("");
            txtMaGiamGia.setText("");
            txtCode.setText("");
            txtMoTa.setText("");

            jdc_ngay_bat_dau_voucher.setEnabled(false);
            jdc_ngay_ket_thuc_voucher.setEnabled(false);
            txt_tien_toi_da.setEnabled(false);
            txt_tien_ap_dung.setEnabled(false);
            txt_so_luong_vou.setEnabled(false);
            txtMaGiamGia.setEnabled(false);
            txtCode.setEnabled(false);
            txtMoTa.setEnabled(false);
            txt_code_chi_tiet.setEnabled(false);
            return;
        }

    }//GEN-LAST:event_TabGiamGiaMouseClicked

    private void btnTienTrangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTienTrangActionPerformed
        us1sv.nextPage();
        try {
            tblNhanVien.setModel(us1sv.loadTableNV());
        } catch (SQLDataException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblSoTrang1.setText(us1sv.getCurrentOrderPage() + " / " + us1sv.getTotalOrderPages());
    }//GEN-LAST:event_btnTienTrangActionPerformed

    private void btnLuiLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuiLaiActionPerformed
        us1sv.previousPage();
        try {
            tblNhanVien.setModel(us1sv.loadTableNV());
        } catch (SQLDataException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblSoTrang1.setText(us1sv.getCurrentOrderPage() + " / " + us1sv.getTotalOrderPages());
    }//GEN-LAST:event_btnLuiLaiActionPerformed

    private void txtTimKiemSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimKiemSPActionPerformed
        //        String tenSanPham = txtTimKiemSP.getText();
        //
        //        if (tenSanPham == null || tenSanPham.trim().isEmpty()) {
        //            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống");
        //            return;
        //        }
        //
        //        try {
        //            thongKeService.TimKiemQuaTenSP(tenSanPham, tblDanhSachSPdaBan);
        //        } catch (SQLException ex) {
        //            ex.printStackTrace();
        //        }
    }//GEN-LAST:event_txtTimKiemSPActionPerformed

    private void jTuNgayPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTuNgayPropertyChange
        //        if ("date".equals(evt.getPropertyName())) {
        //            thongKeService.filterProductsByDate(jTuNgay, jDenNgay, tblDanhSachSPdaBan);
        //        }
    }//GEN-LAST:event_jTuNgayPropertyChange

    private void jDenNgayPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDenNgayPropertyChange
        //        thongKeService.filterProductsByDate(jTuNgay, jDenNgay, tblDanhSachSPdaBan);
    }//GEN-LAST:event_jDenNgayPropertyChange

    private void btnLocKhoangSanPhamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocKhoangSanPhamActionPerformed
        thongKeService.UpdateProductsTheoDate(jTuNgay, jDenNgay, tblDanhSachSPdaBan);

    }//GEN-LAST:event_btnLocKhoangSanPhamActionPerformed

    private void tblDanhSachSPdaBanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachSPdaBanMouseClicked
        int row = this.tblDanhSachSPdaBan.getSelectedRow();
        if (row != -1) { // Kiểm tra nếu có hàng được chọn
            String productName = tblDanhSachSPdaBan.getValueAt(row, 2).toString(); // Lấy tên sản phẩm từ cột thứ 2 (index 1)

            // Cập nhật JTextField với tên sản phẩm
            //            this.txtTimKiemSP.setText(productName);
            try {
                int totalQuantity = thongKeService.getTongSLSP(productName);
                lblTongSP1.setText(totalQuantity + " Sản phẩm");
                int soldQuantity = thongKeService.getTongSPDaBan(productName);
                lblSPDaBan1.setText(soldQuantity + " Sản phẩm");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_tblDanhSachSPdaBanMouseClicked

    private void jDTTThangPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDTTThangPropertyChange
        thongKeService.LocDSTheoThang(jDTTNam, jDTTThang, lblDoanhThuThang);
    }//GEN-LAST:event_jDTTThangPropertyChange

    private void jDTTNamPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDTTNamPropertyChange
        thongKeService.LocDSTheoThang(jDTTNam, jDTTThang, lblDoanhThuThang);
    }//GEN-LAST:event_jDTTNamPropertyChange

    private void jDateDoanhThuNgayPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateDoanhThuNgayPropertyChange
        thongKeService.LocDoanhSoTheoNgay(jDateDoanhThuNgay, lblDoanhThuNgay);
    }//GEN-LAST:event_jDateDoanhThuNgayPropertyChange

    private void TuLocTheoKhoangUpdatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_TuLocTheoKhoangUpdatePropertyChange
        //        thongKeService.updateTongDoanhSoTheoNgayThangNam(TuLocTheoKhoangUpdate, DenLocTheoKhoangUpdate, lblDoanhThuTong);
    }//GEN-LAST:event_TuLocTheoKhoangUpdatePropertyChange

    private void DenLocTheoKhoangUpdatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DenLocTheoKhoangUpdatePropertyChange
        //        thongKeService.updateTongDoanhSoTheoNgayThangNam(TuLocTheoKhoangUpdate, DenLocTheoKhoangUpdate, lblDoanhThuTong);
    }//GEN-LAST:event_DenLocTheoKhoangUpdatePropertyChange

    private void btnLocDoanhSoUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocDoanhSoUpdateActionPerformed
        thongKeService.updateTongDoanhSoTheoNgayThangNam(TuLocTheoKhoangUpdate, DenLocTheoKhoangUpdate, lblDoanhThuTong);
    }//GEN-LAST:event_btnLocDoanhSoUpdateActionPerformed

    private void cbbTOPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbTOPActionPerformed
        if (cbbTOP.getSelectedIndex() == 1) {
            try {
                thongKeService.LoadTableTop10SPBanItNhat(tblDanhSachSPdaBan);

            } catch (SQLException ex) {
                Logger.getLogger(view_TrangChu.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                thongKeService.LoadTableTop10BanChayNhat(tblDanhSachSPdaBan);

            } catch (SQLException ex) {
                Logger.getLogger(view_TrangChu.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_cbbTOPActionPerformed

    private void btnLamMoiThongKeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiThongKeActionPerformed
        lblTongSP.setText("quantity: " + 0);
        lblSPDaBan.setText("Quantity sold:" + 0);
        txtTimKiemSP.setText("");
        thongKeService.loadTongSoLuong(lblTongSP);
        thongKeService.loadTongSoLuongDaBan(lblSPDaBan);
        try {
            thongKeService.LoadTableThongKe(tblDanhSachSPdaBan);

        } catch (SQLException ex) {
            Logger.getLogger(view_TrangChu.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLamMoiThongKeActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Date current = new Date();
        jTuNgay.setDate(current);
        jDenNgay.setDate(current);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Date current = new Date();
        TuLocTheoKhoangUpdate.setDate(current);
        DenLocTheoKhoangUpdate.setDate(current);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnVeDauuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVeDauuActionPerformed
        us1sv.setCurrentOrderPage(1); // Đặt trang hiện tại về 1
        try {
            tblNhanVien.setModel(us1sv.loadTableNV()); // Tải dữ liệu cho trang đầu
        } catch (SQLDataException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblSoTrang1.setText(us1sv.getCurrentOrderPage() + " / " + us1sv.getTotalOrderPages()); // Cập nhật lblSoTrang      
    }//GEN-LAST:event_btnVeDauuActionPerformed

    private void btnVeCuoiiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVeCuoiiActionPerformed
        us1sv.setCurrentOrderPage(us1sv.getTotalOrderPages());
        try {
            tblNhanVien.setModel(us1sv.loadTableNV()); // Tải dữ liệu cho trang đầu
        } catch (SQLDataException ex) {
            Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblSoTrang1.setText(us1sv.getCurrentOrderPage() + " / " + us1sv.getTotalOrderPages()); // Cập nhật lblSoTrang   
    }//GEN-LAST:event_btnVeCuoiiActionPerformed

    private void btnNhapTuExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNhapTuExcelActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                ArrayList<Products> productList = (ArrayList) importer.importProductsFromExcel(selectedFile.getAbsolutePath());

                if (productList.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tệp Excel trống hoặc không có dữ liệu hợp lệ.");
                    return;
                }

                int successfulImports = 0;

                for (Products product : productList) {
                    String validationError = this.qlsp.validateProductInput(
                            product.getProduct_code(),
                            product.getProduct_name(),
                            product.getDescription(),
                            product.getCategory().getCategory_name(),
                            product.getSupplier().getSupplier_name(),
                            product.getStatus(),
                            true // isAdd
                    );

                    if (validationError != null) {
//                        JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + validationError);
                        continue; // Bỏ qua sản phẩm này nếu có lỗi
                    }

                    this.qlsp.addNewProduct(product);
                    successfulImports++;
                }

                if (successfulImports > 0) {
                    this.qlsp.loadTable(tblHienThiKetQua, this.qlsp.getAllProduct(), currentPages, rowsPerPage);
                    updatePaginations(qlsp.getAllProduct());
                    this.setEnableFormSP();
                    JOptionPane.showMessageDialog(this, "Đã nhập thành công " + successfulImports + " sản phẩm từ tệp Excel.");
                } else {
                    JOptionPane.showMessageDialog(this, "Không có sản phẩm nào được nhập thành công.");
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Định dạng tệp không hợp lệ hoặc tệp bị hỏng.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi nhập dữ liệu: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnNhapTuExcelActionPerformed

    private void btnNhapSPCTExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNhapSPCTExcelActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Nhập danh sách chi tiết sản phẩm từ tệp Excel
                ArrayList<ProductDetails> productDetailList = (ArrayList) importer.importProductDetailFromExcel(selectedFile.getAbsolutePath());

                if (productDetailList.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tệp Excel trống hoặc không có dữ liệu hợp lệ.");
                    return;
                }

                int successfulImports = 0;
                int failedImports = 0;
                int rowNumber = 0; // Để theo dõi số dòng

                for (ProductDetails productDetail : productDetailList) {
                    rowNumber++; // Tăng số dòng lên mỗi khi lặp qua một sản phẩm

                    // Xác thực dữ liệu chi tiết sản phẩm
                    String validationError = this.qlsp.validateProductDetailInput(
                            productDetail.getProduct_code(),
                            productDetail.getColor(),
                            productDetail.getStyle(),
                            productDetail.getSize(),
                            String.valueOf(productDetail.getPrice()),
                            String.valueOf(productDetail.getQuantity()),
                            productDetail.getImage(),
                            true // isAdd
                    );

                    if (validationError != null) {
                        JOptionPane.showMessageDialog(this, "Lỗi dữ liệu tại dòng " + rowNumber + ": " + validationError);
                        failedImports++;
                        continue; // Bỏ qua chi tiết sản phẩm này nếu có lỗi
                    }

                    // Thêm chi tiết sản phẩm vào cơ sở dữ liệu
                    Integer productId = this.qlsp.getProductIdByCode(productDetail.getProduct_code());
                    String proDeStyle = this.qlsp.getStyleIdByName(productDetail.getStyle());
                    String proDeSize = this.qlsp.getSizeIdByName(productDetail.getSize());
                    String proDeColor = this.qlsp.getColorIdByName(productDetail.getColor());
                    Double proDePrice = productDetail.getPrice();
                    Integer proDeQuantity = productDetail.getQuantity();
                    String imageUrl = productDetail.getImage();

                    if (productId == null) {
                        JOptionPane.showMessageDialog(this, "Mã sản phẩm không hợp lệ tại dòng " + rowNumber + ": " + productDetail.getProduct_code());
                        failedImports++;
                        continue;
                    }
                    if (proDeStyle == null) {
                        JOptionPane.showMessageDialog(this, "Kiểu dáng không hợp lệ tại dòng " + rowNumber + ": " + productDetail.getStyle());
                        failedImports++;
                        continue;
                    }
                    if (proDeSize == null) {
                        JOptionPane.showMessageDialog(this, "Kích thước không hợp lệ tại dòng " + rowNumber + ": " + productDetail.getSize());
                        failedImports++;
                        continue;
                    }
                    if (proDeColor == null) {
                        JOptionPane.showMessageDialog(this, "Màu sắc không hợp lệ tại dòng " + rowNumber + ": " + productDetail.getColor());
                        failedImports++;
                        continue;
                    }

                    ProductDetails existingDetail = this.qlsp.getProductDetailByAttributes(productId, proDeStyle, proDeSize, proDeColor);
                    if (existingDetail != null) {
                        JOptionPane.showMessageDialog(this, "Chi tiết sản phẩm đã tồn tại tại dòng " + rowNumber);
                        failedImports++;
                        continue;
                    }

                    ProductDetails proDe = new ProductDetails(productId, proDeStyle, proDeSize, proDeColor, proDePrice, proDeQuantity, imageUrl);
                    this.qlsp.addProductDetail(proDe);

                    successfulImports++;
                }

                // Cập nhật bảng và giao diện
                if (successfulImports > 0) {
                    this.qlsp.loadTableDetail(tblSPCT, this.qlsp.getAllProductDetail(), currentPage, rowsPerPage);
                    updatePagination(qlsp.getAllProductDetail());
                    this.setEnableFormSPCT();
                    JOptionPane.showMessageDialog(this, "Đã nhập thành công " + successfulImports + " chi tiết sản phẩm từ tệp Excel.");
                } else {
                    JOptionPane.showMessageDialog(this, "Không có chi tiết sản phẩm nào được nhập thành công.");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Định dạng tệp không hợp lệ hoặc tệp bị hỏng.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi nhập dữ liệu: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnNhapSPCTExcelActionPerformed

    private void txtTimKiemSPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTimKiemSPKeyReleased
        String tenSanPham = txtTimKiemSP.getText();

        try {
            thongKeService.TimKiemQuaTenSP(tenSanPham, tblDanhSachSPdaBan);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_txtTimKiemSPKeyReleased

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
            java.util.logging.Logger.getLogger(view_TrangChu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(view_TrangChu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(view_TrangChu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(view_TrangChu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new view_TrangChu(2).setVisible(true);
                } catch (SQLDataException ex) {
                    Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(view_TrangChu.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BangHoaDon;
    private javax.swing.JPanel CRUD_NhanVien;
    private javax.swing.JPanel Coupon;
    private javax.swing.JPanel DanhSachKhachHang1;
    private javax.swing.JPanel DanhSachNhanVien;
    private com.toedter.calendar.JDateChooser DenLocTheoKhoangUpdate;
    private javax.swing.JPanel Discount;
    private javax.swing.JPanel MenuHeThong;
    private javax.swing.JPanel SanPhamTrongHoaDon;
    private javax.swing.JTabbedPane TabGiamGia;
    private javax.swing.JPanel ThongKeSP;
    private javax.swing.JPanel ThongTinNhanVien;
    private com.toedter.calendar.JDateChooser TuLocTheoKhoangUpdate;
    private javax.swing.JPanel ViewHeThong;
    private javax.swing.JLabel anhQRcode;
    private javax.swing.JButton btnAddChiTietSP;
    private javax.swing.JButton btnAddCou;
    private javax.swing.JButton btnAddDis;
    private javax.swing.JButton btnAddProduct;
    private javax.swing.JButton btnAddProductDetails;
    private javax.swing.JButton btnBackProducts;
    private javax.swing.JButton btnBackkkkkk;
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnCapNhatGioHang;
    private javax.swing.JButton btnCapNhatKT;
    private javax.swing.JButton btnCapNhatMS;
    private javax.swing.JButton btnCapNhatNCC;
    private javax.swing.JButton btnCapNhatNhanVien;
    private javax.swing.JButton btnCapNhatPC;
    private javax.swing.JButton btnCapNhatThongTinCN;
    private javax.swing.JButton btnDangXuat;
    private javax.swing.JButton btnDoiMatKhau;
    private javax.swing.JButton btnDowloadQr;
    private javax.swing.JButton btnEditCou;
    private javax.swing.JButton btnEditDis;
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnExcelProductDetail;
    private javax.swing.JButton btnEyeMK;
    private javax.swing.JButton btnEyeMKMoi;
    private javax.swing.JButton btnEyeXacNhan;
    private javax.swing.JButton btnHoanTac1;
    private javax.swing.JButton btnKhachLe;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnLamMoi2;
    private javax.swing.JButton btnLamMoiKH1;
    private javax.swing.JButton btnLamMoiTTSP;
    private javax.swing.JButton btnLamMoiThongKe;
    private javax.swing.JButton btnLocDoanhSoUpdate;
    private javax.swing.JButton btnLocKhoangSanPham;
    private javax.swing.JButton btnLocTheoKhoang;
    private javax.swing.JButton btnLui;
    private javax.swing.JButton btnLuiCuoiVoucher1;
    private javax.swing.JButton btnLuiLai;
    private javax.swing.JButton btnLuiVoucher;
    private javax.swing.JButton btnLuu;
    private javax.swing.JButton btnLuu2;
    private javax.swing.JButton btnLuu3;
    private javax.swing.JButton btnLuuColor;
    private javax.swing.JButton btnLuuSPCT;
    private javax.swing.JButton btnLuuSize;
    private javax.swing.JButton btnLuuStyle;
    private javax.swing.JButton btnLuuSupp;
    private javax.swing.JButton btnLuuThayDoi;
    private javax.swing.JButton btnLuuTruHD;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnNextAll;
    private javax.swing.JButton btnNextt;
    private javax.swing.JButton btnNexttALL;
    private javax.swing.JButton btnNhapSPCTExcel;
    private javax.swing.JButton btnNhapTuExcel;
    private javax.swing.JButton btnPreviou;
    private javax.swing.JButton btnPreviouAll;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnPreviousAll;
    private javax.swing.JButton btnQRcode;
    private javax.swing.JButton btnQuaylai;
    private javax.swing.JButton btnQuetMaQRCodee;
    private javax.swing.JButton btnResetPoint;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSuaDM;
    private javax.swing.JButton btnSuaKhachHang1;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnThemKHtuHD;
    private javax.swing.JButton btnThemMoi;
    private javax.swing.JButton btnThemMoiDM;
    private javax.swing.JButton btnThemMoiKH1;
    private javax.swing.JButton btnThemMoiKT;
    private javax.swing.JButton btnThemMoiThuocTinh;
    private javax.swing.JButton btnThemNCC;
    private javax.swing.JButton btnThemNhanVien;
    private javax.swing.JButton btnThemPC;
    private javax.swing.JButton btnThemSPVaoHD;
    private javax.swing.JButton btnTien;
    private javax.swing.JButton btnTienCuoiVoucher1;
    private javax.swing.JButton btnTienTrang;
    private javax.swing.JButton btnTienVoucher;
    private javax.swing.JButton btnTiepTucThanhToan;
    private javax.swing.JButton btnUpdateProduct;
    private javax.swing.JButton btnVeCuoii;
    private javax.swing.JButton btnVeDauu;
    private javax.swing.JButton btnVeTrangCuoi;
    private javax.swing.JButton btnVeTrangDau;
    private javax.swing.JButton btnVoHH;
    private javax.swing.JButton btnXacNhanDMK;
    private javax.swing.JButton btnXemDSSPCT;
    private javax.swing.JButton btnXoaHoaDonCho;
    private javax.swing.JButton btnXoaKhoiGioHang;
    private javax.swing.JButton btnXuatExcelHoaDon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.JPanel camera;
    private javax.swing.JPanel capNhatTichDiem;
    private javax.swing.JComboBox<String> cbbColor;
    private javax.swing.JComboBox<String> cbbDanhMuc;
    private javax.swing.JComboBox<String> cbbGiaBan;
    private javax.swing.JComboBox<String> cbbKichThuocSPCT;
    private javax.swing.JComboBox<String> cbbLoaiSPCT;
    private javax.swing.JComboBox<String> cbbLocNhaCC;
    private javax.swing.JComboBox<String> cbbLocTheoDM;
    private javax.swing.JComboBox<String> cbbLocTrangThaiHoaDon;
    private javax.swing.JComboBox<String> cbbMauSacSPCT;
    private javax.swing.JComboBox<String> cbbNhaCC;
    private javax.swing.JComboBox<String> cbbSizes;
    private javax.swing.JComboBox<String> cbbSoLuong;
    private javax.swing.JComboBox<String> cbbStyle;
    private javax.swing.JComboBox<String> cbbStyles;
    private javax.swing.JComboBox<String> cbbTOP;
    private javax.swing.JComboBox<String> cbbTrangThaiSP;
    private javax.swing.JComboBox<String> cbbVaiTro;
    private javax.swing.JComboBox<String> cbbVouchers;
    private com.toedter.calendar.JDateChooser dateDen;
    private com.toedter.calendar.JDateChooser dateNgayTao;
    private com.toedter.calendar.JDateChooser dateTu;
    private javax.swing.JPanel formDoiMK;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private com.toedter.calendar.JYearChooser jDTTNam;
    private com.toedter.calendar.JMonthChooser jDTTThang;
    private com.toedter.calendar.JDateChooser jDateDoanhThuNgay;
    private com.toedter.calendar.JDateChooser jDenNgay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel147;
    private javax.swing.JLabel jLabel148;
    private javax.swing.JLabel jLabel149;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel150;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel152;
    private javax.swing.JLabel jLabel153;
    private javax.swing.JLabel jLabel154;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel161;
    private javax.swing.JLabel jLabel162;
    private javax.swing.JLabel jLabel163;
    private javax.swing.JLabel jLabel164;
    private javax.swing.JLabel jLabel165;
    private javax.swing.JLabel jLabel166;
    private javax.swing.JLabel jLabel167;
    private javax.swing.JLabel jLabel168;
    private javax.swing.JLabel jLabel169;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel170;
    private javax.swing.JLabel jLabel171;
    private javax.swing.JLabel jLabel172;
    private javax.swing.JLabel jLabel173;
    private javax.swing.JLabel jLabel174;
    private javax.swing.JLabel jLabel175;
    private javax.swing.JLabel jLabel176;
    private javax.swing.JLabel jLabel177;
    private javax.swing.JLabel jLabel178;
    private javax.swing.JLabel jLabel179;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel180;
    private javax.swing.JLabel jLabel181;
    private javax.swing.JLabel jLabel182;
    private javax.swing.JLabel jLabel183;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private com.toedter.calendar.JDateChooser jTuNgay;
    private com.toedter.calendar.JDateChooser jdc_ngay_bat_dau;
    private com.toedter.calendar.JDateChooser jdc_ngay_bat_dau_voucher;
    private com.toedter.calendar.JDateChooser jdc_ngay_ket_thuc;
    private com.toedter.calendar.JDateChooser jdc_ngay_ket_thuc_voucher;
    private javax.swing.JPanel jpContainer;
    private javax.swing.JPanel jpQLBH;
    private javax.swing.JPanel jpQLDM;
    private javax.swing.JPanel jpQLHD;
    private javax.swing.JPanel jpQLKH;
    private javax.swing.JPanel jpQLKM;
    private javax.swing.JPanel jpQLNV;
    private javax.swing.JPanel jpQLSP;
    private javax.swing.JPanel jpQLTK;
    private javax.swing.JPanel jpTK;
    private javax.swing.JPanel jpThongTinCN;
    private javax.swing.JPanel jpViewDoiMK;
    private javax.swing.JPanel jpViewQLBH2;
    private javax.swing.JPanel jpViewQLDM;
    private javax.swing.JPanel jpViewQLHD;
    private javax.swing.JPanel jpViewQLKH;
    private javax.swing.JPanel jpViewQLKM;
    private javax.swing.JPanel jpViewQLSPP;
    private javax.swing.JPanel jpViewQLSPP2;
    private javax.swing.JPanel jpViewQLSPP3;
    private javax.swing.JPanel jpViewQLTK;
    private javax.swing.JPanel jpViewTK;
    private javax.swing.JPanel jp_V_HoaDon;
    private javax.swing.JPanel jp_V_ThemMoiHD;
    private javax.swing.JLabel lblDiemConLai;
    private javax.swing.JLabel lblDoanhThuNgay;
    private javax.swing.JLabel lblDoanhThuThang;
    private javax.swing.JLabel lblDoanhThuTong;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMatKhau;
    private javax.swing.JLabel lblNhanVienBan;
    private javax.swing.JLabel lblPage;
    private javax.swing.JLabel lblPages;
    private javax.swing.JLabel lblSPDaBan;
    private javax.swing.JLabel lblSPDaBan1;
    private javax.swing.JLabel lblSoTrang;
    private javax.swing.JLabel lblSoTrang1;
    private javax.swing.JLabel lblTienTruocKhiGiam;
    private javax.swing.JLabel lblTongSP;
    private javax.swing.JLabel lblTongSP1;
    private javax.swing.JLabel lblTongTienThanhToan;
    private javax.swing.JLabel lblVAT;
    private javax.swing.JLabel pnWebCam;
    private javax.swing.JRadioButton rdoChuyenKhoan;
    private javax.swing.JRadioButton rdoDangKM;
    private javax.swing.JRadioButton rdoDanglam;
    private javax.swing.JRadioButton rdoDungKM;
    private javax.swing.JRadioButton rdoNghilam;
    private javax.swing.JRadioButton rdoRun;
    private javax.swing.JRadioButton rdoStop;
    private javax.swing.JRadioButton rdoTienMat;
    private javax.swing.JPanel spDaBan;
    private javax.swing.JPanel spDaBan1;
    private javax.swing.JTabbedPane tabQLNV;
    private javax.swing.JTable tblCoupon;
    private javax.swing.JTable tblDSHD;
    private javax.swing.JTable tblDSSPtrongHD;
    private javax.swing.JTable tblDSSanPham;
    private javax.swing.JTable tblDanhSachDM;
    private javax.swing.JTable tblDanhSachSPdaBan;
    private javax.swing.JTable tblDiscount;
    private javax.swing.JTable tblHienThiKetQua;
    private javax.swing.JTable tblHoaDonCho;
    private javax.swing.JTable tblKhachH;
    private javax.swing.JTable tblKichThuoc;
    private javax.swing.JTable tblMauSac;
    private javax.swing.JTable tblNhaCungCap;
    private javax.swing.JTable tblNhanVien;
    private javax.swing.JTable tblPhongCach;
    private javax.swing.JTable tblSPCT;
    private javax.swing.JTable tblSanPhamTrongHoaDon;
    private javax.swing.JTable tbl_san_pham_khuyen_mai;
    private javax.swing.JTabbedPane tbpThuocTinh;
    private javax.swing.JPanel tongSP;
    private javax.swing.JPanel tongSP1;
    private javax.swing.JTextField txtCode;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtDiaChi1;
    private javax.swing.JTextField txtDiaChiNCC;
    private javax.swing.JTextField txtDiemDaDung;
    private javax.swing.JTextField txtDiemTichLuy;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEmail1;
    private javax.swing.JTextField txtEmailNCC;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtHoTen1;
    private javax.swing.JTextField txtHoTen2;
    private javax.swing.JTextField txtITenKhachHang1;
    private javax.swing.JTextField txtKichThuoc;
    private javax.swing.JTextField txtKichThuoccc;
    private javax.swing.JTextField txtKieuDang;
    private javax.swing.JTextField txtLoaiSPpp;
    private javax.swing.JTextField txtLocHoaDonTheoMaHD;
    private javax.swing.JTextField txtMaCTSP;
    private javax.swing.JTextField txtMaDM;
    private javax.swing.JTextField txtMaGiamGia;
    private javax.swing.JTextField txtMaKH;
    private javax.swing.JTextField txtMaKT;
    private javax.swing.JTextField txtMaMau;
    private javax.swing.JTextField txtMaNhanVienn;
    private javax.swing.JTextField txtMaPC;
    private javax.swing.JTextField txtMaSP;
    private javax.swing.JTextField txtMaSPCT;
    private javax.swing.JTextField txtMaSPCT1;
    private javax.swing.JTextField txtMaSanPham;
    private javax.swing.JTextField txtMa_chiet_khau;
    private javax.swing.JPasswordField txtMatKhau;
    private javax.swing.JTextField txtMau;
    private javax.swing.JTextField txtMauSaccc;
    private javax.swing.JTextArea txtMoTa;
    private javax.swing.JTextArea txtMoTaPC;
    private javax.swing.JTextArea txtMoTaSize;
    private javax.swing.JTextField txtMoney;
    private javax.swing.JTextField txtMoney1;
    private javax.swing.JTextArea txtMotaSPP;
    private javax.swing.JTextField txtNCC;
    private javax.swing.JPasswordField txtNewPassword;
    private javax.swing.JTextField txtNhapSoLuong;
    private javax.swing.JTextField txtNhapSoLuong1;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPhanTramChietKhau;
    private javax.swing.JTextField txtPhongCachhh;
    private javax.swing.JTextField txtPoint;
    private javax.swing.JTextField txtPoint1;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSDTKhachHang;
    private javax.swing.JTextField txtSDTNCC;
    private javax.swing.JTextField txtSdt;
    private javax.swing.JTextField txtSearchKH;
    private javax.swing.JTextField txtSearhProductDetail;
    private javax.swing.JTextField txtSoDiemDaDung;
    private javax.swing.JTextField txtSoDiienThoai;
    private javax.swing.JTextField txtTenDM;
    private javax.swing.JTextField txtTenDangNhap;
    private javax.swing.JTextField txtTenKhachHang;
    private javax.swing.JTextField txtTenKichThuoc;
    private javax.swing.JTextField txtTenMau;
    private javax.swing.JTextField txtTenNCC;
    private javax.swing.JTextField txtTenPhongCach;
    private javax.swing.JTextField txtTenSP;
    private javax.swing.JTextField txtTenSanPham;
    private javax.swing.JTextField txtTimKiemSP;
    private javax.swing.JTextField txtTimKiemTheoMa;
    private javax.swing.JTextField txtTinhDiem;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JTextField txtUsername1;
    private javax.swing.JTextField txtVaiTro;
    private javax.swing.JTextField txt_code_chi_tiet;
    private javax.swing.JLabel txt_phan_trangVoucher;
    private javax.swing.JTextField txt_so_luong_vou;
    private javax.swing.JTextField txt_so_tien_giam;
    private javax.swing.JTextField txt_tien_ap_dung;
    private javax.swing.JTextField txt_tien_toi_da;
    private javax.swing.JTextField txt_tim_Voucher;
    private javax.swing.JTextField txt_tim_chuong_trình;
    private javax.swing.JTextField txt_tim_sp;
    // End of variables declaration//GEN-END:variables

}

package Service;

import dao.DatabaseConnectionManager;
import Model.Categories;
import Model.Color;
import Model.ProductDetails;
import Model.Products;
import Model.Size;
import Model.Style;
import Model.Suppliers;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HoaNguyenVan
 */
public class QuanLySanPhamService {

    DatabaseConnectionManager dcm = new DatabaseConnectionManager("QuanLyShirtService", "sa", "123456");

    public List<Categories> getAllCategory() {
        List<Categories> listCategory = new ArrayList();
        String sql = "SELECT category_id, category_name FROM Categories";
        listCategory.clear();
        try (Connection con = dcm.getConnection(); PreparedStatement statement = con.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String category_id = rs.getString(1);
                String category_name = rs.getString(2);

                Categories cate = new Categories(category_id, category_name);
                listCategory.add(cate);
            }
        } catch (SQLException ex) {

            ex.printStackTrace();
        }

        return listCategory;

    }

    public List<Style> getAllStyle() {
        List<Style> listStyle = new ArrayList();
        String sql = "SELECT style_id, style_name, [description] FROM Styles";
        listStyle.clear();
        try (Connection con = dcm.getConnection(); PreparedStatement statement = con.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String style_id = rs.getString(1);
                String style_name = rs.getString(2);
                String description = rs.getString(3);

                Style style = new Style(style_id, style_name, description);
                listStyle.add(style);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listStyle;

    }

    public List<Size> getAllSize() {
        List<Size> listSize = new ArrayList();
        String sql = "SELECT size_id, size_name, size_description FROM Sizes";
        listSize.clear();
        try (Connection con = dcm.getConnection(); PreparedStatement statement = con.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String size_id = rs.getString(1);
                String size_name = rs.getString(2);
                String size_description = rs.getString(3);

                Size size = new Size(size_id, size_name, size_description);
                listSize.add(size);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listSize;

    }

    public List<Color> getAllColor() {
        List<Color> listColor = new ArrayList();
        String sql = "SELECT color_id, color_name FROM Color";
        listColor.clear();
        try (Connection con = dcm.getConnection(); PreparedStatement statement = con.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String color_id = rs.getString(1);
                String color_name = rs.getString(2);

                Color color = new Color(color_id, color_name);
                listColor.add(color);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listColor;

    }

    public List<Suppliers> getAllSuppliers() {
        List<Suppliers> listSuppliers = new ArrayList();
        String sql = "SELECT supplier_id, supplier_name, phone, email, [address] FROM Suppliers";
        listSuppliers.clear();
        try (Connection con = dcm.getConnection(); PreparedStatement statement = con.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String supplier_id = rs.getString(1);
                String supplier_name = rs.getString(2);
                String phone = rs.getString(3);
                String email = rs.getString(4);
                String address = rs.getString(5);

                Suppliers sup = new Suppliers(supplier_id, supplier_name, phone, email, address);
                listSuppliers.add(sup);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listSuppliers;

    }

    public List<Products> getAllProduct() {
        List<Products> listProducts = new ArrayList<>();
        String sql = """
                 SELECT 
                     P.product_code, 
                     P.product_name, 
                     P.category_id, 
                     (SELECT SUM(PD.quantity) FROM ProductDetails PD WHERE PD.product_id = P.product_id) AS product_quantity, 
                     P.description, 
                     P.supplier_id, 
                     P.status,
                     C.category_name,
                     S.supplier_name,
                     P.product_id
                 FROM 
                     Products P
                 LEFT JOIN 
                     Categories C ON P.category_id = C.category_id
                 LEFT JOIN 
                     Suppliers S ON P.supplier_id = S.supplier_id;
                 """;
        listProducts.clear();
        try (Connection con = dcm.getConnection(); PreparedStatement statement = con.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String product_code = rs.getString(1);
                String product_name = rs.getString(2);
                String category_id = rs.getString(3);
                Integer product_quantity = rs.getInt(4);
                String description = rs.getString(5);
                String supplier_id = rs.getString(6);
                Boolean status = rs.getBoolean(7);
                String category_name = rs.getString(8);
                String supplier_name = rs.getString(9);
                Integer product_id = rs.getInt(10);

                Categories cate = new Categories(category_id, category_name);
                Suppliers sup = new Suppliers(supplier_id, supplier_name);

                Products pro = new Products(product_id, product_code, product_name, cate, product_quantity, description, sup, status);
                listProducts.add(pro);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return listProducts;
    }

    public List<ProductDetails> getAllProductDetail() {
        List<ProductDetails> listProductDetail = new ArrayList<>();

        String sql = """
                     SELECT 
                         p.product_code,
                         pd.productDetails_code,
                         p.product_name,
                         c.color_name,
                         s.size_name,
                         st.style_name,
                         pd.image_url,
                         pd.quantity,
                         pd.price,
                         ca.category_name,
                         pd.productDetails_id,
                         p.product_id
                     FROM 
                         Products p
                     JOIN 
                         ProductDetails pd ON p.product_id = pd.product_id
                     JOIN 
                         Color c ON pd.color_id = c.color_id
                     JOIN 
                         Sizes s ON pd.size_id = s.size_id
                     JOIN 
                         Styles st ON pd.style_id = st.style_id
                     JOIN 
                         Categories ca ON ca.category_id = p.category_id;
                     """;
        listProductDetail.clear();

        try (Connection con = dcm.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String product_code = rs.getString(1);
                String productDetails_code = rs.getString(2);
                String product_name = rs.getString(3);
                String color_name = rs.getString(4);
                String size_name = rs.getString(5);
                String style_name = rs.getString(6);
                String image_url = rs.getString(7);
                Integer quantity = rs.getInt(8);
                Double price = rs.getDouble(9);
                String category_name = rs.getString(10);
                Integer productDetails_id = rs.getInt(11);
                Integer product_id = rs.getInt(12);

                ProductDetails proDe = new ProductDetails(productDetails_id, product_id, product_code, productDetails_code, product_name, style_name, size_name, color_name, image_url, price, quantity, category_name);
                listProductDetail.add(proDe);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listProductDetail;
    }

    public void loadTable(JTable table, List<Products> filteredProducts, int currentPage, int rowsPerPage) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        List<Products> listProduct = filteredProducts;
        int stt = (currentPage - 1) * rowsPerPage + 1;
        if (!listProduct.isEmpty()) {
            for (Products pro : listProduct) {
                Object[] rowData = {
                    stt++,
                    pro.getProduct_code(),
                    pro.getProduct_name(),
                    pro.getCategory().getCategory_name(),
                    pro.getProduct_quantity(),
                    pro.getDescription(),
                    pro.getSupplier().getSupplier_name(),
                    pro.getStatus() ? "Đang kinh doanh" : "Ngừng kinh doanh"
                };
                dtm.addRow(rowData);
            }
        } else {
            dtm.addRow(new Object[]{"", "", "", "Danh sách trống", "", "", "", ""});
        }

    }

    public void loadTableColor(JTable table, List<Color> listColor) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        int stt = 1;
        if (!listColor.isEmpty()) {
            for (Color color : listColor) {
                Object[] rowData = {
                    stt++,
                    color.getColor_id(),
                    color.getColor_name()
                };
                dtm.addRow(rowData);
            }
        } else {
            dtm.addRow(new Object[]{"", "Danh sách trống", "", ""});
        }
    }

    public void loadTableSize(JTable table, List<Size> listSize) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        int stt = 1;
        if (!listSize.isEmpty()) {
            for (Size size : listSize) {
                Object[] rowData = {
                    stt++,
                    size.getSize_id(),
                    size.getSize_name(),
                    size.getSize_description()
                };
                dtm.addRow(rowData);
            }
        } else {
            dtm.addRow(new Object[]{"", "Danh sách trống", "", ""});
        }
    }

    public void loadTableStyle(JTable table, List<Style> listStyle) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        int stt = 1;
        if (!listStyle.isEmpty()) {
            for (Style style : listStyle) {
                Object[] rowData = {
                    stt++,
                    style.getStyle_id(),
                    style.getStyle_name(),
                    style.getStyle_description()
                };
                dtm.addRow(rowData);
            }
        } else {
            dtm.addRow(new Object[]{"", "Danh sách trống", "", ""});
        }
    }

    public void loadTableSupplier(JTable table, List<Suppliers> listSupp) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        int stt = 1;
        if (!listSupp.isEmpty()) {
            for (Suppliers supp : listSupp) {
                Object[] rowData = {
                    stt++,
                    supp.getSupplier_id(),
                    supp.getSupplier_name(),
                    supp.getPhone(),
                    supp.getEmail(),
                    supp.getAddress()
                };
                dtm.addRow(rowData);
            }
        } else {
            dtm.addRow(new Object[]{"", "", "Danh sách trống", "", "", ""});
        }
    }

    public void addCbbDanhMuc(JComboBox<String> cbb) {
        cbb.removeAllItems();
        cbb.addItem("");
        for (Categories cate : getAllCategory()) {
            cbb.addItem(cate.getCategory_name());
        }
    }

    public void addCbbNhaCC(JComboBox<String> cbb) {
        cbb.removeAllItems();
        cbb.addItem("");
        for (Suppliers sup : getAllSuppliers()) {
            cbb.addItem(sup.getSupplier_name());
        }
    }

    public void addCbbStyle(JComboBox<String> cbb) {
        cbb.removeAllItems();
        cbb.addItem("");
        for (Style style : getAllStyle()) {
            cbb.addItem(style.getStyle_name());
        }
    }

    public void addCbbLocTrangThai(JComboBox<String> cbb) {
        cbb.removeAllItems();
        cbb.addItem("");
        cbb.addItem("Đang kinh doanh");
        cbb.addItem("Ngừng kinh doanh");
    }

    public void addCbbColor(JComboBox<String> cbb) {
        cbb.removeAllItems();
        cbb.addItem("");
        for (Color color : getAllColor()) {
            cbb.addItem(color.getColor_name());
        }
    }

    public void addCbbSize(JComboBox<String> cbb) {
        cbb.removeAllItems();
        cbb.addItem("");
        for (Size size : getAllSize()) {
            cbb.addItem(size.getSize_name());
        }
    }

    public void addCbbLocGiaBan(JComboBox<String> cbb) {
        cbb.removeAllItems();
        cbb.addItem("");
        cbb.addItem("dưới 200.000 VNĐ");
        cbb.addItem("từ 200.000 VNĐ - 500.000 VNĐ");
        cbb.addItem("trên 500.000 VNĐ");

    }

    public void addCbbLocSoLuong(JComboBox<String> cbb) {
        cbb.removeAllItems();
        cbb.addItem("");
        cbb.addItem("dưới 50 cái");
        cbb.addItem("từ 50 cái đến 300 cái");
        cbb.addItem("trên 300 cái");

    }

    //Lọc SP
    public List<Products> filterProducts(String categoryName, String supplierName, String status) {
        String sql = """
                 SELECT 
                     P.product_code, 
                     P.product_name, 
                     P.category_id, 
                     (SELECT SUM(PD.quantity) FROM ProductDetails PD WHERE PD.product_id = P.product_id) AS product_quantity, 
                     P.description, 
                     P.supplier_id, 
                     P.status,
                     C.category_name,
                     S.supplier_name
                 FROM 
                     Products P
                 LEFT JOIN 
                     Categories C ON P.category_id = C.category_id
                 LEFT JOIN 
                     Suppliers S ON P.supplier_id = S.supplier_id
                 WHERE 1 = 1 
                 """;

        List<Object> params = new ArrayList<>();

        if (categoryName != null && !categoryName.isEmpty()) {
            sql += "AND C.category_name = ? ";
            params.add(categoryName);
        }
        if (supplierName != null && !supplierName.isEmpty()) {
            sql += "AND S.supplier_name = ? ";
            params.add(supplierName);
        }
        if (status != null && !status.isEmpty()) {
            if (status.contains("Đang kinh doanh")) {
                sql += "AND P.status = 1 ";
            } else {
                sql += "AND P.status = 0 ";
            }
        }

        List<Products> filteredProducts = new ArrayList<>();

        try (Connection con = dcm.getConnection(); PreparedStatement statement = con.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String product_code = rs.getString(1);
                    String product_name = rs.getString(2);
                    String category_id = rs.getString(3);
                    int product_quantity = rs.getInt(4);
                    String description = rs.getString(5);
                    String supplier_id = rs.getString(6);
                    boolean product_status = rs.getBoolean(7);
                    String category_name = rs.getString(8);
                    String supplier_name = rs.getString(9);

                    Categories cate = new Categories(category_id, category_name);
                    Suppliers sup = new Suppliers(supplier_id, supplier_name);

                    Products pro = new Products(product_code, product_name, cate, product_quantity, description, sup, product_status);
                    filteredProducts.add(pro);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return filteredProducts;
    }

    public List<Products> GetListSearch(String keywordUP) {
        String keyword = removeVietnameseAccents(keywordUP).toUpperCase();
        List<Products> listSearch = new ArrayList<>();
        List<Products> allProducts = getAllProduct();
        String statusStr;

        for (Products products : allProducts) {
            if (products.getStatus()) {
                statusStr = "Đang kinh doanh";
            } else {
                statusStr = "Ngừng kinh doanh";
            }

            if (removeVietnameseAccents(products.getProduct_name()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(products.getProduct_code()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(products.getCategory().getCategory_name()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(products.getSupplier().getSupplier_name()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(statusStr).toUpperCase().contains(keyword)) {
                listSearch.add(products);
            }
        }

        return listSearch;
    }

    private String removeVietnameseAccents(String str) {
        str = str.replaceAll("[đĐ]", "d");
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    public String getCategoryIdByName(String categoryName) {
        List<Categories> listCategory = getAllCategory();
        String categoryId = null;
        for (Categories cate : listCategory) {
            if (cate.getCategory_name().equals(categoryName)) {
                categoryId = cate.getCategory_id();
                break;
            }
        }
        return categoryId;
    }

    public String getSupplierIdByName(String supplierName) {
        List<Suppliers> listSupplierses = getAllSuppliers();
        String supplierId = null;
        for (Suppliers sup : listSupplierses) {
            if (sup.getSupplier_name().equals(supplierName)) {
                supplierId = sup.getSupplier_id();
                break;
            }
        }
        return supplierId;
    }

    public void addNewProduct(Products product) {
        String sql = """
                     INSERT INTO Products(product_name, [description], category_id,[status], supplier_id) VALUES (?, ?, ?, ?, ?)
                     """;

        try (Connection con = dcm.getConnection(); PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, product.getProduct_name());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getCategory().getCategory_id());
            statement.setBoolean(4, product.getStatus());
            statement.setString(5, product.getSupplier().getSupplier_id());

            statement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();

        }
    }

    public void updateProduct(Products product) {
        String sql = "UPDATE Products SET product_name = ?, description = ?, category_id = ?, status = ?, supplier_id = ? WHERE product_code = ?";

        try (Connection con = dcm.getConnection(); PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, product.getProduct_name());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getCategory().getCategory_id());
            statement.setBoolean(4, product.getStatus());
            statement.setString(5, product.getSupplier().getSupplier_id());
            statement.setString(6, product.getProduct_code());

            statement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Boolean isDulicateName(String productName) {
        for (Products pro : getAllProduct()) {
            if (removeVietnameseAccents(pro.getProduct_name()).equalsIgnoreCase(removeVietnameseAccents(productName))) {
                return true;
            }
        }
        return false;
    }

    // SPCT
    public void loadTableDetail(JTable table, List<ProductDetails> list, int currentPage, int rowsPerPage) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        int stt = (currentPage - 1) * rowsPerPage + 1;
        if (!list.isEmpty()) {
            for (ProductDetails proDe : list) {
                Object[] rowData = {
                    stt++,
                    proDe.getProductDetail_code(),
                    proDe.getProduct_code(),
                    proDe.getProduct_name(),
                    proDe.getCategory_name(),
                    proDe.getColor(),
                    proDe.getSize(),
                    proDe.getImage(),
                    proDe.getStyle(),
                    proDe.getQuantity(),
                    proDe.getPrice()
                };
                dtm.addRow(rowData);
            }
        } else {
            dtm.addRow(new Object[]{"", "", "", "", "Danh sách trống", "", "", "", "", "", ""});
        }
    }

    public List<ProductDetails> filterProductDetail(String category_name, String style_name, String size_name, String color_name, Integer quantity, Double[] price) {
        String sql = """
                 SELECT 
                     p.product_code,
                     pd.productDetails_code,
                     p.product_name,
                     c.color_name,
                     s.size_name,
                     st.style_name,
                     pd.image_url,
                     pd.quantity,
                     pd.price,
                     ca.category_name
                 FROM 
                     Products p
                 JOIN 
                     ProductDetails pd ON p.product_id = pd.product_id
                 JOIN 
                     Color c ON pd.color_id = c.color_id
                 JOIN 
                     Sizes s ON pd.size_id = s.size_id
                 JOIN 
                     Styles st ON pd.style_id = st.style_id
                 JOIN 
                     Categories ca ON ca.category_id = p.category_id
                 WHERE 1 = 1
                 """;
        List<Object> params = new ArrayList<>();

        if (category_name != null && !category_name.isEmpty()) {
            sql += "AND ca.category_name = ? ";
            params.add(category_name);
        }
        if (style_name != null && !style_name.isEmpty()) {
            sql += "AND st.style_name = ? ";
            params.add(style_name);
        }
        if (size_name != null && !size_name.isEmpty()) {
            sql += "AND s.size_name = ? ";
            params.add(size_name);
        }
        if (color_name != null && !color_name.isEmpty()) {
            sql += "AND c.color_name = ? ";
            params.add(color_name);
        }
        if (quantity != null) {
            if (quantity < 50) {
                sql += "AND pd.quantity < 50 ";
            } else if (quantity >= 50 && quantity <= 300) {
                sql += "AND pd.quantity BETWEEN 50 AND 300 ";
            } else if (quantity > 300) {
                sql += "AND pd.quantity > 300 ";
            }
        }
        if (price != null) {
            sql += "AND pd.price BETWEEN ? AND ? ";
            params.add(price[0]);
            params.add(price[1]);
        }

        List<ProductDetails> productList = new ArrayList<>();
        try (Connection conn = dcm.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ProductDetails productDetail = new ProductDetails();
                productDetail.setProduct_code(rs.getString("product_code"));
                productDetail.setProductDetail_code(rs.getString("productDetails_code"));
                productDetail.setProduct_name(rs.getString("product_name"));
                productDetail.setColor(rs.getString("color_name"));
                productDetail.setSize(rs.getString("size_name"));
                productDetail.setStyle(rs.getString("style_name"));
                productDetail.setImage(rs.getString("image_url"));
                productDetail.setQuantity(rs.getInt("quantity"));
                productDetail.setPrice(rs.getDouble("price"));
                productDetail.setCategory_name(rs.getString("category_name"));

                productList.add(productDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

    public List<ProductDetails> GetListSearchProductDetail(String keywordUP) {
        String keyword = removeVietnameseAccents(keywordUP).toUpperCase();
        List<ProductDetails> listSearch = new ArrayList<>();
        List<ProductDetails> allProducts = getAllProductDetail();

        for (ProductDetails proDe : allProducts) {
            if (removeVietnameseAccents(proDe.getCategory_name()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(proDe.getStyle()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(proDe.getSize()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(proDe.getColor()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(proDe.getProductDetail_code()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(proDe.getProduct_code()).toUpperCase().contains(keyword)
                    || removeVietnameseAccents(proDe.getProduct_name()).toUpperCase().contains(keyword)
                    || String.valueOf(proDe.getQuantity()).toUpperCase().contains(keyword)
                    || String.valueOf(proDe.getPrice()).toUpperCase().contains(keyword)) {
                listSearch.add(proDe);
            }
        }

        return listSearch;
    }

    public Double[] convertPrice(String price) {
        if (price == null || price.isEmpty()) {
            return null;
        }
        switch (price) {
            case "dưới 200.000 VNĐ":
                return new Double[]{0.0, 199999.99};
            case "từ 200.000 VNĐ - 500.000 VNĐ":
                return new Double[]{200000.0, 500000.0};
            case "trên 500.000 VNĐ":
                return new Double[]{500000.01, Double.MAX_VALUE};
            default:
                return null;
        }
    }

    public Integer convertQuantity(String quantity) {
        if (quantity == null || quantity.isEmpty()) {
            return null;
        }
        switch (quantity) {
            case "dưới 50 cái":
                return 49;
            case "từ 50 cái đến 300 cái":
                return 50;
            case "trên 300 cái":
                return 301;
            default:
                return null;
        }
    }

    public List<ProductDetails> getListById(String productCode) {
        List<ProductDetails> list = new ArrayList<>();

        for (ProductDetails proDe : getAllProductDetail()) {
            if (proDe.getProduct_code().equals(productCode)) {
                list.add(proDe);
            }
        }

        return list;
    }

    public void addProductDetail(ProductDetails proDe) {
        String sql = """
                     INSERT INTO ProductDetails(product_id, color_id, style_id, size_id, price, quantity, image_url) VALUES (?, ?, ?, ?, ?, ?, ?);
                     """;
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setInt(1, proDe.getProduct_id());
            pre.setString(2, proDe.getColor());
            pre.setString(3, proDe.getStyle());
            pre.setString(4, proDe.getSize());
            pre.setDouble(5, proDe.getPrice());
            pre.setInt(6, proDe.getQuantity());
            pre.setString(7, proDe.getImage());

            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateProductDetail(ProductDetails proDe) {
        String sql = """
                     UPDATE ProductDetails 
                     SET color_id = ?, style_id = ?, size_id = ?, price = ?, quantity = ?, product_id = ?, image_url = ? WHERE productDetails_id = ?;
                     """;
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, proDe.getColor());
            pre.setString(2, proDe.getStyle());
            pre.setString(3, proDe.getSize());
            pre.setDouble(4, proDe.getPrice());
            pre.setInt(5, proDe.getQuantity());
            pre.setInt(6, proDe.getProduct_id());
            pre.setString(7, proDe.getImage());
            pre.setInt(8, proDe.getProductDetail_id());

            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ProductDetails getProductDetailByQR(String stringQr) {
        for (ProductDetails productDetails : getAllProductDetail()) {
            if (productDetails.getProductDetail_code().equals(stringQr)) {
                return productDetails;
            }
        }
        return null;
    }

    public List<ProductDetails> getListQR(String stringQr) {
        List<ProductDetails> listQR = new ArrayList<ProductDetails>();

        for (ProductDetails productDetails : getAllProductDetail()) {
            if (productDetails.getProductDetail_code().equals(stringQr)) {
                listQR.add(productDetails);
            }
        }
        return listQR;
    }

    public Integer getProductDetailIdByCode(String productDetailCode) throws SQLException {
        List<ProductDetails> listProductDetail = getAllProductDetail();
        Integer productDetailId = null;
        for (ProductDetails proDe : listProductDetail) {
            if (proDe.getProductDetail_code().equals(productDetailCode)) {
                productDetailId = proDe.getProductDetail_id();
                break;
            }
        }
        return productDetailId;
    }

    public String getColorIdByName(String colorName) throws SQLException {
        List<Color> listColor = getAllColor();
        String color_id = null;
        for (Color color : listColor) {
            if (removeVietnameseAccents(color.getColor_name()).equalsIgnoreCase(removeVietnameseAccents(colorName))) {
                color_id = color.getColor_id();
                break;
            }
        }
        return color_id;
    }

    public Integer getProductIdByCode(String productCode) throws SQLException {
        List<Products> listProduct = getAllProduct();
        Integer product_id = null;
        for (Products pro : listProduct) {
            if (pro.getProduct_code().toUpperCase().equalsIgnoreCase(productCode.toUpperCase())) {
                product_id = pro.getProduct_id();
                break;
            }
        }
        return product_id;
    }

    public String getStyleIdByName(String styleName) throws SQLException {
        List<Style> listStyle = getAllStyle();
        String style_id = null;
        for (Style style : listStyle) {
            if (style.getStyle_name().equalsIgnoreCase(styleName)) {
                style_id = style.getStyle_id();
                break;
            }
        }
        return style_id;
    }

    public String getSizeIdByName(String sizeName) throws SQLException {
        List<Size> listSize = getAllSize();
        String size_id = null;
        for (Size size : listSize) {
            if (size.getSize_name().equalsIgnoreCase(sizeName)) {
                size_id = size.getSize_id();
                break;
            }
        }
        return size_id;
    }

    public Style getStyleById(String styleId) {
        for (Style style : getAllStyle()) {
            if (style.getStyle_id().equalsIgnoreCase(styleId)) {
                return style;
            }
        }
        return null;
    }

    public Color getColorById(String colorId) {
        for (Color color : getAllColor()) {
            if (color.getColor_id().equalsIgnoreCase(colorId)) {
                return color;
            }
        }
        return null;
    }

    public Suppliers getSupplierById(String suppId) {
        for (Suppliers supp : getAllSuppliers()) {
            if (supp.getSupplier_id().equalsIgnoreCase(suppId)) {
                return supp;
            }
        }
        return null;
    }

    public Size getSizeById(String sizeId) {
        for (Size size : getAllSize()) {
            if (size.getSize_id().equalsIgnoreCase(sizeId)) {
                return size;
            }
        }
        return null;
    }

    public Products getProductByCode(String productCode) {
        for (Products pro : getAllProduct()) {
            if (pro.getProduct_code().equalsIgnoreCase(productCode)) {
                return pro;
            }
        }
        return null;
    }

    public ProductDetails getProductDetailByCode(String productDetailCode) {
        for (ProductDetails proDe : getAllProductDetail()) {
            if (proDe.getProductDetail_code().equalsIgnoreCase(productDetailCode)) {
                return proDe;
            }
        }
        return null;
    }

    // Thêm mới Color
    public void addColor(Color color) {
        String sql = """
                 INSERT INTO Color(color_id, color_name) VALUES (?, ?)
                 """;
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, color.getColor_id());
            pre.setString(2, color.getColor_name());

            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

// Cập nhật Color
    public void updateColor(Color color) {
        String sql = """
                 UPDATE Color 
                 SET color_name = ? 
                 WHERE color_id = ?
                 """;
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, color.getColor_name());
            pre.setString(2, color.getColor_id());

            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Thêm mới Suppliers
    public void addSupplier(Suppliers supplier) {
        String sql = """
                 INSERT INTO Suppliers(supplier_id, supplier_name, phone, email, [address]) 
                 VALUES (?, ?, ?, ?, ?)
                 """;
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, supplier.getSupplier_id());
            pre.setString(2, supplier.getSupplier_name());
            pre.setString(3, supplier.getPhone());
            pre.setString(4, supplier.getEmail());
            pre.setString(5, supplier.getAddress());

            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

// Cập nhật Suppliers
    public void updateSupplier(Suppliers supplier) {
        String sql = """
                 UPDATE Suppliers 
                 SET supplier_name = ?, phone = ?, email = ?, [address] = ? 
                 WHERE supplier_id = ?
                 """;
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, supplier.getSupplier_name());
            pre.setString(2, supplier.getPhone());
            pre.setString(3, supplier.getEmail());
            pre.setString(4, supplier.getAddress());
            pre.setString(5, supplier.getSupplier_id());

            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Thêm mới Sizes
    public void addSize(Size size) {
        String sql = """
                 INSERT INTO Sizes(size_id, size_name, size_description) 
                 VALUES (?, ?, ?)
                 """;
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, size.getSize_id());
            pre.setString(2, size.getSize_name());
            pre.setString(3, size.getSize_description());

            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

// Cập nhật Sizes
    public void updateSize(Size size) {
        String sql = """
                 UPDATE Sizes 
                 SET size_name = ?, size_description = ? 
                 WHERE size_id = ?
                 """;
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, size.getSize_name());
            pre.setString(2, size.getSize_description());
            pre.setString(3, size.getSize_id());

            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Thêm mới Styles
    public void addStyle(Style style) {
        String sql = """
                 INSERT INTO Styles(style_id, style_name, [description]) 
                 VALUES (?, ?, ?)
                 """;
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, style.getStyle_id());
            pre.setString(2, style.getStyle_name());
            pre.setString(3, style.getStyle_description());

            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

// Cập nhật Styles
    public void updateStyle(Style style) {
        String sql = """
                 UPDATE Styles 
                 SET style_name = ?, [description] = ? 
                 WHERE style_id = ?
                 """;
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, style.getStyle_name());
            pre.setString(2, style.getStyle_description());
            pre.setString(3, style.getStyle_id());

            pre.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Boolean isDuplicateColorById(String colorId) {
        for (Color color : getAllColor()) {
            if (color.getColor_id().equalsIgnoreCase(colorId)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDuplicateSizeById(String sizeId) {
        for (Size size : getAllSize()) {
            if (size.getSize_id().equalsIgnoreCase(sizeId)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDuplicateStyleById(String styleId) {
        for (Style style : getAllStyle()) {
            if (style.getStyle_id().equalsIgnoreCase(styleId)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDuplicateSupplierById(String supplierId) {
        for (Suppliers supplier : getAllSuppliers()) {
            if (supplier.getSupplier_id().equalsIgnoreCase(supplierId)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDuplicateSupplierByPhone(String supplierPhone) {
        for (Suppliers supplier : getAllSuppliers()) {
            if (supplier.getPhone().equalsIgnoreCase(supplierPhone)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDuplicateSupplierByEmail(String supplierEmail) {
        for (Suppliers supplier : getAllSuppliers()) {
            if (supplier.getEmail().equalsIgnoreCase(supplierEmail)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDuplicateProductByCode(String productCode) {
        for (Products pro : getAllProduct()) {
            if (pro.getProduct_code().equalsIgnoreCase(productCode)) {
                return true;
            }
        }
        return false;
    }

    public String validateColorInput(String colorId, String colorName, boolean isAdd) {
        if (colorId.trim() == null || colorId.trim().isEmpty()) {
            return "Vui lòng nhập mã màu sắc.";
        }
        if (colorId.contains(" ")) {
            return "Mã màu sắc không được chứa dấu cách.";
        }
        if (!colorId.matches(".*[a-zA-Z].*") || !colorId.matches(".*[0-9].*")) {
            return "Mã màu sắc phải chứa cả chữ và số.";
        }
        if (isAdd && isDuplicateColorById(colorId)) {
            return "Mã màu sắc đã tồn tại.";
        }
        if (colorName == null || colorName.isEmpty()) {
            return "Vui lòng nhập tên màu sắc.";
        }
        if (isAdd && isDuplicateColorByName(colorName)) {
            return "Tên màu sắc đã tồn tại.";
        }
        if (colorName.matches(".*\\d.*")) {
            return "Tên màu sắc không được chứa số.";
        }
        return null;
    }

    public String validateSizeInput(String sizeId, String sizeName, boolean isAdd) {
        if (sizeId.trim() == null || sizeId.trim().isEmpty()) {
            return "Vui lòng nhập mã kích thước.";
        }
        if (sizeId.contains(" ")) {
            return "Mã kích thước không được chứa dấu cách.";
        }
        if (!sizeId.matches(".*[a-zA-Z].*") || !sizeId.matches(".*[0-9].*")) {
            return "Mã kích thước phải chứa cả chữ và số.";
        }
        if (isAdd && isDuplicateSizeById(sizeId)) {
            return "Mã kích thước đã tồn tại.";
        }
        if (sizeName == null || sizeName.isEmpty()) {
            return "Vui lòng nhập tên kích thước.";
        }
        if (isAdd && isDuplicateSizeByName(sizeName)) {
            return "Tên kích thước đã tồn tại.";
        }
        if (sizeName.matches(".*\\d.*")) {
            return "Tên kích thước không được chứa số.";
        }

        return null; // Đầu vào hợp lệ
    }

    public String validateSupplierInput(String supplierId, String supplierName, String phone, String email, String address, boolean isAdd) {
        if (supplierId == null || supplierId.trim().isEmpty()) {
            return "Vui lòng nhập mã nhà cung cấp.";
        }
        if (supplierId.contains(" ")) {
            return "Mã nhà cung cấp không được chứa dấu cách.";
        }

        if (!supplierId.matches(".*[a-zA-Z].*") || !supplierId.matches(".*[0-9].*")) {
            return "Mã nhà cung cấp phải chứa cả chữ và số.";
        }

        if (isAdd && isDuplicateSupplierById(supplierId)) {
            return "Mã nhà cung cấp đã tồn tại.";
        }
        if (supplierName == null || supplierName.isEmpty()) {
            return "Vui lòng nhập tên nhà cung cấp.";
        }
        if (isAdd && isDuplicateSupplierByName(supplierName)) {
            return "Tên nhà cung cấp đã tồn tại.";
        }
        if (supplierName.matches("\\d+")) {
            return "Tên nhà cung cấp không được phép tất cả là số.";
        }
        if (phone == null || phone.isEmpty()) {
            return "Vui lòng nhập số điện thoại nhà cung cấp.";
        }
        if (!phone.matches("0[0-9]{9}")) {
            return "Số điện thoại không đúng định dạng.";
        }
        if (isAdd && isDuplicateSupplierByPhone(phone)) {
            return "Số điện thoại đã tồn tại.";
        }
        if (email == null || email.isEmpty()) {
            return "Vui lòng nhập email nhà cung cấp.";
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            return "Email không đúng định dạng.";
        }
        if (isAdd && isDuplicateSupplierByEmail(email)) {
            return "Email này đã tồn tại.";
        }
        if (address == null || address.isEmpty()) {
            return "Vui lòng nhập địa chỉ nhà cung cấp.";
        }
        if (!address.matches(".*[a-zA-Z].*")) {
            return "Địa chỉ không được chứa toàn bộ là số.";
        }

        return null;
    }

    public String validateStyleInput(String styleId, String styleName, boolean isAdd) {
        if (styleId == null || styleId.trim().isEmpty()) {
            return "Vui lòng nhập mã phong cách.";
        }
        if (styleId.contains(" ")) {
            return "Mã phong cách không được chứa dấu cách.";
        }
        if (!styleId.matches(".*[a-zA-Z].*") || !styleId.matches(".*[0-9].*")) {
            return "Mã phong cách phải chứa cả chữ và số.";
        }
        if (isAdd && isDuplicateStyleById(styleId)) {
            return "Mã phong cách đã tồn tại.";
        }
        if (styleName == null || styleName.isEmpty()) {
            return "Vui lòng nhập tên phong cách.";
        }
        if (styleName.matches(".*\\d.*")) {
            return "Tên phong cách không được chứa số.";
        }

        return null; // Đầu vào hợp lệ
    }

    public String validateProductInput(String productCode, String productName, String description,
            String categoryName, String supplierName, Boolean status, boolean isAdd) {
        // Kiểm tra tên sản phẩm
        if (productName == null || productName.trim().isEmpty()) {
            return "Vui lòng nhập tên sản phẩm.";
        }

        // Kiểm tra nếu tên sản phẩm chứa số hehe
        if (productName.matches(".*\\d.*")) {
            return "Tên sản phẩm không được chứa số.";
        }

        // Kiểm tra tính hợp lệ của tên sản phẩm hhihi
        if (isAdd && isDulicateName(productName)) {
            return "Tên sản phẩm này đã tồn tại.";
        }

        // Kiểm tra danh mục hoho
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return "Vui lòng chọn danh mục.";
        }

        // Kiểm tra nhà cung cấp
        if (supplierName == null || supplierName.trim().isEmpty()) {
            return "Vui lòng chọn nhà cung cấp.";
        }

        // Kiểm tra trạng thái
        if (status == null) {
            return "Vui lòng chọn trạng thái sản phẩm.";
        }

        return null; // Đầu vào hợp lệ
    }

    public String validateProductDetailInput(String productCode, String colorName, String styleName,
            String sizeName, String priceText, String quantityText, String image_url, boolean isAdd) {

        // Kiểm tra tên sản phẩm
        if (productCode == null || productCode.trim().isEmpty()) {
            return "Vui lòng nhập mã sản phẩm.";
        }
        if (!isDuplicateProductByCode(productCode)) {
            return "Mã sản phẩm không tồn tại.";
        }

        // Kiểm tra màu sắc
        if (colorName == null || colorName.trim().isEmpty()) {
            return "Vui lòng chọn màu sắc.";
        }

        // Kiểm tra kiểu dáng
        if (styleName == null || styleName.trim().isEmpty()) {
            return "Vui lòng chọn kiểu dáng.";
        }

        // Kiểm tra kích thước
        if (sizeName == null || sizeName.trim().isEmpty()) {
            return "Vui lòng chọn kích thước.";
        }

        // Kiểm tra số lượng
        if (quantityText == null || quantityText.trim().isEmpty()) {
            return "Vui lòng nhập số lượng.";
        }

        // Kiểm tra định dạng số lượng
        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                return "Số lượng phải là số nguyên dương hợp lệ.";
            }
        } catch (NumberFormatException e) {
            return "Số lượng phải là số nguyên hợp lệ.";
        }

        // Kiểm tra giá
        if (priceText == null || priceText.trim().isEmpty()) {
            return "Vui lòng nhập giá.";
        }

        // Kiểm tra định dạng giá
        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                return "Giá phải là số dương hợp lệ.";
            }
        } catch (NumberFormatException e) {
            return "Giá phải là số hợp lệ.";
        }

        // Kiểm tra hình ảnh
        if (image_url == null || image_url.trim().isEmpty()) {
            return "Vui lòng chọn ảnh";
        }

        return null; // Đầu vào hợp lệ
    }

    public ProductDetails getProductDetailByAttributes(Integer productId, String styleId, String sizeId, String colorId) {
        String sql = "SELECT * FROM ProductDetails WHERE product_id = ? AND style_id = ? AND size_id = ? AND color_id = ?";
        try (Connection con = dcm.getConnection(); PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setInt(1, productId);
            pre.setString(2, styleId);
            pre.setString(3, sizeId);
            pre.setString(4, colorId);

            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                // Tạo đối tượng ProductDetails từ kết quả truy vấn
                return new ProductDetails(
                        rs.getString("productDetails_code"), // Sửa thành cột ID của chi tiết sản phẩm nếu cần
                        rs.getInt("product_id"),
                        rs.getString("style_id"),
                        rs.getString("size_id"),
                        rs.getString("color_id")
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Boolean isDuplicateSizeByName(String sizeName) {
        for (Size size : getAllSize()) {
            if (removeVietnameseAccents(size.getSize_name()).equalsIgnoreCase(removeVietnameseAccents(sizeName))) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDuplicateColorByName(String colorName) {
        for (Color color : getAllColor()) {
            if (removeVietnameseAccents(color.getColor_name()).equalsIgnoreCase(removeVietnameseAccents(colorName))) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDuplicateSupplierByName(String suppName) {
        for (Suppliers supp : getAllSuppliers()) {
            if (removeVietnameseAccents(supp.getSupplier_name()).equalsIgnoreCase(removeVietnameseAccents(suppName))) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDuplicateStyleByName(String suppName) {
        for (Style style : getAllStyle()) {
            if (removeVietnameseAccents(style.getStyle_name()).equalsIgnoreCase(removeVietnameseAccents(suppName))) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDulicateProductByName(String productName) {
        for (Products pro : getAllProduct()) {
            if (removeVietnameseAccents(pro.getProduct_name()).equalsIgnoreCase(removeVietnameseAccents(productName))) {
                return true;
            }
        }
        return false;
    }

}

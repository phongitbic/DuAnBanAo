
package Utils;

import Model.Categories;
import Model.ProductDetails;
import Model.Products;
import Model.Suppliers;
import Service.QuanLySanPhamService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelImporter {

    QuanLySanPhamService qlsp = new QuanLySanPhamService();

    public List<Products> importProductsFromExcel(String fileName) throws IOException {

        List<Products> productList = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(fileName); Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();

                if (row.getLastCellNum() < 7) {
                    throw new IllegalArgumentException("Số cột trong tệp Excel không hợp lý.");
                }

                try {
                    String productName = row.getCell(2).getStringCellValue();
                    String categoryName = row.getCell(3).getStringCellValue();
                    String description = row.getCell(4).getStringCellValue();
                    String supplierName = row.getCell(5).getStringCellValue();
                    boolean status = row.getCell(6).getStringCellValue().equalsIgnoreCase("Đang kinh doanh");

                    String categoryId = qlsp.getCategoryIdByName(categoryName);
                    String supplierId = qlsp.getSupplierIdByName(supplierName);

                    Categories category = new Categories(categoryId, categoryName);
                    Suppliers supplier = new Suppliers(supplierId, supplierName);

                    Products product = new Products(productName, category, description, supplier, status);
                    productList.add(product);
                } catch (Exception e) {
                    // Nếu có lỗi, bỏ qua hàng đó và tiếp tục với hàng tiếp theo
                    continue;
                }
            }
        } catch (IOException e) {
            throw new IOException("Lỗi khi đọc tệp Excel: " + e);
        }

        return productList;

    }

    public List<ProductDetails> importProductDetailFromExcel(String fileName) throws IOException {
        List<ProductDetails> productDetailList = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(fileName); Workbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next(); // Bỏ qua hàng tiêu đề
            }

while (rows.hasNext()) {
                Row row = rows.next();

                if (row.getLastCellNum() < 8) {
                    throw new IllegalArgumentException("Số cột trong tệp Excel không hợp lý.");
                }

                try {
                    // Đọc dữ liệu từ các ô trong hàng
                    String productCode = row.getCell(1).getStringCellValue().trim();
                    String style = row.getCell(2).getStringCellValue().trim();
                    String size = row.getCell(3).getStringCellValue().trim();
                    String color = row.getCell(4).getStringCellValue().trim();
                    Double price = row.getCell(5).getNumericCellValue();
                    Integer quantity = (int) row.getCell(6).getNumericCellValue();
                    String image = row.getCell(7).getStringCellValue().trim();

                    ProductDetails productDetail = new ProductDetails(productCode, style, size, color, image, price, quantity);
                    productDetailList.add(productDetail);

                } catch (Exception e) {
                    // Bỏ qua hàng này nếu có lỗi và tiếp tục
                    continue;
                }
            }
        } catch (IOException e) {
            throw new IOException("Lỗi khi đọc tệp Excel: " + e.getMessage(), e);
        }

        return productDetailList;
    }
}
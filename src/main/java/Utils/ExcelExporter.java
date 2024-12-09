package Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import Model.Products;
import Model.ProductDetails;

public class ExcelExporter {

    public void exportProductListToExcel(List<Products> productList, String fileName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        // Tạo tiêu đề cột
        String[] columns = {"STT", "Mã sản phẩm", "Tên sản phẩm", "Danh mục", "Số lượng", "Mô tả", "Nhà cung cấp", "Trạng thái"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Điền dữ liệu từ danh sách vào tệp Excel
        int rowNum = 1;
        for (Products product : productList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(product.getProduct_code());
            row.createCell(2).setCellValue(product.getProduct_name());
            row.createCell(3).setCellValue(product.getCategory().getCategory_name());
            row.createCell(4).setCellValue(product.getProduct_quantity());
            row.createCell(5).setCellValue(product.getDescription());
            row.createCell(6).setCellValue(product.getSupplier().getSupplier_name());
            row.createCell(7).setCellValue(product.getStatus() ? "Đang kinh doanh" : "Ngừng kinh doanh");
        }

        saveWorkbook(workbook, fileName);
    }

    public void exportProductDetailListToExcel(List<ProductDetails> productDetailList, String fileName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ProductDetails");

        // Tạo tiêu đề cột
        String[] columns = {"STT", "Mã chi tiết sản phẩm", "Mã sản phẩm", "Tên sản phẩm", "Danh mục", "Màu sắc", "Kích thước", "Kiểu dáng", "Số lượng", "Giá bán", "Image_URL"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Điền dữ liệu từ danh sách vào tệp Excel
        int rowNum = 1;
        for (ProductDetails proDe : productDetailList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(proDe.getProductDetail_code());
            row.createCell(2).setCellValue(proDe.getProduct_code());
            row.createCell(3).setCellValue(proDe.getProduct_name());
            row.createCell(4).setCellValue(proDe.getCategory_name());
            row.createCell(5).setCellValue(proDe.getColor());
            row.createCell(6).setCellValue(proDe.getSize());
            row.createCell(7).setCellValue(proDe.getStyle());
            row.createCell(8).setCellValue(proDe.getQuantity());
            row.createCell(9).setCellValue(proDe.getPrice());
            row.createCell(10).setCellValue(proDe.getImage());
        }

        saveWorkbook(workbook, fileName);
    }

    private void saveWorkbook(Workbook workbook, String fileName) {
        Path path = Paths.get(fileName);
        try {
            if (Files.exists(path)) {
                Files.delete(path); // Xóa tệp nếu nó đã tồn tại
            }

            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

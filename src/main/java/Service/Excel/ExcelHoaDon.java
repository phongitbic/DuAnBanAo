package Service.Excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import Model.Order;
import Model.ProductDetails;

public class ExcelHoaDon {

    public void xuatHoaDon(List<Order> dsHD, String fileName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Order");

        // Tạo tiêu đề cột
        String[] columns = {"STT", "Mã Hóa đơn", "Mã nhân viên", "Tên nhân viên", "Mã khách hàng", "Tên khách hàng","Số điện thoại", "Ngày tạo", "Mã giảm giá","VAT","Phương thức thanh toán","Tông giá","Trạng thái"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Điền dữ liệu từ danh sách vào tệp Excel
        int rowNum = 1;
        for (Order od : dsHD) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(od.getOrderCode());
            row.createCell(2).setCellValue(od.getUserCode());
            row.createCell(3).setCellValue(od.getUserName());
            row.createCell(4).setCellValue(od.getCustomerCode());
            row.createCell(5).setCellValue(od.getCustomerName());
             row.createCell(6).setCellValue(od.getPhone());
            row.createCell(7).setCellValue(od.getOrderDate());
            row.createCell(8).setCellValue(od.getVoucherCode());
            row.createCell(9).setCellValue(od.getVat());
            row.createCell(10).setCellValue(od.getPaymentMethod());
            row.createCell(11).setCellValue(od.getTotalPrice());
            row.createCell(12).setCellValue(od.getStatus().equals("Đã thanh toán") ? "Đã thanh toán" : "Chưa thanh toán");

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

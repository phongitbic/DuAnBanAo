
package Model;

import java.math.BigDecimal;

/**
 *
 * @author Admin
 */
public class ProductsInfo {

   private int productId;
    private String productCode;
    private String productName;
    private String description;
    private String categoryId;
    private String status;
    private int soLuong;
    private String nhaCungCap;
    private BigDecimal tongTien;

    public ProductsInfo() {
    }

    public ProductsInfo(int productId, String productCode, String productName, String description, String categoryId, String status, int soLuong, String nhaCungCap, BigDecimal tongTien) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.description = description;
        this.categoryId = categoryId;
        this.status = status;
        this.soLuong = soLuong;
        this.nhaCungCap = nhaCungCap;
        this.tongTien = tongTien;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(String nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    
}

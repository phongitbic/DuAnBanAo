/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author DELL
 */
public class OriginalPrice {
    private  int productDetails_id ;
    private double  original_price;

    public int getProductDetails_id() {
        return productDetails_id;
    }

    public void setProductDetails_id(int productDetails_id) {
        this.productDetails_id = productDetails_id;
    }

    public double getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(double original_price) {
        this.original_price = original_price;
    }

    public OriginalPrice(int productDetails_id, double original_price) {
        this.productDetails_id = productDetails_id;
        this.original_price = original_price;
    }

    public OriginalPrice() {
    }
}

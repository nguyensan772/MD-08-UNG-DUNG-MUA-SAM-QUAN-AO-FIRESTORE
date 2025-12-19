package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model cho sản phẩm trong giỏ hàng
 */
public class CartItem implements Serializable {
    @SerializedName("_id")
    private String _id;
    private Product product_id; // Changed from String to Product to handle populated field
    private String name;
    private String image;
    private String size;
    private String color;
    private int quantity;
    private double price;

    public CartItem() {
    }

    public CartItem(Product product_id, String name, String image, String size, String color, int quantity,
            double price) {
        this.product_id = product_id;
        this.name = name;
        this.image = image;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    // Modified getter to return ID string from Product object
    public String getProduct_id() {
        return product_id != null ? product_id.getId() : null;
    }

    // Getter for the full Product object if needed
    public Product getProduct() {
        return product_id;
    }

    public void setProduct_id(Product product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Tính tổng giá trị của item này (price * quantity)
     */
    public double getSubtotal() {
        return price * quantity;
    }
}

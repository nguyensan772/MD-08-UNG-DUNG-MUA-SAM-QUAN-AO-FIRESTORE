package com.example.md_08_ungdungfivestore.models;

public class LocalCartItem {
    private String id;
    private String productName;
    private double price;
    private int quantity;
    private String image;



    private String size;  // Thêm Size (Vd: L, XL, 42...)
    private String color; // Thêm Màu (Vd: Đen, Trắng, Đỏ...)

    public LocalCartItem(String id, String productName, double price, int quantity, String image, String size, String color) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.size = size;
        this.color = color;
    }

    // Getters
    public String getId() { return id; }

    public String getImage() {
        return image;
    }

    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getSize() { return size; }
    public String getColor() { return color; }

    // Setters
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSize(String size) { this.size = size; }
    public void setColor(String color) { this.color = color; }
}
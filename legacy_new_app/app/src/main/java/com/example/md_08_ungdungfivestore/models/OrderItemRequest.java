package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

// Mô hình dữ liệu TỐI THIỂU và CHÍNH XÁC theo yêu cầu Backend
public class OrderItemRequest {

    // ✅ PHẢI LÀ 'product_id' (Backend yêu cầu)
    @SerializedName("product_id")
    private String productId;

    // ⭐ BỔ SUNG QUAN TRỌNG: Backend yêu cầu trường 'color'
    private String color;

    // ✅ PHẢI LÀ 'size' (Backend yêu cầu)
    private String size;

    private int quantity;
    private double price; // Giá tiền tại thời điểm đặt hàng

    // CONSTRUCTOR ĐÃ CẬP NHẬT (Thêm color)
    public OrderItemRequest(String productId, String color, String size, int quantity, double price) {
        this.productId = productId;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
    }

    // GETTERS
    public String getProductId() { return productId; }
    public String getColor() { return color; }
    public String getSize() { return size; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    // SETTERS
    public void setProductId(String productId) { this.productId = productId; }
    public void setColor(String color) { this.color = color; }
    public void setSize(String size) { this.size = size; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
}
package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

public class OrderItem {
    // Đổi kiểu String thành Object để hứng cả chuỗi hoặc Object từ Server
    @SerializedName(value = "productId", alternate = {"product_id", "product"})
    private Object productId;

    private String productName;

    @SerializedName(value = "imageUrl", alternate = {"image", "image_url"})
    private String imageUrl;

    private String size;
    private String color;
    private int quantity;

    @SerializedName(value = "unitPrice", alternate = {"price"})
    private double unitPrice;

    private double subtotal;

    public OrderItem() {}

    // Hàm lấy ID cực kỳ quan trọng để không bị crash
    public String getProductId() {
        if (productId instanceof String) {
            return (String) productId;
        } else if (productId instanceof LinkedTreeMap) {
            // Nếu nó là Object, bốc cái field _id hoặc id ra
            LinkedTreeMap map = (LinkedTreeMap) productId;
            if (map.containsKey("_id")) return map.get("_id").toString();
            if (map.containsKey("id")) return map.get("id").toString();
        }
        return "";
    }

    public String getProductName() { return productName; }
    public String getImageUrl() { return imageUrl; }
    public String getSize() { return size; }
    public String getColor() { return color; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return subtotal; }

    // Setters
    public void setProductId(Object productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setSize(String size) { this.size = size; }
    public void setColor(String color) { this.color = color; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
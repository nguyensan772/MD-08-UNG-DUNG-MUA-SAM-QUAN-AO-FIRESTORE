package com.example.md_08_ungdungfivestore.models;

/**
 * Request body cho cập nhật số lượng sản phẩm trong giỏ
 */
public class UpdateCartRequest {
    private int quantity;

    public UpdateCartRequest() {
    }

    public UpdateCartRequest(int quantity) {
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

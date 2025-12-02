package com.example.md_08_ungdungfivestore.models;

import java.io.Serializable;
import java.util.List;

/**
 * Model cho giỏ hàng
 */
public class Cart implements Serializable {
    private String user_id;
    private List<CartItem> items;
    private double total_amount;
    private String updated_at;

    public Cart() {
    }

    public Cart(String user_id, List<CartItem> items, double total_amount, String updated_at) {
        this.user_id = user_id;
        this.items = items;
        this.total_amount = total_amount;
        this.updated_at = updated_at;
    }

    // Getters and Setters
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    /**
     * Tính tổng số lượng sản phẩm trong giỏ
     */
    public int getTotalItems() {
        if (items == null)
            return 0;
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    /**
     * Kiểm tra giỏ hàng có trống không
     */
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}

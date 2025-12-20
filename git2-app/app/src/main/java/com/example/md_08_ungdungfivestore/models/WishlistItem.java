package com.example.md_08_ungdungfivestore.models;

import java.io.Serializable;

/**
 * Model cho sản phẩm yêu thích (wishlist item)
 */
public class WishlistItem implements Serializable {
    private String _id;
    private String user_id;
    private String product_id;
    private Product product; // Populated product data
    private String created_at;

    public WishlistItem() {
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}

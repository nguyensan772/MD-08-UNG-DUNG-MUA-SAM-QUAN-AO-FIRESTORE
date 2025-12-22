package com.example.md_08_ungdungfivestore.models;

public class LocalCartItem {
    private String productId;
    private String variantId; // ID biến thể (Size/Màu)
    private int quantity;

    public LocalCartItem(String productId, String variantId, int quantity) {
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
    }

    public String getProductId() { return productId; }
    public String getVariantId() { return variantId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

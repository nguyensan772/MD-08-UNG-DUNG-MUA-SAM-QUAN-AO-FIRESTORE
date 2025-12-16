package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderRequest {

    private Address shippingAddress;
    private String paymentMethod;
    private String note;

    // ✅ Dùng tên biến Java 'items' (Backend dùng req.body.items)
    private List<OrderItemRequest> items;

    // ✅ PHẢI LÀ 'shipping_fee' (Backend yêu cầu)
    @SerializedName("shipping_fee")
    private double shippingFee;

    // ✅ PHẢI LÀ 'total_amount' (Backend yêu cầu)
    @SerializedName("total_amount")
    private double totalAmount;

    // CONSTRUCTOR ĐÃ CẬP NHẬT
    public OrderRequest(Address shippingAddress, String paymentMethod, String note, List<OrderItemRequest> items, double shippingFee, double totalAmount) {
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.note = note;
        this.items = items;
        this.shippingFee = shippingFee;
        this.totalAmount = totalAmount;
    }

    // GETTERS CẬP NHẬT
    public Address getShippingAddress() { return shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getNote() { return note; }
    public List<OrderItemRequest> getItems() { return items; }
    public double getShippingFee() { return shippingFee; }
    public double getTotalAmount() { return totalAmount; }
}
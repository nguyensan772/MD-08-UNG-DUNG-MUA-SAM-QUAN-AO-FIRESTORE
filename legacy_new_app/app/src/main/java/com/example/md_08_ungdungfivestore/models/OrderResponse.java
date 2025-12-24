package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderResponse {
    @SerializedName("_id")
    private String orderId;
    private String userId;
    private String status;

    @SerializedName(value = "orderDate", alternate = {"createdAt", "created_at"})
    private String orderDate;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("address")
    private Address shippingAddress;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("shipping_fee")
    private Double shippingFee;

    @SerializedName("items")
    private List<OrderItem> items;

    public OrderResponse() {}

    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public Address getShippingAddress() { return shippingAddress; }
    public List<OrderItem> getItems() { return items; }
    public String getPaymentMethod() { return paymentMethod; }
    public Double getShippingFee() { return shippingFee; }

    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setStatus(String status) { this.status = status; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setShippingFee(Double shippingFee) { this.shippingFee = shippingFee; }
}
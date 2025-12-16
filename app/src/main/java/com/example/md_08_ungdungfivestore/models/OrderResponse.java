package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class OrderResponse {

    @SerializedName("_id")
    private String orderId;

    private String userId;
    private String status;

    // Sửa lỗi ngày đặt
    @SerializedName(value = "orderDate", alternate = {"createdAt", "created_at"})
    private Date orderDate;

    // Sửa lỗi tổng giá
    @SerializedName("total_amount")
    private double totalAmount;

    // Sửa lỗi địa chỉ
    @SerializedName("address")
    private Address shippingAddress;

    // ⭐ SỬA LỖI PHƯƠNG THỨC THANH TOÁN: Ánh xạ từ "payment_method"
    @SerializedName("payment_method")
    private String paymentMethod;

    // ⭐ SỬA LỖI PHÍ VẬN CHUYỂN: Ánh xạ từ "shipping_fee"
    @SerializedName("shipping_fee")
    private Double shippingFee;

    private List<OrderItem> items;

    // Constructor rỗng (cần thiết cho Retrofit/Gson)
    public OrderResponse() {
    }

    // Constructor đầy đủ (Tùy chọn)
    public OrderResponse(String orderId, String userId, String status, Date orderDate, double totalAmount, Address shippingAddress, String paymentMethod, Double shippingFee, List<OrderItem> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.shippingFee = shippingFee;
        this.items = items;
    }

    // --- GETTERS ---

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Double getShippingFee() {
        return shippingFee;
    }

    // --- SETTERS --- (Cần thiết cho quá trình Deserialization)

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setShippingFee(Double shippingFee) {
        this.shippingFee = shippingFee;
    }
}
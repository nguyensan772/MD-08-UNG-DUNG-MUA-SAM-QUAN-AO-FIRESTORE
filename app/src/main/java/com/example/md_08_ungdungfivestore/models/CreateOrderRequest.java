package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Request body cho tạo đơn hàng (COD hoặc VNPay)
 */
public class CreateOrderRequest {
    private List<OrderItemRequest> items;

    @SerializedName("shipping_address")
    private Address address;

    private double shipping_fee;
    private double total_amount;
    private List<String> cart_item_ids;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(List<OrderItemRequest> items, Address address,
            double shipping_fee, double total_amount) {
        this(items, address, shipping_fee, total_amount, null);
    }

    public CreateOrderRequest(List<OrderItemRequest> items, Address address,
            double shipping_fee, double total_amount, List<String> cart_item_ids) {
        this.items = items;
        this.address = address;
        this.shipping_fee = shipping_fee;
        this.total_amount = total_amount;
        this.cart_item_ids = cart_item_ids;
    }

    // Getters and Setters
    public List<String> getCart_item_ids() {
        return cart_item_ids;
    }

    public void setCart_item_ids(List<String> cart_item_ids) {
        this.cart_item_ids = cart_item_ids;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public double getShipping_fee() {
        return shipping_fee;
    }

    public void setShipping_fee(double shipping_fee) {
        this.shipping_fee = shipping_fee;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    /**
     * Inner class cho OrderItemRequest
     */
    public static class OrderItemRequest {
        private String product_id;
        private String name;
        private String image;
        private String size;
        private String color;
        private int quantity;
        private double price;

        public OrderItemRequest() {
        }

        public OrderItemRequest(String product_id, String name, String image,
                String size, String color, int quantity, double price) {
            this.product_id = product_id;
            this.name = name;
            this.image = image;
            this.size = size;
            this.color = color;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters and Setters
        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}

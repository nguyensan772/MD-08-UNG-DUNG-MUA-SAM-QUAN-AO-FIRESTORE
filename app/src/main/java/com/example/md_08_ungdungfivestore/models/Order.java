package com.example.md_08_ungdungfivestore.models;

import java.io.Serializable;
import java.util.List;

/**
 * Model cho đơn hàng
 */
public class Order implements Serializable {
    private String _id;
    private String user_id;
    private List<OrderItem> items;
    private Address address;
    private String payment_method;
    private String payment_status;
    private String status;
    private double shipping_fee;
    private double total_amount;
    private String created_at;
    private String updated_at;

    public Order() {
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    /**
     * Tính tổng số lượng sản phẩm trong đơn hàng
     */
    public int getTotalItems() {
        if (items == null)
            return 0;
        int total = 0;
        for (OrderItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    /**
     * Tính tổng tiền hàng (chưa bao gồm phí ship)
     */
    public double getSubtotal() {
        return total_amount - shipping_fee;
    }

    /**
     * Inner class cho OrderItem
     */
    public static class OrderItem implements Serializable {
        private Product product_id;
        private String name;
        private String image;
        private String size;
        private String color;
        private int quantity;
        private double price;

        public OrderItem() {
        }

        public OrderItem(Product product_id, String name, String image, String size,
                String color, int quantity, double price) {
            this.product_id = product_id;
            this.name = name;
            this.image = image;
            this.size = size;
            this.color = color;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters and Setters
        public Product getProduct_id() {
            return product_id;
        }

        public void setProduct_id(Product product_id) {
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

        public double getSubtotal() {
            return price * quantity;
        }
    }
}

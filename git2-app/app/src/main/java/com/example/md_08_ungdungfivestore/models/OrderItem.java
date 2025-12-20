package com.example.md_08_ungdungfivestore.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String product_id;
    private String name;
    private String image;
    private String size;
    private String color;
    private int quantity;
    private double price;

    public OrderItem() {
    }

    public OrderItem(String product_id, String name, String image, String size,
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

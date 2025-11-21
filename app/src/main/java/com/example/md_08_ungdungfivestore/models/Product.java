package com.example.md_08_ungdungfivestore.models;

import java.util.List;

public class Product {
    private String id; // nếu dùng MongoDB thì có _id
    private String name;
    private String image;
    private List<String> images;

    private List<Description> description;

    private double importPrice; // giá nhập
    private double price;
    private int quantity = 0;
    private int sold = 0;

    private String category;
    private String brand;

    private List<Variation> variations;

    private String status = "Đang bán";
    private boolean isFeatured = false;

    private String createdAt; // timestamps
    private String updatedAt;

    // --- Inner Classes ---
    public static class Description {
        private String field;
        private String value;

        public Description(String field, String value) {
            this.field = field;
            this.value = value;
        }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public static class Variation {
        private String color;
        private String size;
        private int quantity;

        public Variation(String color, String size, int quantity) {
            this.color = color;
            this.size = size;
            this.quantity = quantity;
        }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }


}

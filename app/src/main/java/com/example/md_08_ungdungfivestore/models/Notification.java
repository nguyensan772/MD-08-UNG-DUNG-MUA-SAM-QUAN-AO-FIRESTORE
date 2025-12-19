package com.example.md_08_ungdungfivestore.models;

public class Notification {
    private String _id;
    private String title;
    private String message;
    private String image;
    private String type;
    private String order_id;
    private boolean read;
    private String createdAt;

    public String get_id() { return _id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getImage() { return image; }
    public String getType() { return type; }
    public String getOrder_id() { return order_id; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public String getCreatedAt() { return createdAt; }
}
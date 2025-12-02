package com.example.md_08_ungdungfivestore.models;



public class Comment {
    private String _id;
    private String productId;
    private String userName;
    private String content;
    private float rating; // điểm đánh giá (1-5)
    private String date;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public Comment() {}

    public Comment(String _id, String productId, String userName, String content, float rating, String date) {
        this._id = _id;
        this.productId = productId;
        this.userName = userName;
        this.content = content;
        this.rating = rating;
        this.date = date;
    }

    public Comment(String productId, String userName, String content, float rating) {
        this.productId = productId;
        this.userName = userName;
        this.content = content;
        this.rating = rating;
    }

    // Getters và Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}


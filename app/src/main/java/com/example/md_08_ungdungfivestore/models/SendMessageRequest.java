package com.example.md_08_ungdungfivestore.models;

/**
 * Request body cho gửi tin nhắn
 */
public class SendMessageRequest {
    private String content;
    private String type; // "text" hoặc "image"
    private String image_url;

    public SendMessageRequest() {
    }

    public SendMessageRequest(String content, String type) {
        this.content = content;
        this.type = type;
    }

    public SendMessageRequest(String content, String type, String image_url) {
        this.content = content;
        this.type = type;
        this.image_url = image_url;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}

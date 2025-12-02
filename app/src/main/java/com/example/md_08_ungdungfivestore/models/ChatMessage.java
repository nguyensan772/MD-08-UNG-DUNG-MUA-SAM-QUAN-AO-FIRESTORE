package com.example.md_08_ungdungfivestore.models;

import java.io.Serializable;

/**
 * Model cho tin nhắn chat
 */
public class ChatMessage implements Serializable {
    private String _id;
    private String conversation_id;
    private String sender_id;
    private String content;
    private String type; // "text" hoặc "image"
    private String image_url;
    private String created_at;

    public ChatMessage() {
    }

    public ChatMessage(String content, String type) {
        this.content = content;
        this.type = type;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * Kiểm tra xem tin nhắn có phải là hình ảnh không
     */
    public boolean isImageMessage() {
        return "image".equals(type);
    }
}

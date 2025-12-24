package com.example.md_08_ungdungfivestore.models;

public class ChatMessage {
    private String id;
    private String message;
    private String senderId;
    private String senderType; // "user", "bot", "admin"
    private long timestamp;
    private boolean isFromUser;

    public ChatMessage() {
    }

    public ChatMessage(String message, boolean isFromUser, String senderType) {
        this.message = message;
        this.isFromUser = isFromUser;
        this.senderType = senderType;
        this.timestamp = System.currentTimeMillis();
    }

    public ChatMessage(String id, String message, String senderId, String senderType, long timestamp) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.senderType = senderType;
        this.timestamp = timestamp;
        this.isFromUser = "user".equals(senderType);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFromUser() {
        return isFromUser;
    }

    public void setFromUser(boolean fromUser) {
        isFromUser = fromUser;
    }
}


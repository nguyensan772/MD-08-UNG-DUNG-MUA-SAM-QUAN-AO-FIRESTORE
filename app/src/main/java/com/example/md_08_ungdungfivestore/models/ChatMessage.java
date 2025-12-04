package com.example.md_08_ungdungfivestore.models;

public class ChatMessage {
    public String message;
    public boolean isUser;

    public ChatMessage(String msg, boolean isUser) {
        this.message = msg;
        this.isUser = isUser;
    }
}

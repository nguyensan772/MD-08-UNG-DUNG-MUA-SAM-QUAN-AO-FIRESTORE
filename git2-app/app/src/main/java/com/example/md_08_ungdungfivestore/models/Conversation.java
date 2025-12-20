package com.example.md_08_ungdungfivestore.models;

import java.io.Serializable;
import java.util.List;

/**
 * Model cho cuộc trò chuyện (contact/support)
 */
public class Conversation implements Serializable {
    private String _id;
    private List<String> participants;
    private ChatMessage last_message;
    private String updated_at;

    public Conversation() {
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public ChatMessage getLast_message() {
        return last_message;
    }

    public void setLast_message(ChatMessage last_message) {
        this.last_message = last_message;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}

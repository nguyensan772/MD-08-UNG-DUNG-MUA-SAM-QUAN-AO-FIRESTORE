package com.example.md_08_ungdungfivestore.models;

/**
 * Request body cho tạo cuộc trò chuyện mới
 */
public class CreateConversationRequest {
    private String participant_id;
    private String message;

    public CreateConversationRequest() {
    }

    public CreateConversationRequest(String participant_id, String message) {
        this.participant_id = participant_id;
        this.message = message;
    }

    // Getters and Setters
    public String getParticipant_id() {
        return participant_id;
    }

    public void setParticipant_id(String participant_id) {
        this.participant_id = participant_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

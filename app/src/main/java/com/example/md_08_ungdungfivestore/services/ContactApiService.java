package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.ChatMessage;
import com.example.md_08_ungdungfivestore.models.Conversation;
import com.example.md_08_ungdungfivestore.models.CreateConversationRequest;
import com.example.md_08_ungdungfivestore.models.SendMessageRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * API Service cho Contact/Chat
 */
public interface ContactApiService {

    /**
     * Lấy danh sách cuộc trò chuyện của user
     */
    @GET("api/chat-new/conversations")
    Call<ApiResponse<List<Conversation>>> getConversations();

    /**
     * Tạo cuộc trò chuyện mới với admin (support)
     */
    @POST("api/chat-new/conversations")
    Call<ApiResponse<Conversation>> createConversation(@Body CreateConversationRequest request);

    /**
     * Lấy tin nhắn trong cuộc trò chuyện
     */
    @GET("api/chat-new/conversations/{id}/messages")
    Call<ApiResponse<List<ChatMessage>>> getMessages(@Path("id") String conversationId);

    /**
     * Gửi tin nhắn trong cuộc trò chuyện
     */
    @POST("api/chat-new/conversations/{id}/messages")
    Call<ApiResponse<ChatMessage>> sendMessage(
            @Path("id") String conversationId,
            @Body SendMessageRequest request);
}

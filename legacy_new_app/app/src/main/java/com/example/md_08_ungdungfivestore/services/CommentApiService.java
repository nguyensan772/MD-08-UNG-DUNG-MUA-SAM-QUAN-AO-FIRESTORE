package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.Comment;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentApiService {

    @POST("api/comments")
    Call<Comment> addComment(@Body Comment comment);

    // Thay đổi List<Comment> thành CommentResponse ở đây
    @GET("api/comments/product/{productId}")
    Call<CommentResponse> getCommentsByProduct(@Path("productId") String productId);
}
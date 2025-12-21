package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.Comment;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CommentResponse {
    @SerializedName("items") // ĐỔI TỪ data THÀNH items CHO KHỚP SERVER
    private List<Comment> items;

    @SerializedName("summary")
    private Summary summary;

    public List<Comment> getItems() { return items; }
    public Summary getSummary() { return summary; }

    public static class Summary {
        @SerializedName("ratingAvg")
        public float ratingAvg;
        @SerializedName("ratingCount")
        public int ratingCount;
    }
}
package com.example.md_08_ungdungfivestore.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocalCartClient {
    private static Retrofit retrofit = null;

    // Link ngrok của bạn
    private static final String BASE_URL = "https://bruce-brutish-duane.ngrok-free.dev/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Cấu hình OkHttpClient để vượt qua cảnh báo của ngrok
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("ngrok-skip-browser-warning", "true") // Header quan trọng
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
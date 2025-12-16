package com.example.md_08_ungdungfivestore.services;

import android.content.Context;

import com.example.md_08_ungdungfivestore.utils.AuthManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientCaNhan {

    private static final String BASE_URL = "http://10.0.2.2:5001/"; // Đã xác nhận cổng
    private static Retrofit retrofit = null;

    private static class AuthInterceptor implements Interceptor {
        private final Context context;

        public AuthInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            // ⭐ ĐÃ SỬA: Lấy Token từ AuthManager
            String token = AuthManager.getToken(context);

            Request.Builder builder = originalRequest.newBuilder();

            if (token != null && !token.isEmpty()) {
                // Thêm Header Authorization: Bearer [Token]
                builder.header("Authorization", "Bearer " + token);
            }

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }
    }

    private static Retrofit getClient(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static UserApiService getUserApiService(Context context) {
        return getClient(context).create(UserApiService.class);
    }
}
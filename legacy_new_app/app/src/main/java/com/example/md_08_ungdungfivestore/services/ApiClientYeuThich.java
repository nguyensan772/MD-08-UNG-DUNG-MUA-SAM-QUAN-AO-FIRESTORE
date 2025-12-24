package com.example.md_08_ungdungfivestore.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.md_08_ungdungfivestore.config.AppConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClientYeuThich {
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // Cấu hình Gson để chấp nhận dữ liệu linh hoạt
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();

                            SharedPreferences sp = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                            String token = sp.getString("token", "");

                            Log.d("ApiClientYeuThich", "Requesting URL: " + original.url());
                            Log.d("ApiClientYeuThich", "Auth Header: " + (token.isEmpty() ? "No Token" : "Bearer " + token.substring(0, Math.min(token.length(), 10)) + "..."));

                            Request.Builder builder = original.newBuilder();
                            if (!token.isEmpty()) {
                                builder.header("Authorization", "Bearer " + token);
                            }

                            Request request = builder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static YeuThichService getYeuThichService(Context context) {
        return getClient(context).create(YeuThichService.class);
    }
}
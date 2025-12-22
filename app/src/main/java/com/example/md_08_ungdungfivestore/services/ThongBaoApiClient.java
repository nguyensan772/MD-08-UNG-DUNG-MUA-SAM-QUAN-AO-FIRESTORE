package com.example.md_08_ungdungfivestore.services;

import android.content.Context;
import com.example.md_08_ungdungfivestore.utils.AuthManager;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ThongBaoApiClient {
    private static final String BASE_URL = "https://bruce-brutish-duane.ngrok-free.dev/";
    private static ApiThongBaoService apiService;

    public static ApiThongBaoService getClient(Context context) {
        if (apiService == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        // Lấy token từ AuthManager
                        String token = AuthManager.getToken(context);

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Bearer " + (token != null ? token : ""))
                                .method(original.method(), original.body());

                        return chain.proceed(requestBuilder.build());
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiThongBaoService.class);
        }
        return apiService;
    }
}
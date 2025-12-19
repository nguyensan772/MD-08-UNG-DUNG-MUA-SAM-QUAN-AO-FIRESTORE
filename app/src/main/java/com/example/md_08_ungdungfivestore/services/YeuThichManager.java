package com.example.md_08_ungdungfivestore.services;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YeuThichManager {

    private static YeuThichService service;

    public static YeuThichService getInstance(Context context) {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiClient.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(YeuThichService.class);
        }
        return service;
    }
}

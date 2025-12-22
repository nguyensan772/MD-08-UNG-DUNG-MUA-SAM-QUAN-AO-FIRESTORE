package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.config.AppConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchApiClient {

    private static SearchApiClient instance;
    private static Retrofit retrofit;

    private SearchApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized SearchApiClient getInstance() {
        if (instance == null) {
            instance = new SearchApiClient();
        }
        return instance;
    }

    public SearchApi getSearchApi() {
        return retrofit.create(SearchApi.class);
    }
}
package com.floodrescue.mobile.core.network;

import android.content.Context;

import com.floodrescue.mobile.BuildConfig;
import com.floodrescue.mobile.data.local.SessionManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private ApiClient() {
    }

    public static ApiService create(Context context) {
        SessionManager sessionManager = new SessionManager(context.getApplicationContext());

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor authInterceptor = chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .header("Accept", "application/json");

            String token = sessionManager.getToken();
            if (token != null && !token.trim().isEmpty()) {
                builder.header("Authorization", "Bearer " + token);
            }

            return chain.proceed(builder.build());
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }
}

package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.response.PublicContentPageResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicContentRepository {

    private final ApiService apiService;

    public PublicContentRepository(Context context) {
        apiService = ApiClient.create(context);
    }

    public void getContentPage(String pageKey, RepositoryCallback<PublicContentPageResponse> callback) {
        apiService.getPublicContentPage(pageKey).enqueue(new Callback<PublicContentPageResponse>() {
            @Override
            public void onResponse(Call<PublicContentPageResponse> call, Response<PublicContentPageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError("Khong tai duoc noi dung trang cong khai.");
            }

            @Override
            public void onFailure(Call<PublicContentPageResponse> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null
                        ? "Khong the ket noi toi server."
                        : throwable.getMessage());
            }
        });
    }

    public void getRuntimeSettings(RepositoryCallback<Map<String, String>> callback) {
        apiService.getPublicRuntimeSettings().enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onSuccess(new HashMap<>());
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable throwable) {
                callback.onSuccess(new HashMap<>());
            }
        });
    }
}

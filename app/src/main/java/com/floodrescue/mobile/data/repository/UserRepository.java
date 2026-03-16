package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.response.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService apiService;

    public UserRepository(Context context) {
        apiService = ApiClient.create(context);
    }

    public void getMyProfile(RepositoryCallback<UserProfileResponse> callback) {
        apiService.getMyProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError("Không tải được hồ sơ từ backend.");
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }
}

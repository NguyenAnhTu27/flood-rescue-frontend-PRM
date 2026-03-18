package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.request.LoginRequest;
import com.floodrescue.mobile.data.model.request.RegisterCitizenRequest;
import com.floodrescue.mobile.data.model.response.ApiMessageResponse;
import com.floodrescue.mobile.data.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository(Context context) {
        apiService = ApiClient.create(context);
    }

    public void login(String identifier, String password, RepositoryCallback<LoginResponse> callback) {
        apiService.login(new LoginRequest(identifier, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError("Đăng nhập thất bại. Vui lòng kiểm tra tài khoản hoặc backend.");
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    public void registerCitizen(String fullName, String phone, String email, String password, RepositoryCallback<ApiMessageResponse> callback) {
        apiService.registerCitizen(new RegisterCitizenRequest(fullName, phone, email, password)).enqueue(new Callback<ApiMessageResponse>() {
            @Override
            public void onResponse(Call<ApiMessageResponse> call, Response<ApiMessageResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError("Đăng ký thất bại. Vui lòng kiểm tra lại dữ liệu.");
            }

            @Override
            public void onFailure(Call<ApiMessageResponse> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }
}

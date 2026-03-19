package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.request.CitizenFeedbackRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitizenFeedbackRepository {

    private final ApiService apiService;

    public CitizenFeedbackRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void submitFeedback(int rating, String feedbackContent, boolean rescued, boolean reliefReceived, RepositoryCallback<String> callback) {
        apiService.submitCitizenFeedback(new CitizenFeedbackRequest(
                rating,
                feedbackContent,
                rescued,
                reliefReceived
        )).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    String message = parseApiMessage(response, "Gửi phản hồi thành công.");
                    callback.onSuccess(message);
                    return;
                }
                callback.onError(parseApiMessage(response, "Không gửi được phản hồi. Vui lòng thử lại."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    private String parseApiMessage(Response<?> response, String fallback) {
        try {
            if (response != null && response.isSuccessful() && response.body() instanceof JsonElement) {
                JsonElement body = (JsonElement) response.body();
                if (body.isJsonObject()) {
                    JsonObject obj = body.getAsJsonObject();
                    if (obj.has("message") && !obj.get("message").isJsonNull()) {
                        return obj.get("message").getAsString();
                    }
                }
            }
            if (response != null && response.errorBody() != null) {
                String raw = response.errorBody().string();
                JSONObject json = new JSONObject(raw);
                if (json.has("message")) {
                    return json.getString("message");
                }
            }
        } catch (Exception ignored) {
            // Ignore parsing errors, use fallback.
        }
        return fallback;
    }
}

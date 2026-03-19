package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.request.RescuerTeamLocationUpdateRequest;
import com.floodrescue.mobile.data.model.ui.RescuerTeamLocationState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RescuerTeamLocationRepository {

    private final ApiService apiService;

    public RescuerTeamLocationRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadState(RepositoryCallback<RescuerTeamLocationState> callback) {
        apiService.getRescuerDashboard().enqueue(new StateCallback(callback, "Không tải được dữ liệu vị trí đội."));
    }

    public void updateLocation(double latitude, double longitude, String locationText, RepositoryCallback<RescuerTeamLocationState> callback) {
        apiService.updateRescuerTeamLocation(new RescuerTeamLocationUpdateRequest(latitude, longitude, locationText))
                .enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (!response.isSuccessful()) {
                            callback.onError(parseApiMessage(response, "Không cập nhật được vị trí đội."));
                            return;
                        }
                        loadState(callback);
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable throwable) {
                        callback.onError(throwable == null || throwable.getMessage() == null
                                ? "Không thể kết nối tới server."
                                : throwable.getMessage());
                    }
                });
    }

    public void returnAssets(RepositoryCallback<RescuerTeamLocationState> callback) {
        apiService.returnRescuerTeamAssets().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful()) {
                    callback.onError(parseApiMessage(response, "Không trả được tài sản về trạng thái sẵn sàng."));
                    return;
                }
                loadState(callback);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    private class StateCallback implements Callback<JsonElement> {
        private final RepositoryCallback<RescuerTeamLocationState> callback;
        private final String fallback;

        StateCallback(RepositoryCallback<RescuerTeamLocationState> callback, String fallback) {
            this.callback = callback;
            this.fallback = fallback;
        }

        @Override
        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
            if (response.isSuccessful() && response.body() != null) {
                callback.onSuccess(parseState(response.body()));
                return;
            }
            callback.onError(parseApiMessage(response, fallback));
        }

        @Override
        public void onFailure(Call<JsonElement> call, Throwable throwable) {
            callback.onError(throwable == null || throwable.getMessage() == null
                    ? "Không thể kết nối tới server."
                    : throwable.getMessage());
        }
    }

    private RescuerTeamLocationState parseState(JsonElement root) {
        JsonObject object = readObject(root);
        if (object == null) {
            return new RescuerTeamLocationState(-1L, "", "READY", null, null, "", "", new ArrayList<>());
        }

        long activeGroups = readLong(object, "activeTaskGroups");
        long activeAssignments = readLong(object, "activeAssignments");
        String teamStatus = activeGroups > 0 || activeAssignments > 0 ? "ACTIVE" : "READY";

        return new RescuerTeamLocationState(
                readLong(object, "teamId"),
                readString(object, "teamName", "Đội cứu hộ"),
                teamStatus,
                readDoubleObject(object, "teamLatitude"),
                readDoubleObject(object, "teamLongitude"),
                readString(object, "teamLocationText", ""),
                readString(object, "teamLocationUpdatedAt", ""),
                parseAssets(readArray(object, "heldAssets"))
        );
    }

    private List<RescuerTeamLocationState.AssetItem> parseAssets(JsonArray array) {
        List<RescuerTeamLocationState.AssetItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new RescuerTeamLocationState.AssetItem(
                    readLong(object, "id"),
                    readString(object, "code", ""),
                    readString(object, "name", "Tài sản đội"),
                    readString(object, "assetType", ""),
                    readString(object, "status", "")
            ));
        }
        return items;
    }

    private JsonObject readObject(JsonElement element) {
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    private JsonArray readArray(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull() || !object.get(key).isJsonArray()) {
            return null;
        }
        return object.getAsJsonArray(key);
    }

    private String readString(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            String value = object.get(key).getAsString();
            return value == null || value.trim().isEmpty() ? fallback : value.trim();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private long readLong(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return 0L;
        }
        try {
            return object.get(key).getAsLong();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private Double readDoubleObject(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return null;
        }
        try {
            return object.get(key).getAsDouble();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String parseApiMessage(Response<?> response, String fallback) {
        try {
            if (response != null && response.errorBody() != null) {
                String raw = response.errorBody().string();
                JSONObject json = new JSONObject(raw);
                if (json.has("message")) {
                    return json.getString("message");
                }
            }
        } catch (Exception ignored) {
            // Use fallback.
        }
        return fallback;
    }
}

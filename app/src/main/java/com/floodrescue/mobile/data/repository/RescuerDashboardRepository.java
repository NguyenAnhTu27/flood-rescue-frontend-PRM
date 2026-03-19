package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.RescuerDashboardState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RescuerDashboardRepository {

    private final ApiService apiService;

    public RescuerDashboardRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadDashboard(RepositoryCallback<RescuerDashboardState> callback) {
        apiService.getRescuerDashboard().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(parseDashboard(response.body()));
                    return;
                }
                callback.onError("Không tải được dashboard đội cứu hộ.");
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    private RescuerDashboardState parseDashboard(JsonElement body) {
        JsonObject object = readObject(body);
        if (object == null) {
            return new RescuerDashboardState(-1L, "", "", "", 0L, 0L, new ArrayList<>(), new ArrayList<>());
        }

        return new RescuerDashboardState(
                readLong(object, "teamId"),
                readString(object, "teamName", "Đội cứu hộ"),
                readString(object, "teamLocationText", ""),
                readString(object, "teamLocationUpdatedAt", ""),
                readLong(object, "activeTaskGroups"),
                readLong(object, "activeAssignments"),
                parseAssets(readArray(object, "heldAssets")),
                parseTaskGroups(readArray(object, "taskGroups"))
        );
    }

    private List<RescuerDashboardState.AssetItem> parseAssets(JsonArray array) {
        List<RescuerDashboardState.AssetItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new RescuerDashboardState.AssetItem(
                    readLong(object, "id"),
                    readString(object, "code", ""),
                    readString(object, "name", "Thiết bị"),
                    readString(object, "assetType", ""),
                    readString(object, "status", "")
            ));
        }
        return items;
    }

    private List<RescuerDashboardState.TaskGroupItem> parseTaskGroups(JsonArray array) {
        List<RescuerDashboardState.TaskGroupItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new RescuerDashboardState.TaskGroupItem(
                    readLong(object, "id"),
                    readString(object, "code", ""),
                    readString(object, "status", ""),
                    readString(object, "note", ""),
                    readString(object, "updatedAt", readString(object, "createdAt", ""))
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
}

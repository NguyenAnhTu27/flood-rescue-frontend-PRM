package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.AdminDashboardState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardRepository {

    private final ApiService apiService;

    public AdminDashboardRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadDashboard(RepositoryCallback<AdminDashboardState> callback) {
        apiService.getAdminStats().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được thống kê hệ thống."));
                    return;
                }
                JsonObject stats = readObject(response.body());
                if (stats == null) {
                    callback.onError("Dữ liệu thống kê hệ thống không hợp lệ.");
                    return;
                }
                loadPermissions(stats, callback);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(defaultFailureMessage(throwable));
            }
        });
    }

    private void loadPermissions(JsonObject stats, RepositoryCallback<AdminDashboardState> callback) {
        apiService.getAdminPermissions().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonObject permissions = response.isSuccessful() && response.body() != null ? readObject(response.body()) : null;
                loadSettings(stats, permissions, callback);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                loadSettings(stats, null, callback);
            }
        });
    }

    private void loadSettings(JsonObject stats, JsonObject permissions, RepositoryCallback<AdminDashboardState> callback) {
        apiService.getAdminSystemSettings().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonObject settings = response.isSuccessful() && response.body() != null ? readObject(response.body()) : null;
                loadAuditLogs(stats, permissions, settings, callback);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                loadAuditLogs(stats, permissions, null, callback);
            }
        });
    }

    private void loadAuditLogs(
            JsonObject stats,
            JsonObject permissions,
            JsonObject settings,
            RepositoryCallback<AdminDashboardState> callback
    ) {
        apiService.getAdminAuditLogs(0, 5, null, null).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonObject logs = response.isSuccessful() && response.body() != null ? readObject(response.body()) : null;
                callback.onSuccess(buildState(stats, permissions, settings, logs));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onSuccess(buildState(stats, permissions, settings, null));
            }
        });
    }

    private AdminDashboardState buildState(JsonObject stats, JsonObject permissions, JsonObject settings, JsonObject logs) {
        return new AdminDashboardState(
                readLong(stats, "totalUsers"),
                readLong(stats, "activeUsers"),
                readLong(stats, "lockedUsers"),
                readArray(permissions, "roles") == null ? 0 : readArray(permissions, "roles").size(),
                settings != null && settings.has("values") && settings.get("values").isJsonObject()
                        ? settings.getAsJsonObject("values").entrySet().size()
                        : 0,
                parseRecentActivities(logs)
        );
    }

    private List<AdminDashboardState.RecentActivityItem> parseRecentActivities(JsonObject root) {
        List<AdminDashboardState.RecentActivityItem> items = new ArrayList<>();
        JsonArray array = readArray(root, "items");
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new AdminDashboardState.RecentActivityItem(
                    readLong(object, "id"),
                    readString(object, "action", ""),
                    readString(object, "actor", "Hệ thống"),
                    readString(object, "target", ""),
                    readString(object, "level", ""),
                    readString(object, "detail", ""),
                    readString(object, "createdAt", "")
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
        try {
            if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
                return fallback;
            }
            String value = object.get(key).getAsString();
            return value == null || value.trim().isEmpty() ? fallback : value.trim();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private long readLong(JsonObject object, String key) {
        try {
            if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
                return 0L;
            }
            return object.get(key).getAsLong();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private String defaultFailureMessage(Throwable throwable) {
        return throwable == null || throwable.getMessage() == null
                ? "Không thể kết nối tới server."
                : throwable.getMessage();
    }

    private String parseApiMessage(Response<?> response, String fallback) {
        try {
            if (response != null && response.errorBody() != null) {
                String raw = response.errorBody().string();
                JSONObject jsonObject = new JSONObject(raw);
                String message = jsonObject.optString("message");
                if (!message.isEmpty()) {
                    return message;
                }
            }
        } catch (Exception ignored) {
            // Use fallback.
        }
        return fallback;
    }
}

package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.CoordinatorRequestItem;
import com.floodrescue.mobile.data.model.ui.CoordinatorTaskGroupItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorDashboardRepository {

    private final ApiService apiService;

    public CoordinatorDashboardRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadUrgentRequests(RepositoryCallback<List<CoordinatorRequestItem>> callback) {
        apiService.getCoordinatorRescueRequests(null, null, null, 0, 10).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(parseRequests(response.body()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Không tải được danh sách yêu cầu."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    public void loadTaskGroups(RepositoryCallback<List<CoordinatorTaskGroupItem>> callback) {
        apiService.getCoordinatorTaskGroups(null, 0, 10).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(parseTaskGroups(response.body()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Không tải được danh sách nhóm nhiệm vụ."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    private List<CoordinatorTaskGroupItem> parseTaskGroups(JsonElement root) {
        List<CoordinatorTaskGroupItem> items = new ArrayList<>();
        JsonArray content = null;
        try {
            if (root.isJsonObject()) {
                JsonObject obj = root.getAsJsonObject();
                if (obj.has("content") && obj.get("content").isJsonArray()) {
                    content = obj.get("content").getAsJsonArray();
                } else if (obj.has("data") && obj.get("data").isJsonArray()) {
                    content = obj.get("data").getAsJsonArray();
                }
            } else if (root.isJsonArray()) {
                content = root.getAsJsonArray();
            }

            if (content == null) return items;
            for (JsonElement element : content) {
                if (!element.isJsonObject()) continue;
                JsonObject o = element.getAsJsonObject();
                long id = getLong(o, "id");
                String name = getString(o, "name", "Nhiệm vụ");
                String status = getString(o, "status", "ONLINE");
                String desc = getString(o, "description", "");
                items.add(new CoordinatorTaskGroupItem(id, name, status, desc));
            }
        } catch (Exception ignored) {
        }
        return items;
    }

    private List<CoordinatorRequestItem> parseRequests(JsonElement root) {
        List<CoordinatorRequestItem> items = new ArrayList<>();
        try {
            JsonArray content = null;
            if (root.isJsonObject()) {
                JsonObject obj = root.getAsJsonObject();
                if (obj.has("content") && obj.get("content").isJsonArray()) {
                    content = obj.get("content").getAsJsonArray();
                } else if (obj.has("data") && obj.get("data").isJsonArray()) {
                    content = obj.get("data").getAsJsonArray();
                }
            } else if (root.isJsonArray()) {
                content = root.getAsJsonArray();
            }

            if (content == null) return items;

            for (JsonElement element : content) {
                if (!element.isJsonObject()) continue;
                JsonObject o = element.getAsJsonObject();
                long id = getLong(o, "id");
                String code = getString(o, "code");
                String title = getString(o, "title", getString(o, "description"));
                String priority = getString(o, "priority", "MEDIUM");
                int people = (int) getLong(o, "peopleCount");
                if (people <= 0) people = (int) getLong(o, "affectedPeopleCount");
                String address = getString(o, "address");
                String updated = getString(o, "updatedAt", getString(o, "createdAt"));
                String status = getString(o, "status", "PENDING");
                items.add(new CoordinatorRequestItem(id, code, title, priority, people, address, updated, status));
            }
        } catch (Exception ignored) {
            // ignore parse error, return what we have
        }
        return items;
    }

    private long getLong(JsonObject o, String key) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsLong() : 0L;
    }

    private String getString(JsonObject o, String key) {
        return getString(o, key, "");
    }

    private String getString(JsonObject o, String key, String fallback) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsString() : fallback;
    }

    private String parseApiMessage(Response<?> response, String fallback) {
        try {
            if (response != null && response.errorBody() != null) {
                String raw = response.errorBody().string();
                if (raw != null && raw.contains("message")) {
                    int idx = raw.indexOf("message");
                    return raw.substring(idx);
                }
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }
}

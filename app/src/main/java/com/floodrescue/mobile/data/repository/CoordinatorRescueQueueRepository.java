package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.CoordinatorQueueItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorRescueQueueRepository {

    private final ApiService apiService;

    public CoordinatorRescueQueueRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void getQueue(String status, String keyword, RepositoryCallback<List<CoordinatorQueueItem>> callback) {
        apiService.getCoordinatorRescueRequests(
                status,
                null,
                keyword,
                0,
                50
        ).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(parseQueue(response.body()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Khong tai duoc hang cho cuu ho."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Khong the ket noi toi server." : throwable.getMessage());
            }
        });
    }

    private List<CoordinatorQueueItem> parseQueue(JsonElement root) {
        List<CoordinatorQueueItem> items = new ArrayList<>();
        JsonArray array = extractArray(root);
        if (array == null) {
            return items;
        }

        for (JsonElement element : array) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject object = element.getAsJsonObject();
            JsonObject citizen = readObject(object, "citizen");
            JsonObject requester = readObject(object, "requester");
            JsonObject taskGroup = readObject(object, "taskGroup");

            items.add(new CoordinatorQueueItem(
                    readLong(object, "id"),
                    readString(object, "code", readString(object, "requestCode", "RESC-0000")),
                    firstNonBlank(
                            readString(citizen, "fullName"),
                            readString(citizen, "name"),
                            readString(requester, "fullName"),
                            readString(requester, "name"),
                            readString(object, "citizenName"),
                            readString(object, "requesterName"),
                            "Nguoi dan"
                    ),
                    firstNonBlank(
                            readString(citizen, "phone"),
                            readString(citizen, "phoneNumber"),
                            readString(requester, "phone"),
                            readString(requester, "phoneNumber"),
                            readString(object, "phone"),
                            readString(object, "phoneNumber"),
                            "--"
                    ),
                    readInt(
                            object,
                            "affectedPeopleCount",
                            readInt(object, "peopleCount", readInt(object, "rescuePeopleCount", 0))
                    ),
                    firstNonBlank(
                            readString(object, "address"),
                            readString(object, "addressText"),
                            readString(object, "locationDescription"),
                            readString(object, "location"),
                            "Chua co dia chi"
                    ),
                    firstNonBlank(readString(object, "priority"), "MEDIUM"),
                    firstNonBlank(readString(object, "status"), "PENDING"),
                    readBoolean(
                            object,
                            "locationVerified",
                            readBoolean(object, "isLocationVerified", readBoolean(object, "verifiedLocation", false))
                    ),
                    firstNonBlank(
                            readString(taskGroup, "name"),
                            readString(object, "assignedTeamName"),
                            readString(object, "teamName"),
                            ""
                    ),
                    firstNonBlank(readString(object, "updatedAt"), readString(object, "createdAt"), "")
            ));
        }
        return items;
    }

    private JsonArray extractArray(JsonElement root) {
        if (root == null) {
            return null;
        }
        if (root.isJsonArray()) {
            return root.getAsJsonArray();
        }
        if (!root.isJsonObject()) {
            return null;
        }
        JsonObject object = root.getAsJsonObject();
        if (object.has("content") && object.get("content").isJsonArray()) {
            return object.getAsJsonArray("content");
        }
        if (object.has("data") && object.get("data").isJsonArray()) {
            return object.getAsJsonArray("data");
        }
        return null;
    }

    private JsonObject readObject(JsonObject source, String key) {
        if (source == null || !source.has(key) || source.get(key).isJsonNull() || !source.get(key).isJsonObject()) {
            return null;
        }
        return source.getAsJsonObject(key);
    }

    private String readString(JsonObject source, String key) {
        return readString(source, key, "");
    }

    private String readString(JsonObject source, String key, String fallback) {
        if (source == null || !source.has(key) || source.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return source.get(key).getAsString();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private long readLong(JsonObject source, String key) {
        if (source == null || !source.has(key) || source.get(key).isJsonNull()) {
            return -1L;
        }
        try {
            return source.get(key).getAsLong();
        } catch (Exception ignored) {
            return -1L;
        }
    }

    private int readInt(JsonObject source, String key, int fallback) {
        if (source == null || !source.has(key) || source.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return source.get(key).getAsInt();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private boolean readBoolean(JsonObject source, String key, boolean fallback) {
        if (source == null || !source.has(key) || source.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return source.get(key).getAsBoolean();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return "";
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

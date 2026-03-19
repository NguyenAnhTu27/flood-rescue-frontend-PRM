package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.request.RescuerReliefStatusUpdateRequest;
import com.floodrescue.mobile.data.model.ui.RescuerReliefDetailState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RescuerReliefDetailRepository {

    private final ApiService apiService;

    public RescuerReliefDetailRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadLatestReliefId(RepositoryCallback<Long> callback) {
        apiService.getRescuerReliefRequests(0, 1).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tìm thấy yêu cầu cứu trợ được giao."));
                    return;
                }
                JsonArray array = extractArray(response.body());
                if (array == null || array.size() == 0) {
                    callback.onError("Đội hiện chưa có yêu cầu cứu trợ được giao.");
                    return;
                }
                JsonObject first = readObject(array.get(0));
                long id = readLong(first, "id");
                if (id <= 0L) {
                    callback.onError("Không đọc được mã giao cứu trợ.");
                    return;
                }
                callback.onSuccess(id);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    public void loadDetail(long reliefId, RepositoryCallback<RescuerReliefDetailState> callback) {
        apiService.getReliefRequest(reliefId).enqueue(new DetailCallback(callback, "Không tải được chi tiết giao cứu trợ."));
    }

    public void updateStatus(long reliefId, String status, String note, RepositoryCallback<RescuerReliefDetailState> callback) {
        apiService.updateRescuerReliefStatus(reliefId, new RescuerReliefStatusUpdateRequest(status, note))
                .enqueue(new DetailCallback(callback, "Không cập nhật được trạng thái giao cứu trợ."));
    }

    private class DetailCallback implements Callback<JsonElement> {
        private final RepositoryCallback<RescuerReliefDetailState> callback;
        private final String fallback;

        DetailCallback(RepositoryCallback<RescuerReliefDetailState> callback, String fallback) {
            this.callback = callback;
            this.fallback = fallback;
        }

        @Override
        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
            if (response.isSuccessful() && response.body() != null) {
                callback.onSuccess(parseDetail(response.body()));
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

    private RescuerReliefDetailState parseDetail(JsonElement root) {
        JsonObject object = readObject(root);
        if (object == null) {
            return new RescuerReliefDetailState(
                    -1L, "REL", "", "", "", "", "", -1L,
                    "", null, null, "", "", "", -1L, "", "", new ArrayList<>()
            );
        }

        return new RescuerReliefDetailState(
                readLong(object, "id"),
                readString(object, "code", "REL"),
                readString(object, "status", ""),
                readString(object, "deliveryStatus", ""),
                readString(object, "targetArea", ""),
                readString(object, "createdByName", ""),
                readString(object, "createdByPhone", ""),
                readLong(object, "rescueRequestId"),
                firstNonBlank(
                        readString(object, "citizenAddressText", ""),
                        readString(object, "targetArea", ""),
                        "Chưa có địa chỉ giao"
                ),
                readDoubleObject(object, "citizenLatitude"),
                readDoubleObject(object, "citizenLongitude"),
                readString(object, "citizenLocationDescription", ""),
                readString(object, "note", ""),
                readString(object, "deliveryNote", ""),
                readLong(object, "assignedIssueId"),
                readString(object, "createdAt", ""),
                readString(object, "updatedAt", ""),
                parseLines(readArray(object, "lines"))
        );
    }

    private List<RescuerReliefDetailState.LineItem> parseLines(JsonArray array) {
        List<RescuerReliefDetailState.LineItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new RescuerReliefDetailState.LineItem(
                    readLong(object, "id"),
                    readString(object, "itemCode", ""),
                    readString(object, "itemName", "Nhu yếu phẩm"),
                    stringValue(object, "qty", "0"),
                    readString(object, "unit", "")
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
        JsonObject object = readObject(root);
        return object == null ? null : readArray(object, "content");
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

    private String stringValue(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return object.get(key).getAsString();
        } catch (Exception ignored) {
            try {
                return object.get(key).getAsNumber().toString();
            } catch (Exception innerIgnored) {
                return fallback;
            }
        }
    }

    private long readLong(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return -1L;
        }
        try {
            return object.get(key).getAsLong();
        } catch (Exception ignored) {
            return -1L;
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

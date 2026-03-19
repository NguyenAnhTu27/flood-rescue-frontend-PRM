package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.RescuerTaskItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RescuerTaskListRepository {

    private final ApiService apiService;

    public RescuerTaskListRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadTasks(RepositoryCallback<List<RescuerTaskItem>> callback) {
        apiService.getRescuerTaskGroups(null, 0, 50).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được nhiệm vụ cứu hộ."));
                    return;
                }

                List<Long> groupIds = extractGroupIds(response.body());
                if (groupIds.isEmpty()) {
                    callback.onSuccess(new ArrayList<>());
                    return;
                }

                Map<Long, RescuerTaskItem> merged = new LinkedHashMap<>();
                AtomicInteger remaining = new AtomicInteger(groupIds.size());
                String[] firstError = new String[1];

                for (Long groupId : groupIds) {
                    apiService.getRescuerTaskGroup(groupId).enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> detailCall, Response<JsonElement> detailResponse) {
                            if (detailResponse.isSuccessful() && detailResponse.body() != null) {
                                List<RescuerTaskItem> items = parseGroupDetail(detailResponse.body());
                                synchronized (merged) {
                                    for (RescuerTaskItem item : items) {
                                        merged.put(item.getRequestId(), item);
                                    }
                                }
                            } else if (firstError[0] == null) {
                                firstError[0] = parseApiMessage(detailResponse, "Không tải đủ dữ liệu nhiệm vụ.");
                            }
                            finishBatch(callback, merged, remaining, firstError[0]);
                        }

                        @Override
                        public void onFailure(Call<JsonElement> detailCall, Throwable throwable) {
                            if (firstError[0] == null) {
                                firstError[0] = throwable == null || throwable.getMessage() == null
                                        ? "Không thể kết nối tới server."
                                        : throwable.getMessage();
                            }
                            finishBatch(callback, merged, remaining, firstError[0]);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    private void finishBatch(
            RepositoryCallback<List<RescuerTaskItem>> callback,
            Map<Long, RescuerTaskItem> merged,
            AtomicInteger remaining,
            String firstError
    ) {
        if (remaining.decrementAndGet() != 0) {
            return;
        }

        List<RescuerTaskItem> items;
        synchronized (merged) {
            items = new ArrayList<>(merged.values());
        }
        items.sort(Comparator.comparing(RescuerTaskItem::getUpdatedAt, Comparator.nullsLast(String::compareTo)).reversed());

        if (items.isEmpty() && firstError != null) {
            callback.onError(firstError);
            return;
        }
        callback.onSuccess(items);
    }

    private List<Long> extractGroupIds(JsonElement root) {
        List<Long> ids = new ArrayList<>();
        JsonArray array = extractArray(root);
        if (array == null) {
            return ids;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            long id = readLong(object, "id");
            if (id > 0L) {
                ids.add(id);
            }
        }
        return ids;
    }

    private List<RescuerTaskItem> parseGroupDetail(JsonElement root) {
        List<RescuerTaskItem> items = new ArrayList<>();
        JsonObject object = readObject(root);
        if (object == null) {
            return items;
        }

        long groupId = readLong(object, "id");
        String groupCode = readString(object, "code", "");
        JsonArray requests = readArray(object, "requests");
        if (requests == null) {
            return items;
        }

        for (JsonElement element : requests) {
            JsonObject request = readObject(element);
            if (request == null) {
                continue;
            }
            items.add(new RescuerTaskItem(
                    readLong(request, "id"),
                    groupId,
                    readString(request, "code", "RESC"),
                    groupCode,
                    readString(request, "citizenName", "Người dân"),
                    readString(request, "citizenPhone", "--"),
                    firstNonBlank(
                            readString(request, "addressText", ""),
                            readString(request, "locationDescription", ""),
                            "Chưa có địa chỉ"
                    ),
                    readString(request, "description", ""),
                    readString(request, "priority", "MEDIUM"),
                    readString(request, "status", ""),
                    firstNonBlank(
                            readString(request, "updatedAt", ""),
                            readString(request, "createdAt", "")
                    ),
                    readInt(request, "affectedPeopleCount", 0),
                    readBoolean(request, "locationVerified", false),
                    readBoolean(request, "emergency", false)
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
        if (object == null) {
            return null;
        }
        return readArray(object, "content");
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
            return -1L;
        }
        try {
            return object.get(key).getAsLong();
        } catch (Exception ignored) {
            return -1L;
        }
    }

    private int readInt(JsonObject object, String key, int fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return object.get(key).getAsInt();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private boolean readBoolean(JsonObject object, String key, boolean fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return object.get(key).getAsBoolean();
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
            // Use fallback below.
        }
        return fallback;
    }
}

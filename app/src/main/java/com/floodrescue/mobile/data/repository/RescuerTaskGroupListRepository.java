package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.RescuerTaskGroupListItem;
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

public class RescuerTaskGroupListRepository {

    private final ApiService apiService;

    public RescuerTaskGroupListRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadTaskGroups(RepositoryCallback<List<RescuerTaskGroupListItem>> callback) {
        apiService.getRescuerTaskGroups(null, 0, 50).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được nhóm nhiệm vụ của đội."));
                    return;
                }

                List<GroupSummary> summaries = parseSummaries(response.body());
                if (summaries.isEmpty()) {
                    callback.onSuccess(new ArrayList<>());
                    return;
                }

                Map<Long, RescuerTaskGroupListItem> merged = new LinkedHashMap<>();
                AtomicInteger remaining = new AtomicInteger(summaries.size());
                String[] firstError = new String[1];

                for (GroupSummary summary : summaries) {
                    apiService.getRescuerTaskGroup(summary.id).enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> detailCall, Response<JsonElement> detailResponse) {
                            if (detailResponse.isSuccessful() && detailResponse.body() != null) {
                                synchronized (merged) {
                                    merged.put(summary.id, parseItem(summary, detailResponse.body()));
                                }
                            } else {
                                synchronized (merged) {
                                    merged.put(summary.id, fallbackItem(summary));
                                }
                                if (firstError[0] == null) {
                                    firstError[0] = parseApiMessage(detailResponse, "Không tải đủ dữ liệu nhóm nhiệm vụ.");
                                }
                            }
                            finish(callback, merged, remaining, firstError[0]);
                        }

                        @Override
                        public void onFailure(Call<JsonElement> detailCall, Throwable throwable) {
                            synchronized (merged) {
                                merged.put(summary.id, fallbackItem(summary));
                            }
                            if (firstError[0] == null) {
                                firstError[0] = throwable == null || throwable.getMessage() == null
                                        ? "Không thể kết nối tới server."
                                        : throwable.getMessage();
                            }
                            finish(callback, merged, remaining, firstError[0]);
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

    private void finish(
            RepositoryCallback<List<RescuerTaskGroupListItem>> callback,
            Map<Long, RescuerTaskGroupListItem> merged,
            AtomicInteger remaining,
            String firstError
    ) {
        if (remaining.decrementAndGet() != 0) {
            return;
        }

        List<RescuerTaskGroupListItem> items;
        synchronized (merged) {
            items = new ArrayList<>(merged.values());
        }
        items.sort(Comparator.comparing(RescuerTaskGroupListItem::getUpdatedAt, Comparator.nullsLast(String::compareTo)).reversed());
        if (items.isEmpty() && firstError != null) {
            callback.onError(firstError);
            return;
        }
        callback.onSuccess(items);
    }

    private List<GroupSummary> parseSummaries(JsonElement root) {
        List<GroupSummary> items = new ArrayList<>();
        JsonArray array = extractArray(root);
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            long id = readLong(object, "id");
            if (id <= 0L) {
                continue;
            }
            items.add(new GroupSummary(
                    id,
                    readString(object, "code", "TG"),
                    readString(object, "status", "NEW"),
                    readString(object, "assignedTeamName", "Đội cứu hộ"),
                    readString(object, "note", ""),
                    readString(object, "createdAt", ""),
                    readString(object, "updatedAt", readString(object, "createdAt", ""))
            ));
        }
        return items;
    }

    private RescuerTaskGroupListItem parseItem(GroupSummary summary, JsonElement root) {
        JsonObject object = readObject(root);
        JsonArray requests = object == null ? null : readArray(object, "requests");
        JsonArray assignments = object == null ? null : readArray(object, "assignments");
        int activeAssignments = 0;
        if (assignments != null) {
            for (JsonElement element : assignments) {
                JsonObject assignment = readObject(element);
                if (assignment != null && readBoolean(assignment, "active")) {
                    activeAssignments++;
                }
            }
        }
        return new RescuerTaskGroupListItem(
                summary.id,
                summary.code,
                summary.status,
                firstNonBlank(readString(object, "assignedTeamName", ""), summary.teamName, "Đội cứu hộ"),
                firstNonBlank(readString(object, "note", ""), summary.note),
                firstNonBlank(readString(object, "createdAt", ""), summary.createdAt),
                firstNonBlank(readString(object, "updatedAt", ""), summary.updatedAt, summary.createdAt),
                requests == null ? 0 : requests.size(),
                activeAssignments
        );
    }

    private RescuerTaskGroupListItem fallbackItem(GroupSummary summary) {
        return new RescuerTaskGroupListItem(
                summary.id,
                summary.code,
                summary.status,
                summary.teamName,
                summary.note,
                summary.createdAt,
                summary.updatedAt,
                0,
                0
        );
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

    private boolean readBoolean(JsonObject object, String key) {
        return object != null && object.has(key) && !object.get(key).isJsonNull() && object.get(key).getAsBoolean();
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

    private static class GroupSummary {
        final long id;
        final String code;
        final String status;
        final String teamName;
        final String note;
        final String createdAt;
        final String updatedAt;

        GroupSummary(long id, String code, String status, String teamName, String note, String createdAt, String updatedAt) {
            this.id = id;
            this.code = code;
            this.status = status;
            this.teamName = teamName;
            this.note = note;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }
}

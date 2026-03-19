package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.request.RescuerTaskGroupEscalateRequest;
import com.floodrescue.mobile.data.model.ui.RescuerTaskGroupDetailState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RescuerTaskGroupDetailRepository {

    private final ApiService apiService;

    public RescuerTaskGroupDetailRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadLatestTaskGroupId(RepositoryCallback<Long> callback) {
        apiService.getRescuerTaskGroups(null, 0, 1).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tìm thấy nhóm nhiệm vụ nào."));
                    return;
                }
                JsonArray content = extractArray(response.body());
                if (content == null || content.size() == 0) {
                    callback.onError("Bạn chưa có nhóm nhiệm vụ nào.");
                    return;
                }
                JsonObject first = readObject(content.get(0));
                long id = readLong(first, "id");
                if (id <= 0L) {
                    callback.onError("Không đọc được mã nhóm nhiệm vụ.");
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

    public void loadDetail(long taskGroupId, RepositoryCallback<RescuerTaskGroupDetailState> callback) {
        apiService.getRescuerTaskGroup(taskGroupId).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được chi tiết nhóm nhiệm vụ."));
                    return;
                }
                JsonElement detailRoot = response.body();
                apiService.getRescuerEmergencyAcks(taskGroupId).enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> ackCall, Response<JsonElement> ackResponse) {
                        if (ackResponse.isSuccessful() && ackResponse.body() != null) {
                            callback.onSuccess(parseDetail(detailRoot, ackResponse.body()));
                            return;
                        }
                        callback.onSuccess(parseDetail(detailRoot, null));
                    }

                    @Override
                    public void onFailure(Call<JsonElement> ackCall, Throwable throwable) {
                        callback.onSuccess(parseDetail(detailRoot, null));
                    }
                });
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    public void updateStatus(long taskGroupId, String status, String note, RepositoryCallback<RescuerTaskGroupDetailState> callback) {
        apiService.updateRescuerTaskGroupStatus(taskGroupId, status, note)
                .enqueue(new ActionCallback(taskGroupId, callback, "Không cập nhật được trạng thái nhóm nhiệm vụ."));
    }

    public void escalate(long taskGroupId, String severity, String reason, RepositoryCallback<RescuerTaskGroupDetailState> callback) {
        apiService.escalateRescuerTaskGroup(taskGroupId, new RescuerTaskGroupEscalateRequest(severity, reason))
                .enqueue(new ActionCallback(taskGroupId, callback, "Không gửi được báo khẩn cấp."));
    }

    private class ActionCallback implements Callback<JsonElement> {
        private final long taskGroupId;
        private final RepositoryCallback<RescuerTaskGroupDetailState> callback;
        private final String fallbackError;

        ActionCallback(long taskGroupId, RepositoryCallback<RescuerTaskGroupDetailState> callback, String fallbackError) {
            this.taskGroupId = taskGroupId;
            this.callback = callback;
            this.fallbackError = fallbackError;
        }

        @Override
        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
            if (!response.isSuccessful()) {
                callback.onError(parseApiMessage(response, fallbackError));
                return;
            }
            loadDetail(taskGroupId, callback);
        }

        @Override
        public void onFailure(Call<JsonElement> call, Throwable throwable) {
            callback.onError(throwable == null || throwable.getMessage() == null
                    ? "Không thể kết nối tới server."
                    : throwable.getMessage());
        }
    }

    private RescuerTaskGroupDetailState parseDetail(JsonElement root, JsonElement ackRoot) {
        JsonObject object = readObject(root);
        if (object == null) {
            return new RescuerTaskGroupDetailState(
                    -1L,
                    "TG",
                    "NEW",
                    "",
                    "",
                    "",
                    "",
                    "",
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        return new RescuerTaskGroupDetailState(
                readLong(object, "id"),
                readString(object, "code", "TG"),
                readString(object, "status", "NEW"),
                readString(object, "note", ""),
                readString(object, "assignedTeamName", "Đội cứu hộ"),
                readString(object, "createdByName", "Điều phối viên"),
                readString(object, "createdAt", ""),
                readString(object, "updatedAt", ""),
                parseRequests(readArray(object, "requests")),
                parseAssignments(readArray(object, "assignments")),
                parseTimeline(readArray(object, "timeline")),
                parseEmergencyAcks(ackRoot)
        );
    }

    private List<RescuerTaskGroupDetailState.RequestItem> parseRequests(JsonArray array) {
        List<RescuerTaskGroupDetailState.RequestItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new RescuerTaskGroupDetailState.RequestItem(
                    readLong(object, "id"),
                    readString(object, "code", "RESC"),
                    readString(object, "status", ""),
                    readString(object, "priority", "MEDIUM"),
                    readInt(object, "affectedPeopleCount", 0),
                    readString(object, "addressText", "Chưa có địa chỉ"),
                    readString(object, "locationDescription", ""),
                    readString(object, "description", ""),
                    readString(object, "citizenName", "Người dân"),
                    readString(object, "citizenPhone", "--"),
                    readString(object, "createdAt", ""),
                    readString(object, "updatedAt", ""),
                    readBoolean(object, "locationVerified", false),
                    readBoolean(object, "emergency", false)
            ));
        }
        return items;
    }

    private List<RescuerTaskGroupDetailState.AssignmentItem> parseAssignments(JsonArray array) {
        List<RescuerTaskGroupDetailState.AssignmentItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new RescuerTaskGroupDetailState.AssignmentItem(
                    readLong(object, "id"),
                    readString(object, "teamName", "Đội cứu hộ"),
                    readString(object, "assetCode", ""),
                    readString(object, "assetName", "Chưa có phương tiện"),
                    readString(object, "assignedByName", "Điều phối viên"),
                    readString(object, "assignedAt", ""),
                    readBoolean(object, "active", false)
            ));
        }
        return items;
    }

    private List<RescuerTaskGroupDetailState.TimelineItem> parseTimeline(JsonArray array) {
        List<RescuerTaskGroupDetailState.TimelineItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new RescuerTaskGroupDetailState.TimelineItem(
                    readLong(object, "id"),
                    readString(object, "actorName", "Hệ thống"),
                    readString(object, "eventType", ""),
                    readString(object, "note", ""),
                    readString(object, "createdAt", "")
            ));
        }
        return items;
    }

    private List<RescuerTaskGroupDetailState.EmergencyAckItem> parseEmergencyAcks(JsonElement root) {
        List<RescuerTaskGroupDetailState.EmergencyAckItem> items = new ArrayList<>();
        JsonArray array = extractArray(root);
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new RescuerTaskGroupDetailState.EmergencyAckItem(
                    readLong(object, "coordinatorId"),
                    readString(object, "coordinatorName", "Điều phối viên"),
                    readBoolean(object, "read", false),
                    readString(object, "actionStatus", ""),
                    readString(object, "actionNote", ""),
                    readLong(object, "queueRequestId"),
                    readString(object, "acknowledgedAt", "")
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

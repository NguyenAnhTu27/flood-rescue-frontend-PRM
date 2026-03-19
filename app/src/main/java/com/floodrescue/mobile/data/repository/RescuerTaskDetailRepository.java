package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.RescuerTaskDetailState;
import com.floodrescue.mobile.data.model.request.RescueNoteRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RescuerTaskDetailRepository {

    private final ApiService apiService;

    public RescuerTaskDetailRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadDetail(long taskId, RepositoryCallback<RescuerTaskDetailState> callback) {
        apiService.getRescuerTask(taskId).enqueue(new DetailCallback(callback, "Không tải được chi tiết nhiệm vụ."));
    }

    public void updateStatus(long taskId, String status, String note, RepositoryCallback<RescuerTaskDetailState> callback) {
        apiService.updateRescuerTaskStatus(taskId, status, note)
                .enqueue(new DetailCallback(callback, "Không cập nhật được trạng thái nhiệm vụ."));
    }

    public void addNote(long taskId, String note, RepositoryCallback<RescuerTaskDetailState> callback) {
        apiService.addRescuerTaskNote(taskId, new RescueNoteRequest(note))
                .enqueue(new DetailCallback(callback, "Không gửi được ghi chú nhiệm vụ."));
    }

    private class DetailCallback implements Callback<JsonElement> {
        private final RepositoryCallback<RescuerTaskDetailState> callback;
        private final String fallbackMessage;

        DetailCallback(RepositoryCallback<RescuerTaskDetailState> callback, String fallbackMessage) {
            this.callback = callback;
            this.fallbackMessage = fallbackMessage;
        }

        @Override
        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
            if (response.isSuccessful() && response.body() != null && response.body().isJsonObject()) {
                callback.onSuccess(parseDetail(response.body().getAsJsonObject()));
                return;
            }
            callback.onError(parseApiMessage(response, fallbackMessage));
        }

        @Override
        public void onFailure(Call<JsonElement> call, Throwable throwable) {
            callback.onError(throwable == null || throwable.getMessage() == null
                    ? "Không thể kết nối tới server."
                    : throwable.getMessage());
        }
    }

    private RescuerTaskDetailState parseDetail(JsonObject item) {
        List<RescuerTaskDetailState.AttachmentItem> attachments = new ArrayList<>();
        JsonArray attachmentArray = readArray(item, "attachments");
        if (attachmentArray != null) {
            for (JsonElement element : attachmentArray) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject attachment = element.getAsJsonObject();
                attachments.add(new RescuerTaskDetailState.AttachmentItem(
                        readLong(attachment, "id"),
                        readString(attachment, "fileUrl", ""),
                        readString(attachment, "fileType", "")
                ));
            }
        }

        List<RescuerTaskDetailState.TimelineItem> timeline = new ArrayList<>();
        JsonArray timelineArray = readArray(item, "timeline");
        if (timelineArray != null) {
            for (JsonElement element : timelineArray) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject timelineItem = element.getAsJsonObject();
                timeline.add(new RescuerTaskDetailState.TimelineItem(
                        readLong(timelineItem, "id"),
                        readString(timelineItem, "actorName", "Hệ thống"),
                        readString(timelineItem, "eventType", ""),
                        readString(timelineItem, "fromStatus", ""),
                        readString(timelineItem, "toStatus", ""),
                        readString(timelineItem, "note", ""),
                        readString(timelineItem, "createdAt", "")
                ));
            }
        }

        return new RescuerTaskDetailState(
                readLong(item, "id"),
                readString(item, "code", "RESC"),
                readString(item, "citizenName", "Người dân"),
                readString(item, "citizenPhone", ""),
                readString(item, "status", ""),
                readString(item, "priority", ""),
                readInt(item, "affectedPeopleCount"),
                readString(item, "description", ""),
                readString(item, "addressText", ""),
                readDoubleObject(item, "latitude"),
                readDoubleObject(item, "longitude"),
                readString(item, "locationDescription", ""),
                readBoolean(item, "locationVerified"),
                readString(item, "createdAt", ""),
                readString(item, "updatedAt", ""),
                attachments,
                timeline
        );
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
        String value = object.get(key).getAsString();
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private boolean readBoolean(JsonObject object, String key) {
        return object != null && object.has(key) && !object.get(key).isJsonNull() && object.get(key).getAsBoolean();
    }

    private long readLong(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return -1L;
        }
        return object.get(key).getAsLong();
    }

    private int readInt(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return 0;
        }
        return object.get(key).getAsInt();
    }

    private Double readDoubleObject(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return null;
        }
        return object.get(key).getAsDouble();
    }

    private String parseApiMessage(Response<?> response, String fallback) {
        try {
            if (response == null || response.errorBody() == null) {
                return fallback;
            }
            String raw = response.errorBody().string();
            if (raw == null || raw.trim().isEmpty()) {
                return fallback;
            }
            JSONObject jsonObject = new JSONObject(raw);
            String message = jsonObject.optString("message");
            if (!message.isEmpty()) {
                return message;
            }
        } catch (Exception ignored) {
            // Use fallback.
        }
        return fallback;
    }
}

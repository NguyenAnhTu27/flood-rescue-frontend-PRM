package com.floodrescue.mobile.data.repository;

import android.content.Context;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.request.CitizenRescueConfirmRequest;
import com.floodrescue.mobile.data.model.request.CitizenRescueReopenRequest;
import com.floodrescue.mobile.data.model.request.CitizenRescueUpdateRequest;
import com.floodrescue.mobile.data.model.request.RescueNoteRequest;
import com.floodrescue.mobile.data.model.request.RescueRequestCreatePayload;
import com.floodrescue.mobile.data.model.response.ApiMessageResponse;
import com.floodrescue.mobile.data.model.response.AttachmentUploadResponse;
import com.floodrescue.mobile.data.model.response.CitizenRescueConfirmResponse;
import com.floodrescue.mobile.data.model.ui.CitizenRescueDetailState;
import com.floodrescue.mobile.data.model.ui.CitizenRescueListItem;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitizenRescueRepository {

    private final Context context;
    private final ApiService apiService;

    public CitizenRescueRepository(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = ApiClient.create(context);
    }

    public void uploadAttachments(List<Uri> imageUris, RepositoryCallback<List<AttachmentUploadResponse>> callback) {
        if (imageUris == null || imageUris.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        try {
            List<MultipartBody.Part> parts = new ArrayList<>();
            for (Uri uri : imageUris) {
                byte[] data = readAllBytes(uri);
                String mimeType = context.getContentResolver().getType(uri);
                if (mimeType == null || mimeType.trim().isEmpty()) {
                    mimeType = "image/*";
                }
                String fileName = readFileName(uri);
                RequestBody requestBody = RequestBody.create(data, MediaType.parse(mimeType));
                parts.add(MultipartBody.Part.createFormData("files", fileName, requestBody));
            }

            apiService.uploadCitizenRescueAttachments(parts).enqueue(new Callback<List<AttachmentUploadResponse>>() {
                @Override
                public void onResponse(Call<List<AttachmentUploadResponse>> call, Response<List<AttachmentUploadResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                        return;
                    }
                    callback.onError(parseApiMessage(response, "Không upload được ảnh hiện trường."));
                }

                @Override
                public void onFailure(Call<List<AttachmentUploadResponse>> call, Throwable throwable) {
                    callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
                }
            });
        } catch (Exception exception) {
            callback.onError(exception.getMessage() == null ? "Không đọc được ảnh đã chọn." : exception.getMessage());
        }
    }

    public void createRescueRequest(RescueRequestCreatePayload payload, RepositoryCallback<Long> callback) {
        apiService.createCitizenRescueRequest(payload).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isJsonObject()) {
                    JsonObject object = response.body().getAsJsonObject();
                    long requestId = object.has("id") && !object.get("id").isJsonNull()
                            ? object.get("id").getAsLong()
                            : -1L;
                    callback.onSuccess(requestId);
                    return;
                }
                callback.onError(parseApiMessage(response, "Không tạo được yêu cầu cứu hộ."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    public void getMyRescueRequests(RepositoryCallback<List<CitizenRescueListItem>> callback) {
        apiService.getCitizenRescueRequests(0, 50, null).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isJsonObject()) {
                    callback.onSuccess(parseRescueItems(response.body().getAsJsonObject()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Không tải được danh sách yêu cầu cứu hộ."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    public void getRescueRequestDetail(long requestId, RepositoryCallback<CitizenRescueDetailState> callback) {
        apiService.getCitizenRescueRequest(requestId).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isJsonObject()) {
                    callback.onSuccess(parseRescueDetail(response.body().getAsJsonObject()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Không tải được chi tiết yêu cầu cứu hộ."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    public void updateRescueRequest(
            long requestId,
            CitizenRescueUpdateRequest request,
            RepositoryCallback<CitizenRescueDetailState> callback
    ) {
        apiService.updateCitizenRescueRequest(requestId, request).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isJsonObject()) {
                    callback.onSuccess(parseRescueDetail(response.body().getAsJsonObject()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Không cập nhật được yêu cầu cứu hộ."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    public void addRescueNote(long requestId, String note, RepositoryCallback<CitizenRescueDetailState> callback) {
        apiService.addCitizenRescueNote(requestId, new RescueNoteRequest(note)).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isJsonObject()) {
                    callback.onSuccess(parseRescueDetail(response.body().getAsJsonObject()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Không gửi được ghi chú."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    public void cancelRescueRequest(long requestId, RepositoryCallback<String> callback) {
        apiService.cancelCitizenRescueRequest(requestId).enqueue(new Callback<ApiMessageResponse>() {
            @Override
            public void onResponse(Call<ApiMessageResponse> call, Response<ApiMessageResponse> response) {
                if (response.isSuccessful()) {
                    ApiMessageResponse body = response.body();
                    callback.onSuccess(body == null ? "Yêu cầu cứu hộ đã được hủy" : body.getMessage());
                    return;
                }
                callback.onError(parseApiMessage(response, "Không hủy được yêu cầu cứu hộ."));
            }

            @Override
            public void onFailure(Call<ApiMessageResponse> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    public void confirmRescueResult(
            long requestId,
            boolean rescued,
            String reason,
            RepositoryCallback<CitizenRescueConfirmResponse> callback
    ) {
        apiService.confirmCitizenRescueResult(
                requestId,
                new CitizenRescueConfirmRequest(rescued, reason)
        ).enqueue(new Callback<CitizenRescueConfirmResponse>() {
            @Override
            public void onResponse(Call<CitizenRescueConfirmResponse> call, Response<CitizenRescueConfirmResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(parseApiMessage(response, "Không xác nhận được kết quả cứu hộ."));
            }

            @Override
            public void onFailure(Call<CitizenRescueConfirmResponse> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    public void reopenRescueRequest(long requestId, String reason, RepositoryCallback<String> callback) {
        apiService.reopenCitizenRescueRequest(
                requestId,
                new CitizenRescueReopenRequest(reason)
        ).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Đã mở lại yêu cầu cứu hộ.");
                    return;
                }
                callback.onError(parseApiMessage(response, "Không mở lại được yêu cầu cứu hộ."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable.getMessage() == null ? "Không thể kết nối tới server." : throwable.getMessage());
            }
        });
    }

    private List<CitizenRescueListItem> parseRescueItems(JsonObject root) {
        List<CitizenRescueListItem> items = new ArrayList<>();
        if (root == null || !root.has("content") || root.get("content").isJsonNull()) {
            return items;
        }
        JsonArray content = root.getAsJsonArray("content");
        for (JsonElement element : content) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject item = element.getAsJsonObject();
            items.add(new CitizenRescueListItem(
                    readLong(item, "id"),
                    readString(item, "code", "#RESC"),
                    readString(item, "status", ""),
                    readString(item, "priority", ""),
                    readString(item, "addressText", "Chưa có địa chỉ"),
                    readInt(item, "affectedPeopleCount"),
                    readBoolean(item, "waitingForTeam"),
                    readBoolean(item, "locationVerified"),
                    readString(item, "createdAt", ""),
                    readString(item, "updatedAt", "")
            ));
        }
        return items;
    }

    private CitizenRescueDetailState parseRescueDetail(JsonObject item) {
        List<CitizenRescueDetailState.AttachmentItem> attachments = new ArrayList<>();
        if (item != null && item.has("attachments") && !item.get("attachments").isJsonNull()) {
            JsonArray array = item.getAsJsonArray("attachments");
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject attachment = element.getAsJsonObject();
                attachments.add(new CitizenRescueDetailState.AttachmentItem(
                        readLong(attachment, "id"),
                        readString(attachment, "fileUrl", ""),
                        readString(attachment, "fileType", ""),
                        readString(attachment, "createdAt", "")
                ));
            }
        }

        List<CitizenRescueDetailState.TimelineItem> timeline = new ArrayList<>();
        if (item != null && item.has("timeline") && !item.get("timeline").isJsonNull()) {
            JsonArray array = item.getAsJsonArray("timeline");
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject timelineItem = element.getAsJsonObject();
                timeline.add(new CitizenRescueDetailState.TimelineItem(
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

        return new CitizenRescueDetailState(
                readLong(item, "id"),
                readString(item, "code", "RESC"),
                readString(item, "status", ""),
                readString(item, "priority", ""),
                readInt(item, "affectedPeopleCount"),
                readString(item, "description", ""),
                readString(item, "addressText", ""),
                readDoubleObject(item, "latitude"),
                readDoubleObject(item, "longitude"),
                readString(item, "locationDescription", ""),
                readBoolean(item, "locationVerified"),
                readBoolean(item, "waitingForTeam"),
                readString(item, "coordinatorCancelNote", ""),
                readBoolean(item, "waitingCitizenRescueConfirmation"),
                readString(item, "rescueResultConfirmationStatus", ""),
                readString(item, "rescueResultConfirmationNote", ""),
                readString(item, "createdAt", ""),
                readString(item, "updatedAt", ""),
                attachments,
                timeline
        );
    }

    private byte[] readAllBytes(Uri uri) throws Exception {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (inputStream == null) {
                throw new IllegalStateException("Không mở được ảnh đã chọn.");
            }
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return outputStream.toByteArray();
        }
    }

    private String readFileName(Uri uri) {
        try (android.database.Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex >= 0) {
                    String fileName = cursor.getString(columnIndex);
                    if (fileName != null && !fileName.trim().isEmpty()) {
                        return fileName;
                    }
                }
            }
        }
        return "attachment_" + System.currentTimeMillis() + ".jpg";
    }

    private String parseApiMessage(Response<?> response, String fallback) {
        try {
            if (response.errorBody() == null) {
                return fallback;
            }
            String raw = response.errorBody().string();
            if (raw == null || raw.trim().isEmpty()) {
                return fallback;
            }
            JSONObject jsonObject = new JSONObject(raw);
            JSONObject errors = jsonObject.optJSONObject("errors");
            if (errors != null) {
                String[] keys = {
                        "description",
                        "addressText",
                        "latitude",
                        "longitude",
                        "affectedPeopleCount",
                        "priority",
                        "note",
                        "reason",
                        "rescued"
                };
                for (String key : keys) {
                    String value = errors.optString(key);
                    if (!value.isEmpty()) {
                        return value;
                    }
                }
            }
            String message = jsonObject.optString("message");
            if (!message.isEmpty()) {
                return message;
            }
        } catch (Exception ignored) {
            // Fall back to generic message.
        }
        return fallback;
    }

    private String readString(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        String value = object.get(key).getAsString();
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private boolean readBoolean(JsonObject object, String key) {
        return object != null
                && object.has(key)
                && !object.get(key).isJsonNull()
                && object.get(key).getAsBoolean();
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
}

package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.request.CoordinatorAddNoteRequest;
import com.floodrescue.mobile.data.model.request.CoordinatorBlockCitizenRequest;
import com.floodrescue.mobile.data.model.request.CoordinatorCreateTaskGroupRequest;
import com.floodrescue.mobile.data.model.request.CoordinatorDuplicateRequest;
import com.floodrescue.mobile.data.model.request.CoordinatorPrioritizeRequest;
import com.floodrescue.mobile.data.model.request.CoordinatorUnblockCitizenRequest;
import com.floodrescue.mobile.data.model.request.CoordinatorVerifyRequest;
import com.floodrescue.mobile.data.model.ui.BlockedCitizenItem;
import com.floodrescue.mobile.data.model.ui.CoordinatorRescueDetailState;
import com.floodrescue.mobile.data.model.ui.CoordinatorTaskGroupListItem;
import com.floodrescue.mobile.data.model.ui.CoordinatorTeamOption;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorOperationsRepository {

    private final ApiService apiService;

    public CoordinatorOperationsRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void getBlockedCitizens(RepositoryCallback<List<BlockedCitizenItem>> callback) {
        apiService.getBlockedCitizens().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(parseBlockedCitizens(response.body()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Khong tai duoc cong dan bi chan."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(messageOf(throwable));
            }
        });
    }

    public void unblockCitizen(long citizenId, String reason, RepositoryCallback<String> callback) {
        apiService.unblockBlockedCitizen(citizenId, new CoordinatorUnblockCitizenRequest(reason)).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(parseApiMessage(response, "Da bo chan cong dan."));
                    return;
                }
                callback.onError(parseApiMessage(response, "Khong the bo chan cong dan."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(messageOf(throwable));
            }
        });
    }

    public void getCoordinatorTeams(RepositoryCallback<List<CoordinatorTeamOption>> callback) {
        apiService.getCoordinatorDashboard().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(parseCoordinatorTeams(response.body()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Khong tai duoc doi cuu ho."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(messageOf(throwable));
            }
        });
    }

    public void createTaskGroup(List<Long> requestIds, Long assignedTeamId, String note, RepositoryCallback<Long> callback) {
        apiService.createCoordinatorTaskGroup(new CoordinatorCreateTaskGroupRequest(requestIds, assignedTeamId, note))
                .enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(readLong(readObject(response.body()), "id"));
                            return;
                        }
                        callback.onError(parseApiMessage(response, "Khong tao duoc nhom nhiem vu."));
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable throwable) {
                        callback.onError(messageOf(throwable));
                    }
                });
    }

    public void getTaskGroups(String status, RepositoryCallback<List<CoordinatorTaskGroupListItem>> callback) {
        apiService.getCoordinatorTaskGroups(status, 0, 50).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(parseTaskGroups(response.body()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Khong tai duoc nhom nhiem vu."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(messageOf(throwable));
            }
        });
    }

    public void getRescueRequestDetail(long id, RepositoryCallback<CoordinatorRescueDetailState> callback) {
        apiService.getCoordinatorRescueRequest(id).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(parseRescueDetail(response.body()));
                    return;
                }
                callback.onError(parseApiMessage(response, "Khong tai duoc chi tiet yeu cau."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(messageOf(throwable));
            }
        });
    }

    public void verifyRequest(long id, boolean verified, String note, RepositoryCallback<CoordinatorRescueDetailState> callback) {
        apiService.verifyCoordinatorRescueRequest(id, new CoordinatorVerifyRequest(verified, note, false, null, null))
                .enqueue(detailCallback(callback, "Khong xac minh duoc yeu cau."));
    }

    public void updatePriority(long id, String priority, RepositoryCallback<CoordinatorRescueDetailState> callback) {
        apiService.updateCoordinatorRescuePriority(id, new CoordinatorPrioritizeRequest(priority))
                .enqueue(detailCallback(callback, "Khong doi duoc muc do uu tien."));
    }

    public void addNote(long id, String note, RepositoryCallback<CoordinatorRescueDetailState> callback) {
        apiService.addCoordinatorRescueNote(id, new CoordinatorAddNoteRequest(note))
                .enqueue(detailCallback(callback, "Khong them duoc ghi chu."));
    }

    public void markDuplicate(long id, long masterRequestId, String note, RepositoryCallback<CoordinatorRescueDetailState> callback) {
        apiService.markCoordinatorRescueDuplicate(id, new CoordinatorDuplicateRequest(masterRequestId, note))
                .enqueue(detailCallback(callback, "Khong danh dau duoc yeu cau trung."));
    }

    public void changeStatus(long id, String status, String note, RepositoryCallback<CoordinatorRescueDetailState> callback) {
        apiService.updateCoordinatorRescueStatus(id, status, note).enqueue(detailCallback(callback, "Khong cap nhat duoc trang thai."));
    }

    public void blockCitizenByRequest(long requestId, boolean blocked, String reason, RepositoryCallback<String> callback) {
        apiService.blockCoordinatorCitizenFromRequest(requestId, new CoordinatorBlockCitizenRequest(blocked, reason))
                .enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(parseApiMessage(response, blocked ? "Da chan cong dan." : "Da bo chan cong dan."));
                            return;
                        }
                        callback.onError(parseApiMessage(response, "Khong cap nhat duoc trang thai chan."));
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable throwable) {
                        callback.onError(messageOf(throwable));
                    }
                });
    }

    private Callback<JsonElement> detailCallback(RepositoryCallback<CoordinatorRescueDetailState> callback, String fallbackError) {
        return new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(parseRescueDetail(response.body()));
                    return;
                }
                callback.onError(parseApiMessage(response, fallbackError));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(messageOf(throwable));
            }
        };
    }

    private List<BlockedCitizenItem> parseBlockedCitizens(JsonElement body) {
        List<BlockedCitizenItem> items = new ArrayList<>();
        JsonArray array = readArray(body);
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            items.add(new BlockedCitizenItem(
                    readLong(object, "id"),
                    readString(object, "fullName", "Nguoi dung"),
                    readString(object, "phone", "--"),
                    readString(object, "email", "--"),
                    readString(object, "blockedReason", "")
            ));
        }
        return items;
    }

    private List<CoordinatorTeamOption> parseCoordinatorTeams(JsonElement body) {
        List<CoordinatorTeamOption> items = new ArrayList<>();
        JsonObject root = readObject(body);
        JsonArray teams = root != null && root.has("teams") && root.get("teams").isJsonArray()
                ? root.getAsJsonArray("teams")
                : null;
        if (teams == null) {
            return items;
        }
        for (JsonElement element : teams) {
            JsonObject object = readObject(element);
            items.add(new CoordinatorTeamOption(
                    readLong(object, "id"),
                    readString(object, "name", "Doi cuu ho"),
                    readString(object, "status", "AVAILABLE"),
                    readString(object, "area", readString(object, "currentLocationText", "")),
                    readString(object, "lastUpdate", ""),
                    readBoolean(object, "online", true)
            ));
        }
        return items;
    }

    private List<CoordinatorTaskGroupListItem> parseTaskGroups(JsonElement body) {
        List<CoordinatorTaskGroupListItem> items = new ArrayList<>();
        JsonArray array = extractPageArray(body);
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            items.add(new CoordinatorTaskGroupListItem(
                    readLong(object, "id"),
                    readString(object, "code", "TG-0000"),
                    readString(object, "status", "NEW"),
                    readString(object, "assignedTeamName", ""),
                    readString(object, "createdByName", ""),
                    readString(object, "createdAt", ""),
                    readString(object, "updatedAt", ""),
                    readString(object, "note", "")
            ));
        }
        return items;
    }

    private CoordinatorRescueDetailState parseRescueDetail(JsonElement body) {
        JsonObject object = readObject(body);
        if (object == null) {
            return new CoordinatorRescueDetailState(-1L, "", "", "", "", "", 0, "", "", "", null, null, false, false, "", "", new ArrayList<>(), new ArrayList<>());
        }

        List<CoordinatorRescueDetailState.AttachmentItem> attachments = new ArrayList<>();
        JsonArray attachmentArray = object.has("attachments") && object.get("attachments").isJsonArray()
                ? object.getAsJsonArray("attachments")
                : null;
        if (attachmentArray != null) {
            for (JsonElement element : attachmentArray) {
                JsonObject attachment = readObject(element);
                attachments.add(new CoordinatorRescueDetailState.AttachmentItem(
                        readString(attachment, "fileUrl", "")
                ));
            }
        }

        List<CoordinatorRescueDetailState.TimelineItem> timeline = new ArrayList<>();
        JsonArray timelineArray = object.has("timeline") && object.get("timeline").isJsonArray()
                ? object.getAsJsonArray("timeline")
                : null;
        if (timelineArray != null) {
            for (JsonElement element : timelineArray) {
                JsonObject item = readObject(element);
                timeline.add(new CoordinatorRescueDetailState.TimelineItem(
                        readString(item, "eventType", ""),
                        readString(item, "actorName", "He thong"),
                        readString(item, "note", ""),
                        readString(item, "createdAt", "")
                ));
            }
        }

        return new CoordinatorRescueDetailState(
                readLong(object, "id"),
                readString(object, "code", ""),
                readString(object, "citizenName", "Nguoi dan"),
                readString(object, "citizenPhone", "--"),
                readString(object, "status", "PENDING"),
                readString(object, "priority", "MEDIUM"),
                readInt(object, "affectedPeopleCount", 0),
                readString(object, "description", ""),
                readString(object, "addressText", ""),
                readString(object, "locationDescription", ""),
                readDouble(object, "latitude"),
                readDouble(object, "longitude"),
                readBoolean(object, "locationVerified", false),
                readBoolean(object, "waitingForTeam", false),
                readString(object, "masterRequestCode", ""),
                readString(object, "coordinatorCancelNote", ""),
                attachments,
                timeline
        );
    }

    private JsonArray extractPageArray(JsonElement body) {
        JsonObject root = readObject(body);
        if (root != null && root.has("content") && root.get("content").isJsonArray()) {
            return root.getAsJsonArray("content");
        }
        return readArray(body);
    }

    private JsonArray readArray(JsonElement body) {
        if (body == null) {
            return null;
        }
        if (body.isJsonArray()) {
            return body.getAsJsonArray();
        }
        JsonObject object = readObject(body);
        if (object != null && object.has("data") && object.get("data").isJsonArray()) {
            return object.getAsJsonArray("data");
        }
        return null;
    }

    private JsonObject readObject(JsonElement body) {
        return body != null && body.isJsonObject() ? body.getAsJsonObject() : null;
    }

    private String readString(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return object.get(key).getAsString();
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

    private Double readDouble(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return null;
        }
        try {
            return object.get(key).getAsDouble();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String messageOf(Throwable throwable) {
        return throwable == null || throwable.getMessage() == null ? "Khong the ket noi toi server." : throwable.getMessage();
    }

    private String parseApiMessage(Response<?> response, String fallback) {
        try {
            if (response != null && response.body() instanceof JsonElement body && body != null) {
                JsonObject object = readObject(body);
                if (object != null && object.has("message") && !object.get("message").isJsonNull()) {
                    return object.get("message").getAsString();
                }
            }
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

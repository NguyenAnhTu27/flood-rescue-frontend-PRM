package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.ManagerDispatchState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerDispatchRepository extends JsonRepositorySupport {

    private final ApiService apiService;

    public ManagerDispatchRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadDashboard(RepositoryCallback<ManagerDispatchState> callback) {
        apiService.getManagerDispatchDashboard().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được dữ liệu điều phối cứu trợ."));
                    return;
                }
                callback.onSuccess(parseState(response.body()));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    public void approveDispatch(long requestId, long teamId, String note, RepositoryCallback<Boolean> callback) {
        JsonObject payload = new JsonObject();
        payload.addProperty("assignedTeamId", teamId);
        if (note != null && !note.trim().isEmpty()) {
            payload.addProperty("note", note.trim());
        }
        apiService.approveManagerReliefDispatch(requestId, payload).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful()) {
                    callback.onError(parseApiMessage(response, "Không điều phối được yêu cầu cứu trợ."));
                    return;
                }
                callback.onSuccess(true);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    private ManagerDispatchState parseState(JsonElement root) {
        JsonObject object = readObject(root);
        return new ManagerDispatchState(
                parseRequests(readArray(object, "requests")),
                parseTeams(readArray(object, "teams")),
                parseVehicles(readArray(object, "vehicles"))
        );
    }

    private List<ManagerDispatchState.QueueItem> parseRequests(JsonArray array) {
        List<ManagerDispatchState.QueueItem> items = new ArrayList<>();
        if (array == null) return items;
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) continue;
            items.add(new ManagerDispatchState.QueueItem(
                    readLong(object, "id"),
                    readString(object, "code", "-"),
                    readString(object, "priority", "NORMAL"),
                    readInt(object, "peopleCount", 0),
                    readString(object, "timeAgo", "-"),
                    readString(object, "status", "PENDING"),
                    readBoolean(object, "waitingForTeam", false),
                    readDoubleObject(object, "lat"),
                    readDoubleObject(object, "lng")
            ));
        }
        return items;
    }

    private List<ManagerDispatchState.TeamItem> parseTeams(JsonArray array) {
        List<ManagerDispatchState.TeamItem> items = new ArrayList<>();
        if (array == null) return items;
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) continue;
            items.add(new ManagerDispatchState.TeamItem(
                    readLong(object, "id"),
                    readString(object, "name", "Đội cứu trợ"),
                    readString(object, "area", "-"),
                    readString(object, "status", "UNKNOWN"),
                    readDoubleObject(object, "distance"),
                    readString(object, "lastUpdate", ""),
                    readBoolean(object, "online", false)
            ));
        }
        return items;
    }

    private List<ManagerDispatchState.VehicleItem> parseVehicles(JsonArray array) {
        List<ManagerDispatchState.VehicleItem> items = new ArrayList<>();
        if (array == null) return items;
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) continue;
            items.add(new ManagerDispatchState.VehicleItem(
                    readLong(object, "id"),
                    readString(object, "code", "-"),
                    readString(object, "name", "Phương tiện"),
                    readString(object, "type", "-"),
                    readIntegerObject(object, "capacity"),
                    readString(object, "status", "UNKNOWN"),
                    readDoubleObject(object, "distance"),
                    readString(object, "location", "-"),
                    readBoolean(object, "online", false)
            ));
        }
        return items;
    }
}

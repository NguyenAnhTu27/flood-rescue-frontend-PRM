package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.ManagerReliefDetailState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerReliefDetailRepository extends JsonRepositorySupport {

    private final ApiService apiService;

    public ManagerReliefDetailRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadDetail(long requestId, RepositoryCallback<ManagerReliefDetailState> callback) {
        apiService.getReliefRequest(requestId).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được chi tiết yêu cầu cứu trợ."));
                    return;
                }
                callback.onSuccess(parseDetailStatic(response.body()));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    public void rejectRequest(long requestId, String reason, RepositoryCallback<ManagerReliefDetailState> callback) {
        JsonObject payload = new JsonObject();
        if (reason != null && !reason.trim().isEmpty()) {
            payload.addProperty("reason", reason.trim());
        }
        apiService.rejectManagerReliefRequest(requestId, payload).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không từ chối được yêu cầu cứu trợ."));
                    return;
                }
                callback.onSuccess(parseDetailStatic(response.body()));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    public static ManagerReliefDetailState parseDetailStatic(JsonElement root) {
        JsonObject object = root != null && root.isJsonObject() ? root.getAsJsonObject() : null;
        List<ManagerReliefDetailState.LineItem> lines = new ArrayList<>();
        if (object != null && object.has("lines") && object.get("lines").isJsonArray()) {
            for (JsonElement element : object.getAsJsonArray("lines")) {
                JsonObject line = element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
                if (line == null) continue;
                lines.add(new ManagerReliefDetailState.LineItem(
                        line.has("itemCategoryId") && !line.get("itemCategoryId").isJsonNull() ? line.get("itemCategoryId").getAsInt() : 0,
                        line.has("itemCode") && !line.get("itemCode").isJsonNull() ? line.get("itemCode").getAsString() : "",
                        line.has("itemName") && !line.get("itemName").isJsonNull() ? line.get("itemName").getAsString() : "-",
                        line.has("qty") && !line.get("qty").isJsonNull() ? line.get("qty").getAsBigDecimal().stripTrailingZeros().toPlainString() : "0",
                        line.has("unit") && !line.get("unit").isJsonNull() ? line.get("unit").getAsString() : ""
                ));
            }
        }

        return new ManagerReliefDetailState(
                object != null && object.has("id") && !object.get("id").isJsonNull() ? object.get("id").getAsLong() : 0L,
                object != null && object.has("code") && !object.get("code").isJsonNull() ? object.get("code").getAsString() : "-",
                object != null && object.has("status") && !object.get("status").isJsonNull() ? object.get("status").getAsString() : "DRAFT",
                object != null && object.has("deliveryStatus") && !object.get("deliveryStatus").isJsonNull() ? object.get("deliveryStatus").getAsString() : "REQUESTED",
                object != null && object.has("targetArea") && !object.get("targetArea").isJsonNull() ? object.get("targetArea").getAsString() : "",
                object != null && object.has("createdByName") && !object.get("createdByName").isJsonNull() ? object.get("createdByName").getAsString() : "",
                object != null && object.has("createdByPhone") && !object.get("createdByPhone").isJsonNull() ? object.get("createdByPhone").getAsString() : "",
                object != null && object.has("rescueRequestId") && !object.get("rescueRequestId").isJsonNull() ? object.get("rescueRequestId").getAsLong() : 0L,
                object != null && object.has("citizenAddressText") && !object.get("citizenAddressText").isJsonNull() ? object.get("citizenAddressText").getAsString() : "",
                object != null && object.has("citizenLatitude") && !object.get("citizenLatitude").isJsonNull() ? object.get("citizenLatitude").getAsDouble() : null,
                object != null && object.has("citizenLongitude") && !object.get("citizenLongitude").isJsonNull() ? object.get("citizenLongitude").getAsDouble() : null,
                object != null && object.has("citizenLocationDescription") && !object.get("citizenLocationDescription").isJsonNull() ? object.get("citizenLocationDescription").getAsString() : "",
                object != null && object.has("note") && !object.get("note").isJsonNull() ? object.get("note").getAsString() : "",
                object != null && object.has("deliveryNote") && !object.get("deliveryNote").isJsonNull() ? object.get("deliveryNote").getAsString() : "",
                object != null && object.has("assignedTeamId") && !object.get("assignedTeamId").isJsonNull() ? object.get("assignedTeamId").getAsLong() : 0L,
                object != null && object.has("assignedIssueId") && !object.get("assignedIssueId").isJsonNull() ? object.get("assignedIssueId").getAsLong() : 0L,
                object != null && object.has("createdAt") && !object.get("createdAt").isJsonNull() ? object.get("createdAt").getAsString() : "",
                object != null && object.has("updatedAt") && !object.get("updatedAt").isJsonNull() ? object.get("updatedAt").getAsString() : "",
                lines
        );
    }
}

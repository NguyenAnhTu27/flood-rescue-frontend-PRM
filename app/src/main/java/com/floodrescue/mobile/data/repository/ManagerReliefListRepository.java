package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.ManagerReliefDetailState;
import com.floodrescue.mobile.data.model.ui.ManagerReliefRequestItem;
import com.floodrescue.mobile.data.model.ui.ManagerReliefRequestListState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerReliefListRepository extends JsonRepositorySupport {

    private final ApiService apiService;

    public ManagerReliefListRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadRequests(String status, RepositoryCallback<ManagerReliefRequestListState> callback) {
        apiService.getManagerReliefRequests(status, 0, 50).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được danh sách yêu cầu cứu trợ."));
                    return;
                }
                callback.onSuccess(parsePage(response.body()));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    private ManagerReliefRequestListState parsePage(JsonElement root) {
        JsonObject object = readObject(root);
        JsonArray content = readArray(object, "content");
        List<ManagerReliefRequestItem> items = new ArrayList<>();
        if (content != null) {
            for (JsonElement element : content) {
                JsonObject item = readObject(element);
                if (item == null) continue;
                items.add(new ManagerReliefRequestItem(
                        readLong(item, "id"),
                        readString(item, "code", "-"),
                        readString(item, "status", "DRAFT"),
                        readString(item, "deliveryStatus", "REQUESTED"),
                        readString(item, "targetArea", "-"),
                        readString(item, "createdByName", "-"),
                        readString(item, "createdByPhone", "-"),
                        readString(item, "citizenAddressText", ""),
                        countArray(item, "lines"),
                        readString(item, "updatedAt", readString(item, "createdAt", ""))
                ));
            }
        }
        return new ManagerReliefRequestListState(items, readInt(object, "totalElements", items.size()));
    }

    private int countArray(JsonObject object, String key) {
        JsonArray array = readArray(object, key);
        return array == null ? 0 : array.size();
    }
}

package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.ItemCategoryOption;
import com.floodrescue.mobile.data.model.ui.ManagerReliefDetailState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerReliefCreateRepository extends JsonRepositorySupport {

    private final ApiService apiService;

    public ManagerReliefCreateRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void generateCode(RepositoryCallback<String> callback) {
        apiService.generateReliefRequestCode().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tạo được mã yêu cầu cứu trợ."));
                    return;
                }
                JsonObject object = readObject(response.body());
                callback.onSuccess(readString(object, "code", "AUTO"));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    public void loadItemCategories(RepositoryCallback<List<ItemCategoryOption>> callback) {
        apiService.getItemCategories().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được danh mục vật tư."));
                    return;
                }
                callback.onSuccess(parseCategories(response.body()));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    public void createRequest(JsonObject payload, RepositoryCallback<ManagerReliefDetailState> callback) {
        apiService.createManagerReliefRequest(payload).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tạo được yêu cầu cứu trợ."));
                    return;
                }
                callback.onSuccess(parseDetail(response.body()));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(throwable == null || throwable.getMessage() == null
                        ? "Không thể kết nối tới server."
                        : throwable.getMessage());
            }
        });
    }

    private List<ItemCategoryOption> parseCategories(JsonElement root) {
        List<ItemCategoryOption> items = new ArrayList<>();
        if (root == null || !root.isJsonArray()) {
            return items;
        }
        for (JsonElement element : root.getAsJsonArray()) {
            JsonObject object = readObject(element);
            if (object == null) continue;
            items.add(new ItemCategoryOption(
                    readInt(object, "id", 0),
                    readString(object, "code", ""),
                    readString(object, "name", "-"),
                    readString(object, "unit", ""),
                    readString(object, "classificationName", "")
            ));
        }
        return items;
    }

    private ManagerReliefDetailState parseDetail(JsonElement root) {
        return ManagerReliefDetailRepository.parseDetailStatic(root);
    }
}

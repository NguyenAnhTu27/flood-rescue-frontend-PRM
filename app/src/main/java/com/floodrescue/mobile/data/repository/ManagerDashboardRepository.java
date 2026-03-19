package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.ManagerDashboardState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerDashboardRepository extends JsonRepositorySupport {

    private final ApiService apiService;

    public ManagerDashboardRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadDashboard(RepositoryCallback<ManagerDashboardState> callback) {
        apiService.getManagerDashboard().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được dashboard cứu trợ."));
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

    private ManagerDashboardState parseState(JsonElement root) {
        JsonObject object = readObject(root);
        return new ManagerDashboardState(
                parseOverview(readArray(object, "overview")),
                parseTransactions(readArray(object, "recentTransactions")),
                parseInventorySummary(readArray(object, "inventorySummary")),
                parseInventoryItems(readArray(object, "inventoryItems"))
        );
    }

    private List<ManagerDashboardState.OverviewItem> parseOverview(JsonArray array) {
        List<ManagerDashboardState.OverviewItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) continue;
            items.add(new ManagerDashboardState.OverviewItem(
                    readString(object, "id", ""),
                    readString(object, "label", "-"),
                    readString(object, "value", "0"),
                    readString(object, "unit", ""),
                    readString(object, "sub", ""),
                    readString(object, "color", "info"),
                    readBoolean(object, "highlighted", false)
            ));
        }
        return items;
    }

    private List<ManagerDashboardState.TransactionItem> parseTransactions(JsonArray array) {
        List<ManagerDashboardState.TransactionItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) continue;
            items.add(new ManagerDashboardState.TransactionItem(
                    readString(object, "id", ""),
                    readString(object, "code", "-"),
                    readString(object, "typeLabel", readString(object, "type", "-")),
                    readString(object, "typeColor", "info"),
                    readString(object, "destination", "-"),
                    readString(object, "statusLabel", readString(object, "status", "-")),
                    readString(object, "statusColor", "info"),
                    readString(object, "time", "-")
            ));
        }
        return items;
    }

    private List<ManagerDashboardState.InventorySummaryItem> parseInventorySummary(JsonArray array) {
        List<ManagerDashboardState.InventorySummaryItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) continue;
            items.add(new ManagerDashboardState.InventorySummaryItem(
                    readString(object, "id", ""),
                    readString(object, "label", "-"),
                    readString(object, "value", "0"),
                    readString(object, "color", "info")
            ));
        }
        return items;
    }

    private List<ManagerDashboardState.InventoryItem> parseInventoryItems(JsonArray array) {
        List<ManagerDashboardState.InventoryItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) continue;
            items.add(new ManagerDashboardState.InventoryItem(
                    readString(object, "code", ""),
                    readString(object, "name", "-"),
                    readString(object, "categoryName", ""),
                    readString(object, "unit", ""),
                    decimalToString(object, "qty", "0"),
                    readString(object, "statusLabel", readString(object, "status", "-")),
                    readString(object, "statusColor", "info")
            ));
        }
        return items;
    }
}

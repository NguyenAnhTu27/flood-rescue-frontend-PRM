package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.AdminPermissionMatrixState;
import com.floodrescue.mobile.data.model.ui.AdminPermissionOption;
import com.floodrescue.mobile.data.model.ui.AdminRoleOption;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPermissionRepository {

    private final ApiService apiService;

    public AdminPermissionRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadMatrix(RepositoryCallback<AdminPermissionMatrixState> callback) {
        apiService.getAdminPermissions().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được ma trận phân quyền."));
                    return;
                }
                JsonObject root = readObject(response.body());
                if (root == null) {
                    callback.onError("Dữ liệu phân quyền không hợp lệ.");
                    return;
                }
                callback.onSuccess(new AdminPermissionMatrixState(
                        parseRoles(readArray(root, "roles")),
                        parsePermissions(readArray(root, "permissions")),
                        parseRolePermissions(root.has("rolePermissions") && root.get("rolePermissions").isJsonObject()
                                ? root.getAsJsonObject("rolePermissions") : null)
                ));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(defaultFailureMessage(throwable));
            }
        });
    }

    public void updateRolePermissions(String roleCode, List<String> permissions, RepositoryCallback<String> callback) {
        JsonObject payload = new JsonObject();
        JsonArray array = new JsonArray();
        if (permissions != null) {
            for (String code : permissions) {
                if (code != null && !code.trim().isEmpty()) {
                    array.add(code.trim());
                }
            }
        }
        payload.add("permissions", array);
        apiService.updateAdminRolePermissions(roleCode, payload).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful()) {
                    callback.onError(parseApiMessage(response, "Không lưu được phân quyền."));
                    return;
                }
                callback.onSuccess(parseBodyMessage(response.body(), "Đã lưu phân quyền."));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(defaultFailureMessage(throwable));
            }
        });
    }

    private List<AdminRoleOption> parseRoles(JsonArray array) {
        List<AdminRoleOption> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new AdminRoleOption(
                    readInt(object, "id"),
                    readString(object, "code", ""),
                    readString(object, "name", "Vai trò"),
                    readLong(object, "userCount")
            ));
        }
        return items;
    }

    private List<AdminPermissionOption> parsePermissions(JsonArray array) {
        List<AdminPermissionOption> items = new ArrayList<>();
        if (array == null) {
            return items;
        }
        for (JsonElement element : array) {
            JsonObject object = readObject(element);
            if (object == null) {
                continue;
            }
            items.add(new AdminPermissionOption(
                    readInt(object, "id"),
                    readString(object, "code", ""),
                    readString(object, "name", "Quyền hệ thống"),
                    readString(object, "module", "SYSTEM")
            ));
        }
        return items;
    }

    private Map<String, List<String>> parseRolePermissions(JsonObject object) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        if (object == null) {
            return result;
        }
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            JsonArray array = entry.getValue() != null && entry.getValue().isJsonArray() ? entry.getValue().getAsJsonArray() : null;
            List<String> codes = new ArrayList<>();
            if (array != null) {
                for (JsonElement element : array) {
                    if (element == null || element.isJsonNull()) {
                        continue;
                    }
                    try {
                        codes.add(element.getAsString());
                    } catch (Exception ignored) {
                        // Skip malformed permission.
                    }
                }
            }
            result.put(entry.getKey(), codes);
        }
        return result;
    }

    private String parseBodyMessage(JsonElement body, String fallback) {
        try {
            JsonObject object = readObject(body);
            if (object != null && object.has("message") && !object.get("message").isJsonNull()) {
                return object.get("message").getAsString();
            }
        } catch (Exception ignored) {
            // Use fallback.
        }
        return fallback;
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
        try {
            if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
                return fallback;
            }
            String value = object.get(key).getAsString();
            return value == null || value.trim().isEmpty() ? fallback : value.trim();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private int readInt(JsonObject object, String key) {
        try {
            if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
                return 0;
            }
            return object.get(key).getAsInt();
        } catch (Exception ignored) {
            return 0;
        }
    }

    private long readLong(JsonObject object, String key) {
        try {
            if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
                return 0L;
            }
            return object.get(key).getAsLong();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private String defaultFailureMessage(Throwable throwable) {
        return throwable == null || throwable.getMessage() == null
                ? "Không thể kết nối tới server."
                : throwable.getMessage();
    }

    private String parseApiMessage(Response<?> response, String fallback) {
        try {
            if (response != null && response.errorBody() != null) {
                String raw = response.errorBody().string();
                JSONObject jsonObject = new JSONObject(raw);
                String message = jsonObject.optString("message");
                if (!message.isEmpty()) {
                    return message;
                }
            }
        } catch (Exception ignored) {
            // Use fallback.
        }
        return fallback;
    }
}

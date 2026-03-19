package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.ui.AdminRoleOption;
import com.floodrescue.mobile.data.model.ui.AdminTeamOption;
import com.floodrescue.mobile.data.model.ui.AdminUserItem;
import com.floodrescue.mobile.data.model.ui.AdminUserPage;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserRepository {

    private final ApiService apiService;

    public AdminUserRepository(Context context) {
        this.apiService = ApiClient.create(context);
    }

    public void loadUsers(int page, int size, String keyword, Integer roleId, RepositoryCallback<AdminUserPage> callback) {
        apiService.getAdminUsers(page, size, keyword, roleId).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được danh sách người dùng."));
                    return;
                }
                JsonObject root = readObject(response.body());
                if (root == null) {
                    callback.onError("Dữ liệu người dùng không hợp lệ.");
                    return;
                }
                List<AdminUserItem> users = new ArrayList<>();
                JsonArray array = readArray(root, "users");
                if (array != null) {
                    for (JsonElement element : array) {
                        JsonObject object = readObject(element);
                        if (object == null) {
                            continue;
                        }
                        users.add(new AdminUserItem(
                                readLong(object, "id"),
                                readString(object, "fullName", "Người dùng"),
                                readString(object, "email", ""),
                                readString(object, "phone", ""),
                                readString(object, "status", "ACTIVE"),
                                readInt(object, "roleId"),
                                readString(object, "role", ""),
                                readString(object, "createdAt", "")
                        ));
                    }
                }
                callback.onSuccess(new AdminUserPage(
                        users,
                        readLong(root, "totalUsers"),
                        Math.max(1, readInt(root, "totalPages")),
                        Math.max(0, readInt(root, "page"))
                ));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(defaultFailureMessage(throwable));
            }
        });
    }

    public void loadRoles(RepositoryCallback<List<AdminRoleOption>> callback) {
        apiService.getAdminPermissions().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được vai trò hệ thống."));
                    return;
                }
                JsonObject root = readObject(response.body());
                JsonArray array = readArray(root, "roles");
                List<AdminRoleOption> items = new ArrayList<>();
                if (array != null) {
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
                }
                callback.onSuccess(items);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(defaultFailureMessage(throwable));
            }
        });
    }

    public void loadTeams(RepositoryCallback<List<AdminTeamOption>> callback) {
        apiService.getAdminTeams().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(parseApiMessage(response, "Không tải được danh sách đội cứu hộ."));
                    return;
                }
                JsonArray array = response.body().isJsonArray() ? response.body().getAsJsonArray() : null;
                List<AdminTeamOption> items = new ArrayList<>();
                if (array != null) {
                    for (JsonElement element : array) {
                        JsonObject object = readObject(element);
                        if (object == null) {
                            continue;
                        }
                        items.add(new AdminTeamOption(
                                readLong(object, "id"),
                                readString(object, "code", ""),
                                readString(object, "name", "Đội cứu hộ"),
                                readString(object, "status", "")
                        ));
                    }
                }
                callback.onSuccess(items);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onError(defaultFailureMessage(throwable));
            }
        });
    }

    public void createUser(
            String fullName,
            String email,
            String phone,
            String password,
            int roleId,
            Long teamId,
            RepositoryCallback<String> callback
    ) {
        JsonObject payload = new JsonObject();
        payload.addProperty("fullName", fullName);
        payload.addProperty("email", email);
        payload.addProperty("phone", phone);
        payload.addProperty("password", password);
        payload.addProperty("roleId", roleId);
        if (teamId != null && teamId > 0L) {
            payload.addProperty("teamId", teamId);
        }
        apiService.createAdminUser(payload).enqueue(new MessageCallback(callback, "Tạo tài khoản thất bại."));
    }

    public void updateUser(
            long userId,
            String fullName,
            String email,
            String phone,
            int roleId,
            String status,
            RepositoryCallback<String> callback
    ) {
        JsonObject payload = new JsonObject();
        payload.addProperty("fullName", fullName);
        payload.addProperty("email", email);
        payload.addProperty("phone", phone);
        payload.addProperty("roleId", roleId);
        payload.addProperty("status", status);
        apiService.updateAdminUser(userId, payload).enqueue(new MessageCallback(callback, "Cập nhật người dùng thất bại."));
    }

    public void updateUserStatus(long userId, String status, RepositoryCallback<String> callback) {
        JsonObject payload = new JsonObject();
        payload.addProperty("status", status);
        apiService.updateAdminUserStatus(userId, payload).enqueue(new MessageCallback(callback, "Không cập nhật được trạng thái người dùng."));
    }

    public void resetPassword(long userId, String password, RepositoryCallback<String> callback) {
        JsonObject payload = new JsonObject();
        payload.addProperty("password", password);
        apiService.resetAdminUserPassword(userId, payload).enqueue(new MessageCallback(callback, "Không reset được mật khẩu."));
    }

    public void deleteUser(long userId, RepositoryCallback<String> callback) {
        apiService.deleteAdminUser(userId).enqueue(new MessageCallback(callback, "Không xoá được người dùng."));
    }

    private class MessageCallback implements Callback<JsonElement> {
        private final RepositoryCallback<String> callback;
        private final String fallback;

        MessageCallback(RepositoryCallback<String> callback, String fallback) {
            this.callback = callback;
            this.fallback = fallback;
        }

        @Override
        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
            if (!response.isSuccessful()) {
                callback.onError(parseApiMessage(response, fallback));
                return;
            }
            callback.onSuccess(parseBodyMessage(response.body(), "Thành công"));
        }

        @Override
        public void onFailure(Call<JsonElement> call, Throwable throwable) {
            callback.onError(defaultFailureMessage(throwable));
        }
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

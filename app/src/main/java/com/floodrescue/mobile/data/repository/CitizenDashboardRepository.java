package com.floodrescue.mobile.data.repository;

import android.content.Context;

import com.floodrescue.mobile.core.network.ApiClient;
import com.floodrescue.mobile.core.network.ApiService;
import com.floodrescue.mobile.data.model.response.UnreadCountResponse;
import com.floodrescue.mobile.data.model.response.UserProfileResponse;
import com.floodrescue.mobile.data.model.ui.CitizenDashboardState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitizenDashboardRepository {

    private static final int REQUEST_COUNT = 4;

    private final ApiService apiService;

    public CitizenDashboardRepository(Context context) {
        apiService = ApiClient.create(context);
    }

    public void loadDashboard(
            String fallbackFullName,
            String fallbackRole,
            RepositoryCallback<CitizenDashboardState> callback
    ) {
        DashboardAccumulator accumulator = new DashboardAccumulator(fallbackFullName, fallbackRole, callback);

        loadProfile(accumulator);
        loadUnreadCount(accumulator);
        loadHighlightNotification(accumulator);
        loadLatestRescue(accumulator);
    }

    private void loadProfile(DashboardAccumulator accumulator) {
        apiService.getMyProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accumulator.setProfile(response.body());
                }
                accumulator.completeRequest();
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable throwable) {
                accumulator.completeRequest();
            }
        });
    }

    private void loadUnreadCount(DashboardAccumulator accumulator) {
        apiService.getUnreadNotificationCount().enqueue(new Callback<UnreadCountResponse>() {
            @Override
            public void onResponse(Call<UnreadCountResponse> call, Response<UnreadCountResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUnreadCount() != null) {
                    accumulator.setUnreadCount(response.body().getUnreadCount().intValue());
                }
                accumulator.completeRequest();
            }

            @Override
            public void onFailure(Call<UnreadCountResponse> call, Throwable throwable) {
                accumulator.completeRequest();
            }
        });
    }

    private void loadHighlightNotification(DashboardAccumulator accumulator) {
        apiService.getMyNotifications(Boolean.TRUE, 0, 1).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonArray content = getArray(getObject(response.body()), "content");
                    if (content != null && content.size() > 0 && content.get(0).isJsonObject()) {
                        JsonObject item = content.get(0).getAsJsonObject();
                        accumulator.setHighlightNotification(new CitizenDashboardState.HighlightNotification(
                                readString(item, "title", "Thông báo mới"),
                                readString(item, "content", "Bạn có thông báo mới từ hệ thống."),
                                readBoolean(item, "urgent")
                        ));
                    }
                }
                accumulator.completeRequest();
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                accumulator.completeRequest();
            }
        });
    }

    private void loadLatestRescue(DashboardAccumulator accumulator) {
        apiService.getCitizenRescueRequests(0, 1, null).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonArray content = getArray(getObject(response.body()), "content");
                    if (content != null && content.size() > 0 && content.get(0).isJsonObject()) {
                        JsonObject item = content.get(0).getAsJsonObject();
                        accumulator.setLatestRequest(new CitizenDashboardState.RescueSummary(
                                readLong(item, "id"),
                                readString(item, "code", "Yêu cầu cứu hộ"),
                                readString(item, "status", ""),
                                readString(item, "priority", ""),
                                readString(item, "addressText", "Chưa có địa chỉ"),
                                readString(item, "description", "Chưa có mô tả chi tiết."),
                                readString(item, "updatedAt", readString(item, "createdAt", "")),
                                readBoolean(item, "waitingForTeam"),
                                readBoolean(item, "locationVerified")
                        ));
                    }
                }
                accumulator.completeRequest();
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                accumulator.completeRequest();
            }
        });
    }

    private static JsonObject getObject(JsonElement element) {
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    private static JsonArray getArray(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return null;
        }
        return object.get(key).getAsJsonArray();
    }

    private static String readString(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        String value = object.get(key).getAsString();
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private static boolean readBoolean(JsonObject object, String key) {
        return object != null
                && object.has(key)
                && !object.get(key).isJsonNull()
                && object.get(key).getAsBoolean();
    }

    private static long readLong(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return -1L;
        }
        return object.get(key).getAsLong();
    }

    private static final class DashboardAccumulator {
        private final String fallbackFullName;
        private final String fallbackRole;
        private final RepositoryCallback<CitizenDashboardState> callback;

        private int completedRequests;
        private String fullName;
        private String role;
        private boolean rescueRequestBlocked;
        private String rescueRequestBlockedReason;
        private int unreadCount;
        private CitizenDashboardState.HighlightNotification highlightNotification;
        private CitizenDashboardState.RescueSummary latestRequest;

        private DashboardAccumulator(
                String fallbackFullName,
                String fallbackRole,
                RepositoryCallback<CitizenDashboardState> callback
        ) {
            this.fallbackFullName = fallbackFullName;
            this.fallbackRole = fallbackRole;
            this.callback = callback;
        }

        private synchronized void setProfile(UserProfileResponse response) {
            fullName = response.getFullName();
            role = response.getRole();
            rescueRequestBlocked = Boolean.TRUE.equals(response.getRescueRequestBlocked());
            rescueRequestBlockedReason = response.getRescueRequestBlockedReason();
        }

        private synchronized void setUnreadCount(int unreadCount) {
            this.unreadCount = Math.max(unreadCount, 0);
        }

        private synchronized void setHighlightNotification(CitizenDashboardState.HighlightNotification notification) {
            highlightNotification = notification;
        }

        private synchronized void setLatestRequest(CitizenDashboardState.RescueSummary latestRequest) {
            this.latestRequest = latestRequest;
        }

        private synchronized void completeRequest() {
            completedRequests++;
            if (completedRequests < REQUEST_COUNT) {
                return;
            }

            if (highlightNotification == null && unreadCount > 0) {
                String title = rescueRequestBlocked
                        ? "Tài khoản bị hạn chế"
                        : "Bạn có thông báo mới";
                String content = rescueRequestBlocked && rescueRequestBlockedReason != null && !rescueRequestBlockedReason.trim().isEmpty()
                        ? rescueRequestBlockedReason.trim()
                        : "Mở trung tâm thông báo để xem cập nhật mới nhất từ hệ thống.";
                highlightNotification = new CitizenDashboardState.HighlightNotification(title, content, rescueRequestBlocked);
            }

            callback.onSuccess(new CitizenDashboardState(
                    isBlank(fullName) ? fallbackFullName : fullName,
                    isBlank(role) ? fallbackRole : role,
                    rescueRequestBlocked,
                    rescueRequestBlockedReason,
                    unreadCount,
                    highlightNotification,
                    latestRequest
            ));
        }

        private boolean isBlank(String value) {
            return value == null || value.trim().isEmpty();
        }
    }
}

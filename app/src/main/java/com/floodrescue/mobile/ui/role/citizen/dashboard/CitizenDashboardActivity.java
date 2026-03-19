package com.floodrescue.mobile.ui.role.citizen.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.data.model.ui.CitizenDashboardState;
import com.floodrescue.mobile.data.repository.CitizenDashboardRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.citizen.feedback.CitizenFeedbackActivity;
import com.floodrescue.mobile.ui.role.citizen.rescue.create.CreateRescueRequestActivity;
import com.floodrescue.mobile.ui.role.citizen.rescue.detail.CitizenRescueDetailActivity;
import com.floodrescue.mobile.ui.role.citizen.rescue.list.CitizenRescueListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

public class CitizenDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private CitizenDashboardRepository dashboardRepository;
    @Nullable
    private CitizenDashboardState.RescueSummary latestRequest;

    private View alertCard;
    private TextView textAlertTitle;
    private TextView textAlertContent;
    private TextView textCitizenName;
    private TextView textCitizenRole;
    private TextView textCitizenInitial;
    private TextView textUnreadSummary;
    private TextView textNotificationBadge;
    private ProgressBar progressDashboard;
    private View cardRecentRequest;
    private TextView textRecentRequestCode;
    private TextView textRecentRequestTime;
    private TextView textRecentRequestStatus;
    private TextView textRecentRequestAddress;
    private TextView textRecentRequestDescription;
    private TextView textRecentPriority;
    private TextView textRecentFlags;
    private TextView textRecentEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_citizen_dashboard);

        sessionManager = new SessionManager(this);
        dashboardRepository = new CitizenDashboardRepository(this);

        bindViews();
        bindNavigation();
        renderSessionFallback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboard();
    }

    private void bindViews() {
        alertCard = findViewById(R.id.cardDashboardAlert);
        textAlertTitle = findViewById(R.id.textAlertTitle);
        textAlertContent = findViewById(R.id.textAlertContent);
        textCitizenName = findViewById(R.id.textCitizenName);
        textCitizenRole = findViewById(R.id.textCitizenRole);
        textCitizenInitial = findViewById(R.id.textCitizenInitial);
        textUnreadSummary = findViewById(R.id.textUnreadSummary);
        textNotificationBadge = findViewById(R.id.textNotificationBadge);
        progressDashboard = findViewById(R.id.progressDashboard);
        cardRecentRequest = findViewById(R.id.cardRecentRequest);
        textRecentRequestCode = findViewById(R.id.textRecentRequestCode);
        textRecentRequestTime = findViewById(R.id.textRecentRequestTime);
        textRecentRequestStatus = findViewById(R.id.textRecentRequestStatus);
        textRecentRequestAddress = findViewById(R.id.textRecentRequestAddress);
        textRecentRequestDescription = findViewById(R.id.textRecentRequestDescription);
        textRecentPriority = findViewById(R.id.textRecentPriority);
        textRecentFlags = findViewById(R.id.textRecentFlags);
        textRecentEmpty = findViewById(R.id.textRecentEmpty);
    }

    private void bindNavigation() {
        findViewById(R.id.buttonOpenNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.buttonCreateRescue).setOnClickListener(v -> openCreateRescue());
        findViewById(R.id.cardCitizenRequestList).setOnClickListener(v -> openRescueList());
        findViewById(R.id.cardCitizenHistory).setOnClickListener(v -> openRescueList());
        findViewById(R.id.cardCitizenProfile).setOnClickListener(v -> AppNavigator.openProfile(this));
        findViewById(R.id.cardCitizenFeedback).setOnClickListener(v -> startActivity(new Intent(this, CitizenFeedbackActivity.class)));
        findViewById(R.id.buttonOpenRequestList).setOnClickListener(v -> openRescueList());

        findViewById(R.id.navHome).setOnClickListener(v -> {
            // Already on home.
        });
        findViewById(R.id.navRequestList).setOnClickListener(v -> openRescueList());
        findViewById(R.id.navCreateRescue).setOnClickListener(v -> openCreateRescue());
        findViewById(R.id.navNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.navProfile).setOnClickListener(v -> AppNavigator.openProfile(this));
        cardRecentRequest.setOnClickListener(v -> openRecentRequest());
    }

    private void renderSessionFallback() {
        String fullName = AppNavigator.displayName(sessionManager);
        textCitizenName.setText(fullName);
        textCitizenRole.setText(AppNavigator.displayRole(sessionManager.getRole()));
        textCitizenInitial.setText(AppNavigator.initials(fullName));
        textUnreadSummary.setText(getString(R.string.citizen_dashboard_unread_empty));
        textNotificationBadge.setVisibility(View.GONE);
        alertCard.setVisibility(View.GONE);
    }

    private void loadDashboard() {
        progressDashboard.setVisibility(View.VISIBLE);
        dashboardRepository.loadDashboard(
                AppNavigator.displayName(sessionManager),
                sessionManager.getRole(),
                new RepositoryCallback<CitizenDashboardState>() {
                    @Override
                    public void onSuccess(CitizenDashboardState data) {
                        runOnUiThread(() -> {
                            progressDashboard.setVisibility(View.GONE);
                            renderState(data);
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> progressDashboard.setVisibility(View.GONE));
                    }
                }
        );
    }

    private void renderState(CitizenDashboardState state) {
        String fullName = isBlank(state.getFullName()) ? AppNavigator.displayName(sessionManager) : state.getFullName();
        textCitizenName.setText(fullName);
        textCitizenRole.setText(AppNavigator.displayRole(state.getRole()));
        textCitizenInitial.setText(AppNavigator.initials(fullName));

        renderNotificationState(state);
        renderRecentRequest(state.getLatestRequest());
    }

    private void renderNotificationState(CitizenDashboardState state) {
        int unreadCount = Math.max(state.getUnreadCount(), 0);
        if (unreadCount <= 0) {
            textUnreadSummary.setText(getString(R.string.citizen_dashboard_unread_empty));
            textNotificationBadge.setVisibility(View.GONE);
            alertCard.setVisibility(View.GONE);
            return;
        }

        textUnreadSummary.setText(unreadCount == 1
                ? getString(R.string.citizen_dashboard_unread_one)
                : getString(R.string.citizen_dashboard_unread_many, unreadCount));

        textNotificationBadge.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
        textNotificationBadge.setVisibility(View.VISIBLE);

        CitizenDashboardState.HighlightNotification notification = state.getHighlightNotification();
        if (notification == null) {
            alertCard.setVisibility(View.GONE);
            return;
        }

        alertCard.setVisibility(View.VISIBLE);
        alertCard.setBackgroundResource(notification.isUrgent()
                ? R.drawable.bg_citizen_dashboard_alert_urgent
                : R.drawable.bg_citizen_dashboard_alert);
        textAlertTitle.setText(notification.getTitle());
        textAlertContent.setText(notification.getContent());
        int titleColor = ContextCompat.getColor(this, notification.isUrgent() ? R.color.danger : R.color.accent_dark);
        textAlertTitle.setTextColor(titleColor);
    }

    private void renderRecentRequest(@Nullable CitizenDashboardState.RescueSummary latestRequest) {
        this.latestRequest = latestRequest;
        if (latestRequest == null) {
            cardRecentRequest.setVisibility(View.GONE);
            textRecentEmpty.setVisibility(View.VISIBLE);
            return;
        }

        cardRecentRequest.setVisibility(View.VISIBLE);
        textRecentEmpty.setVisibility(View.GONE);

        textRecentRequestCode.setText(fallback(latestRequest.getCode(), getString(R.string.citizen_dashboard_recent_placeholder_code)));
        textRecentRequestTime.setText(formatUpdatedAt(latestRequest.getUpdatedAt()));
        textRecentRequestStatus.setText(mapStatus(latestRequest.getStatus()));
        textRecentRequestAddress.setText(fallback(latestRequest.getAddressText(), getString(R.string.citizen_dashboard_address_placeholder)));
        textRecentRequestDescription.setText(fallback(latestRequest.getDescription(), getString(R.string.citizen_dashboard_description_placeholder)));
        textRecentPriority.setText(mapPriority(latestRequest.getPriority()));
        textRecentFlags.setText(buildFlags(latestRequest));
        applyStatusStyle(textRecentRequestStatus, latestRequest.getStatus());
        applyPriorityStyle(textRecentPriority, latestRequest.getPriority());
    }

    private void applyStatusStyle(TextView view, String status) {
        String normalized = normalize(status);
        if ("COMPLETED".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else if ("CANCELLED".equals(normalized) || "DUPLICATE".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(ContextCompat.getColor(this, R.color.danger));
        } else if ("ASSIGNED".equals(normalized) || "IN_PROGRESS".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(ContextCompat.getColor(this, R.color.accent_dark));
        } else {
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(ContextCompat.getColor(this, R.color.warning));
        }
    }

    private void applyPriorityStyle(TextView view, String priority) {
        String normalized = normalize(priority);
        if ("HIGH".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(ContextCompat.getColor(this, R.color.danger));
        } else if ("LOW".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else {
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(ContextCompat.getColor(this, R.color.warning));
        }
    }

    private String mapStatus(String status) {
        switch (normalize(status)) {
            case "PENDING":
                return "Chờ xác minh";
            case "VERIFIED":
                return "Đã xác minh";
            case "ASSIGNED":
                return "Đã phân công";
            case "IN_PROGRESS":
                return "Đang xử lý";
            case "COMPLETED":
                return "Đã hoàn thành";
            case "CANCELLED":
                return "Đã hủy";
            case "DUPLICATE":
                return "Trùng lặp";
            default:
                return getString(R.string.citizen_dashboard_status_default);
        }
    }

    private String mapPriority(String priority) {
        switch (normalize(priority)) {
            case "HIGH":
                return "Ưu tiên cao";
            case "LOW":
                return "Ưu tiên thấp";
            case "MEDIUM":
                return "Ưu tiên vừa";
            default:
                return getString(R.string.citizen_dashboard_priority_default);
        }
    }

    private String buildFlags(CitizenDashboardState.RescueSummary latestRequest) {
        if (latestRequest.isWaitingForTeam()) {
            return getString(R.string.citizen_dashboard_flag_waiting);
        }
        return latestRequest.isLocationVerified()
                ? getString(R.string.citizen_dashboard_flag_verified)
                : getString(R.string.citizen_dashboard_flag_unverified);
    }

    private String formatUpdatedAt(String raw) {
        if (isBlank(raw)) {
            return getString(R.string.citizen_dashboard_updated_default);
        }
        String cleaned = raw.replace('T', ' ');
        return "Cập nhật: " + cleaned.replace(".000", "");
    }

    private void openRescueList() {
        startActivity(new Intent(this, CitizenRescueListActivity.class));
    }

    private void openRecentRequest() {
        if (latestRequest == null || latestRequest.getId() <= 0L) {
            return;
        }
        Intent intent = new Intent(this, CitizenRescueDetailActivity.class);
        intent.putExtra(CitizenRescueDetailActivity.EXTRA_REQUEST_ID, latestRequest.getId());
        startActivity(intent);
    }

    private void openCreateRescue() {
        startActivity(new Intent(this, CreateRescueRequestActivity.class));
    }

    private String fallback(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(java.util.Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

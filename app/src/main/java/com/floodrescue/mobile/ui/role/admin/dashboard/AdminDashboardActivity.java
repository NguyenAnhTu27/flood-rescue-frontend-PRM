package com.floodrescue.mobile.ui.role.admin.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.data.model.ui.AdminDashboardState;
import com.floodrescue.mobile.data.repository.AdminDashboardRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.admin.audit.AdminAuditActivity;
import com.floodrescue.mobile.ui.role.admin.permission.AdminPermissionActivity;
import com.floodrescue.mobile.ui.role.admin.setting.AdminSettingActivity;
import com.floodrescue.mobile.ui.role.admin.user.list.AdminUserListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminDashboardActivity extends BaseActivity {

    private AdminDashboardRepository repository;
    private SessionManager sessionManager;

    private TextView textAdminInitial;
    private TextView textAdminGreeting;
    private TextView textAdminName;
    private TextView textAdminRole;
    private TextView textAdminSystemStatus;
    private TextView textAdminTotalUsers;
    private TextView textAdminActiveUsers;
    private TextView textAdminLockedUsers;
    private TextView textAdminDashboardError;
    private TextView textAdminRecentEmpty;
    private ProgressBar progressAdminDashboard;
    private LinearLayout containerAdminRecentActivities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_admin_dashboard);

        repository = new AdminDashboardRepository(this);
        sessionManager = new SessionManager(this);

        bindViews();
        bindStaticContent();
        bindActions();
        loadDashboard();
    }

    private void bindViews() {
        textAdminInitial = findViewById(R.id.textAdminInitial);
        textAdminGreeting = findViewById(R.id.textAdminGreeting);
        textAdminName = findViewById(R.id.textAdminName);
        textAdminRole = findViewById(R.id.textAdminRole);
        textAdminSystemStatus = findViewById(R.id.textAdminSystemStatus);
        textAdminTotalUsers = findViewById(R.id.cardAdminStatTotal).findViewById(R.id.textStatValue);
        textAdminActiveUsers = findViewById(R.id.cardAdminStatActive).findViewById(R.id.textStatValue);
        textAdminLockedUsers = findViewById(R.id.cardAdminStatLocked).findViewById(R.id.textStatValue);
        textAdminDashboardError = findViewById(R.id.textAdminDashboardError);
        textAdminRecentEmpty = findViewById(R.id.textAdminRecentEmpty);
        progressAdminDashboard = findViewById(R.id.progressAdminDashboard);
        containerAdminRecentActivities = findViewById(R.id.containerAdminRecentActivities);

        ((TextView) findViewById(R.id.cardAdminStatTotal).findViewById(R.id.textStatLabel))
                .setText(R.string.admin_dashboard_total_users);
        ((TextView) findViewById(R.id.cardAdminStatActive).findViewById(R.id.textStatLabel))
                .setText(R.string.admin_dashboard_active_users);
        ((TextView) findViewById(R.id.cardAdminStatLocked).findViewById(R.id.textStatLabel))
                .setText(R.string.admin_dashboard_locked_users);
    }

    private void bindStaticContent() {
        String displayName = AppNavigator.displayName(sessionManager);
        textAdminInitial.setText(AppNavigator.initials(displayName));
        textAdminGreeting.setText(getString(R.string.admin_dashboard_subtitle));
        textAdminName.setText(getString(R.string.admin_dashboard_greeting, displayName));
        textAdminRole.setText(AppNavigator.displayRole(sessionManager.getRole()));
        textAdminSystemStatus.setText(getString(
                R.string.admin_dashboard_system_status_time,
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date())
        ));
    }

    private void bindActions() {
        findViewById(R.id.buttonAdminNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.cardAdminUsers).setOnClickListener(v -> startActivity(new Intent(this, AdminUserListActivity.class)));
        findViewById(R.id.cardAdminPermissions).setOnClickListener(v -> startActivity(new Intent(this, AdminPermissionActivity.class)));
        findViewById(R.id.cardAdminAudit).setOnClickListener(v -> startActivity(new Intent(this, AdminAuditActivity.class)));
        findViewById(R.id.cardAdminSettings).setOnClickListener(v -> startActivity(new Intent(this, AdminSettingActivity.class)));
        findViewById(R.id.navAdminOverview).setOnClickListener(v -> { });
        findViewById(R.id.navAdminUsers).setOnClickListener(v -> startActivity(new Intent(this, AdminUserListActivity.class)));
        findViewById(R.id.navAdminReports).setOnClickListener(v -> startActivity(new Intent(this, AdminAuditActivity.class)));
        findViewById(R.id.navAdminSettings).setOnClickListener(v -> startActivity(new Intent(this, AdminSettingActivity.class)));
    }

    private void loadDashboard() {
        setLoading(true);
        repository.loadDashboard(new RepositoryCallback<AdminDashboardState>() {
            @Override
            public void onSuccess(AdminDashboardState data) {
                setLoading(false);
                renderDashboard(data);
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                textAdminDashboardError.setVisibility(View.VISIBLE);
                textAdminDashboardError.setText(message == null ? getString(R.string.admin_dashboard_error) : message);
                textAdminRecentEmpty.setVisibility(View.VISIBLE);
                textAdminRecentEmpty.setText(R.string.admin_dashboard_recent_empty);
            }
        });
    }

    private void renderDashboard(AdminDashboardState state) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(new Locale("vi", "VN"));
        textAdminTotalUsers.setText(numberFormat.format(state.getTotalUsers()));
        textAdminActiveUsers.setText(numberFormat.format(state.getActiveUsers()));
        textAdminLockedUsers.setText(numberFormat.format(state.getLockedUsers()));
        textAdminSystemStatus.setText(getString(
                R.string.admin_dashboard_meta,
                state.getRoleCount(),
                state.getSettingCount()
        ));

        containerAdminRecentActivities.removeAllViews();
        if (state.getRecentActivities() == null || state.getRecentActivities().isEmpty()) {
            textAdminRecentEmpty.setVisibility(View.VISIBLE);
            textAdminRecentEmpty.setText(R.string.admin_dashboard_recent_empty);
            return;
        }
        textAdminRecentEmpty.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (AdminDashboardState.RecentActivityItem item : state.getRecentActivities()) {
            View view = inflater.inflate(R.layout.item_admin_recent_activity, containerAdminRecentActivities, false);
            TextView badge = view.findViewById(R.id.textAdminActivityBadge);
            TextView title = view.findViewById(R.id.textAdminActivityTitle);
            TextView detail = view.findViewById(R.id.textAdminActivityDetail);
            TextView time = view.findViewById(R.id.textAdminActivityTime);

            String action = safe(item.getAction(), "LOG");
            badge.setText(action.substring(0, Math.min(2, action.length())).toUpperCase(Locale.ROOT));
            title.setText(action);
            String summary = safe(item.getDetail(), "");
            if (!safe(item.getActor(), "").isEmpty()) {
                summary = safe(item.getActor(), "") + (summary.isEmpty() ? "" : " • " + summary);
            }
            detail.setText(summary.isEmpty() ? getString(R.string.admin_dashboard_recent_empty) : summary);
            time.setText(formatDateTime(item.getCreatedAt()));
            containerAdminRecentActivities.addView(view);
        }
    }

    private void setLoading(boolean loading) {
        progressAdminDashboard.setVisibility(loading ? View.VISIBLE : View.GONE);
        textAdminDashboardError.setVisibility(View.GONE);
        if (loading) {
            containerAdminRecentActivities.removeAllViews();
            textAdminRecentEmpty.setVisibility(View.VISIBLE);
            textAdminRecentEmpty.setText(R.string.admin_dashboard_recent_loading);
        }
    }

    private String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private String formatDateTime(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "--";
        }
        String value = raw.trim().replace('T', ' ');
        int dotIndex = value.indexOf('.');
        if (dotIndex > 0) {
            value = value.substring(0, dotIndex);
        }
        return value.length() > 16 ? value.substring(0, 16) : value;
    }
}

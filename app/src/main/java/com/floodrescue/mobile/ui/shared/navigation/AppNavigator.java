package com.floodrescue.mobile.ui.shared.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.auth.login.LoginActivity;
import com.floodrescue.mobile.ui.role.admin.dashboard.AdminDashboardActivity;
import com.floodrescue.mobile.ui.role.citizen.dashboard.CitizenDashboardActivity;
import com.floodrescue.mobile.ui.role.coordinator.dashboard.CoordinatorDashboardActivity;
import com.floodrescue.mobile.ui.role.manager.dashboard.ManagerDashboardActivity;
import com.floodrescue.mobile.ui.role.rescuer.dashboard.RescuerDashboardActivity;
import com.floodrescue.mobile.ui.shared.map.SafeMapActivity;
import com.floodrescue.mobile.ui.shared.notification.NotificationActivity;
import com.floodrescue.mobile.ui.shared.profile.ProfileActivity;

public final class AppNavigator {

    public static final String ROLE_CITIZEN = "CITIZEN";
    public static final String ROLE_COORDINATOR = "COORDINATOR";
    public static final String ROLE_RESCUER = "RESCUER";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_ADMIN = "ADMIN";

    private AppNavigator() {
    }

    public static Intent homeIntent(Context context, String role) {
        switch (normalizeRole(role)) {
            case ROLE_CITIZEN:
                return new Intent(context, CitizenDashboardActivity.class);
            case ROLE_COORDINATOR:
                return new Intent(context, CoordinatorDashboardActivity.class);
            case ROLE_RESCUER:
                return new Intent(context, RescuerDashboardActivity.class);
            case ROLE_MANAGER:
                return new Intent(context, ManagerDashboardActivity.class);
            case ROLE_ADMIN:
                return new Intent(context, AdminDashboardActivity.class);
            default:
                return new Intent(context, LoginActivity.class);
        }
    }

    public static void openHome(Activity activity) {
        SessionManager sessionManager = new SessionManager(activity);
        activity.startActivity(homeIntent(activity, sessionManager.getRole()));
        activity.finish();
    }

    public static void openMap(Activity activity) {
        activity.startActivity(new Intent(activity, SafeMapActivity.class));
        activity.finish();
    }

    public static void openNotifications(Activity activity) {
        activity.startActivity(new Intent(activity, NotificationActivity.class));
        activity.finish();
    }

    public static void openProfile(Activity activity) {
        activity.startActivity(new Intent(activity, ProfileActivity.class));
        activity.finish();
    }

    public static void logout(Activity activity) {
        new SessionManager(activity).clearSession();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static String normalizeRole(String role) {
        if (role == null) {
            return "";
        }
        return role.trim().toUpperCase(java.util.Locale.ROOT);
    }

    public static String displayRole(String role) {
        switch (normalizeRole(role)) {
            case ROLE_CITIZEN:
                return "Công dân";
            case ROLE_COORDINATOR:
                return "Điều phối";
            case ROLE_RESCUER:
                return "Đội cứu hộ";
            case ROLE_MANAGER:
                return "Quản lý cứu trợ";
            case ROLE_ADMIN:
                return "Quản trị hệ thống";
            default:
                return "Người dùng";
        }
    }

    public static String displayName(SessionManager sessionManager) {
        String fullName = sessionManager.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Người dùng";
        }
        return fullName.trim();
    }

    public static String initials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "FR";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(java.util.Locale.ROOT);
        }
        String first = parts[0].substring(0, 1);
        String last = parts[parts.length - 1].substring(0, 1);
        return (first + last).toUpperCase(java.util.Locale.ROOT);
    }
}

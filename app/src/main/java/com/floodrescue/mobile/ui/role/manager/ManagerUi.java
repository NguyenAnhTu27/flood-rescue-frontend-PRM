package com.floodrescue.mobile.ui.role.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.ui.role.manager.dashboard.ManagerDashboardActivity;
import com.floodrescue.mobile.ui.role.manager.dispatch.ManagerDispatchActivity;
import com.floodrescue.mobile.ui.role.manager.relief.list.ManagerReliefListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public final class ManagerUi {

    public static final String EXTRA_REQUEST_ID = "manager_request_id";
    public static final String EXTRA_REQUEST_CODE = "manager_request_code";

    private ManagerUi() {
    }

    public static void bindBottomNav(Activity activity, int selectedId) {
        int[] navIds = new int[]{
                R.id.navManagerDashboard,
                R.id.navManagerRequests,
                R.id.navManagerDispatch,
                R.id.navManagerProfile
        };
        for (int id : navIds) {
            TextView tab = activity.findViewById(id);
            if (tab == null) {
                continue;
            }
            boolean selected = id == selectedId;
            tab.setTextColor(ContextCompat.getColor(activity, selected ? R.color.accent_dark : R.color.text_muted));
            tab.setTypeface(selected ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        }

        TextView dashboard = activity.findViewById(R.id.navManagerDashboard);
        TextView requests = activity.findViewById(R.id.navManagerRequests);
        TextView dispatch = activity.findViewById(R.id.navManagerDispatch);
        TextView profile = activity.findViewById(R.id.navManagerProfile);

        if (dashboard != null) {
            dashboard.setOnClickListener(v -> {
                if (!(activity instanceof ManagerDashboardActivity)) {
                    activity.startActivity(new Intent(activity, ManagerDashboardActivity.class));
                }
            });
        }
        if (requests != null) {
            requests.setOnClickListener(v -> {
                if (!(activity instanceof ManagerReliefListActivity)) {
                    activity.startActivity(new Intent(activity, ManagerReliefListActivity.class));
                }
            });
        }
        if (dispatch != null) {
            dispatch.setOnClickListener(v -> {
                if (!(activity instanceof ManagerDispatchActivity)) {
                    activity.startActivity(new Intent(activity, ManagerDispatchActivity.class));
                }
            });
        }
        if (profile != null) {
            profile.setOnClickListener(v -> AppNavigator.openProfile(activity));
        }
    }

    public static void styleTag(TextView textView, String label, String colorKey, boolean outlined) {
        if (textView == null) {
            return;
        }
        textView.setText(label);
        int baseColor = resolveColor(textView.getContext(), colorKey);
        int fillColor = outlined ? Color.TRANSPARENT : withAlpha(baseColor, 0.14f);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(dp(textView.getContext(), 999));
        drawable.setColor(fillColor);
        drawable.setStroke(dp(textView.getContext(), 1), withAlpha(baseColor, 0.4f));
        textView.setBackground(drawable);
        textView.setTextColor(baseColor);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public static void styleSelectableCard(android.view.View card, boolean selected) {
        if (card instanceof MaterialCardView) {
            MaterialCardView materialCardView = (MaterialCardView) card;
            materialCardView.setCardBackgroundColor(ContextCompat.getColor(card.getContext(), selected ? R.color.info_soft : R.color.white));
            materialCardView.setStrokeWidth(dp(card.getContext(), selected ? 2 : 1));
            materialCardView.setStrokeColor(ContextCompat.getColor(card.getContext(), selected ? R.color.accent : R.color.stroke_soft));
            return;
        }
        if (!(card.getBackground() instanceof GradientDrawable)) {
            return;
        }
        GradientDrawable drawable = (GradientDrawable) card.getBackground().mutate();
        int strokeColor = ContextCompat.getColor(card.getContext(), selected ? R.color.accent : R.color.stroke_soft);
        int fillColor = ContextCompat.getColor(card.getContext(), selected ? R.color.info_soft : R.color.white);
        drawable.setColor(fillColor);
        drawable.setStroke(dp(card.getContext(), selected ? 2 : 1), strokeColor);
    }

    public static String inventoryStatusLabel(String raw) {
        String normalized = normalize(raw);
        switch (normalized) {
            case "DONE":
            case "COMPLETED":
            case "COMPLETED_OK":
            case "GREEN":
            case "AVAILABLE":
            case "Còn hàng":
                return "Sẵn sàng";
            case "APPROVED":
            case "MANAGER_APPROVED":
                return "Đã duyệt";
            case "REJECTED":
            case "CANCELLED":
            case "RED":
                return "Khẩn cấp";
            default:
                return prettify(raw);
        }
    }

    public static String deliveryStatusLabel(String raw) {
        String normalized = normalize(raw);
        switch (normalized) {
            case "REQUESTED": return "Chờ xử lý";
            case "MANAGER_APPROVED": return "Đã duyệt điều phối";
            case "RESCUER_RECEIVED": return "Đội đã nhận";
            case "ARRIVED_WAREHOUSE": return "Đã tới kho";
            case "ARRIVED_RELIEF_POINT": return "Đang giao tại điểm cứu trợ";
            case "RETURNED_TO_WAREHOUSE": return "Đã hoàn kho";
            case "COMPLETED": return "Hoàn tất";
            case "REJECTED": return "Đã từ chối";
            default: return prettify(raw);
        }
    }

    public static String documentStatusLabel(String raw) {
        String normalized = normalize(raw);
        switch (normalized) {
            case "DRAFT": return "Chờ duyệt";
            case "APPROVED": return "Đã duyệt";
            case "DONE": return "Hoàn tất";
            case "CANCELLED": return "Đã hủy";
            default: return prettify(raw);
        }
    }

    public static String colorForStatus(String raw) {
        String normalized = normalize(raw);
        switch (normalized) {
            case "DONE":
            case "COMPLETED":
            case "AVAILABLE":
            case "GREEN":
            case "ONLINE":
            case "Sẵn sàng":
                return "green";
            case "APPROVED":
            case "MANAGER_APPROVED":
            case "REQUESTED":
            case "RESCUER_RECEIVED":
            case "BLUE":
                return "blue";
            case "REJECTED":
            case "CANCELLED":
            case "DANGER":
            case "OFFLINE":
            case "BUSY":
            case "RED":
                return "red";
            case "MEDIUM":
            case "WARNING":
            case "ORANGE":
                return "orange";
            default:
                return "slate";
        }
    }

    public static String priorityLabel(String raw) {
        String normalized = normalize(raw);
        switch (normalized) {
            case "HIGH":
            case "URGENT":
            case "CRITICAL":
                return "Ưu tiên cao";
            case "MEDIUM":
                return "Ưu tiên trung bình";
            case "LOW":
                return "Ưu tiên thấp";
            default:
                return prettify(raw);
        }
    }

    public static String prettify(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        String normalized = value.trim().replace('_', ' ').toLowerCase(Locale.ROOT);
        String[] parts = normalized.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (builder.length() > 0) builder.append(' ');
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return builder.toString();
    }

    public static String prettyDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "-";
        }
        String cleaned = raw.trim().replace('T', ' ');
        int dotIndex = cleaned.indexOf('.');
        if (dotIndex > 0) {
            cleaned = cleaned.substring(0, dotIndex);
        }
        if (cleaned.length() >= 16) {
            return cleaned.substring(0, 16);
        }
        return cleaned;
    }

    public static int resolveColor(Context context, String colorKey) {
        String normalized = normalize(colorKey);
        switch (normalized) {
            case "GREEN": return ContextCompat.getColor(context, R.color.success);
            case "RED": return ContextCompat.getColor(context, R.color.danger);
            case "ORANGE": return ContextCompat.getColor(context, R.color.warning);
            case "BLUE": return ContextCompat.getColor(context, R.color.accent);
            default: return ContextCompat.getColor(context, R.color.text_secondary);
        }
    }

    public static int dp(Context context, int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }

    private static int withAlpha(int color, float factor) {
        int alpha = Math.min(255, Math.max(0, Math.round(255 * factor)));
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private static String normalize(String raw) {
        return raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
    }
}

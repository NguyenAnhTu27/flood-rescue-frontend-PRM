package com.floodrescue.mobile.ui.role.citizen.rescue.list;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.CitizenRescueListItem;
import com.floodrescue.mobile.data.repository.CitizenRescueRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.citizen.rescue.create.CreateRescueRequestActivity;
import com.floodrescue.mobile.ui.role.citizen.rescue.detail.CitizenRescueDetailActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CitizenRescueListActivity extends BaseActivity {

    private enum FilterTab {
        ALL,
        PROCESSING,
        RECEIVED,
        COMPLETED
    }

    private final List<CitizenRescueListItem> allItems = new ArrayList<>();
    private CitizenRescueRepository rescueRepository;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout requestListContainer;
    private TextView textSectionCount;
    private TextView textEmptyState;
    private TextView tabAll;
    private TextView tabProcessing;
    private TextView tabReceived;
    private TextView tabCompleted;

    private FilterTab currentFilter = FilterTab.ALL;
    private String currentQuery = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_citizen_rescue_list);

        rescueRepository = new CitizenRescueRepository(this);

        bindViews();
        bindActions();
        applyTabSelection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRequests();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBack);
        swipeRefreshLayout = findViewById(R.id.swipeCitizenRequests);
        progressBar = findViewById(R.id.progressCitizenRequests);
        requestListContainer = findViewById(R.id.containerCitizenRequests);
        textSectionCount = findViewById(R.id.textCitizenRequestSectionCount);
        textEmptyState = findViewById(R.id.textCitizenRequestEmpty);
        tabAll = findViewById(R.id.tabAllRequests);
        tabProcessing = findViewById(R.id.tabProcessingRequests);
        tabReceived = findViewById(R.id.tabReceivedRequests);
        tabCompleted = findViewById(R.id.tabCompletedRequests);
    }

    private void bindActions() {
        swipeRefreshLayout.setOnRefreshListener(this::loadRequests);

        tabAll.setOnClickListener(v -> selectFilter(FilterTab.ALL));
        tabProcessing.setOnClickListener(v -> selectFilter(FilterTab.PROCESSING));
        tabReceived.setOnClickListener(v -> selectFilter(FilterTab.RECEIVED));
        tabCompleted.setOnClickListener(v -> selectFilter(FilterTab.COMPLETED));

        findViewById(R.id.buttonSearchRequests).setOnClickListener(v -> openSearchDialog());
        findViewById(R.id.buttonSortNewest).setOnClickListener(v ->
                showShortToast(getString(R.string.citizen_request_sort_toast)));

        findViewById(R.id.fabCreateRequest).setOnClickListener(v -> openCreateRequest());
        findViewById(R.id.navHome).setOnClickListener(v -> AppNavigator.openHome(this));
        findViewById(R.id.navRequestList).setOnClickListener(v -> {
            // Already on request list.
        });
        findViewById(R.id.navCreateRescue).setOnClickListener(v -> openCreateRequest());
        findViewById(R.id.navNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.navProfile).setOnClickListener(v -> AppNavigator.openProfile(this));
    }

    private void loadRequests() {
        if (allItems.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        textEmptyState.setVisibility(View.GONE);
        rescueRepository.getMyRescueRequests(new RepositoryCallback<List<CitizenRescueListItem>>() {
            @Override
            public void onSuccess(List<CitizenRescueListItem> data) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    allItems.clear();
                    if (data != null) {
                        allItems.addAll(data);
                    }
                    sortNewestFirst(allItems);
                    applyFilters();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    applyFilters();
                    showShortToast(message == null || message.trim().isEmpty()
                            ? getString(R.string.citizen_request_load_error)
                            : message);
                });
            }
        });
    }

    private void selectFilter(FilterTab filterTab) {
        if (currentFilter == filterTab) {
            return;
        }
        currentFilter = filterTab;
        applyTabSelection();
        applyFilters();
    }

    private void applyTabSelection() {
        styleTab(tabAll, currentFilter == FilterTab.ALL);
        styleTab(tabProcessing, currentFilter == FilterTab.PROCESSING);
        styleTab(tabReceived, currentFilter == FilterTab.RECEIVED);
        styleTab(tabCompleted, currentFilter == FilterTab.COMPLETED);
    }

    private void styleTab(TextView tab, boolean selected) {
        tab.setBackgroundResource(selected
                ? R.drawable.bg_citizen_request_tab_active
                : R.drawable.bg_citizen_request_tab_inactive);
        tab.setTextColor(ContextCompat.getColor(this, selected ? R.color.accent_dark : R.color.text_secondary));
    }

    private void applyFilters() {
        List<CitizenRescueListItem> filteredItems = new ArrayList<>();
        for (CitizenRescueListItem item : allItems) {
            if (!matchesFilter(item) || !matchesQuery(item)) {
                continue;
            }
            filteredItems.add(item);
        }

        textSectionCount.setText(getString(R.string.citizen_request_section_count, filteredItems.size()));
        renderItems(filteredItems);
    }

    private boolean matchesFilter(CitizenRescueListItem item) {
        String status = normalize(item.getStatus());
        switch (currentFilter) {
            case PROCESSING:
                return "PENDING".equals(status) || "IN_PROGRESS".equals(status);
            case RECEIVED:
                return "VERIFIED".equals(status) || "ASSIGNED".equals(status);
            case COMPLETED:
                return "COMPLETED".equals(status);
            case ALL:
            default:
                return true;
        }
    }

    private boolean matchesQuery(CitizenRescueListItem item) {
        if (currentQuery == null || currentQuery.trim().isEmpty()) {
            return true;
        }
        String keyword = currentQuery.trim().toLowerCase(Locale.ROOT);
        return safe(item.getCode()).toLowerCase(Locale.ROOT).contains(keyword)
                || safe(item.getAddressText()).toLowerCase(Locale.ROOT).contains(keyword);
    }

    private void renderItems(List<CitizenRescueListItem> items) {
        requestListContainer.removeAllViews();
        if (items.isEmpty()) {
            textEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        textEmptyState.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (CitizenRescueListItem item : items) {
            View itemView = inflater.inflate(R.layout.item_citizen_rescue_request, requestListContainer, false);
            bindItemView(itemView, item);
            requestListContainer.addView(itemView);
        }
    }

    private void bindItemView(View itemView, CitizenRescueListItem item) {
        TextView textCode = itemView.findViewById(R.id.textRequestCode);
        TextView textStatus = itemView.findViewById(R.id.textRequestStatus);
        TextView textTime = itemView.findViewById(R.id.textRequestTime);
        TextView textAddress = itemView.findViewById(R.id.textRequestAddress);
        TextView textPeople = itemView.findViewById(R.id.textRequestPeopleCount);
        TextView textPriority = itemView.findViewById(R.id.textRequestPriority);
        TextView textFlagWaiting = itemView.findViewById(R.id.textRequestFlagWaiting);
        TextView textFlagVerified = itemView.findViewById(R.id.textRequestFlagVerified);
        TextView buttonAction = itemView.findViewById(R.id.buttonRequestAction);

        textCode.setText(safe(item.getCode()));
        textStatus.setText(mapStatusLabel(item.getStatus()));
        textTime.setText(buildTimeLabel(item));
        textAddress.setText(safe(item.getAddressText()));
        textPeople.setText(getResources().getQuantityString(
                R.plurals.citizen_request_people_count,
                Math.max(item.getAffectedPeopleCount(), 0),
                Math.max(item.getAffectedPeopleCount(), 0)
        ));
        textPriority.setText(mapPriorityLabel(item.getPriority()));
        textFlagWaiting.setVisibility(item.isWaitingForTeam() ? View.VISIBLE : View.GONE);
        textFlagVerified.setVisibility(item.isLocationVerified() ? View.VISIBLE : View.GONE);
        buttonAction.setText(isCompleted(item.getStatus())
                ? R.string.citizen_request_action_history
                : R.string.citizen_request_action_detail);

        applyStatusStyle(textStatus, item.getStatus());
        applyPriorityStyle(textPriority, item.getPriority());

        View.OnClickListener openDetailListener = v -> openRequestDetail(item);
        itemView.setOnClickListener(openDetailListener);
        buttonAction.setOnClickListener(openDetailListener);
    }

    private void openRequestDetail(CitizenRescueListItem item) {
        Intent intent = new Intent(this, CitizenRescueDetailActivity.class);
        intent.putExtra(CitizenRescueDetailActivity.EXTRA_REQUEST_ID, item.getId());
        startActivity(intent);
    }

    private void openCreateRequest() {
        startActivity(new Intent(this, CreateRescueRequestActivity.class));
    }

    private void openSearchDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.citizen_request_search_hint);
        input.setText(currentQuery);
        int padding = Math.round(getResources().getDisplayMetrics().density * 18);
        input.setPadding(padding, padding, padding, padding);

        new AlertDialog.Builder(this)
                .setTitle(R.string.citizen_request_search_title)
                .setView(input)
                .setPositiveButton(R.string.citizen_request_search_apply, (dialog, which) -> {
                    currentQuery = safe(input.getText() == null ? null : input.getText().toString()).trim();
                    applyFilters();
                })
                .setNeutralButton(R.string.citizen_request_search_clear, (dialog, which) -> {
                    currentQuery = "";
                    applyFilters();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void sortNewestFirst(List<CitizenRescueListItem> items) {
        Collections.sort(items, new Comparator<CitizenRescueListItem>() {
            @Override
            public int compare(CitizenRescueListItem first, CitizenRescueListItem second) {
                long firstTime = resolveSortTime(first);
                long secondTime = resolveSortTime(second);
                return Long.compare(secondTime, firstTime);
            }
        });
    }

    private long resolveSortTime(CitizenRescueListItem item) {
        long updatedAt = parseTimestamp(item.getUpdatedAt());
        if (updatedAt > 0L) {
            return updatedAt;
        }
        return parseTimestamp(item.getCreatedAt());
    }

    private String buildTimeLabel(CitizenRescueListItem item) {
        String prefix;
        long timestamp;
        if (isCompleted(item.getStatus())) {
            prefix = getString(R.string.citizen_request_time_completed_prefix);
            timestamp = parseTimestamp(item.getUpdatedAt());
        } else {
            timestamp = parseTimestamp(item.getUpdatedAt());
            if (timestamp > 0L) {
                prefix = getString(R.string.citizen_request_time_updated_prefix);
            } else {
                prefix = getString(R.string.citizen_request_time_created_prefix);
                timestamp = parseTimestamp(item.getCreatedAt());
            }
        }
        return prefix + " " + humanizeTimestamp(timestamp, item.getUpdatedAt(), item.getCreatedAt());
    }

    private String humanizeTimestamp(long timestamp, String updatedAt, String createdAt) {
        if (timestamp <= 0L) {
            String raw = safe(updatedAt).isEmpty() ? safe(createdAt) : safe(updatedAt);
            return cleanTimestamp(raw);
        }

        long diffMillis = Math.max(System.currentTimeMillis() - timestamp, 0L);
        long minutes = diffMillis / 60000L;
        if (minutes < 1L) {
            return getString(R.string.citizen_request_time_now);
        }
        if (minutes < 60L) {
            return getResources().getQuantityString(R.plurals.citizen_request_time_minutes, (int) minutes, minutes);
        }

        long hours = diffMillis / 3600000L;
        if (hours < 24L) {
            return getResources().getQuantityString(R.plurals.citizen_request_time_hours, (int) hours, hours);
        }

        long days = diffMillis / 86400000L;
        if (days < 7L) {
            return getResources().getQuantityString(R.plurals.citizen_request_time_days, (int) days, days);
        }

        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(timestamp));
    }

    private long parseTimestamp(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return -1L;
        }
        String normalized = raw.trim();
        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd HH:mm:ss"
        };
        for (String pattern : patterns) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
                Date date = format.parse(normalized);
                if (date != null) {
                    return date.getTime();
                }
            } catch (Exception ignored) {
                // Try next pattern.
            }
        }
        return -1L;
    }

    private void applyStatusStyle(TextView view, String status) {
        String normalized = normalize(status);
        if ("COMPLETED".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else if ("VERIFIED".equals(normalized) || "ASSIGNED".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(ContextCompat.getColor(this, R.color.accent_dark));
        } else if ("CANCELLED".equals(normalized) || "DUPLICATE".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(ContextCompat.getColor(this, R.color.danger));
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

    private String mapStatusLabel(String status) {
        switch (normalize(status)) {
            case "VERIFIED":
            case "ASSIGNED":
                return getString(R.string.citizen_request_status_received);
            case "COMPLETED":
                return getString(R.string.citizen_request_status_completed);
            case "CANCELLED":
                return getString(R.string.citizen_request_status_cancelled);
            case "DUPLICATE":
                return getString(R.string.citizen_request_status_duplicate);
            case "PENDING":
            case "IN_PROGRESS":
            default:
                return getString(R.string.citizen_request_status_processing);
        }
    }

    private String mapPriorityLabel(String priority) {
        switch (normalize(priority)) {
            case "HIGH":
                return getString(R.string.citizen_request_priority_high);
            case "LOW":
                return getString(R.string.citizen_request_priority_low);
            case "MEDIUM":
            default:
                return getString(R.string.citizen_request_priority_medium);
        }
    }

    private boolean isCompleted(String status) {
        return "COMPLETED".equals(normalize(status));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String cleanTimestamp(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return getString(R.string.citizen_request_time_unknown);
        }
        return raw.replace('T', ' ').replace(".000", "");
    }
}

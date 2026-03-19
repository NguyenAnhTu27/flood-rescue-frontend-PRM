package com.floodrescue.mobile.ui.role.rescuer.task.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.RescuerTaskItem;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.data.repository.RescuerTaskListRepository;
import com.floodrescue.mobile.ui.role.rescuer.dashboard.RescuerDashboardActivity;
import com.floodrescue.mobile.ui.role.rescuer.task.detail.RescuerTaskDetailActivity;
import com.floodrescue.mobile.ui.role.rescuer.taskgroup.list.RescuerTaskGroupListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerTaskListActivity extends BaseActivity implements RescuerTaskAdapter.Listener {

    private static final String FILTER_ALL = "ALL";
    private static final String FILTER_PROGRESS = "IN_PROGRESS";
    private static final String FILTER_PENDING = "PENDING";
    private static final String FILTER_DONE = "COMPLETED";

    private static final String QUICK_HIGH = "HIGH";
    private static final String QUICK_EMERGENCY = "EMERGENCY";
    private static final String QUICK_VERIFY = "UNVERIFIED";

    private RescuerTaskListRepository repository;
    private RescuerTaskAdapter adapter;

    private final List<RescuerTaskItem> allItems = new ArrayList<>();

    private EditText editSearch;
    private TextView buttonFilterAll;
    private TextView buttonFilterProgress;
    private TextView buttonFilterPending;
    private TextView buttonFilterDone;
    private TextView buttonQuickHigh;
    private TextView buttonQuickEmergency;
    private TextView buttonQuickVerify;
    private TextView textCount;
    private TextView textEmpty;
    private TextView textError;
    private View progressView;

    private String activeFilter = FILTER_ALL;
    private String activeQuickFilter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_rescuer_task_list);

        repository = new RescuerTaskListRepository(this);
        bindViews();
        setupList();
        bindActions();
        updateFilterState();
        updateQuickState();
        loadTasks();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackRescuerTasks);
        editSearch = findViewById(R.id.editRescuerTaskSearch);
        buttonFilterAll = findViewById(R.id.buttonRescuerFilterAll);
        buttonFilterProgress = findViewById(R.id.buttonRescuerFilterProgress);
        buttonFilterPending = findViewById(R.id.buttonRescuerFilterPending);
        buttonFilterDone = findViewById(R.id.buttonRescuerFilterDone);
        buttonQuickHigh = findViewById(R.id.buttonRescuerQuickHigh);
        buttonQuickEmergency = findViewById(R.id.buttonRescuerQuickEmergency);
        buttonQuickVerify = findViewById(R.id.buttonRescuerQuickVerify);
        textCount = findViewById(R.id.textRescuerTaskCount);
        textEmpty = findViewById(R.id.textRescuerTasksEmpty);
        textError = findViewById(R.id.textRescuerTasksError);
        progressView = findViewById(R.id.progressRescuerTasks);
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerRescuerTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RescuerTaskAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void bindActions() {
        findViewById(R.id.buttonRefreshRescuerTasks).setOnClickListener(v -> loadTasks());
        findViewById(R.id.buttonRescuerTaskSearch).setOnClickListener(v -> applyFilters());
        editSearch.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                applyFilters();
                return true;
            }
            return false;
        });

        buttonFilterAll.setOnClickListener(v -> selectFilter(FILTER_ALL));
        buttonFilterProgress.setOnClickListener(v -> selectFilter(FILTER_PROGRESS));
        buttonFilterPending.setOnClickListener(v -> selectFilter(FILTER_PENDING));
        buttonFilterDone.setOnClickListener(v -> selectFilter(FILTER_DONE));

        buttonQuickHigh.setOnClickListener(v -> toggleQuickFilter(QUICK_HIGH));
        buttonQuickEmergency.setOnClickListener(v -> toggleQuickFilter(QUICK_EMERGENCY));
        buttonQuickVerify.setOnClickListener(v -> toggleQuickFilter(QUICK_VERIFY));

        findViewById(R.id.buttonRescuerTaskGroupFab).setOnClickListener(v ->
                startActivity(new Intent(this, RescuerTaskGroupListActivity.class)));

        findViewById(R.id.navRescuerTasksOverview).setOnClickListener(v ->
                startActivity(new Intent(this, RescuerDashboardActivity.class)));
        findViewById(R.id.navRescuerTasksCurrent).setOnClickListener(v -> { });
        findViewById(R.id.navRescuerTasksNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.navRescuerTasksProfile).setOnClickListener(v -> AppNavigator.openProfile(this));
    }

    private void loadTasks() {
        setLoading(true);
        repository.loadTasks(new RepositoryCallback<List<RescuerTaskItem>>() {
            @Override
            public void onSuccess(List<RescuerTaskItem> data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    allItems.clear();
                    if (data != null) {
                        allItems.addAll(data);
                    }
                    applyFilters();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.rescuer_task_list_error) : message);
                    applyFilters();
                });
            }
        });
    }

    private void applyFilters() {
        List<RescuerTaskItem> filtered = new ArrayList<>();
        String keyword = textOf(editSearch).toLowerCase(Locale.ROOT);
        for (RescuerTaskItem item : allItems) {
            if (!matchesStatus(item)) {
                continue;
            }
            if (!matchesQuick(item)) {
                continue;
            }
            if (!matchesKeyword(item, keyword)) {
                continue;
            }
            filtered.add(item);
        }

        adapter.submit(filtered);
        textEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        textCount.setText(getString(R.string.rescuer_task_list_count, filtered.size()));
    }

    private boolean matchesStatus(RescuerTaskItem item) {
        String status = normalize(item.getStatus());
        if (FILTER_PROGRESS.equals(activeFilter)) {
            return "IN_PROGRESS".equals(status);
        }
        if (FILTER_PENDING.equals(activeFilter)) {
            return "ASSIGNED".equals(status) || "PENDING".equals(status) || "VERIFIED".equals(status);
        }
        if (FILTER_DONE.equals(activeFilter)) {
            return "COMPLETED".equals(status);
        }
        return true;
    }

    private boolean matchesQuick(RescuerTaskItem item) {
        if (activeQuickFilter == null) {
            return true;
        }
        if (QUICK_HIGH.equals(activeQuickFilter)) {
            return "HIGH".equals(normalize(item.getPriority()));
        }
        if (QUICK_EMERGENCY.equals(activeQuickFilter)) {
            return item.isEmergency();
        }
        return !item.isLocationVerified();
    }

    private boolean matchesKeyword(RescuerTaskItem item, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }
        return safe(item.getRequestCode()).contains(keyword)
                || safe(item.getTaskGroupCode()).contains(keyword)
                || safe(item.getCitizenName()).contains(keyword)
                || safe(item.getCitizenPhone()).contains(keyword)
                || safe(item.getAddress()).contains(keyword)
                || safe(item.getDescription()).contains(keyword);
    }

    private void selectFilter(String filter) {
        activeFilter = filter;
        updateFilterState();
        applyFilters();
    }

    private void toggleQuickFilter(String quickFilter) {
        activeQuickFilter = quickFilter.equals(activeQuickFilter) ? null : quickFilter;
        updateQuickState();
        applyFilters();
    }

    private void updateFilterState() {
        styleFilter(buttonFilterAll, FILTER_ALL.equals(activeFilter));
        styleFilter(buttonFilterProgress, FILTER_PROGRESS.equals(activeFilter));
        styleFilter(buttonFilterPending, FILTER_PENDING.equals(activeFilter));
        styleFilter(buttonFilterDone, FILTER_DONE.equals(activeFilter));
    }

    private void updateQuickState() {
        styleQuickFilter(buttonQuickHigh, QUICK_HIGH.equals(activeQuickFilter), R.drawable.bg_chip_danger, R.color.danger);
        styleQuickFilter(buttonQuickEmergency, QUICK_EMERGENCY.equals(activeQuickFilter), R.drawable.bg_chip_warning, R.color.warning);
        styleQuickFilter(buttonQuickVerify, QUICK_VERIFY.equals(activeQuickFilter), R.drawable.bg_chip_info, R.color.accent_dark);
    }

    private void styleFilter(TextView view, boolean active) {
        view.setBackgroundResource(active ? R.drawable.bg_citizen_request_tab_active : R.drawable.bg_citizen_request_tab_inactive);
        view.setTextColor(getColor(active ? R.color.white : R.color.text_secondary));
    }

    private void styleQuickFilter(TextView view, boolean active, int backgroundRes, int activeTextColor) {
        if (active) {
            view.setBackgroundResource(backgroundRes);
            view.setTextColor(getColor(activeTextColor));
            return;
        }
        view.setBackgroundResource(R.drawable.bg_citizen_request_tab_inactive);
        view.setTextColor(getColor(R.color.text_secondary));
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        textError.setVisibility(View.GONE);
        if (loading) {
            textEmpty.setVisibility(View.GONE);
        }
    }

    private String textOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public void onOpenTask(RescuerTaskItem item) {
        if (item == null || item.getRequestId() <= 0L) {
            showShortToast(getString(R.string.rescuer_task_group_detail_missing_id));
            return;
        }
        Intent intent = new Intent(this, RescuerTaskDetailActivity.class);
        intent.putExtra(RescuerTaskDetailActivity.EXTRA_TASK_ID, item.getRequestId());
        intent.putExtra(RescuerTaskDetailActivity.EXTRA_TASK_GROUP_ID, item.getTaskGroupId());
        startActivity(intent);
    }
}

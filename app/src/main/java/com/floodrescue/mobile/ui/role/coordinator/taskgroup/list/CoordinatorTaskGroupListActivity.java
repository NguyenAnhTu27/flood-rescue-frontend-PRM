package com.floodrescue.mobile.ui.role.coordinator.taskgroup.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.CoordinatorTaskGroupListItem;
import com.floodrescue.mobile.data.repository.CoordinatorOperationsRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.coordinator.dashboard.CoordinatorDashboardActivity;
import com.floodrescue.mobile.ui.role.coordinator.rescuequeue.CoordinatorRescueQueueActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoordinatorTaskGroupListActivity extends BaseActivity implements CoordinatorTaskGroupListAdapter.Listener {

    private CoordinatorOperationsRepository repository;
    private CoordinatorTaskGroupListAdapter adapter;
    private final List<CoordinatorTaskGroupListItem> fullData = new ArrayList<>();

    private View progressView;
    private TextView textError;
    private TextView textEmpty;
    private TextView buttonFilterAll;
    private TextView buttonFilterWaiting;
    private TextView buttonFilterActive;
    private TextView buttonFilterDone;
    private String activeFilter = "ALL";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_coordinator_task_group_list);

        repository = new CoordinatorOperationsRepository(this);
        bindViews();
        setupList();
        bindActions();
        applyFilterStyle();
        loadTaskGroups();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackTaskGroupList);
        progressView = findViewById(R.id.progressTaskGroupList);
        textError = findViewById(R.id.textTaskGroupListError);
        textEmpty = findViewById(R.id.textTaskGroupListEmpty);
        buttonFilterAll = findViewById(R.id.buttonTaskGroupFilterAll);
        buttonFilterWaiting = findViewById(R.id.buttonTaskGroupFilterWaiting);
        buttonFilterActive = findViewById(R.id.buttonTaskGroupFilterActive);
        buttonFilterDone = findViewById(R.id.buttonTaskGroupFilterDone);
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerTaskGroupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CoordinatorTaskGroupListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void bindActions() {
        buttonFilterAll.setOnClickListener(v -> switchFilter("ALL"));
        buttonFilterWaiting.setOnClickListener(v -> switchFilter("WAITING"));
        buttonFilterActive.setOnClickListener(v -> switchFilter("ACTIVE"));
        buttonFilterDone.setOnClickListener(v -> switchFilter("DONE"));

        findViewById(R.id.buttonTaskGroupCreateNew).setOnClickListener(v ->
                startActivity(new Intent(this, CoordinatorRescueQueueActivity.class)));

        findViewById(R.id.navTaskGroupOverview).setOnClickListener(v ->
                startActivity(new Intent(this, CoordinatorDashboardActivity.class)));
        findViewById(R.id.navTaskGroupQueue).setOnClickListener(v ->
                startActivity(new Intent(this, CoordinatorRescueQueueActivity.class)));
        findViewById(R.id.navTaskGroupMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.navTaskGroupTeam).setOnClickListener(v -> { });
        findViewById(R.id.navTaskGroupSettings).setOnClickListener(v -> AppNavigator.openProfile(this));
    }

    private void switchFilter(String filter) {
        activeFilter = filter;
        applyFilterStyle();
        renderFilteredData();
    }

    private void applyFilterStyle() {
        styleFilter(buttonFilterAll, "ALL".equals(activeFilter));
        styleFilter(buttonFilterWaiting, "WAITING".equals(activeFilter));
        styleFilter(buttonFilterActive, "ACTIVE".equals(activeFilter));
        styleFilter(buttonFilterDone, "DONE".equals(activeFilter));
    }

    private void styleFilter(TextView view, boolean active) {
        view.setBackgroundResource(active ? R.drawable.bg_citizen_request_tab_active : R.drawable.bg_citizen_request_tab_inactive);
        view.setTextColor(getColor(active ? R.color.white : R.color.text_secondary));
    }

    private void loadTaskGroups() {
        progressView.setVisibility(View.VISIBLE);
        textError.setVisibility(View.GONE);
        repository.getTaskGroups(null, new RepositoryCallback<List<CoordinatorTaskGroupListItem>>() {
            @Override
            public void onSuccess(List<CoordinatorTaskGroupListItem> data) {
                runOnUiThread(() -> {
                    progressView.setVisibility(View.GONE);
                    fullData.clear();
                    if (data != null) {
                        fullData.addAll(data);
                    }
                    renderFilteredData();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progressView.setVisibility(View.GONE);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.coordinator_task_group_list_error) : message);
                });
            }
        });
    }

    private void renderFilteredData() {
        List<CoordinatorTaskGroupListItem> filtered = new ArrayList<>();
        for (CoordinatorTaskGroupListItem item : fullData) {
            if (matchesFilter(item)) {
                filtered.add(item);
            }
        }
        adapter.submit(filtered);
        textEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean matchesFilter(CoordinatorTaskGroupListItem item) {
        String status = normalize(item.getStatus());
        if ("WAITING".equals(activeFilter)) {
            return "NEW".equals(status);
        }
        if ("ACTIVE".equals(activeFilter)) {
            return "ASSIGNED".equals(status) || "IN_PROGRESS".equals(status);
        }
        if ("DONE".equals(activeFilter)) {
            return "DONE".equals(status);
        }
        return true;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public void onOpenDetail(CoordinatorTaskGroupListItem item) {
        String message = formatCode(item.getCode())
                + "\n" + getString(R.string.coordinator_task_group_team, fallback(item.getAssignedTeamName(), "Chưa phân công"))
                + "\n" + getString(R.string.coordinator_task_group_creator, fallback(item.getCreatedByName(), "Hệ thống"))
                + "\n" + getString(R.string.coordinator_task_group_updated, fallback(item.getUpdatedAt(), fallback(item.getCreatedAt(), "Chưa cập nhật")))
                + "\n\n" + (item.getNote() == null || item.getNote().trim().isEmpty()
                ? getString(R.string.coordinator_task_group_note_empty)
                : item.getNote().trim());

        new AlertDialog.Builder(this)
                .setTitle(R.string.coordinator_task_group_summary_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private String formatCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "#TG";
        }
        return code.startsWith("#") ? code : "#" + code;
    }

    private String fallback(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}

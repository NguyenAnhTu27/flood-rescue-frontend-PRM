package com.floodrescue.mobile.ui.role.rescuer.taskgroup.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.RescuerTaskGroupListItem;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.data.repository.RescuerTaskGroupListRepository;
import com.floodrescue.mobile.ui.role.rescuer.dashboard.RescuerDashboardActivity;
import com.floodrescue.mobile.ui.role.rescuer.task.detail.RescuerTaskDetailActivity;
import com.floodrescue.mobile.ui.role.rescuer.task.list.RescuerTaskListActivity;
import com.floodrescue.mobile.ui.role.rescuer.taskgroup.detail.RescuerTaskGroupDetailActivity;
import com.floodrescue.mobile.ui.role.rescuer.teamlocation.RescuerTeamLocationActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerTaskGroupListActivity extends BaseActivity implements RescuerTaskGroupListAdapter.Listener {

    private static final String FILTER_ALL = "ALL";
    private static final String FILTER_ACTIVE = "ACTIVE";
    private static final String FILTER_PENDING = "PENDING";
    private static final String FILTER_DONE = "DONE";

    private RescuerTaskGroupListRepository repository;
    private RescuerTaskGroupListAdapter adapter;
    private final List<RescuerTaskGroupListItem> fullData = new ArrayList<>();

    private View progressView;
    private TextView textError;
    private TextView textEmpty;
    private TextView buttonAll;
    private TextView buttonActive;
    private TextView buttonPending;
    private TextView buttonDone;
    private String activeFilter = FILTER_ALL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_rescuer_task_group_list);

        repository = new RescuerTaskGroupListRepository(this);
        bindViews();
        setupList();
        bindActions();
        applyFilterStyle();
        loadData();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackRescuerTaskGroups);
        progressView = findViewById(R.id.progressRescuerTaskGroups);
        textError = findViewById(R.id.textRescuerTaskGroupsError);
        textEmpty = findViewById(R.id.textRescuerTaskGroupsEmpty);
        buttonAll = findViewById(R.id.buttonRescuerTaskGroupsAll);
        buttonActive = findViewById(R.id.buttonRescuerTaskGroupsActive);
        buttonPending = findViewById(R.id.buttonRescuerTaskGroupsPending);
        buttonDone = findViewById(R.id.buttonRescuerTaskGroupsDone);
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerRescuerTaskGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RescuerTaskGroupListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void bindActions() {
        findViewById(R.id.buttonRefreshRescuerTaskGroups).setOnClickListener(v -> loadData());
        findViewById(R.id.buttonRescuerTaskGroupsPrimary).setOnClickListener(v -> openLatestGroup());
        findViewById(R.id.buttonRescuerTaskGroupsFab).setOnClickListener(v -> openLatestGroup());

        buttonAll.setOnClickListener(v -> switchFilter(FILTER_ALL));
        buttonActive.setOnClickListener(v -> switchFilter(FILTER_ACTIVE));
        buttonPending.setOnClickListener(v -> switchFilter(FILTER_PENDING));
        buttonDone.setOnClickListener(v -> switchFilter(FILTER_DONE));

        findViewById(R.id.navRescuerGroupsTasks).setOnClickListener(v ->
                startActivity(new Intent(this, RescuerTaskListActivity.class)));
        findViewById(R.id.navRescuerGroupsMap).setOnClickListener(v ->
                startActivity(new Intent(this, RescuerTeamLocationActivity.class)));
        findViewById(R.id.navRescuerGroupsNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.navRescuerGroupsProfile).setOnClickListener(v -> AppNavigator.openProfile(this));
    }

    private void switchFilter(String filter) {
        activeFilter = filter;
        applyFilterStyle();
        renderFiltered();
    }

    private void applyFilterStyle() {
        styleFilter(buttonAll, FILTER_ALL.equals(activeFilter));
        styleFilter(buttonActive, FILTER_ACTIVE.equals(activeFilter));
        styleFilter(buttonPending, FILTER_PENDING.equals(activeFilter));
        styleFilter(buttonDone, FILTER_DONE.equals(activeFilter));
    }

    private void styleFilter(TextView view, boolean active) {
        view.setBackgroundResource(active ? R.drawable.bg_citizen_request_tab_active : R.drawable.bg_citizen_request_tab_inactive);
        view.setTextColor(getColor(active ? R.color.white : R.color.text_secondary));
    }

    private void loadData() {
        setLoading(true);
        repository.loadTaskGroups(new RepositoryCallback<List<RescuerTaskGroupListItem>>() {
            @Override
            public void onSuccess(List<RescuerTaskGroupListItem> data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    fullData.clear();
                    if (data != null) {
                        fullData.addAll(data);
                    }
                    renderFiltered();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.rescuer_task_group_list_error) : message);
                    renderFiltered();
                });
            }
        });
    }

    private void renderFiltered() {
        List<RescuerTaskGroupListItem> filtered = new ArrayList<>();
        for (RescuerTaskGroupListItem item : fullData) {
            if (matchesFilter(item)) {
                filtered.add(item);
            }
        }
        adapter.submit(filtered);
        textEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean matchesFilter(RescuerTaskGroupListItem item) {
        String status = normalize(item.getStatus());
        if (FILTER_ACTIVE.equals(activeFilter)) {
            return "ASSIGNED".equals(status) || "IN_PROGRESS".equals(status);
        }
        if (FILTER_PENDING.equals(activeFilter)) {
            return "NEW".equals(status);
        }
        if (FILTER_DONE.equals(activeFilter)) {
            return "DONE".equals(status) || "COMPLETED".equals(status) || "CANCELLED".equals(status);
        }
        return true;
    }

    private void openLatestGroup() {
        if (fullData.isEmpty()) {
            showShortToast(getString(R.string.rescuer_task_group_list_empty));
            return;
        }
        onOpenDetail(fullData.get(0));
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            textError.setVisibility(View.GONE);
            textEmpty.setVisibility(View.GONE);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public void onOpenDetail(RescuerTaskGroupListItem item) {
        if (item == null || item.getId() <= 0L) {
            showShortToast(getString(R.string.rescuer_task_group_detail_missing_id));
            return;
        }
        Intent intent = new Intent(this, RescuerTaskGroupDetailActivity.class);
        intent.putExtra(RescuerTaskGroupDetailActivity.EXTRA_TASK_GROUP_ID, item.getId());
        startActivity(intent);
    }
}

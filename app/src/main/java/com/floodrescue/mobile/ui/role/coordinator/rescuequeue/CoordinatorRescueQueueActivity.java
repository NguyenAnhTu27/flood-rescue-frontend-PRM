package com.floodrescue.mobile.ui.role.coordinator.rescuequeue;

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
import com.floodrescue.mobile.data.model.ui.CoordinatorQueueItem;
import com.floodrescue.mobile.data.repository.CoordinatorRescueQueueRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.coordinator.dashboard.CoordinatorDashboardActivity;
import com.floodrescue.mobile.ui.role.coordinator.rescuedetail.CoordinatorRescueDetailActivity;
import com.floodrescue.mobile.ui.role.coordinator.taskgroup.create.CoordinatorTaskGroupCreateActivity;
import com.floodrescue.mobile.ui.role.coordinator.taskgroup.list.CoordinatorTaskGroupListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

import java.util.List;

public class CoordinatorRescueQueueActivity extends BaseActivity implements CoordinatorQueueAdapter.Listener {

    private CoordinatorRescueQueueRepository repository;
    private CoordinatorQueueAdapter adapter;

    private EditText editSearch;
    private TextView buttonFilterAll;
    private TextView buttonFilterPending;
    private TextView buttonFilterAssigned;
    private TextView buttonFilterCompleted;
    private TextView textEmpty;
    private TextView textError;
    private TextView buttonCreateTaskGroup;
    private View progressView;

    private String activeStatus = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_coordinator_rescue_queue);

        repository = new CoordinatorRescueQueueRepository(this);
        bindViews();
        bindActions();
        setupList();
        applyFilterState();
        loadQueue();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackQueue);
        editSearch = findViewById(R.id.editQueueSearch);
        buttonFilterAll = findViewById(R.id.buttonFilterAll);
        buttonFilterPending = findViewById(R.id.buttonFilterPending);
        buttonFilterAssigned = findViewById(R.id.buttonFilterAssigned);
        buttonFilterCompleted = findViewById(R.id.buttonFilterCompleted);
        textEmpty = findViewById(R.id.textQueueEmpty);
        textError = findViewById(R.id.textQueueError);
        buttonCreateTaskGroup = findViewById(R.id.buttonCreateTaskGroup);
        progressView = findViewById(R.id.progressQueue);
    }

    private void bindActions() {
        findViewById(R.id.buttonSearchSubmit).setOnClickListener(v -> loadQueue());
        findViewById(R.id.buttonSearchFilter).setOnClickListener(v -> {
            editSearch.setText("");
            loadQueue();
        });
        editSearch.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                loadQueue();
                return true;
            }
            return false;
        });

        buttonFilterAll.setOnClickListener(v -> switchStatus(null));
        buttonFilterPending.setOnClickListener(v -> switchStatus("PENDING"));
        buttonFilterAssigned.setOnClickListener(v -> switchStatus("ASSIGNED"));
        buttonFilterCompleted.setOnClickListener(v -> switchStatus("COMPLETED"));

        buttonCreateTaskGroup.setOnClickListener(v -> openCreateTaskGroup());

        findViewById(R.id.navCoordinatorOverview).setOnClickListener(v -> startActivity(new Intent(this, CoordinatorDashboardActivity.class)));
        findViewById(R.id.navCoordinatorQueue).setOnClickListener(v -> {
            // Current screen.
        });
        findViewById(R.id.navCoordinatorMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.navCoordinatorTeams).setOnClickListener(v -> startActivity(new Intent(this, CoordinatorTaskGroupListActivity.class)));
        findViewById(R.id.navCoordinatorSettings).setOnClickListener(v -> AppNavigator.openProfile(this));
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerCoordinatorQueue);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CoordinatorQueueAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void switchStatus(String status) {
        activeStatus = status;
        applyFilterState();
        loadQueue();
    }

    private void applyFilterState() {
        styleFilter(buttonFilterAll, activeStatus == null);
        styleFilter(buttonFilterPending, "PENDING".equals(activeStatus));
        styleFilter(buttonFilterAssigned, "ASSIGNED".equals(activeStatus));
        styleFilter(buttonFilterCompleted, "COMPLETED".equals(activeStatus));
    }

    private void styleFilter(TextView view, boolean active) {
        view.setBackgroundResource(active ? R.drawable.bg_citizen_request_tab_active : R.drawable.bg_citizen_request_tab_inactive);
        view.setTextColor(getColor(active ? R.color.white : R.color.text_secondary));
    }

    private void loadQueue() {
        setLoading(true);
        repository.getQueue(activeStatus, textOf(editSearch), new RepositoryCallback<List<CoordinatorQueueItem>>() {
            @Override
            public void onSuccess(List<CoordinatorQueueItem> data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    adapter.submit(data);
                    textEmpty.setVisibility(data == null || data.isEmpty() ? View.VISIBLE : View.GONE);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.coordinator_queue_load_error) : message);
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        textError.setVisibility(View.GONE);
        if (loading) {
            textEmpty.setVisibility(View.GONE);
        }
    }

    private void openCreateTaskGroup() {
        List<Long> selectedIds = adapter.getSelectedIds();
        if (selectedIds.isEmpty()) {
            showShortToast(getString(R.string.coordinator_queue_select_error));
            return;
        }
        Intent intent = new Intent(this, CoordinatorTaskGroupCreateActivity.class);
        intent.putExtra("selected_request_ids", joinIds(selectedIds));
        startActivity(intent);
    }

    private String joinIds(List<Long> ids) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(ids.get(i));
        }
        return builder.toString();
    }

    private String textOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    @Override
    public void onSelectionChanged(int count) {
        buttonCreateTaskGroup.setText(getString(R.string.coordinator_queue_create_group, count));
        buttonCreateTaskGroup.setAlpha(count > 0 ? 1f : 0.6f);
    }

    @Override
    public void onOpenDetail(CoordinatorQueueItem item) {
        Intent intent = new Intent(this, CoordinatorRescueDetailActivity.class);
        intent.putExtra("request_id", item.getId());
        startActivity(intent);
    }
}

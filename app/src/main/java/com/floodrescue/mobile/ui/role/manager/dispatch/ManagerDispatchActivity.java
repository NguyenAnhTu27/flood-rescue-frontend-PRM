package com.floodrescue.mobile.ui.role.manager.dispatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.ManagerDispatchState;
import com.floodrescue.mobile.data.repository.ManagerDispatchRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.manager.ManagerUi;
import com.floodrescue.mobile.ui.role.manager.relief.detail.ManagerReliefDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ManagerDispatchActivity extends BaseActivity {

    private ManagerDispatchRepository repository;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView errorView;
    private TextView selectedView;
    private TextView summaryView;
    private TextView requestCountView;
    private TextView teamCountView;
    private TextView vehicleCountView;
    private ManagerDispatchRequestAdapter requestAdapter;
    private ManagerDispatchTeamAdapter teamAdapter;
    private ManagerDispatchVehicleAdapter vehicleAdapter;

    private final List<ManagerDispatchState.QueueItem> requests = new ArrayList<>();
    private final List<ManagerDispatchState.TeamItem> teams = new ArrayList<>();
    private long selectedRequestId;
    private long selectedTeamId;
    private boolean submitting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_manager_dispatch);

        repository = new ManagerDispatchRepository(this);
        selectedRequestId = getIntent().getLongExtra(ManagerUi.EXTRA_REQUEST_ID, 0L);

        bindBackButton(R.id.buttonManagerDispatchBack);
        swipeRefreshLayout = findViewById(R.id.swipeManagerDispatch);
        progressBar = findViewById(R.id.progressManagerDispatch);
        errorView = findViewById(R.id.textManagerDispatchError);
        selectedView = findViewById(R.id.textManagerDispatchSelected);
        summaryView = findViewById(R.id.textManagerDispatchSummary);
        requestCountView = findViewById(R.id.textManagerDispatchRequestCount);
        teamCountView = findViewById(R.id.textManagerDispatchTeamCount);
        vehicleCountView = findViewById(R.id.textManagerDispatchVehicleCount);

        setupRecyclerViews();
        findViewById(R.id.buttonManagerConfirmDispatch).setOnClickListener(v -> confirmDispatch());
        swipeRefreshLayout.setOnRefreshListener(this::loadDashboard);

        loadDashboard();
    }

    private void setupRecyclerViews() {
        androidx.recyclerview.widget.RecyclerView requestRecycler = findViewById(R.id.recyclerManagerDispatchRequests);
        requestAdapter = new ManagerDispatchRequestAdapter(item -> {
            selectedRequestId = item.getId();
            requestAdapter.setSelectedId(selectedRequestId);
            syncSelectionSummary();
        });
        requestRecycler.setLayoutManager(new LinearLayoutManager(this));
        requestRecycler.setAdapter(requestAdapter);
        requestRecycler.setNestedScrollingEnabled(false);

        androidx.recyclerview.widget.RecyclerView teamRecycler = findViewById(R.id.recyclerManagerDispatchTeams);
        teamAdapter = new ManagerDispatchTeamAdapter(item -> {
            selectedTeamId = item.getId();
            teamAdapter.setSelectedId(selectedTeamId);
            syncSelectionSummary();
        });
        teamRecycler.setLayoutManager(new LinearLayoutManager(this));
        teamRecycler.setAdapter(teamAdapter);
        teamRecycler.setNestedScrollingEnabled(false);

        androidx.recyclerview.widget.RecyclerView vehicleRecycler = findViewById(R.id.recyclerManagerDispatchVehicles);
        vehicleAdapter = new ManagerDispatchVehicleAdapter();
        vehicleRecycler.setLayoutManager(new LinearLayoutManager(this));
        vehicleRecycler.setAdapter(vehicleAdapter);
        vehicleRecycler.setNestedScrollingEnabled(false);
    }

    private void loadDashboard() {
        showLoading(true);
        repository.loadDashboard(new RepositoryCallback<ManagerDispatchState>() {
            @Override
            public void onSuccess(ManagerDispatchState data) {
                runOnUiThread(() -> {
                    showLoading(false);
                    requests.clear();
                    requests.addAll(data.getRequests());
                    teams.clear();
                    teams.addAll(data.getTeams());

                    if (selectedRequestId == 0L && !requests.isEmpty()) {
                        selectedRequestId = requests.get(0).getId();
                    }
                    if (selectedTeamId == 0L) {
                        for (ManagerDispatchState.TeamItem team : teams) {
                            if (team.isOnline() && "AVAILABLE".equalsIgnoreCase(team.getStatus())) {
                                selectedTeamId = team.getId();
                                break;
                            }
                        }
                        if (selectedTeamId == 0L && !teams.isEmpty()) {
                            selectedTeamId = teams.get(0).getId();
                        }
                    }

                    requestAdapter.submit(data.getRequests(), selectedRequestId);
                    teamAdapter.submit(data.getTeams(), selectedTeamId);
                    vehicleAdapter.submit(data.getVehicles());
                    requestCountView.setText(String.valueOf(data.getRequests().size()));
                    teamCountView.setText(String.valueOf(countReadyTeams(data.getTeams())));
                    vehicleCountView.setText(String.valueOf(data.getVehicles().size()));
                    syncSelectionSummary();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(message == null ? "Không tải được dữ liệu điều phối cứu trợ." : message);
                });
            }
        });
    }

    private int countReadyTeams(List<ManagerDispatchState.TeamItem> data) {
        int count = 0;
        for (ManagerDispatchState.TeamItem item : data) {
            if (item.isOnline() && "AVAILABLE".equalsIgnoreCase(item.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private void syncSelectionSummary() {
        ManagerDispatchState.QueueItem request = findSelectedRequest();
        ManagerDispatchState.TeamItem team = findSelectedTeam();
        selectedView.setText(request == null ? "" : request.getCode());
        String requestLabel = request == null ? "chưa chọn yêu cầu" : request.getCode();
        String teamLabel = team == null ? "chưa chọn đội" : team.getName();
        summaryView.setText("Đang chọn " + requestLabel + " -> " + teamLabel + ".");
        findViewById(R.id.buttonManagerConfirmDispatch).setEnabled(!submitting && request != null && team != null);
    }

    private ManagerDispatchState.QueueItem findSelectedRequest() {
        for (ManagerDispatchState.QueueItem item : requests) {
            if (item.getId() == selectedRequestId) return item;
        }
        return null;
    }

    private ManagerDispatchState.TeamItem findSelectedTeam() {
        for (ManagerDispatchState.TeamItem item : teams) {
            if (item.getId() == selectedTeamId) return item;
        }
        return null;
    }

    private void confirmDispatch() {
        ManagerDispatchState.QueueItem request = findSelectedRequest();
        ManagerDispatchState.TeamItem team = findSelectedTeam();
        if (request == null || team == null) {
            showShortToast("Vui lòng chọn yêu cầu và đội cứu trợ.");
            return;
        }
        submitting = true;
        syncSelectionSummary();
        repository.approveDispatch(request.getId(), team.getId(), "Điều phối từ mobile manager", new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                runOnUiThread(() -> {
                    submitting = false;
                    syncSelectionSummary();
                    showShortToast("Điều phối cứu trợ thành công.");
                    Intent intent = new Intent(ManagerDispatchActivity.this, ManagerReliefDetailActivity.class);
                    intent.putExtra(ManagerUi.EXTRA_REQUEST_ID, request.getId());
                    intent.putExtra(ManagerUi.EXTRA_REQUEST_CODE, request.getCode());
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    submitting = false;
                    syncSelectionSummary();
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(message == null ? "Không điều phối được yêu cầu cứu trợ." : message);
                });
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (!loading) {
            swipeRefreshLayout.setRefreshing(false);
        }
        errorView.setVisibility(View.GONE);
    }
}

package com.floodrescue.mobile.ui.role.coordinator.dashboard;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.data.model.ui.CoordinatorRequestItem;
import com.floodrescue.mobile.data.repository.CoordinatorDashboardRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.coordinator.blockedcitizen.BlockedCitizenListActivity;
import com.floodrescue.mobile.ui.role.coordinator.rescuequeue.CoordinatorRescueQueueActivity;
import com.floodrescue.mobile.ui.role.coordinator.taskgroup.list.CoordinatorTaskGroupListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CoordinatorDashboardActivity extends com.floodrescue.mobile.core.base.BaseActivity {

    private CoordinatorDashboardRepository repository;
    private CoordinatorUrgentRequestAdapter requestAdapter;
    private CoordinatorTaskGroupAdapter teamAdapter;
    private View progress;
    private TextView errorView;
    private LinearLayout emptySection;
    private LinearLayout emptyTeams;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_coordinator_dashboard);

        repository = new CoordinatorDashboardRepository(this);

        SessionManager sessionManager = new SessionManager(this);
        String fullName = AppNavigator.displayName(sessionManager);

        ((TextView) findViewById(R.id.textCoordinatorName)).setText(fullName);
        ((TextView) findViewById(R.id.textCoordinatorRole)).setText(AppNavigator.displayRole(sessionManager.getRole()));
        ((TextView) findViewById(R.id.textCoordinatorInitial)).setText(AppNavigator.initials(fullName));
        ((TextView) findViewById(R.id.textCoordinatorDate)).setText(formatToday());

        findViewById(R.id.cardCoordinatorQueue).setOnClickListener(v -> startActivity(new Intent(this, CoordinatorRescueQueueActivity.class)));
        findViewById(R.id.cardCoordinatorTeams).setOnClickListener(v -> startActivity(new Intent(this, CoordinatorTaskGroupListActivity.class)));

        ((MaterialButton) findViewById(R.id.buttonCoordinatorMap)).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.buttonCoordinatorNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.buttonCoordinatorLogout).setOnClickListener(v -> AppNavigator.logout(this));

        progress = findViewById(R.id.progressCoordinator);
        errorView = findViewById(R.id.textCoordinatorError);
        emptySection = findViewById(R.id.sectionEmptyRequests);
        emptyTeams = findViewById(R.id.sectionEmptyTeams);

        RecyclerView recycler = findViewById(R.id.recyclerUrgentRequests);
        requestAdapter = new CoordinatorUrgentRequestAdapter(this);
        recycler.setAdapter(requestAdapter);
        recycler.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        RecyclerView recyclerTeams = findViewById(R.id.recyclerCoordinatorTeams);
        teamAdapter = new CoordinatorTaskGroupAdapter(this);
        recyclerTeams.setAdapter(teamAdapter);
        recyclerTeams.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        loadUrgentRequests();
        loadTeams();
    }

    private void loadUrgentRequests() {
        showLoading(true);
        repository.loadUrgentRequests(new RepositoryCallback<List<CoordinatorRequestItem>>() {
            @Override
            public void onSuccess(List<CoordinatorRequestItem> data) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (data == null || data.isEmpty()) {
                        emptySection.setVisibility(View.VISIBLE);
                    } else {
                        emptySection.setVisibility(View.GONE);
                    }
                    requestAdapter.submit(data);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(message == null ? getString(R.string.citizen_request_load_error) : message);
                });
            }
        });
    }

    private void loadTeams() {
        repository.loadTaskGroups(new RepositoryCallback<List<com.floodrescue.mobile.data.model.ui.CoordinatorTaskGroupItem>>() {
            @Override
            public void onSuccess(List<com.floodrescue.mobile.data.model.ui.CoordinatorTaskGroupItem> data) {
                runOnUiThread(() -> {
                    if (data == null || data.isEmpty()) {
                        emptyTeams.setVisibility(View.VISIBLE);
                    } else {
                        emptyTeams.setVisibility(View.GONE);
                    }
                    teamAdapter.submit(data);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    emptyTeams.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.textCoordinatorTeamsError)).setText(
                            message == null ? getString(R.string.citizen_request_load_error) : message
                    );
                });
            }
        });
    }

    private void showLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        errorView.setVisibility(View.GONE);
    }

    private String formatToday() {
        return new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("vi", "VN")).format(new Date());
    }
}

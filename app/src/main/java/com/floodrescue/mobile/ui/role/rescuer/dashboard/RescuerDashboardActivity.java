package com.floodrescue.mobile.ui.role.rescuer.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.RescuerDashboardState;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.data.repository.RescuerDashboardRepository;
import com.floodrescue.mobile.ui.role.rescuer.relief.list.RescuerReliefListActivity;
import com.floodrescue.mobile.ui.role.rescuer.task.list.RescuerTaskListActivity;
import com.floodrescue.mobile.ui.role.rescuer.taskgroup.detail.RescuerTaskGroupDetailActivity;
import com.floodrescue.mobile.ui.role.rescuer.taskgroup.list.RescuerTaskGroupListActivity;
import com.floodrescue.mobile.ui.role.rescuer.teamlocation.RescuerTeamLocationActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RescuerDashboardActivity extends BaseActivity {

    private RescuerDashboardRepository repository;
    private RescuerAssetAdapter assetAdapter;
    private RescuerTaskGroupSummaryAdapter taskGroupAdapter;
    private View progressView;
    private TextView textError;
    private View notificationDot;
    private TextView textTeamStatus;
    private TextView textTeamName;
    private TextView textTeamLocation;
    private TextView textTeamUpdated;
    private TextView textActiveGroups;
    private TextView textActiveAssignments;
    private TextView textAssetsEmpty;
    private TextView textTaskGroupsEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_rescuer_dashboard);

        repository = new RescuerDashboardRepository(this);
        bindViews();
        setupLists();
        bindActions();
        loadDashboard();
    }

    private void bindViews() {
        progressView = findViewById(R.id.progressRescuerDashboard);
        textError = findViewById(R.id.textRescuerError);
        notificationDot = findViewById(R.id.viewRescuerNotificationDot);
        textTeamStatus = findViewById(R.id.textRescuerTeamStatus);
        textTeamName = findViewById(R.id.textRescuerTeamName);
        textTeamLocation = findViewById(R.id.textRescuerTeamLocation);
        textTeamUpdated = findViewById(R.id.textRescuerTeamUpdated);
        textActiveGroups = findViewById(R.id.textRescuerActiveGroups);
        textActiveAssignments = findViewById(R.id.textRescuerActiveAssignments);
        textAssetsEmpty = findViewById(R.id.textRescuerAssetsEmpty);
        textTaskGroupsEmpty = findViewById(R.id.textRescuerTaskGroupsEmpty);
    }

    private void setupLists() {
        RecyclerView recyclerAssets = findViewById(R.id.recyclerRescuerAssets);
        recyclerAssets.setLayoutManager(new LinearLayoutManager(this));
        recyclerAssets.setNestedScrollingEnabled(false);
        assetAdapter = new RescuerAssetAdapter();
        recyclerAssets.setAdapter(assetAdapter);

        RecyclerView recyclerTaskGroups = findViewById(R.id.recyclerRescuerTaskGroups);
        recyclerTaskGroups.setLayoutManager(new LinearLayoutManager(this));
        recyclerTaskGroups.setNestedScrollingEnabled(false);
        taskGroupAdapter = new RescuerTaskGroupSummaryAdapter(item -> {
            Intent intent = new Intent(this, RescuerTaskGroupDetailActivity.class);
            intent.putExtra(RescuerTaskGroupDetailActivity.EXTRA_TASK_GROUP_ID, item.getId());
            startActivity(intent);
        });
        recyclerTaskGroups.setAdapter(taskGroupAdapter);
    }

    private void bindActions() {
        findViewById(R.id.buttonRescuerNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.cardRescuerMission).setOnClickListener(v -> startActivity(new Intent(this, RescuerTaskListActivity.class)));
        findViewById(R.id.cardRescuerUpdate).setOnClickListener(v -> startActivity(new Intent(this, RescuerTaskGroupListActivity.class)));
        findViewById(R.id.cardRescuerMap).setOnClickListener(v -> startActivity(new Intent(this, RescuerTeamLocationActivity.class)));
        findViewById(R.id.cardRescuerChat).setOnClickListener(v -> startActivity(new Intent(this, RescuerReliefListActivity.class)));
        findViewById(R.id.buttonRescuerViewAssets).setOnClickListener(v -> startActivity(new Intent(this, RescuerTeamLocationActivity.class)));

        findViewById(R.id.navRescuerOverview).setOnClickListener(v -> { });
        findViewById(R.id.navRescuerMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.navRescuerTasks).setOnClickListener(v -> startActivity(new Intent(this, RescuerTaskListActivity.class)));
        findViewById(R.id.navRescuerProfile).setOnClickListener(v -> AppNavigator.openProfile(this));
    }

    private void loadDashboard() {
        setLoading(true);
        repository.loadDashboard(new RepositoryCallback<RescuerDashboardState>() {
            @Override
            public void onSuccess(RescuerDashboardState data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    renderState(data);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.rescuer_dashboard_error) : message);
                });
            }
        });
    }

    private void renderState(RescuerDashboardState state) {
        if (state == null) {
            return;
        }

        textTeamName.setText(isBlank(state.getTeamName()) ? "Đội cứu hộ" : state.getTeamName());
        textTeamLocation.setText(isBlank(state.getTeamLocationText())
                ? getString(R.string.rescuer_dashboard_location_unknown)
                : state.getTeamLocationText());
        textTeamUpdated.setText(getString(
                R.string.rescuer_dashboard_updated,
                formatUpdated(state.getTeamLocationUpdatedAt())
        ));

        boolean active = state.getActiveTaskGroups() > 0 || state.getActiveAssignments() > 0;
        textTeamStatus.setText(active
                ? getString(R.string.rescuer_dashboard_team_active)
                : getString(R.string.rescuer_dashboard_team_ready));
        textTeamStatus.setBackgroundResource(active ? R.drawable.bg_chip_info : R.drawable.bg_chip_success);
        textTeamStatus.setTextColor(getColor(active ? R.color.accent_dark : R.color.success));

        textActiveGroups.setText(String.format(Locale.getDefault(), "%02d", Math.max(state.getActiveTaskGroups(), 0)));
        textActiveAssignments.setText(String.format(Locale.getDefault(), "%02d", Math.max(state.getActiveAssignments(), 0)));

        assetAdapter.submit(state.getHeldAssets());
        taskGroupAdapter.submit(state.getTaskGroups());
        textAssetsEmpty.setVisibility(state.getHeldAssets() == null || state.getHeldAssets().isEmpty() ? View.VISIBLE : View.GONE);
        textTaskGroupsEmpty.setVisibility(state.getTaskGroups() == null || state.getTaskGroups().isEmpty() ? View.VISIBLE : View.GONE);

        notificationDot.setVisibility(state.getActiveTaskGroups() > 0 ? View.VISIBLE : View.GONE);
    }

    private String formatUpdated(String raw) {
        if (isBlank(raw)) {
            return new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault()).format(new Date());
        }
        return raw.replace('T', ' ');
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            textError.setVisibility(View.GONE);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

package com.floodrescue.mobile.ui.role.rescuer.teamlocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.RescuerTeamLocationState;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.data.repository.RescuerTeamLocationRepository;
import com.floodrescue.mobile.ui.role.rescuer.dashboard.RescuerDashboardActivity;
import com.floodrescue.mobile.ui.role.rescuer.task.list.RescuerTaskListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

import java.util.Locale;

public class RescuerTeamLocationActivity extends BaseActivity {

    private RescuerTeamLocationRepository repository;
    private RescuerTeamAssetAdapter assetAdapter;
    private RescuerTeamLocationState currentState;

    private View progressView;
    private TextView textError;
    private TextView textTeamName;
    private TextView textTeamStatus;
    private TextView textUpdated;
    private TextView textCoordinates;
    private TextView textCurrentText;
    private EditText editLatitude;
    private EditText editLongitude;
    private EditText editLocationText;
    private TextView textAssetsEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_rescuer_team_location);

        repository = new RescuerTeamLocationRepository(this);
        bindViews();
        setupList();
        bindActions();
        loadState();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackRescuerTeamLocation);
        progressView = findViewById(R.id.progressRescuerTeamLocation);
        textError = findViewById(R.id.textRescuerTeamLocationError);
        textTeamName = findViewById(R.id.textRescuerTeamLocationName);
        textTeamStatus = findViewById(R.id.textRescuerTeamLocationStatus);
        textUpdated = findViewById(R.id.textRescuerTeamLocationUpdated);
        textCoordinates = findViewById(R.id.textRescuerTeamLocationCoordinates);
        textCurrentText = findViewById(R.id.textRescuerTeamLocationCurrentText);
        editLatitude = findViewById(R.id.editRescuerLatitude);
        editLongitude = findViewById(R.id.editRescuerLongitude);
        editLocationText = findViewById(R.id.editRescuerLocationText);
        textAssetsEmpty = findViewById(R.id.textRescuerTeamAssetsEmpty);
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerRescuerTeamAssets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        assetAdapter = new RescuerTeamAssetAdapter();
        recyclerView.setAdapter(assetAdapter);
    }

    private void bindActions() {
        findViewById(R.id.buttonRefreshRescuerTeamLocation).setOnClickListener(v -> loadState());
        findViewById(R.id.buttonRescuerUpdateLocation).setOnClickListener(v -> submitLocation());
        findViewById(R.id.buttonRescuerReturnAssets).setOnClickListener(v -> returnAssets());

        findViewById(R.id.navRescuerLocationOverview).setOnClickListener(v ->
                startActivity(new Intent(this, RescuerDashboardActivity.class)));
        findViewById(R.id.navRescuerLocationCurrent).setOnClickListener(v -> { });
        findViewById(R.id.navRescuerLocationTasks).setOnClickListener(v ->
                startActivity(new Intent(this, RescuerTaskListActivity.class)));
        findViewById(R.id.navRescuerLocationProfile).setOnClickListener(v -> AppNavigator.openProfile(this));
    }

    private void loadState() {
        setLoading(true);
        repository.loadState(new RepositoryCallback<RescuerTeamLocationState>() {
            @Override
            public void onSuccess(RescuerTeamLocationState data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    currentState = data;
                    renderState(data);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.rescuer_team_location_error) : message);
                });
            }
        });
    }

    private void renderState(RescuerTeamLocationState state) {
        if (state == null) {
            return;
        }
        textTeamName.setText(isBlank(state.getTeamName())
                ? getString(R.string.rescuer_task_group_list_team_fallback)
                : state.getTeamName().trim());
        textCurrentText.setText(isBlank(state.getLocationText())
                ? getString(R.string.rescuer_team_location_current_empty)
                : state.getLocationText().trim());
        textUpdated.setText(getString(R.string.rescuer_dashboard_updated, formatTime(state.getUpdatedAt())));
        textCoordinates.setText(buildCoordinateLabel(state.getLatitude(), state.getLongitude()));
        editLatitude.setText(state.getLatitude() == null ? "" : String.valueOf(state.getLatitude()));
        editLongitude.setText(state.getLongitude() == null ? "" : String.valueOf(state.getLongitude()));
        editLocationText.setText(state.getLocationText() == null ? "" : state.getLocationText());

        boolean active = "ACTIVE".equalsIgnoreCase(state.getTeamStatus());
        textTeamStatus.setText(active
                ? getString(R.string.rescuer_dashboard_team_active)
                : getString(R.string.rescuer_dashboard_team_ready));
        textTeamStatus.setBackgroundResource(active ? R.drawable.bg_chip_info : R.drawable.bg_chip_success);
        textTeamStatus.setTextColor(getColor(active ? R.color.accent_dark : R.color.success));

        assetAdapter.submit(state.getHeldAssets());
        textAssetsEmpty.setVisibility(state.getHeldAssets() == null || state.getHeldAssets().isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void submitLocation() {
        Double latitude = readDouble(editLatitude);
        Double longitude = readDouble(editLongitude);
        if (latitude == null || longitude == null) {
            showShortToast(getString(R.string.rescuer_team_location_invalid_coordinates));
            return;
        }

        setLoading(true);
        repository.updateLocation(latitude, longitude, valueOf(editLocationText), new RepositoryCallback<RescuerTeamLocationState>() {
            @Override
            public void onSuccess(RescuerTeamLocationState data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    currentState = data;
                    renderState(data);
                    showShortToast(getString(R.string.rescuer_team_location_update_success));
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.rescuer_team_location_update_error) : message);
                });
            }
        });
    }

    private void returnAssets() {
        if (currentState == null || currentState.getHeldAssets() == null || currentState.getHeldAssets().isEmpty()) {
            showShortToast(getString(R.string.rescuer_team_location_no_assets));
            return;
        }

        setLoading(true);
        repository.returnAssets(new RepositoryCallback<RescuerTeamLocationState>() {
            @Override
            public void onSuccess(RescuerTeamLocationState data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    currentState = data;
                    renderState(data);
                    showShortToast(getString(R.string.rescuer_team_location_return_success));
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.rescuer_team_location_return_error) : message);
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            textError.setVisibility(View.GONE);
        }
    }

    private Double readDouble(EditText editText) {
        try {
            String value = valueOf(editText);
            return value.isEmpty() ? null : Double.parseDouble(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String valueOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String buildCoordinateLabel(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return getString(R.string.citizen_detail_location_unknown);
        }
        return String.format(Locale.getDefault(), "%.5f, %.5f", latitude, longitude);
    }

    private String formatTime(String raw) {
        if (isBlank(raw)) {
            return getString(R.string.citizen_request_time_unknown);
        }
        return raw.trim().replace('T', ' ');
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

package com.floodrescue.mobile.ui.role.coordinator.taskgroup.create;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.CoordinatorRescueDetailState;
import com.floodrescue.mobile.data.model.ui.CoordinatorTeamOption;
import com.floodrescue.mobile.data.repository.CoordinatorOperationsRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.coordinator.rescuequeue.CoordinatorRescueQueueActivity;
import com.floodrescue.mobile.ui.role.coordinator.taskgroup.list.CoordinatorTaskGroupListActivity;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorTaskGroupCreateActivity extends BaseActivity
        implements SelectedRescueRequestAdapter.Listener, CoordinatorTeamOptionAdapter.Listener {

    public static final String EXTRA_SELECTED_REQUEST_IDS = "selected_request_ids";

    private final List<Long> selectedRequestIds = new ArrayList<>();
    private final List<CoordinatorRescueDetailState> selectedRequests = new ArrayList<>();

    private CoordinatorOperationsRepository repository;
    private SelectedRescueRequestAdapter requestAdapter;
    private CoordinatorTeamOptionAdapter teamAdapter;
    private View progressView;
    private TextView textError;
    private TextView textSelectedTitle;
    private TextView textSelectionEmpty;
    private TextView textTeamEmpty;
    private TextView textNoteCount;
    private EditText editNote;
    private TextView buttonSubmit;
    private boolean loadingRequests;
    private boolean loadingTeams;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_coordinator_task_group_create);

        repository = new CoordinatorOperationsRepository(this);
        parseSelectedIds();
        if (selectedRequestIds.isEmpty()) {
            showShortToast(getString(R.string.coordinator_task_group_create_empty_selection));
            finish();
            return;
        }

        bindViews();
        setupLists();
        bindActions();
        updateSelectedTitle();
        updateNoteCount();
        loadSelectedRequests();
        loadTeams();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackTaskGroupCreate);
        progressView = findViewById(R.id.progressTaskGroupCreate);
        textError = findViewById(R.id.textTaskGroupCreateError);
        textSelectedTitle = findViewById(R.id.textSelectedRequestSectionTitle);
        textSelectionEmpty = findViewById(R.id.textTaskGroupSelectionEmpty);
        textTeamEmpty = findViewById(R.id.textTaskGroupTeamEmpty);
        textNoteCount = findViewById(R.id.textTaskGroupNoteCount);
        editNote = findViewById(R.id.editTaskGroupNote);
        buttonSubmit = findViewById(R.id.buttonCreateTaskGroupSubmit);
    }

    private void setupLists() {
        RecyclerView recyclerSelectedRequests = findViewById(R.id.recyclerSelectedRequests);
        recyclerSelectedRequests.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new SelectedRescueRequestAdapter(this);
        recyclerSelectedRequests.setAdapter(requestAdapter);

        RecyclerView recyclerTeamOptions = findViewById(R.id.recyclerTeamOptions);
        recyclerTeamOptions.setLayoutManager(new LinearLayoutManager(this));
        teamAdapter = new CoordinatorTeamOptionAdapter(this);
        recyclerTeamOptions.setAdapter(teamAdapter);
    }

    private void bindActions() {
        findViewById(R.id.buttonTaskGroupAddMore).setOnClickListener(v ->
                startActivity(new Intent(this, CoordinatorRescueQueueActivity.class)));
        buttonSubmit.setOnClickListener(v -> submitTaskGroup());
        editNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateNoteCount();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void parseSelectedIds() {
        String raw = getIntent().getStringExtra(EXTRA_SELECTED_REQUEST_IDS);
        if (raw == null || raw.trim().isEmpty()) {
            return;
        }
        String[] parts = raw.split(",");
        for (String part : parts) {
            try {
                long id = Long.parseLong(part.trim());
                if (id > 0L) {
                    selectedRequestIds.add(id);
                }
            } catch (Exception ignored) {
                // Skip malformed values.
            }
        }
    }

    private void loadSelectedRequests() {
        if (selectedRequestIds.isEmpty()) {
            renderSelectedRequests();
            return;
        }
        loadingRequests = true;
        updateLoadingState();

        selectedRequests.clear();
        final int[] completed = {0};
        final String[] firstError = {null};

        for (Long id : selectedRequestIds) {
            repository.getRescueRequestDetail(id, new RepositoryCallback<CoordinatorRescueDetailState>() {
                @Override
                public void onSuccess(CoordinatorRescueDetailState data) {
                    runOnUiThread(() -> {
                        if (data != null) {
                            selectedRequests.add(data);
                        }
                        finishRequestLoad(completed, firstError);
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        if (firstError[0] == null) {
                            firstError[0] = message;
                        }
                        finishRequestLoad(completed, firstError);
                    });
                }
            });
        }
    }

    private void finishRequestLoad(int[] completed, String[] firstError) {
        completed[0]++;
        if (completed[0] < selectedRequestIds.size()) {
            return;
        }
        loadingRequests = false;
        updateLoadingState();
        orderSelectedRequests();
        renderSelectedRequests();
        if (firstError[0] != null && selectedRequests.isEmpty()) {
            showError(firstError[0]);
        }
    }

    private void orderSelectedRequests() {
        List<CoordinatorRescueDetailState> ordered = new ArrayList<>();
        for (Long id : selectedRequestIds) {
            for (CoordinatorRescueDetailState state : selectedRequests) {
                if (state.getId() == id) {
                    ordered.add(state);
                    break;
                }
            }
        }
        selectedRequests.clear();
        selectedRequests.addAll(ordered);
    }

    private void renderSelectedRequests() {
        requestAdapter.submit(selectedRequests);
        textSelectionEmpty.setVisibility(selectedRequests.isEmpty() ? View.VISIBLE : View.GONE);
        updateSelectedTitle();
        updateSubmitState();
    }

    private void loadTeams() {
        loadingTeams = true;
        updateLoadingState();
        repository.getCoordinatorTeams(new RepositoryCallback<List<CoordinatorTeamOption>>() {
            @Override
            public void onSuccess(List<CoordinatorTeamOption> data) {
                runOnUiThread(() -> {
                    loadingTeams = false;
                    updateLoadingState();
                    teamAdapter.submit(data);
                    textTeamEmpty.setVisibility(data == null || data.isEmpty() ? View.VISIBLE : View.GONE);
                    if (data != null && !data.isEmpty() && teamAdapter.getSelectedId() <= 0L) {
                        CoordinatorTeamOption preferred = data.get(0);
                        for (CoordinatorTeamOption item : data) {
                            if (item.isOnline()) {
                                preferred = item;
                                break;
                            }
                        }
                        teamAdapter.setSelectedId(preferred.getId());
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    loadingTeams = false;
                    updateLoadingState();
                    textTeamEmpty.setVisibility(View.VISIBLE);
                    showError(message == null ? getString(R.string.coordinator_task_group_create_load_error) : message);
                });
            }
        });
    }

    private void submitTaskGroup() {
        if (selectedRequestIds.isEmpty()) {
            showShortToast(getString(R.string.coordinator_task_group_create_empty_selection));
            return;
        }
        setSubmitEnabled(false);
        showError(null);

        Long teamId = teamAdapter.getSelectedId() > 0L ? teamAdapter.getSelectedId() : null;
        repository.createTaskGroup(
                new ArrayList<>(selectedRequestIds),
                teamId,
                textOf(editNote),
                new RepositoryCallback<Long>() {
                    @Override
                    public void onSuccess(Long data) {
                        runOnUiThread(() -> {
                            setSubmitEnabled(true);
                            showShortToast(getString(R.string.coordinator_task_group_create_success));
                            startActivity(new Intent(CoordinatorTaskGroupCreateActivity.this, CoordinatorTaskGroupListActivity.class));
                            finish();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            setSubmitEnabled(true);
                            showError(message == null ? getString(R.string.coordinator_task_group_create_load_error) : message);
                        });
                    }
                }
        );
    }

    private void setSubmitEnabled(boolean enabled) {
        buttonSubmit.setEnabled(enabled);
        buttonSubmit.setAlpha(enabled ? 1f : 0.6f);
    }

    private void updateSelectedTitle() {
        textSelectedTitle.setText(getString(R.string.coordinator_task_group_create_selected, selectedRequestIds.size()));
    }

    private void updateNoteCount() {
        textNoteCount.setText(textOf(editNote).length() + "/500");
    }

    private void updateLoadingState() {
        progressView.setVisibility((loadingRequests || loadingTeams) ? View.VISIBLE : View.GONE);
    }

    private void updateSubmitState() {
        setSubmitEnabled(!selectedRequestIds.isEmpty());
    }

    private void showError(String message) {
        if (message == null || message.trim().isEmpty()) {
            textError.setVisibility(View.GONE);
            return;
        }
        textError.setVisibility(View.VISIBLE);
        textError.setText(message);
    }

    private String textOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    @Override
    public void onRemoveRequest(CoordinatorRescueDetailState item) {
        if (item == null) {
            return;
        }
        selectedRequestIds.remove(item.getId());
        List<CoordinatorRescueDetailState> next = new ArrayList<>(selectedRequests);
        next.remove(item);
        selectedRequests.clear();
        selectedRequests.addAll(next);
        renderSelectedRequests();
    }

    @Override
    public void onSelectTeam(CoordinatorTeamOption item) {
        if (item != null) {
            teamAdapter.setSelectedId(item.getId());
        }
    }
}

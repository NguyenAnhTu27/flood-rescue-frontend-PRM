package com.floodrescue.mobile.ui.role.rescuer.taskgroup.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.RescuerTaskGroupDetailState;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.data.repository.RescuerTaskGroupDetailRepository;
import com.floodrescue.mobile.ui.role.rescuer.task.detail.RescuerTaskDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerTaskGroupDetailActivity extends BaseActivity implements RescuerTaskGroupRequestAdapter.Listener {

    public static final String EXTRA_TASK_GROUP_ID = "task_group_id";
    private static final int TAB_REQUESTS = 0;
    private static final int TAB_ASSIGNMENTS = 1;
    private static final int TAB_TIMELINE = 2;
    private static final int TAB_ACKS = 3;

    private long taskGroupId = -1L;
    private int activeTab = TAB_REQUESTS;
    private RescuerTaskGroupDetailRepository repository;
    private RescuerTaskGroupDetailState currentState;

    private View progressView;
    private TextView textError;
    private TextView textCode;
    private TextView textStatus;
    private TextView textTeam;
    private TextView textMeta;
    private TextView textNote;
    private TextView textSectionTitle;
    private View sectionRequests;
    private View sectionAssignments;
    private View sectionTimeline;
    private View sectionAcks;
    private RecyclerView recyclerRequests;
    private RecyclerView recyclerAssignments;
    private RecyclerView recyclerTimeline;
    private RecyclerView recyclerEmergency;
    private TextView textRequestsEmpty;
    private TextView textAssignmentsEmpty;
    private TextView textTimelineEmpty;
    private TextView textAcksEmpty;
    private TextView buttonTabRequests;
    private TextView buttonTabAssignments;
    private TextView buttonTabTimeline;
    private TextView buttonTabAcks;

    private RescuerTaskGroupRequestAdapter requestAdapter;
    private RescuerTaskGroupAssignmentAdapter assignmentAdapter;
    private RescuerTaskGroupTimelineAdapter timelineAdapter;
    private RescuerTaskGroupEmergencyAdapter emergencyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_rescuer_task_group_detail);

        repository = new RescuerTaskGroupDetailRepository(this);
        taskGroupId = getIntent().getLongExtra(EXTRA_TASK_GROUP_ID, -1L);
        if (taskGroupId <= 0L) {
            showShortToast(getString(R.string.rescuer_task_group_detail_missing_id));
            finish();
            return;
        }

        bindViews();
        setupLists();
        bindActions();
        switchTab(TAB_REQUESTS);
        loadDetail();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackRescuerTaskGroupDetail);
        progressView = findViewById(R.id.progressRescuerTaskGroupDetail);
        textError = findViewById(R.id.textRescuerTaskGroupDetailError);
        textCode = findViewById(R.id.textRescuerTaskGroupCode);
        textStatus = findViewById(R.id.textRescuerTaskGroupStatus);
        textTeam = findViewById(R.id.textRescuerTaskGroupTeam);
        textMeta = findViewById(R.id.textRescuerTaskGroupMeta);
        textNote = findViewById(R.id.textRescuerTaskGroupNote);
        textSectionTitle = findViewById(R.id.textRescuerTaskGroupSectionTitle);
        sectionRequests = findViewById(R.id.sectionRescuerGroupRequests);
        sectionAssignments = findViewById(R.id.sectionRescuerGroupAssignments);
        sectionTimeline = findViewById(R.id.sectionRescuerGroupTimeline);
        sectionAcks = findViewById(R.id.sectionRescuerGroupAcks);
        recyclerRequests = findViewById(R.id.recyclerRescuerGroupRequests);
        recyclerAssignments = findViewById(R.id.recyclerRescuerGroupAssignments);
        recyclerTimeline = findViewById(R.id.recyclerRescuerGroupTimeline);
        recyclerEmergency = findViewById(R.id.recyclerRescuerGroupEmergency);
        textRequestsEmpty = findViewById(R.id.textRescuerGroupRequestsEmpty);
        textAssignmentsEmpty = findViewById(R.id.textRescuerGroupAssignmentsEmpty);
        textTimelineEmpty = findViewById(R.id.textRescuerGroupTimelineEmpty);
        textAcksEmpty = findViewById(R.id.textRescuerGroupAcksEmpty);
        buttonTabRequests = findViewById(R.id.buttonRescuerGroupTabRequests);
        buttonTabAssignments = findViewById(R.id.buttonRescuerGroupTabAssignments);
        buttonTabTimeline = findViewById(R.id.buttonRescuerGroupTabTimeline);
        buttonTabAcks = findViewById(R.id.buttonRescuerGroupTabAcks);
    }

    private void setupLists() {
        recyclerRequests.setLayoutManager(new LinearLayoutManager(this));
        recyclerRequests.setNestedScrollingEnabled(false);
        requestAdapter = new RescuerTaskGroupRequestAdapter(this);
        recyclerRequests.setAdapter(requestAdapter);

        recyclerAssignments.setLayoutManager(new LinearLayoutManager(this));
        recyclerAssignments.setNestedScrollingEnabled(false);
        assignmentAdapter = new RescuerTaskGroupAssignmentAdapter();
        recyclerAssignments.setAdapter(assignmentAdapter);

        recyclerTimeline.setLayoutManager(new LinearLayoutManager(this));
        recyclerTimeline.setNestedScrollingEnabled(false);
        timelineAdapter = new RescuerTaskGroupTimelineAdapter();
        recyclerTimeline.setAdapter(timelineAdapter);

        recyclerEmergency.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmergency.setNestedScrollingEnabled(false);
        emergencyAdapter = new RescuerTaskGroupEmergencyAdapter();
        recyclerEmergency.setAdapter(emergencyAdapter);
    }

    private void bindActions() {
        findViewById(R.id.buttonRefreshRescuerTaskGroupDetail).setOnClickListener(v -> loadDetail());
        findViewById(R.id.buttonRescuerGroupStatus).setOnClickListener(v -> openStatusDialog());
        findViewById(R.id.buttonRescuerGroupEmergency).setOnClickListener(v -> openEmergencyDialog());
        buttonTabRequests.setOnClickListener(v -> switchTab(TAB_REQUESTS));
        buttonTabAssignments.setOnClickListener(v -> switchTab(TAB_ASSIGNMENTS));
        buttonTabTimeline.setOnClickListener(v -> switchTab(TAB_TIMELINE));
        buttonTabAcks.setOnClickListener(v -> switchTab(TAB_ACKS));
    }

    private void loadDetail() {
        setLoading(true);
        repository.loadDetail(taskGroupId, new RepositoryCallback<RescuerTaskGroupDetailState>() {
            @Override
            public void onSuccess(RescuerTaskGroupDetailState data) {
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
                    textError.setText(message == null ? getString(R.string.rescuer_task_group_detail_error) : message);
                });
            }
        });
    }

    private void renderState(RescuerTaskGroupDetailState state) {
        if (state == null) {
            return;
        }

        textCode.setText(formatCode(state.getCode()));
        textStatus.setText(mapStatusLabel(state.getStatus()));
        applyStatusStyle(textStatus, state.getStatus());
        textTeam.setText(isBlank(state.getTeamName())
                ? getString(R.string.rescuer_task_group_list_team_fallback)
                : state.getTeamName().trim());

        String creator = isBlank(state.getCreatedByName())
                ? getString(R.string.rescuer_task_group_detail_actor_default)
                : state.getCreatedByName().trim();
        String updated = formatTime(state.getUpdatedAt(), state.getCreatedAt());
        textMeta.setText(
                getString(R.string.rescuer_task_group_detail_creator) + ": " + creator
                        + "\n"
                        + getString(R.string.rescuer_task_group_detail_updated) + ": " + updated
        );

        textNote.setText(isBlank(state.getNote())
                ? getString(R.string.rescuer_task_group_detail_note_empty)
                : state.getNote().trim());

        requestAdapter.submit(state.getRequests());
        assignmentAdapter.submit(state.getAssignments());
        timelineAdapter.submit(state.getTimeline());
        emergencyAdapter.submit(state.getEmergencyAcks());

        textRequestsEmpty.setVisibility(state.getRequests() == null || state.getRequests().isEmpty() ? View.VISIBLE : View.GONE);
        textAssignmentsEmpty.setVisibility(state.getAssignments() == null || state.getAssignments().isEmpty() ? View.VISIBLE : View.GONE);
        textTimelineEmpty.setVisibility(state.getTimeline() == null || state.getTimeline().isEmpty() ? View.VISIBLE : View.GONE);
        textAcksEmpty.setVisibility(state.getEmergencyAcks() == null || state.getEmergencyAcks().isEmpty() ? View.VISIBLE : View.GONE);
        updateSectionTitle();
    }

    private void switchTab(int tab) {
        activeTab = tab;
        styleTab(buttonTabRequests, tab == TAB_REQUESTS);
        styleTab(buttonTabAssignments, tab == TAB_ASSIGNMENTS);
        styleTab(buttonTabTimeline, tab == TAB_TIMELINE);
        styleTab(buttonTabAcks, tab == TAB_ACKS);

        sectionRequests.setVisibility(tab == TAB_REQUESTS ? View.VISIBLE : View.GONE);
        sectionAssignments.setVisibility(tab == TAB_ASSIGNMENTS ? View.VISIBLE : View.GONE);
        sectionTimeline.setVisibility(tab == TAB_TIMELINE ? View.VISIBLE : View.GONE);
        sectionAcks.setVisibility(tab == TAB_ACKS ? View.VISIBLE : View.GONE);
        updateSectionTitle();
    }

    private void styleTab(TextView view, boolean active) {
        view.setBackgroundResource(active ? R.drawable.bg_rescuer_tab_active : R.drawable.bg_rescuer_tab_inactive);
        view.setTextColor(getColor(active ? R.color.accent_dark : R.color.text_secondary));
    }

    private void updateSectionTitle() {
        if (textSectionTitle == null) {
            return;
        }
        int titleRes = R.string.rescuer_task_group_detail_section_requests;
        if (activeTab == TAB_ASSIGNMENTS) {
            titleRes = R.string.rescuer_task_group_detail_section_assignments;
        } else if (activeTab == TAB_TIMELINE) {
            titleRes = R.string.rescuer_task_group_detail_section_timeline;
        } else if (activeTab == TAB_ACKS) {
            titleRes = R.string.rescuer_task_group_detail_section_acks;
        }
        textSectionTitle.setText(titleRes);
    }

    private void openStatusDialog() {
        if (currentState == null) {
            return;
        }

        List<String> options = new ArrayList<>();
        String status = normalize(currentState.getStatus());
        if ("NEW".equals(status)) {
            options.add("ASSIGNED");
            options.add("IN_PROGRESS");
            options.add("CANCELLED");
        } else if ("ASSIGNED".equals(status) || "IN_PROGRESS".equals(status)) {
            options.add("COMPLETED");
            options.add("CANCELLED");
        }

        if (options.isEmpty()) {
            showShortToast(getString(R.string.rescuer_task_group_detail_no_action));
            return;
        }

        String[] labels = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            labels[i] = mapStatusLabel(options.get(i));
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.rescuer_task_group_detail_status_dialog_title)
                .setItems(labels, (dialog, which) -> openStatusNoteDialog(options.get(which)))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void openStatusNoteDialog(String targetStatus) {
        EditText editText = new EditText(this);
        editText.setHint(R.string.rescuer_task_group_detail_status_note_hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMinLines(3);
        editText.setPadding(dp(18), dp(16), dp(18), dp(16));

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.rescuer_task_group_detail_status_confirm, mapStatusLabel(targetStatus)))
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> updateStatus(targetStatus, valueOf(editText)))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void updateStatus(String status, String note) {
        setLoading(true);
        repository.updateStatus(taskGroupId, status, note, new RepositoryCallback<RescuerTaskGroupDetailState>() {
            @Override
            public void onSuccess(RescuerTaskGroupDetailState data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    currentState = data;
                    renderState(data);
                    showShortToast(getString(R.string.rescuer_task_group_detail_status_success));
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.rescuer_task_group_detail_status_error) : message);
                });
            }
        });
    }

    private void openEmergencyDialog() {
        String[] labels = new String[] {
                getString(R.string.rescuer_task_group_detail_emergency_high),
                getString(R.string.rescuer_task_group_detail_emergency_medium),
                getString(R.string.rescuer_task_group_detail_emergency_low)
        };
        String[] values = new String[] {"HIGH", "MEDIUM", "LOW"};

        new AlertDialog.Builder(this)
                .setTitle(R.string.rescuer_task_group_detail_emergency_dialog_title)
                .setItems(labels, (dialog, which) -> openEmergencyReasonDialog(values[which]))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void openEmergencyReasonDialog(String severity) {
        EditText editText = new EditText(this);
        editText.setHint(R.string.rescuer_task_group_detail_emergency_reason_hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMinLines(3);
        editText.setPadding(dp(18), dp(16), dp(18), dp(16));

        new AlertDialog.Builder(this)
                .setTitle(R.string.rescuer_task_group_detail_emergency_dialog_title)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String reason = valueOf(editText);
                    if (isBlank(reason)) {
                        showShortToast(getString(R.string.rescuer_task_group_detail_emergency_reason_error));
                        return;
                    }
                    sendEmergency(severity, reason);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void sendEmergency(String severity, String reason) {
        setLoading(true);
        repository.escalate(taskGroupId, severity, reason, new RepositoryCallback<RescuerTaskGroupDetailState>() {
            @Override
            public void onSuccess(RescuerTaskGroupDetailState data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    currentState = data;
                    renderState(data);
                    switchTab(TAB_ACKS);
                    showShortToast(getString(R.string.rescuer_task_group_detail_emergency_success));
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.rescuer_task_group_detail_emergency_error) : message);
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

    private String mapStatusLabel(String status) {
        String safe = normalize(status);
        if ("ASSIGNED".equals(safe) || "IN_PROGRESS".equals(safe)) {
            return getString(R.string.rescuer_task_group_list_status_active);
        }
        if ("DONE".equals(safe) || "COMPLETED".equals(safe)) {
            return getString(R.string.rescuer_task_group_list_status_done);
        }
        if ("CANCELLED".equals(safe)) {
            return getString(R.string.citizen_request_status_cancelled);
        }
        return getString(R.string.rescuer_task_group_list_status_pending);
    }

    private void applyStatusStyle(TextView view, String status) {
        String safe = normalize(status);
        if ("ASSIGNED".equals(safe) || "IN_PROGRESS".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(getColor(R.color.success));
            return;
        }
        if ("DONE".equals(safe) || "COMPLETED".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_soft);
            view.setTextColor(getColor(R.color.text_secondary));
            return;
        }
        if ("CANCELLED".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(getColor(R.color.danger));
            return;
        }
        view.setBackgroundResource(R.drawable.bg_chip_warning);
        view.setTextColor(getColor(R.color.warning));
    }

    private String formatCode(String code) {
        if (isBlank(code)) {
            return "#TG";
        }
        String safe = code.trim();
        return safe.startsWith("#") ? safe : ("#" + safe);
    }

    private String formatTime(String updatedAt, String createdAt) {
        String value = !isBlank(updatedAt) ? updatedAt : createdAt;
        if (isBlank(value)) {
            return getString(R.string.citizen_request_time_unknown);
        }
        return value.trim().replace('T', ' ');
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public void onOpenTaskDetail(RescuerTaskGroupDetailState.RequestItem item) {
        if (item == null || item.getId() <= 0L) {
            showShortToast(getString(R.string.rescuer_task_detail_missing_id));
            return;
        }
        Intent intent = new Intent(this, RescuerTaskDetailActivity.class);
        intent.putExtra(RescuerTaskDetailActivity.EXTRA_TASK_ID, item.getId());
        intent.putExtra(RescuerTaskDetailActivity.EXTRA_TASK_GROUP_ID, taskGroupId);
        startActivity(intent);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String valueOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}

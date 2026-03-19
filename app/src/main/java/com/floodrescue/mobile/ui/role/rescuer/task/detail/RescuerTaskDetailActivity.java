package com.floodrescue.mobile.ui.role.rescuer.task.detail;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.floodrescue.mobile.BuildConfig;
import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.core.util.RemoteImageLoader;
import com.floodrescue.mobile.data.model.ui.RescuerTaskDetailState;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.data.repository.RescuerTaskDetailRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerTaskDetailActivity extends BaseActivity {

    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_TASK_GROUP_ID = "task_group_id";

    private long taskId = -1L;
    private RescuerTaskDetailRepository repository;
    private RescuerTaskDetailState currentState;

    private View progressView;
    private TextView textError;
    private TextView textCode;
    private TextView textStatus;
    private TextView textPriority;
    private TextView textCitizenBadge;
    private TextView textCitizenName;
    private TextView textPeople;
    private TextView textTime;
    private TextView textVerified;
    private TextView textAddress;
    private TextView textCoordinates;
    private TextView textLocationDescription;
    private TextView textDescription;
    private TextView textAttachmentCount;
    private LinearLayout containerAttachments;
    private TextView textAttachmentsEmpty;
    private LinearLayout containerTimeline;
    private TextView textTimelineEmpty;
    private TextView buttonCall;
    private TextView buttonNote;
    private TextView buttonStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_rescuer_task_detail);

        repository = new RescuerTaskDetailRepository(this);
        taskId = getIntent().getLongExtra(EXTRA_TASK_ID, -1L);
        if (taskId <= 0L) {
            showShortToast(getString(R.string.rescuer_task_detail_missing_id));
            finish();
            return;
        }

        bindViews();
        bindActions();
        loadDetail();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackRescuerTaskDetail);
        progressView = findViewById(R.id.progressRescuerTaskDetail);
        textError = findViewById(R.id.textRescuerTaskDetailError);
        textCode = findViewById(R.id.textRescuerTaskCode);
        textStatus = findViewById(R.id.textRescuerTaskStatus);
        textPriority = findViewById(R.id.textRescuerTaskPriority);
        textCitizenBadge = findViewById(R.id.textRescuerCitizenBadge);
        textCitizenName = findViewById(R.id.textRescuerCitizenName);
        textPeople = findViewById(R.id.textRescuerTaskPeople);
        textTime = findViewById(R.id.textRescuerTaskTime);
        textVerified = findViewById(R.id.textRescuerTaskVerifiedChip);
        textAddress = findViewById(R.id.textRescuerTaskAddress);
        textCoordinates = findViewById(R.id.textRescuerTaskCoordinates);
        textLocationDescription = findViewById(R.id.textRescuerTaskLocationDescription);
        textDescription = findViewById(R.id.textRescuerTaskDescription);
        textAttachmentCount = findViewById(R.id.textRescuerTaskAttachmentCount);
        containerAttachments = findViewById(R.id.containerRescuerTaskAttachments);
        textAttachmentsEmpty = findViewById(R.id.textRescuerTaskAttachmentsEmpty);
        containerTimeline = findViewById(R.id.containerRescuerTaskTimeline);
        textTimelineEmpty = findViewById(R.id.textRescuerTaskTimelineEmpty);
        buttonCall = findViewById(R.id.buttonRescuerCallCitizen);
        buttonNote = findViewById(R.id.buttonRescuerTaskNote);
        buttonStatus = findViewById(R.id.buttonRescuerTaskStatus);
    }

    private void bindActions() {
        findViewById(R.id.buttonRefreshRescuerTaskDetail).setOnClickListener(v -> loadDetail());
        buttonCall.setOnClickListener(v -> callCitizen());
        buttonNote.setOnClickListener(v -> openNoteDialog());
        buttonStatus.setOnClickListener(v -> openStatusDialog());
    }

    private void loadDetail() {
        setLoading(true);
        repository.loadDetail(taskId, new RepositoryCallback<RescuerTaskDetailState>() {
            @Override
            public void onSuccess(RescuerTaskDetailState data) {
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
                    showError(message == null ? getString(R.string.rescuer_task_detail_error) : message);
                });
            }
        });
    }

    private void renderState(RescuerTaskDetailState state) {
        if (state == null) {
            return;
        }

        textCode.setText(formatCode(state.getCode()));
        textStatus.setText(mapStatusLabel(state.getStatus()));
        applyStatusStyle(textStatus, state.getStatus());
        textPriority.setText(mapPriorityLabel(state.getPriority()));
        applyPriorityStyle(textPriority, state.getPriority());
        textCitizenBadge.setText(initials(state.getCitizenName()));
        textCitizenName.setText(isBlank(state.getCitizenName())
                ? getString(R.string.coordinator_rescue_detail_citizen_unknown)
                : state.getCitizenName().trim());
        textPeople.setText(getResources().getQuantityString(
                R.plurals.citizen_request_people_count,
                Math.max(state.getPeopleCount(), 0),
                Math.max(state.getPeopleCount(), 0)
        ));
        textTime.setText(formatTime(state.getUpdatedAt()));
        textVerified.setVisibility(state.isLocationVerified() ? View.VISIBLE : View.GONE);
        textAddress.setText(isBlank(state.getAddress())
                ? getString(R.string.coordinator_rescue_detail_address_unknown)
                : state.getAddress().trim());
        textCoordinates.setText(buildCoordinateLabel(state.getLatitude(), state.getLongitude()));
        textLocationDescription.setText(isBlank(state.getLocationDescription())
                ? getString(R.string.citizen_dashboard_address_placeholder)
                : state.getLocationDescription().trim());
        textDescription.setText(isBlank(state.getDescription())
                ? getString(R.string.rescuer_task_list_description_placeholder)
                : state.getDescription().trim());

        buttonCall.setEnabled(!isBlank(state.getCitizenPhone()));
        buttonCall.setAlpha(!isBlank(state.getCitizenPhone()) ? 1f : 0.45f);

        renderAttachments(state.getAttachments());
        renderTimeline(state.getTimeline());
    }

    private void renderAttachments(List<RescuerTaskDetailState.AttachmentItem> attachments) {
        containerAttachments.removeAllViews();
        int count = attachments == null ? 0 : attachments.size();
        textAttachmentCount.setText(String.valueOf(count));
        if (count == 0) {
            textAttachmentsEmpty.setVisibility(View.VISIBLE);
            return;
        }
        textAttachmentsEmpty.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (RescuerTaskDetailState.AttachmentItem item : attachments) {
            View itemView = inflater.inflate(R.layout.item_citizen_rescue_attachment, containerAttachments, false);
            RemoteImageLoader.load((ImageView) itemView.findViewById(R.id.imageAttachment), buildAttachmentUrl(item.getFileUrl()));
            containerAttachments.addView(itemView);
        }
    }

    private void renderTimeline(List<RescuerTaskDetailState.TimelineItem> timeline) {
        containerTimeline.removeAllViews();
        if (timeline == null || timeline.isEmpty()) {
            textTimelineEmpty.setVisibility(View.VISIBLE);
            return;
        }
        textTimelineEmpty.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < timeline.size(); i++) {
            View itemView = inflater.inflate(R.layout.item_citizen_rescue_timeline, containerTimeline, false);
            bindTimelineItem(itemView, timeline.get(i), i == timeline.size() - 1);
            containerTimeline.addView(itemView);
        }
    }

    private void bindTimelineItem(View itemView, RescuerTaskDetailState.TimelineItem item, boolean isLast) {
        View dot = itemView.findViewById(R.id.viewTimelineDot);
        View line = itemView.findViewById(R.id.viewTimelineLine);
        ((TextView) itemView.findViewById(R.id.textTimelineTitle)).setText(mapTimelineTitle(item));
        ((TextView) itemView.findViewById(R.id.textTimelineTime)).setText(formatTime(item.getCreatedAt()));
        TextView noteView = itemView.findViewById(R.id.textTimelineNote);
        noteView.setText(buildTimelineNote(item));
        noteView.setVisibility(isBlank(noteView.getText() == null ? null : noteView.getText().toString()) ? View.GONE : View.VISIBLE);

        int color = resolveTimelineColor(item.getEventType());
        dot.setBackground(makeCircle(color));
        line.setBackgroundColor(isLast ? ContextCompat.getColor(this, android.R.color.transparent) : color);
        line.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
    }

    private void openNoteDialog() {
        EditText editText = new EditText(this);
        editText.setHint(R.string.rescuer_task_detail_note_hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMinLines(3);
        editText.setPadding(36, 26, 36, 26);

        new AlertDialog.Builder(this)
                .setTitle(R.string.rescuer_task_detail_note_action)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String note = valueOf(editText);
                    if (isBlank(note)) {
                        showShortToast(getString(R.string.citizen_detail_note_error));
                        return;
                    }
                    setLoading(true);
                    repository.addNote(taskId, note, new RepositoryCallback<RescuerTaskDetailState>() {
                        @Override
                        public void onSuccess(RescuerTaskDetailState data) {
                            runOnUiThread(() -> {
                                setLoading(false);
                                currentState = data;
                                renderState(data);
                                showShortToast(getString(R.string.citizen_detail_note_success));
                            });
                        }

                        @Override
                        public void onError(String message) {
                            runOnUiThread(() -> {
                                setLoading(false);
                                showError(message);
                            });
                        }
                    });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void openStatusDialog() {
        if (currentState == null) {
            return;
        }

        List<String> options = new ArrayList<>();
        String status = normalize(currentState.getStatus());
        if ("PENDING".equals(status) || "VERIFIED".equals(status) || "ASSIGNED".equals(status)) {
            options.add("IN_PROGRESS");
            options.add("COMPLETED");
            options.add("CANCELLED");
        } else if ("IN_PROGRESS".equals(status)) {
            options.add("COMPLETED");
            options.add("CANCELLED");
        }

        if (options.isEmpty()) {
            showShortToast(getString(R.string.rescuer_task_detail_no_action));
            return;
        }

        String[] labels = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            labels[i] = mapStatusLabel(options.get(i));
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.rescuer_task_detail_status_action)
                .setItems(labels, (dialog, which) -> openStatusNoteDialog(options.get(which)))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void openStatusNoteDialog(String targetStatus) {
        EditText editText = new EditText(this);
        editText.setHint(R.string.rescuer_task_detail_status_note_hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMinLines(3);
        editText.setPadding(36, 26, 36, 26);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.rescuer_task_detail_status_confirm, mapStatusLabel(targetStatus)))
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> performStatusUpdate(targetStatus, valueOf(editText)))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void performStatusUpdate(String targetStatus, String note) {
        setLoading(true);
        repository.updateStatus(taskId, targetStatus, note, new RepositoryCallback<RescuerTaskDetailState>() {
            @Override
            public void onSuccess(RescuerTaskDetailState data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    currentState = data;
                    renderState(data);
                    showShortToast(getString(R.string.rescuer_task_detail_status_success));
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    showError(message == null ? getString(R.string.rescuer_task_detail_status_error) : message);
                });
            }
        });
    }

    private void callCitizen() {
        if (currentState == null || isBlank(currentState.getCitizenPhone())) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + currentState.getCitizenPhone().trim()));
        startActivity(intent);
    }

    private void showError(String message) {
        textError.setVisibility(View.VISIBLE);
        textError.setText(message);
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            textError.setVisibility(View.GONE);
        }
    }

    private String buildAttachmentUrl(String value) {
        if (isBlank(value)) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        String base = BuildConfig.BASE_URL;
        if (base.endsWith("/") && trimmed.startsWith("/")) {
            return base.substring(0, base.length() - 1) + trimmed;
        }
        if (!base.endsWith("/") && !trimmed.startsWith("/")) {
            return base + "/" + trimmed;
        }
        return base + trimmed;
    }

    private String mapStatusLabel(String status) {
        String safe = normalize(status);
        if ("IN_PROGRESS".equals(safe)) {
            return getString(R.string.rescuer_task_list_status_progress);
        }
        if ("COMPLETED".equals(safe) || "DONE".equals(safe)) {
            return getString(R.string.rescuer_task_list_status_done);
        }
        if ("CANCELLED".equals(safe)) {
            return getString(R.string.citizen_request_status_cancelled);
        }
        return getString(R.string.rescuer_task_list_status_pending);
    }

    private void applyStatusStyle(TextView view, String status) {
        String safe = normalize(status);
        if ("IN_PROGRESS".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(getColor(R.color.accent_dark));
            return;
        }
        if ("COMPLETED".equals(safe) || "DONE".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(getColor(R.color.success));
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

    private String mapPriorityLabel(String priority) {
        String safe = normalize(priority);
        if ("HIGH".equals(safe)) {
            return getString(R.string.rescuer_task_list_priority_high);
        }
        if ("LOW".equals(safe)) {
            return getString(R.string.rescuer_task_list_priority_low);
        }
        return getString(R.string.rescuer_task_list_priority_medium);
    }

    private void applyPriorityStyle(TextView view, String priority) {
        String safe = normalize(priority);
        if ("HIGH".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(getColor(R.color.danger));
            return;
        }
        if ("LOW".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(getColor(R.color.accent_dark));
            return;
        }
        view.setBackgroundResource(R.drawable.bg_chip_warning);
        view.setTextColor(getColor(R.color.warning));
    }

    private String mapTimelineTitle(RescuerTaskDetailState.TimelineItem item) {
        String eventType = normalize(item.getEventType());
        if ("STATUS_CHANGED".equals(eventType) || "STATUS_CHANGE".equals(eventType)) {
            return getString(R.string.rescuer_task_detail_timeline_status_change);
        }
        if ("NOTE_ADDED".equals(eventType)) {
            return getString(R.string.rescuer_task_detail_timeline_note_added);
        }
        return getString(R.string.rescuer_task_detail_timeline_default);
    }

    private String buildTimelineNote(RescuerTaskDetailState.TimelineItem item) {
        if (!isBlank(item.getNote())) {
            return item.getNote().trim();
        }
        if (!isBlank(item.getFromStatus()) || !isBlank(item.getToStatus())) {
            return (isBlank(item.getFromStatus()) ? "?" : item.getFromStatus())
                    + " -> "
                    + (isBlank(item.getToStatus()) ? "?" : item.getToStatus());
        }
        return "";
    }

    private int resolveTimelineColor(String eventType) {
        String safe = normalize(eventType);
        if ("NOTE_ADDED".equals(safe)) {
            return getColor(R.color.success);
        }
        if ("STATUS_CHANGED".equals(safe) || "STATUS_CHANGE".equals(safe)) {
            return getColor(R.color.accent_dark);
        }
        return getColor(R.color.warning);
    }

    private GradientDrawable makeCircle(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    private String buildCoordinateLabel(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return getString(R.string.citizen_detail_location_unknown);
        }
        return String.format(Locale.getDefault(), "%.4f, %.4f", latitude, longitude);
    }

    private String formatCode(String code) {
        if (isBlank(code)) {
            return "#RESC";
        }
        String safe = code.trim();
        return safe.startsWith("#") ? safe : ("#" + safe);
    }

    private String formatTime(String raw) {
        if (isBlank(raw)) {
            return getString(R.string.citizen_request_time_unknown);
        }
        return raw.trim().replace('T', ' ');
    }

    private String valueOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String initials(String fullName) {
        if (isBlank(fullName)) {
            return "RG";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

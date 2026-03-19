package com.floodrescue.mobile.ui.role.coordinator.rescuedetail;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.floodrescue.mobile.data.model.ui.CoordinatorRescueDetailState;
import com.floodrescue.mobile.data.repository.CoordinatorOperationsRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CoordinatorRescueDetailActivity extends BaseActivity {

    public static final String EXTRA_REQUEST_ID = "request_id";

    private long requestId = -1L;
    private CoordinatorOperationsRepository repository;
    private CoordinatorRescueDetailState currentState;

    private View progressView;
    private TextView textError;
    private TextView textRequestCode;
    private TextView textRequestStatus;
    private TextView textPriorityChip;
    private TextView textVerifiedChip;
    private TextView textDuplicateChip;
    private TextView textPriorityValue;
    private TextView textPeopleCount;
    private TextView textCitizenName;
    private TextView textCitizenPhone;
    private TextView textMapAddress;
    private TextView textMapCoordinates;
    private TextView textLocationDescription;
    private TextView textDescription;
    private TextView textCancelNote;
    private LinearLayout containerAttachments;
    private TextView textAttachmentsEmpty;
    private LinearLayout containerTimeline;
    private TextView textTimelineEmpty;
    private TextView buttonVerify;
    private TextView buttonChangeStatus;
    private TextView buttonPriorityAction;
    private TextView buttonDuplicateAction;
    private TextView buttonNoteAction;
    private TextView buttonBlockCitizen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_coordinator_rescue_detail);

        repository = new CoordinatorOperationsRepository(this);
        requestId = getIntent().getLongExtra(EXTRA_REQUEST_ID, -1L);
        if (requestId <= 0L) {
            showShortToast(getString(R.string.coordinator_rescue_detail_missing_id));
            finish();
            return;
        }

        bindViews();
        bindActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDetail();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackCoordinatorDetail);
        progressView = findViewById(R.id.progressCoordinatorDetail);
        textError = findViewById(R.id.textCoordinatorDetailError);
        textRequestCode = findViewById(R.id.textCoordinatorRequestCode);
        textRequestStatus = findViewById(R.id.textCoordinatorRequestStatus);
        textPriorityChip = findViewById(R.id.textCoordinatorPriorityChip);
        textVerifiedChip = findViewById(R.id.textCoordinatorVerifiedChip);
        textDuplicateChip = findViewById(R.id.textCoordinatorDuplicateChip);
        textPriorityValue = findViewById(R.id.textCoordinatorPriorityValue);
        textPeopleCount = findViewById(R.id.textCoordinatorPeopleCount);
        textCitizenName = findViewById(R.id.textCoordinatorCitizenName);
        textCitizenPhone = findViewById(R.id.textCoordinatorCitizenPhone);
        textMapAddress = findViewById(R.id.textCoordinatorMapAddress);
        textMapCoordinates = findViewById(R.id.textCoordinatorMapCoordinates);
        textLocationDescription = findViewById(R.id.textCoordinatorLocationDescription);
        textDescription = findViewById(R.id.textCoordinatorDescription);
        textCancelNote = findViewById(R.id.textCoordinatorCancelNote);
        containerAttachments = findViewById(R.id.containerCoordinatorAttachments);
        textAttachmentsEmpty = findViewById(R.id.textCoordinatorAttachmentsEmpty);
        containerTimeline = findViewById(R.id.containerCoordinatorTimeline);
        textTimelineEmpty = findViewById(R.id.textCoordinatorTimelineEmpty);
        buttonVerify = findViewById(R.id.buttonCoordinatorVerify);
        buttonChangeStatus = findViewById(R.id.buttonCoordinatorChangeStatus);
        buttonPriorityAction = findViewById(R.id.buttonCoordinatorPriorityAction);
        buttonDuplicateAction = findViewById(R.id.buttonCoordinatorDuplicateAction);
        buttonNoteAction = findViewById(R.id.buttonCoordinatorNoteAction);
        buttonBlockCitizen = findViewById(R.id.buttonCoordinatorBlockCitizen);
    }

    private void bindActions() {
        findViewById(R.id.buttonShareCoordinatorDetail).setOnClickListener(v -> shareRequest());
        buttonVerify.setOnClickListener(v -> openVerifyDialog());
        buttonChangeStatus.setOnClickListener(v -> openStatusDialog());
        buttonPriorityAction.setOnClickListener(v -> openPriorityDialog());
        buttonDuplicateAction.setOnClickListener(v -> openDuplicateDialog());
        buttonNoteAction.setOnClickListener(v -> openNoteDialog());
        buttonBlockCitizen.setOnClickListener(v -> openBlockCitizenDialog());
    }

    private void loadDetail() {
        setLoading(true);
        showError(null);
        repository.getRescueRequestDetail(requestId, new RepositoryCallback<CoordinatorRescueDetailState>() {
            @Override
            public void onSuccess(CoordinatorRescueDetailState data) {
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
                    showError(message == null ? getString(R.string.coordinator_rescue_detail_load_error) : message);
                });
            }
        });
    }

    private void renderState(CoordinatorRescueDetailState state) {
        if (state == null) {
            return;
        }

        textRequestCode.setText(formatCode(state.getCode()));
        textRequestStatus.setText(mapStatusLabel(state.getStatus()));
        applyStatusStyle(textRequestStatus, state.getStatus());

        String priorityLabel = mapPriorityLabel(state.getPriority());
        textPriorityChip.setText(priorityLabel);
        applyPriorityStyle(textPriorityChip, state.getPriority());
        textPriorityValue.setText(priorityLabel);

        textVerifiedChip.setVisibility(state.isLocationVerified() ? View.VISIBLE : View.GONE);

        if (isBlank(state.getMasterRequestCode())) {
            textDuplicateChip.setVisibility(View.GONE);
        } else {
            textDuplicateChip.setVisibility(View.VISIBLE);
            textDuplicateChip.setText(getString(
                    R.string.coordinator_rescue_detail_duplicate_source,
                    formatCode(state.getMasterRequestCode())
            ));
        }

        textPeopleCount.setText(getResources().getQuantityString(
                R.plurals.citizen_request_people_count,
                Math.max(state.getPeopleCount(), 0),
                Math.max(state.getPeopleCount(), 0)
        ));
        textCitizenName.setText(isBlank(state.getCitizenName())
                ? getString(R.string.coordinator_rescue_detail_citizen_unknown)
                : state.getCitizenName());
        textCitizenPhone.setText(isBlank(state.getCitizenPhone())
                ? getString(R.string.coordinator_rescue_detail_phone_unknown)
                : state.getCitizenPhone());
        textMapAddress.setText(isBlank(state.getAddress())
                ? getString(R.string.coordinator_rescue_detail_address_unknown)
                : state.getAddress());
        textMapCoordinates.setText(buildCoordinateLabel(state.getLatitude(), state.getLongitude()));
        textLocationDescription.setText(isBlank(state.getLocationDescription())
                ? getString(R.string.citizen_dashboard_address_placeholder)
                : state.getLocationDescription());
        textDescription.setText(isBlank(state.getDescription())
                ? getString(R.string.citizen_dashboard_description_placeholder)
                : state.getDescription());

        if (isBlank(state.getCancelNote())) {
            textCancelNote.setVisibility(View.GONE);
        } else {
            textCancelNote.setVisibility(View.VISIBLE);
            textCancelNote.setText(getString(R.string.coordinator_rescue_detail_cancel_note, state.getCancelNote().trim()));
        }

        renderAttachments(state.getAttachments());
        renderTimeline(state.getTimeline());
        renderActionState(state);
    }

    private void renderActionState(CoordinatorRescueDetailState state) {
        String status = normalize(state.getStatus());
        boolean terminal = "DUPLICATE".equals(status) || "CANCELLED".equals(status);
        boolean canVerify = "PENDING".equals(status) && !state.isLocationVerified();

        buttonVerify.setEnabled(canVerify);
        buttonVerify.setAlpha(canVerify ? 1f : 0.45f);
        buttonChangeStatus.setEnabled(!terminal);
        buttonChangeStatus.setAlpha(!terminal ? 1f : 0.45f);
        buttonDuplicateAction.setEnabled(!terminal);
        buttonDuplicateAction.setAlpha(!terminal ? 1f : 0.45f);
    }

    private void renderAttachments(List<CoordinatorRescueDetailState.AttachmentItem> attachments) {
        containerAttachments.removeAllViews();
        if (attachments == null || attachments.isEmpty()) {
            textAttachmentsEmpty.setVisibility(View.VISIBLE);
            return;
        }
        textAttachmentsEmpty.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (CoordinatorRescueDetailState.AttachmentItem item : attachments) {
            View itemView = inflater.inflate(R.layout.item_citizen_rescue_attachment, containerAttachments, false);
            RemoteImageLoader.load((ImageView) itemView.findViewById(R.id.imageAttachment), buildAttachmentUrl(item.getFileUrl()));
            containerAttachments.addView(itemView);
        }
    }

    private void renderTimeline(List<CoordinatorRescueDetailState.TimelineItem> timeline) {
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

    private void bindTimelineItem(View itemView, CoordinatorRescueDetailState.TimelineItem item, boolean isLast) {
        View dot = itemView.findViewById(R.id.viewTimelineDot);
        View line = itemView.findViewById(R.id.viewTimelineLine);
        ((TextView) itemView.findViewById(R.id.textTimelineTitle)).setText(mapTimelineTitle(item));
        ((TextView) itemView.findViewById(R.id.textTimelineTime)).setText(formatDateTime(item.getCreatedAt()));
        TextView noteView = itemView.findViewById(R.id.textTimelineNote);
        String note = buildTimelineNote(item);
        noteView.setText(note);
        noteView.setVisibility(isBlank(note) ? View.GONE : View.VISIBLE);

        int color = resolveTimelineColor(item.getEventType());
        dot.setBackground(makeCircle(color));
        line.setBackgroundColor(isLast ? ContextCompat.getColor(this, android.R.color.transparent) : color);
        line.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
    }

    private void openVerifyDialog() {
        if (currentState == null || !"PENDING".equals(normalize(currentState.getStatus())) || currentState.isLocationVerified()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.coordinator_rescue_detail_verify_title)
                .setMessage(R.string.coordinator_rescue_detail_verify_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> performVerify())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void performVerify() {
        setLoading(true);
        repository.verifyRequest(requestId, true, null, new RepositoryCallback<CoordinatorRescueDetailState>() {
            @Override
            public void onSuccess(CoordinatorRescueDetailState data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    currentState = data;
                    renderState(data);
                    showShortToast(getString(R.string.coordinator_rescue_detail_verified));
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
    }

    private void openStatusDialog() {
        if (currentState == null || "DUPLICATE".equals(normalize(currentState.getStatus()))) {
            return;
        }

        List<String> codes = new ArrayList<>();
        String current = normalize(currentState.getStatus());
        for (String status : new String[]{"VERIFIED", "ASSIGNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"}) {
            if (!status.equals(current)) {
                codes.add(status);
            }
        }
        if (codes.isEmpty()) {
            return;
        }

        CharSequence[] labels = new CharSequence[codes.size()];
        for (int i = 0; i < codes.size(); i++) {
            labels[i] = mapStatusLabel(codes.get(i));
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.coordinator_rescue_detail_status_title)
                .setItems(labels, (dialog, which) -> openStatusNoteDialog(codes.get(which)))
                .show();
    }

    private void openStatusNoteDialog(String statusCode) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_coordinator_note, null, false);
        EditText editNote = view.findViewById(R.id.editCoordinatorNote);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.coordinator_rescue_detail_status_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            setLoading(true);
            repository.changeStatus(requestId, statusCode, textOf(editNote), new RepositoryCallback<CoordinatorRescueDetailState>() {
                @Override
                public void onSuccess(CoordinatorRescueDetailState data) {
                    runOnUiThread(() -> {
                        setLoading(false);
                        dialog.dismiss();
                        currentState = data;
                        renderState(data);
                        showShortToast(getString(R.string.coordinator_rescue_detail_status_updated));
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
        }));
        dialog.show();
    }

    private void openPriorityDialog() {
        String[] codes = {"HIGH", "MEDIUM", "LOW"};
        CharSequence[] labels = {"KHẨN CẤP", "TRUNG BÌNH", "THẤP"};
        new AlertDialog.Builder(this)
                .setTitle(R.string.coordinator_rescue_detail_priority_title)
                .setItems(labels, (dialog, which) -> {
                    setLoading(true);
                    repository.updatePriority(requestId, codes[which], new RepositoryCallback<CoordinatorRescueDetailState>() {
                        @Override
                        public void onSuccess(CoordinatorRescueDetailState data) {
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
                                showError(message);
                            });
                        }
                    });
                })
                .show();
    }

    private void openNoteDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_coordinator_note, null, false);
        EditText editNote = view.findViewById(R.id.editCoordinatorNote);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.coordinator_rescue_detail_note_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String note = textOf(editNote);
            if (isBlank(note)) {
                showShortToast(getString(R.string.coordinator_rescue_detail_note_error));
                return;
            }
            setLoading(true);
            repository.addNote(requestId, note, new RepositoryCallback<CoordinatorRescueDetailState>() {
                @Override
                public void onSuccess(CoordinatorRescueDetailState data) {
                    runOnUiThread(() -> {
                        setLoading(false);
                        dialog.dismiss();
                        currentState = data;
                        renderState(data);
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
        }));
        dialog.show();
    }

    private void openDuplicateDialog() {
        if (currentState == null || "DUPLICATE".equals(normalize(currentState.getStatus()))) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_coordinator_duplicate, null, false);
        EditText editMasterId = view.findViewById(R.id.editCoordinatorMasterRequestId);
        EditText editNote = view.findViewById(R.id.editCoordinatorDuplicateNote);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.coordinator_rescue_detail_duplicate_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            long masterId = parseLong(textOf(editMasterId));
            if (masterId <= 0L || masterId == requestId) {
                showShortToast(getString(R.string.coordinator_rescue_detail_duplicate_error));
                return;
            }
            setLoading(true);
            repository.markDuplicate(requestId, masterId, textOf(editNote), new RepositoryCallback<CoordinatorRescueDetailState>() {
                @Override
                public void onSuccess(CoordinatorRescueDetailState data) {
                    runOnUiThread(() -> {
                        setLoading(false);
                        dialog.dismiss();
                        currentState = data;
                        renderState(data);
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
        }));
        dialog.show();
    }

    private void openBlockCitizenDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_coordinator_block_citizen, null, false);
        EditText editReason = view.findViewById(R.id.editCoordinatorBlockReason);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.coordinator_rescue_detail_block_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String reason = textOf(editReason);
            if (isBlank(reason)) {
                showShortToast(getString(R.string.coordinator_rescue_detail_block_error));
                return;
            }
            setLoading(true);
            repository.blockCitizenByRequest(requestId, true, reason, new RepositoryCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    runOnUiThread(() -> {
                        setLoading(false);
                        dialog.dismiss();
                        showShortToast(data);
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
        }));
        dialog.show();
    }

    private void shareRequest() {
        if (currentState == null) {
            return;
        }
        String message = getString(
                R.string.citizen_detail_share_template,
                formatCode(currentState.getCode()),
                mapStatusLabel(currentState.getStatus()),
                isBlank(currentState.getAddress()) ? getString(R.string.coordinator_rescue_detail_address_unknown) : currentState.getAddress()
        );
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.coordinator_rescue_detail_title)));
    }

    private String mapStatusLabel(String status) {
        switch (normalize(status)) {
            case "VERIFIED":
            case "ASSIGNED":
                return getString(R.string.coordinator_rescue_detail_status_received);
            case "IN_PROGRESS":
                return getString(R.string.coordinator_rescue_detail_status_doing);
            case "COMPLETED":
                return getString(R.string.coordinator_rescue_detail_status_done_label);
            case "CANCELLED":
                return getString(R.string.coordinator_rescue_detail_status_cancelled_label);
            case "DUPLICATE":
                return getString(R.string.coordinator_rescue_detail_status_duplicate_label);
            case "PENDING":
            default:
                return getString(R.string.coordinator_rescue_detail_status_processing);
        }
    }

    private String mapPriorityLabel(String priority) {
        switch (normalize(priority)) {
            case "HIGH":
                return "KHẨN CẤP";
            case "LOW":
                return "THẤP";
            case "MEDIUM":
            default:
                return "TRUNG BÌNH";
        }
    }

    private void applyStatusStyle(TextView view, String status) {
        String normalized = normalize(status);
        if ("COMPLETED".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else if ("VERIFIED".equals(normalized) || "ASSIGNED".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(ContextCompat.getColor(this, R.color.accent_dark));
        } else if ("IN_PROGRESS".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(ContextCompat.getColor(this, R.color.warning));
        } else if ("CANCELLED".equals(normalized) || "DUPLICATE".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(ContextCompat.getColor(this, R.color.danger));
        } else {
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(ContextCompat.getColor(this, R.color.warning));
        }
    }

    private void applyPriorityStyle(TextView view, String priority) {
        String normalized = normalize(priority);
        if ("HIGH".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_danger);
            view.setTextColor(ContextCompat.getColor(this, R.color.danger));
        } else if ("LOW".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else {
            view.setBackgroundResource(R.drawable.bg_chip_warning);
            view.setTextColor(ContextCompat.getColor(this, R.color.warning));
        }
    }

    private String mapTimelineTitle(CoordinatorRescueDetailState.TimelineItem item) {
        String eventType = normalize(item.getEventType());
        if ("VERIFY".equals(eventType)) {
            return "Điều phối viên đã xác minh";
        }
        if ("ASSIGN".equals(eventType)) {
            return "Đã phân công đội cứu hộ";
        }
        if ("STATUS_CHANGE".equals(eventType)) {
            return "Cập nhật trạng thái";
        }
        if ("MARK_DUPLICATE".equals(eventType)) {
            return "Đánh dấu bản trùng";
        }
        if ("CANCEL".equals(eventType)) {
            return "Hủy yêu cầu";
        }
        return "Thêm ghi chú";
    }

    private String buildTimelineNote(CoordinatorRescueDetailState.TimelineItem item) {
        if (!isBlank(item.getNote())) {
            return item.getNote().trim();
        }
        return isBlank(item.getActorName())
                ? getString(R.string.coordinator_rescue_detail_timeline_system)
                : item.getActorName().trim();
    }

    private int resolveTimelineColor(String eventType) {
        switch (normalize(eventType)) {
            case "VERIFY":
                return ContextCompat.getColor(this, R.color.success);
            case "MARK_DUPLICATE":
                return ContextCompat.getColor(this, R.color.warning);
            case "CANCEL":
                return ContextCompat.getColor(this, R.color.danger);
            case "STATUS_CHANGE":
                return ContextCompat.getColor(this, R.color.accent_dark);
            default:
                return ContextCompat.getColor(this, R.color.accent);
        }
    }

    private String formatDateTime(String raw) {
        long millis = parseTimestamp(raw);
        if (millis <= 0L) {
            return isBlank(raw) ? getString(R.string.citizen_request_time_unknown) : raw.replace('T', ' ');
        }
        return new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault()).format(new Date(millis));
    }

    private long parseTimestamp(String raw) {
        if (isBlank(raw)) {
            return -1L;
        }
        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss"
        };
        for (String pattern : patterns) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
                Date date = format.parse(raw.trim());
                if (date != null) {
                    return date.getTime();
                }
            } catch (Exception ignored) {
                // Try next pattern.
            }
        }
        return -1L;
    }

    private String buildCoordinateLabel(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return getString(R.string.citizen_detail_location_unknown);
        }
        return String.format(Locale.getDefault(), "Lat %.5f, Lng %.5f", latitude, longitude);
    }

    private String buildAttachmentUrl(String fileUrl) {
        String raw = textOf(fileUrl);
        if (raw.startsWith("http://") || raw.startsWith("https://")) {
            return raw;
        }
        String base = BuildConfig.BASE_URL.endsWith("/")
                ? BuildConfig.BASE_URL.substring(0, BuildConfig.BASE_URL.length() - 1)
                : BuildConfig.BASE_URL;
        if (!raw.startsWith("/")) {
            raw = "/" + raw;
        }
        return base + raw;
    }

    private GradientDrawable makeCircle(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        if (isBlank(message)) {
            textError.setVisibility(View.GONE);
            return;
        }
        textError.setVisibility(View.VISIBLE);
        textError.setText(message);
    }

    private long parseLong(String raw) {
        try {
            return Long.parseLong(raw.trim());
        } catch (Exception ignored) {
            return -1L;
        }
    }

    private String formatCode(String code) {
        if (isBlank(code)) {
            return "#RESC";
        }
        return code.trim().startsWith("#") ? code.trim() : "#" + code.trim();
    }

    private String textOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String textOf(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return TextUtils.isEmpty(value == null ? null : value.trim());
    }
}

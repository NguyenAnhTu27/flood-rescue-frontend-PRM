package com.floodrescue.mobile.ui.role.citizen.rescue.detail;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.floodrescue.mobile.BuildConfig;
import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.core.util.RemoteImageLoader;
import com.floodrescue.mobile.data.model.response.CitizenRescueConfirmResponse;
import com.floodrescue.mobile.data.model.ui.CitizenRescueDetailState;
import com.floodrescue.mobile.data.repository.CitizenRescueRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.citizen.rescue.update.CitizenRescueUpdateActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CitizenRescueDetailActivity extends BaseActivity {

    public static final String EXTRA_REQUEST_ID = "request_id";

    private long requestId = -1L;
    private CitizenRescueRepository rescueRepository;
    private CitizenRescueDetailState currentState;

    private View progressDetail;
    private TextView textDetailError;
    private TextView textRequestCode;
    private TextView textRequestStatus;
    private TextView textPriorityChip;
    private TextView textVerifiedChip;
    private TextView textWaitingChip;
    private TextView textMapAddress;
    private TextView textMapCoordinates;
    private TextView textLocationDescription;
    private TextView textDescription;
    private TextView textCoordinatorCancelNote;
    private TextView textRescueResultNote;
    private TextView textPeopleCount;
    private TextView textDispatchState;
    private LinearLayout containerTimeline;
    private TextView textTimelineEmpty;
    private LinearLayout containerAttachments;
    private TextView textAttachmentsEmpty;
    private TextView buttonConfirmResult;
    private TextView buttonEditRequest;
    private TextView buttonAddNote;
    private TextView buttonCancelRequest;
    private TextView buttonReopenRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_citizen_rescue_detail);

        rescueRepository = new CitizenRescueRepository(this);
        requestId = getIntent().getLongExtra(EXTRA_REQUEST_ID, -1L);
        if (requestId <= 0L) {
            showShortToast(getString(R.string.citizen_detail_missing_id));
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
        bindBackButton(R.id.buttonBack);
        progressDetail = findViewById(R.id.progressDetail);
        textDetailError = findViewById(R.id.textDetailError);
        textRequestCode = findViewById(R.id.textRequestCode);
        textRequestStatus = findViewById(R.id.textRequestStatus);
        textPriorityChip = findViewById(R.id.textPriorityChip);
        textVerifiedChip = findViewById(R.id.textVerifiedChip);
        textWaitingChip = findViewById(R.id.textWaitingChip);
        textMapAddress = findViewById(R.id.textMapAddress);
        textMapCoordinates = findViewById(R.id.textMapCoordinates);
        textLocationDescription = findViewById(R.id.textLocationDescription);
        textDescription = findViewById(R.id.textDescription);
        textCoordinatorCancelNote = findViewById(R.id.textCoordinatorCancelNote);
        textRescueResultNote = findViewById(R.id.textRescueResultNote);
        textPeopleCount = findViewById(R.id.textPeopleCount);
        textDispatchState = findViewById(R.id.textDispatchState);
        containerTimeline = findViewById(R.id.containerTimeline);
        textTimelineEmpty = findViewById(R.id.textTimelineEmpty);
        containerAttachments = findViewById(R.id.containerAttachments);
        textAttachmentsEmpty = findViewById(R.id.textAttachmentsEmpty);
        buttonConfirmResult = findViewById(R.id.buttonConfirmResult);
        buttonEditRequest = findViewById(R.id.buttonEditRequest);
        buttonAddNote = findViewById(R.id.buttonAddNote);
        buttonCancelRequest = findViewById(R.id.buttonCancelRequest);
        buttonReopenRequest = findViewById(R.id.buttonReopenRequest);
    }

    private void bindActions() {
        findViewById(R.id.buttonShare).setOnClickListener(v -> shareRequest());
        buttonConfirmResult.setOnClickListener(v -> openConfirmDialog());
        buttonEditRequest.setOnClickListener(v -> openUpdateScreen());
        buttonAddNote.setOnClickListener(v -> openAddNoteDialog());
        buttonCancelRequest.setOnClickListener(v -> confirmCancelRequest());
        buttonReopenRequest.setOnClickListener(v -> openReopenDialog());
    }

    private void loadDetail() {
        setLoading(true);
        hideError();
        rescueRepository.getRescueRequestDetail(requestId, new RepositoryCallback<CitizenRescueDetailState>() {
            @Override
            public void onSuccess(CitizenRescueDetailState data) {
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
                    showError(isBlank(message) ? getString(R.string.citizen_detail_load_error) : message);
                });
            }
        });
    }

    private void renderState(CitizenRescueDetailState state) {
        textRequestCode.setText(safe(state.getCode()));
        textRequestStatus.setText(mapStatusLabel(state.getStatus()));
        applyStatusStyle(textRequestStatus, state.getStatus());
        textPriorityChip.setText(mapPriorityLabel(state.getPriority()));
        applyPriorityStyle(textPriorityChip, state.getPriority());

        textVerifiedChip.setVisibility(state.isLocationVerified() ? View.VISIBLE : View.GONE);
        textWaitingChip.setVisibility(state.isWaitingForTeam() ? View.VISIBLE : View.GONE);
        textMapAddress.setText(fallback(state.getAddressText(), getString(R.string.citizen_request_time_unknown)));
        textMapCoordinates.setText(buildCoordinateLabel(state.getLatitude(), state.getLongitude()));
        textLocationDescription.setText(fallback(state.getLocationDescription(), getString(R.string.citizen_dashboard_address_placeholder)));
        textDescription.setText(fallback(state.getDescription(), getString(R.string.citizen_dashboard_description_placeholder)));
        textPeopleCount.setText(getResources().getQuantityString(
                R.plurals.citizen_request_people_count,
                Math.max(state.getAffectedPeopleCount(), 0),
                Math.max(state.getAffectedPeopleCount(), 0)
        ));
        textDispatchState.setText(buildDispatchState(state));
        renderOptionalNotes(state);
        renderTimeline(state.getTimeline());
        renderAttachments(state.getAttachments());
        renderActionState(state);
    }

    private void renderOptionalNotes(CitizenRescueDetailState state) {
        if (isBlank(state.getCoordinatorCancelNote())) {
            textCoordinatorCancelNote.setVisibility(View.GONE);
        } else {
            textCoordinatorCancelNote.setVisibility(View.VISIBLE);
            textCoordinatorCancelNote.setText("Ghi chú điều phối: " + state.getCoordinatorCancelNote().trim());
        }

        if (!isBlank(state.getRescueResultConfirmationStatus())) {
            textRescueResultNote.setVisibility(View.VISIBLE);
            String note = isBlank(state.getRescueResultConfirmationNote())
                    ? getString(R.string.citizen_detail_confirmation_done)
                    : state.getRescueResultConfirmationNote().trim();
            textRescueResultNote.setText("Kết quả xác nhận: " + note);
        } else if ("COMPLETED".equals(normalize(state.getStatus())) && state.isWaitingCitizenRescueConfirmation()) {
            textRescueResultNote.setVisibility(View.VISIBLE);
            textRescueResultNote.setText(getString(R.string.citizen_detail_confirmation_pending));
        } else {
            textRescueResultNote.setVisibility(View.GONE);
        }
    }

    private void renderTimeline(List<CitizenRescueDetailState.TimelineItem> timeline) {
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

    private void bindTimelineItem(View itemView, CitizenRescueDetailState.TimelineItem item, boolean isLast) {
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

    private void renderAttachments(List<CitizenRescueDetailState.AttachmentItem> attachments) {
        containerAttachments.removeAllViews();
        if (attachments == null || attachments.isEmpty()) {
            textAttachmentsEmpty.setVisibility(View.VISIBLE);
            return;
        }
        textAttachmentsEmpty.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (CitizenRescueDetailState.AttachmentItem item : attachments) {
            View itemView = inflater.inflate(R.layout.item_citizen_rescue_attachment, containerAttachments, false);
            RemoteImageLoader.load((ImageView) itemView.findViewById(R.id.imageAttachment), buildAttachmentUrl(item.getFileUrl()));
            containerAttachments.addView(itemView);
        }
    }

    private void renderActionState(CitizenRescueDetailState state) {
        String status = normalize(state.getStatus());
        boolean alreadyConfirmed = !isBlank(state.getRescueResultConfirmationStatus());
        boolean canEdit = !"COMPLETED".equals(status) && !"CANCELLED".equals(status) && !"DUPLICATE".equals(status);
        boolean canCancel = !"COMPLETED".equals(status) && !"CANCELLED".equals(status);
        boolean canReopen = "CANCELLED".equals(status);
        boolean canConfirm = "COMPLETED".equals(status) && !alreadyConfirmed;

        buttonConfirmResult.setVisibility(canConfirm ? View.VISIBLE : View.GONE);
        buttonEditRequest.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        buttonCancelRequest.setVisibility(canCancel ? View.VISIBLE : View.GONE);
        buttonReopenRequest.setVisibility(canReopen ? View.VISIBLE : View.GONE);
        adjustButtonRow(buttonEditRequest, buttonAddNote);
        adjustButtonRow(buttonCancelRequest, buttonReopenRequest);
    }

    private void adjustButtonRow(View left, View right) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) right.getLayoutParams();
        params.setMarginStart(left.getVisibility() == View.VISIBLE ? dp(12) : 0);
        right.setLayoutParams(params);
    }

    private void openAddNoteDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_citizen_rescue_note, null, false);
        EditText editText = view.findViewById(R.id.editRescueNote);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.citizen_detail_note_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String note = safe(editText.getText() == null ? null : editText.getText().toString());
            if (isBlank(note)) {
                showShortToast(getString(R.string.citizen_detail_note_error));
                return;
            }
            setLoading(true);
            rescueRepository.addRescueNote(requestId, note, new RepositoryCallback<CitizenRescueDetailState>() {
                @Override
                public void onSuccess(CitizenRescueDetailState data) {
                    runOnUiThread(() -> {
                        setLoading(false);
                        dialog.dismiss();
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
        }));
        dialog.show();
    }

    private void openUpdateScreen() {
        Intent intent = new Intent(this, CitizenRescueUpdateActivity.class);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        startActivity(intent);
    }

    private void openConfirmDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_citizen_rescue_confirm, null, false);
        RadioGroup group = view.findViewById(R.id.groupRescueConfirm);
        EditText editReason = view.findViewById(R.id.editConfirmReason);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.citizen_detail_confirm_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int checkedId = group.getCheckedRadioButtonId();
            if (checkedId == View.NO_ID) {
                showShortToast(getString(R.string.citizen_detail_confirm_choice_error));
                return;
            }
            boolean rescued = checkedId == R.id.radioRescuedYes;
            String reason = safe(editReason.getText() == null ? null : editReason.getText().toString());
            if (!rescued && isBlank(reason)) {
                showShortToast(getString(R.string.citizen_detail_confirm_reason_error));
                return;
            }

            setLoading(true);
            rescueRepository.confirmRescueResult(requestId, rescued, reason, new RepositoryCallback<CitizenRescueConfirmResponse>() {
                @Override
                public void onSuccess(CitizenRescueConfirmResponse data) {
                    runOnUiThread(() -> {
                        setLoading(false);
                        dialog.dismiss();
                        showShortToast(data.getMessage());
                        if (data.getFollowUpRequestId() != null && data.getFollowUpRequestId() > 0) {
                            Intent intent = new Intent(CitizenRescueDetailActivity.this, CitizenRescueDetailActivity.class);
                            intent.putExtra(EXTRA_REQUEST_ID, data.getFollowUpRequestId());
                            startActivity(intent);
                            finish();
                            return;
                        }
                        loadDetail();
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

    private void openReopenDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_citizen_rescue_reopen, null, false);
        EditText editReason = view.findViewById(R.id.editReopenReason);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.citizen_detail_reopen_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            setLoading(true);
            rescueRepository.reopenRescueRequest(
                    requestId,
                    safe(editReason.getText() == null ? null : editReason.getText().toString()),
                    new RepositoryCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            runOnUiThread(() -> {
                                setLoading(false);
                                dialog.dismiss();
                                showShortToast(isBlank(data) ? getString(R.string.citizen_detail_reopen_success) : data);
                                loadDetail();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            runOnUiThread(() -> {
                                setLoading(false);
                                showError(message);
                            });
                        }
                    }
            );
        }));
        dialog.show();
    }

    private void confirmCancelRequest() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.citizen_detail_cancel_confirm_title)
                .setMessage(R.string.citizen_detail_cancel_confirm_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    setLoading(true);
                    rescueRepository.cancelRescueRequest(requestId, new RepositoryCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            runOnUiThread(() -> {
                                setLoading(false);
                                showShortToast(data);
                                loadDetail();
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

    private void shareRequest() {
        if (currentState == null) {
            return;
        }
        String message = getString(
                R.string.citizen_detail_share_template,
                safe(currentState.getCode()),
                mapStatusLabel(currentState.getStatus()),
                fallback(currentState.getAddressText(), "")
        );
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.citizen_detail_title)));
    }

    private String mapStatusLabel(String status) {
        switch (normalize(status)) {
            case "VERIFIED":
            case "ASSIGNED":
                return getString(R.string.citizen_request_status_received);
            case "COMPLETED":
                return getString(R.string.citizen_request_status_completed);
            case "CANCELLED":
                return getString(R.string.citizen_request_status_cancelled);
            case "DUPLICATE":
                return getString(R.string.citizen_request_status_duplicate);
            case "IN_PROGRESS":
                return "ĐANG CỨU HỘ";
            case "PENDING":
            default:
                return getString(R.string.citizen_request_status_processing);
        }
    }

    private String mapPriorityLabel(String priority) {
        switch (normalize(priority)) {
            case "HIGH":
                return getString(R.string.citizen_request_priority_high);
            case "LOW":
                return getString(R.string.citizen_request_priority_low);
            case "MEDIUM":
            default:
                return getString(R.string.citizen_request_priority_medium);
        }
    }

    private String buildDispatchState(CitizenRescueDetailState state) {
        String status = normalize(state.getStatus());
        if ("COMPLETED".equals(status)) {
            return getString(R.string.citizen_detail_dispatch_completed);
        }
        if ("CANCELLED".equals(status)) {
            return getString(R.string.citizen_detail_dispatch_cancelled);
        }
        if ("IN_PROGRESS".equals(status)) {
            return getString(R.string.citizen_detail_dispatch_in_progress);
        }
        if (state.isWaitingForTeam()) {
            return getString(R.string.citizen_detail_dispatch_waiting);
        }
        if ("VERIFIED".equals(status) || "ASSIGNED".equals(status) || state.isLocationVerified()) {
            return getString(R.string.citizen_detail_dispatch_verified);
        }
        return getString(R.string.citizen_request_status_processing);
    }

    private int resolveTimelineColor(String eventType) {
        switch (normalize(eventType)) {
            case "COMPLETE":
                return ContextCompat.getColor(this, R.color.success);
            case "CANCEL":
                return ContextCompat.getColor(this, R.color.danger);
            case "VERIFY":
                return ContextCompat.getColor(this, R.color.accent_dark);
            case "MARK_DUPLICATE":
                return ContextCompat.getColor(this, R.color.warning);
            case "ASSIGN":
            case "NOTE":
            case "STATUS_CHANGE":
            default:
                return ContextCompat.getColor(this, R.color.accent);
        }
    }

    private String mapTimelineTitle(CitizenRescueDetailState.TimelineItem item) {
        String eventType = normalize(item.getEventType());
        if ("STATUS_CHANGE".equals(eventType)) {
            String toStatus = normalize(item.getToStatus());
            if ("PENDING".equals(toStatus)) {
                return "Gửi yêu cầu cứu hộ";
            }
            if ("IN_PROGRESS".equals(toStatus)) {
                return "Đội cứu hộ đang xử lý";
            }
            if ("COMPLETED".equals(toStatus)) {
                return "Hoàn thành cứu hộ";
            }
            if (!isBlank(toStatus)) {
                return "Chuyển sang " + mapStatusLabel(toStatus);
            }
        }
        if ("VERIFY".equals(eventType)) {
            return "Tiếp nhận / xác minh";
        }
        if ("ASSIGN".equals(eventType)) {
            return "Phân công đội cứu hộ";
        }
        if ("COMPLETE".equals(eventType)) {
            return "Hoàn thành";
        }
        if ("CANCEL".equals(eventType)) {
            return "Yêu cầu bị hủy";
        }
        if ("MARK_DUPLICATE".equals(eventType)) {
            return "Đánh dấu trùng lặp";
        }
        return "Thêm ghi chú";
    }

    private String buildTimelineNote(CitizenRescueDetailState.TimelineItem item) {
        String note = safe(item.getNote());
        if (!isBlank(note)) {
            return note;
        }
        return fallback(item.getActorName(), "");
    }

    private String formatDateTime(String raw) {
        long millis = parseTimestamp(raw);
        if (millis <= 0L) {
            return cleanDate(raw);
        }
        return new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault()).format(new Date(millis));
    }

    private String buildCoordinateLabel(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return getString(R.string.citizen_detail_location_unknown);
        }
        return String.format(Locale.getDefault(), "Lat %.5f, Lng %.5f", latitude, longitude);
    }

    private String buildAttachmentUrl(String fileUrl) {
        String raw = safe(fileUrl);
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

    private String cleanDate(String raw) {
        return isBlank(raw) ? getString(R.string.citizen_request_time_unknown) : raw.replace('T', ' ').replace(".000", "");
    }

    private GradientDrawable makeCircle(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    private void applyStatusStyle(TextView view, String status) {
        String normalized = normalize(status);
        if ("COMPLETED".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else if ("VERIFIED".equals(normalized) || "ASSIGNED".equals(normalized)) {
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(ContextCompat.getColor(this, R.color.accent_dark));
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

    private void setLoading(boolean loading) {
        progressDetail.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        textDetailError.setVisibility(View.VISIBLE);
        textDetailError.setText(isBlank(message) ? getString(R.string.citizen_detail_load_error) : message);
    }

    private void hideError() {
        textDetailError.setVisibility(View.GONE);
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }

    private String fallback(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return TextUtils.isEmpty(value == null ? null : value.trim());
    }
}

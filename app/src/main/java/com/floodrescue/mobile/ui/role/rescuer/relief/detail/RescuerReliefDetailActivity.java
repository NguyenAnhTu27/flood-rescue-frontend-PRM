package com.floodrescue.mobile.ui.role.rescuer.relief.detail;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.RescuerReliefDetailState;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.data.repository.RescuerReliefDetailRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RescuerReliefDetailActivity extends BaseActivity {

    public static final String EXTRA_RELIEF_ID = "relief_id";

    private RescuerReliefDetailRepository repository;
    private RescuerReliefLineAdapter lineAdapter;
    private RescuerReliefDetailState currentState;
    private long reliefId = -1L;
    private String draftNote = "";

    private View progressView;
    private TextView textError;
    private TextView textCode;
    private TextView textRequestStatus;
    private TextView textDeliveryStage;
    private TextView textArea;
    private TextView textAddress;
    private TextView textLocationDescription;
    private TextView textIssueCode;
    private TextView textManagerNote;
    private TextView textDeliveryNote;
    private TextView textUpdated;
    private TextView textItemsEmpty;
    private View[] stepDots;
    private View[] stepLines;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_rescuer_relief_detail);

        repository = new RescuerReliefDetailRepository(this);
        reliefId = getIntent().getLongExtra(EXTRA_RELIEF_ID, -1L);

        bindViews();
        setupList();
        bindActions();
        resolveReliefId();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackRescuerReliefDetail);
        progressView = findViewById(R.id.progressRescuerReliefDetail);
        textError = findViewById(R.id.textRescuerReliefError);
        textCode = findViewById(R.id.textRescuerReliefCode);
        textRequestStatus = findViewById(R.id.textRescuerReliefStatus);
        textDeliveryStage = findViewById(R.id.textRescuerReliefStage);
        textArea = findViewById(R.id.textRescuerReliefArea);
        textAddress = findViewById(R.id.textRescuerReliefAddress);
        textLocationDescription = findViewById(R.id.textRescuerReliefLocationDescription);
        textIssueCode = findViewById(R.id.textRescuerReliefIssueCode);
        textManagerNote = findViewById(R.id.textRescuerReliefManagerNote);
        textDeliveryNote = findViewById(R.id.textRescuerReliefDeliveryNote);
        textUpdated = findViewById(R.id.textRescuerReliefUpdated);
        textItemsEmpty = findViewById(R.id.textRescuerReliefItemsEmpty);
        stepDots = new View[] {
                findViewById(R.id.viewRescuerReliefStep1),
                findViewById(R.id.viewRescuerReliefStep2),
                findViewById(R.id.viewRescuerReliefStep3),
                findViewById(R.id.viewRescuerReliefStep4),
                findViewById(R.id.viewRescuerReliefStep5),
                findViewById(R.id.viewRescuerReliefStep6)
        };
        stepLines = new View[] {
                findViewById(R.id.viewRescuerReliefLine1),
                findViewById(R.id.viewRescuerReliefLine2),
                findViewById(R.id.viewRescuerReliefLine3),
                findViewById(R.id.viewRescuerReliefLine4),
                findViewById(R.id.viewRescuerReliefLine5)
        };
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerRescuerReliefItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        lineAdapter = new RescuerReliefLineAdapter();
        recyclerView.setAdapter(lineAdapter);
    }

    private void bindActions() {
        findViewById(R.id.buttonRefreshRescuerReliefDetail).setOnClickListener(v -> loadDetail());
        findViewById(R.id.buttonRescuerReliefNote).setOnClickListener(v -> openDraftNoteDialog());
        findViewById(R.id.buttonRescuerReliefStatus).setOnClickListener(v -> openStatusDialog());
    }

    private void resolveReliefId() {
        if (reliefId > 0L) {
            loadDetail();
            return;
        }

        setLoading(true);
        repository.loadLatestReliefId(new RepositoryCallback<Long>() {
            @Override
            public void onSuccess(Long data) {
                runOnUiThread(() -> {
                    reliefId = data == null ? -1L : data;
                    loadDetail();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.rescuer_relief_detail_missing_id) : message);
                });
            }
        });
    }

    private void loadDetail() {
        if (reliefId <= 0L) {
            textError.setVisibility(View.VISIBLE);
            textError.setText(R.string.rescuer_relief_detail_missing_id);
            return;
        }

        setLoading(true);
        repository.loadDetail(reliefId, new RepositoryCallback<RescuerReliefDetailState>() {
            @Override
            public void onSuccess(RescuerReliefDetailState data) {
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
                    textError.setText(message == null ? getString(R.string.rescuer_relief_detail_error) : message);
                });
            }
        });
    }

    private void renderState(RescuerReliefDetailState state) {
        if (state == null) {
            return;
        }

        textCode.setText(formatCode(state.getCode()));
        textRequestStatus.setText(mapRequestStatus(state.getRequestStatus()));
        applyRequestStatus(textRequestStatus, state.getRequestStatus());
        int stepIndex = resolveStep(state.getDeliveryStatus());
        textDeliveryStage.setText(getString(R.string.rescuer_relief_detail_stage, Math.max(stepIndex + 1, 1), 6));
        updateProgress(stepIndex);

        textArea.setText(isBlank(state.getTargetArea())
                ? getString(R.string.rescuer_relief_detail_area_fallback)
                : state.getTargetArea().trim());
        textAddress.setText(isBlank(state.getAddress())
                ? getString(R.string.citizen_dashboard_address_placeholder)
                : state.getAddress().trim());
        textLocationDescription.setText(isBlank(state.getLocationDescription())
                ? getString(R.string.citizen_detail_location_unknown)
                : state.getLocationDescription().trim());
        textIssueCode.setText(state.getAssignedIssueId() > 0L
                ? String.format(Locale.getDefault(), "#ISS-%d", state.getAssignedIssueId())
                : getString(R.string.rescuer_relief_detail_issue_missing));
        textManagerNote.setText(isBlank(state.getNote())
                ? getString(R.string.rescuer_relief_detail_note_empty)
                : state.getNote().trim());
        String deliveryNote = !isBlank(draftNote) ? draftNote : state.getDeliveryNote();
        textDeliveryNote.setText(isBlank(deliveryNote)
                ? getString(R.string.rescuer_relief_detail_note_draft_empty)
                : deliveryNote.trim());
        textUpdated.setText(formatTime(state.getUpdatedAt()));

        lineAdapter.submit(state.getLines());
        textItemsEmpty.setVisibility(state.getLines() == null || state.getLines().isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openDraftNoteDialog() {
        EditText editText = new EditText(this);
        editText.setHint(R.string.rescuer_relief_detail_note_hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMinLines(3);
        editText.setText(draftNote);
        editText.setPadding(36, 26, 36, 26);

        new AlertDialog.Builder(this)
                .setTitle(R.string.rescuer_relief_detail_note_action)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    draftNote = valueOf(editText);
                    renderState(currentState);
                    showShortToast(getString(R.string.rescuer_relief_detail_note_saved));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void openStatusDialog() {
        if (currentState == null) {
            return;
        }
        List<String> options = nextStatuses(currentState.getDeliveryStatus());
        if (options.isEmpty()) {
            showShortToast(getString(R.string.rescuer_relief_detail_no_action));
            return;
        }

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(36, 18, 36, 6);

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        for (int i = 0; i < options.size(); i++) {
            RadioButton button = new RadioButton(this);
            button.setText(mapDeliveryStatus(options.get(i)));
            button.setTag(options.get(i));
            button.setChecked(i == 0);
            radioGroup.addView(button);
        }

        EditText editText = new EditText(this);
        editText.setHint(R.string.rescuer_relief_detail_note_hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMinLines(3);
        editText.setText(draftNote);
        editText.setPadding(36, 26, 36, 26);
        container.addView(radioGroup);
        container.addView(editText);

        new AlertDialog.Builder(this)
                .setTitle(R.string.rescuer_relief_detail_status_action)
                .setView(container)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    RadioButton selected = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    String status = selected != null && selected.getTag() != null
                            ? selected.getTag().toString()
                            : options.get(0);
                    performStatusUpdate(status, valueOf(editText));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void performStatusUpdate(String targetStatus, String note) {
        setLoading(true);
        repository.updateStatus(reliefId, targetStatus, note, new RepositoryCallback<RescuerReliefDetailState>() {
            @Override
            public void onSuccess(RescuerReliefDetailState data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    draftNote = "";
                    currentState = data;
                    renderState(data);
                    showShortToast(getString(R.string.rescuer_relief_detail_status_success));
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.rescuer_relief_detail_status_error) : message);
                });
            }
        });
    }

    private List<String> nextStatuses(String current) {
        List<String> items = new ArrayList<>();
        String safe = normalize(current);
        if ("REQUESTED".equals(safe) || "MANAGER_APPROVED".equals(safe)) {
            items.add("RESCUER_RECEIVED");
            return items;
        }
        if ("RESCUER_RECEIVED".equals(safe)) {
            items.add("ARRIVED_WAREHOUSE");
            items.add("ARRIVED_RELIEF_POINT");
            items.add("RETURNED_TO_WAREHOUSE");
            return items;
        }
        if ("ARRIVED_WAREHOUSE".equals(safe)) {
            items.add("ARRIVED_RELIEF_POINT");
            items.add("RETURNED_TO_WAREHOUSE");
            return items;
        }
        if ("ARRIVED_RELIEF_POINT".equals(safe)) {
            items.add("COMPLETED");
            items.add("RETURNED_TO_WAREHOUSE");
        }
        return items;
    }

    private void updateProgress(int stepIndex) {
        for (int i = 0; i < stepDots.length; i++) {
            stepDots[i].setBackgroundResource(i <= stepIndex ? R.drawable.bg_chip_success : R.drawable.bg_chip_soft);
        }
        for (int i = 0; i < stepLines.length; i++) {
            stepLines[i].setBackgroundColor(getColor(i < stepIndex ? R.color.success : R.color.stroke_soft));
        }
    }

    private int resolveStep(String deliveryStatus) {
        String safe = normalize(deliveryStatus);
        if ("MANAGER_APPROVED".equals(safe)) {
            return 1;
        }
        if ("RESCUER_RECEIVED".equals(safe)) {
            return 2;
        }
        if ("ARRIVED_WAREHOUSE".equals(safe)) {
            return 3;
        }
        if ("ARRIVED_RELIEF_POINT".equals(safe)) {
            return 4;
        }
        if ("COMPLETED".equals(safe) || "RETURNED_TO_WAREHOUSE".equals(safe)) {
            return 5;
        }
        return 0;
    }

    private String mapRequestStatus(String value) {
        String safe = normalize(value);
        if ("APPROVED".equals(safe)) {
            return getString(R.string.rescuer_relief_detail_status_approved);
        }
        if ("COMPLETED".equals(safe)) {
            return getString(R.string.rescuer_task_list_status_done);
        }
        return getString(R.string.rescuer_task_list_status_pending);
    }

    private void applyRequestStatus(TextView view, String status) {
        String safe = normalize(status);
        if ("APPROVED".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_success);
            view.setTextColor(getColor(R.color.success));
            return;
        }
        if ("COMPLETED".equals(safe)) {
            view.setBackgroundResource(R.drawable.bg_chip_info);
            view.setTextColor(getColor(R.color.accent_dark));
            return;
        }
        view.setBackgroundResource(R.drawable.bg_chip_warning);
        view.setTextColor(getColor(R.color.warning));
    }

    private String mapDeliveryStatus(String value) {
        String safe = normalize(value);
        if ("RESCUER_RECEIVED".equals(safe)) {
            return getString(R.string.rescuer_relief_detail_delivery_received);
        }
        if ("ARRIVED_WAREHOUSE".equals(safe)) {
            return getString(R.string.rescuer_relief_detail_delivery_arrived_warehouse);
        }
        if ("ARRIVED_RELIEF_POINT".equals(safe)) {
            return getString(R.string.rescuer_relief_detail_delivery_arrived_point);
        }
        if ("RETURNED_TO_WAREHOUSE".equals(safe)) {
            return getString(R.string.rescuer_relief_detail_delivery_returned);
        }
        if ("COMPLETED".equals(safe)) {
            return getString(R.string.rescuer_relief_detail_delivery_completed);
        }
        return value;
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            textError.setVisibility(View.GONE);
        }
    }

    private String valueOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String formatCode(String code) {
        if (isBlank(code)) {
            return "#REL";
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

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

package com.floodrescue.mobile.ui.role.manager.relief.detail;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.ManagerReliefDetailState;
import com.floodrescue.mobile.data.repository.ManagerReliefDetailRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.manager.ManagerUi;
import com.floodrescue.mobile.ui.role.manager.dispatch.ManagerDispatchActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ManagerReliefDetailActivity extends BaseActivity {

    private long requestId;
    private ManagerReliefDetailRepository repository;
    private ManagerReliefLineAdapter lineAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView errorView;
    private View contentView;
    private TextView statusTop;
    private TextView deliveryStatus;
    private TextView code;
    private TextView meta;
    private TextView creatorName;
    private TextView creatorPhone;
    private TextView address;
    private TextView note;
    private ManagerReliefDetailState currentState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_manager_relief_detail);

        requestId = getIntent().getLongExtra(ManagerUi.EXTRA_REQUEST_ID, 0L);
        if (requestId <= 0) {
            showShortToast("Thiếu mã yêu cầu cứu trợ.");
            finish();
            return;
        }

        repository = new ManagerReliefDetailRepository(this);
        bindBackButton(R.id.buttonManagerDetailBack);

        swipeRefreshLayout = findViewById(R.id.swipeManagerDetail);
        progressBar = findViewById(R.id.progressManagerDetail);
        errorView = findViewById(R.id.textManagerDetailError);
        contentView = findViewById(R.id.contentManagerDetail);
        statusTop = findViewById(R.id.textManagerDetailStatusTop);
        deliveryStatus = findViewById(R.id.textManagerDetailDeliveryStatus);
        code = findViewById(R.id.textManagerDetailCode);
        meta = findViewById(R.id.textManagerDetailMeta);
        creatorName = findViewById(R.id.textManagerDetailCreatorName);
        creatorPhone = findViewById(R.id.textManagerDetailCreatorPhone);
        address = findViewById(R.id.textManagerDetailAddress);
        note = findViewById(R.id.textManagerDetailNote);

        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.recyclerManagerDetailLines);
        lineAdapter = new ManagerReliefLineAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(lineAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        findViewById(R.id.buttonManagerApproveDispatch).setOnClickListener(v -> openDispatch());
        findViewById(R.id.buttonManagerReject).setOnClickListener(v -> openRejectDialog());
        swipeRefreshLayout.setOnRefreshListener(this::loadDetail);

        loadDetail();
    }

    private void loadDetail() {
        showLoading(true);
        repository.loadDetail(requestId, new RepositoryCallback<ManagerReliefDetailState>() {
            @Override
            public void onSuccess(ManagerReliefDetailState data) {
                runOnUiThread(() -> {
                    currentState = data;
                    showLoading(false);
                    bindState(data);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(message == null ? "Không tải được chi tiết yêu cầu cứu trợ." : message);
                });
            }
        });
    }

    private void bindState(ManagerReliefDetailState state) {
        contentView.setVisibility(View.VISIBLE);
        code.setText(state.getCode());
        meta.setText("Tạo lúc " + ManagerUi.prettyDate(state.getCreatedAt()) + " • Cập nhật " + ManagerUi.prettyDate(state.getUpdatedAt()));
        creatorName.setText(state.getCreatedByName());
        creatorPhone.setText(state.getCreatedByPhone());
        StringBuilder addressBuilder = new StringBuilder();
        if (state.getTargetArea() != null && !state.getTargetArea().trim().isEmpty()) {
            addressBuilder.append("Khu vực: ").append(state.getTargetArea()).append("\n");
        }
        if (state.getAddress() != null && !state.getAddress().trim().isEmpty()) {
            addressBuilder.append(state.getAddress()).append("\n");
        }
        if (state.getLocationDescription() != null && !state.getLocationDescription().trim().isEmpty()) {
            addressBuilder.append(state.getLocationDescription()).append("\n");
        }
        if (state.getRescueRequestId() > 0) {
            addressBuilder.append("Liên kết cứu hộ #").append(state.getRescueRequestId());
        }
        address.setText(addressBuilder.toString().trim());
        note.setText(state.getNote() == null || state.getNote().trim().isEmpty() ? "Chưa có ghi chú điều phối." : state.getNote());
        lineAdapter.submit(state.getLines());

        ManagerUi.styleTag(statusTop, ManagerUi.documentStatusLabel(state.getStatus()), ManagerUi.colorForStatus(state.getStatus()), false);
        ManagerUi.styleTag(deliveryStatus, ManagerUi.deliveryStatusLabel(state.getDeliveryStatus()), ManagerUi.colorForStatus(state.getDeliveryStatus()), false);
        bindWorkflow(state);

        boolean locked = "COMPLETED".equalsIgnoreCase(state.getDeliveryStatus())
                || "REJECTED".equalsIgnoreCase(state.getDeliveryStatus())
                || "CANCELLED".equalsIgnoreCase(state.getStatus());
        findViewById(R.id.buttonManagerApproveDispatch).setEnabled(!locked);
        findViewById(R.id.buttonManagerReject).setEnabled(!locked);
    }

    private void bindWorkflow(ManagerReliefDetailState state) {
        styleStep((TextView) findViewById(R.id.textManagerStepRequested), true, true);
        boolean approved = "APPROVED".equalsIgnoreCase(state.getStatus()) || "MANAGER_APPROVED".equalsIgnoreCase(state.getDeliveryStatus());
        boolean delivering = "RESCUER_RECEIVED".equalsIgnoreCase(state.getDeliveryStatus())
                || "ARRIVED_WAREHOUSE".equalsIgnoreCase(state.getDeliveryStatus())
                || "ARRIVED_RELIEF_POINT".equalsIgnoreCase(state.getDeliveryStatus());
        boolean completed = "COMPLETED".equalsIgnoreCase(state.getDeliveryStatus()) || "DONE".equalsIgnoreCase(state.getStatus());
        styleStep((TextView) findViewById(R.id.textManagerStepApproved), approved, approved || delivering || completed);
        styleStep((TextView) findViewById(R.id.textManagerStepDelivery), delivering, delivering || completed);
        styleStep((TextView) findViewById(R.id.textManagerStepCompleted), completed, completed);
    }

    private void styleStep(TextView view, boolean active, boolean reached) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(ManagerUi.dp(this, 18));
        if (active) {
            drawable.setColor(getColor(R.color.info_soft));
            drawable.setStroke(ManagerUi.dp(this, 1), getColor(R.color.accent));
            view.setTextColor(getColor(R.color.accent_dark));
            view.setTypeface(Typeface.DEFAULT_BOLD);
        } else if (reached) {
            drawable.setColor(getColor(R.color.success_soft));
            drawable.setStroke(ManagerUi.dp(this, 1), getColor(R.color.success));
            view.setTextColor(getColor(R.color.success));
            view.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            drawable.setColor(getColor(R.color.surface_soft));
            drawable.setStroke(ManagerUi.dp(this, 1), getColor(R.color.stroke_soft));
            view.setTextColor(getColor(R.color.text_secondary));
            view.setTypeface(Typeface.DEFAULT);
        }
        view.setBackground(drawable);
    }

    private void openDispatch() {
        if (currentState == null) return;
        Intent intent = new Intent(this, ManagerDispatchActivity.class);
        intent.putExtra(ManagerUi.EXTRA_REQUEST_ID, currentState.getId());
        intent.putExtra(ManagerUi.EXTRA_REQUEST_CODE, currentState.getCode());
        startActivity(intent);
    }

    private void openRejectDialog() {
        if (currentState == null) return;
        EditText input = new EditText(this);
        input.setHint("Nhập lý do từ chối");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        int padding = ManagerUi.dp(this, 16);
        input.setPadding(padding, padding, padding, padding);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Từ chối yêu cầu")
                .setView(input)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xác nhận", (dialog, which) -> rejectRequest(input.getText() == null ? "" : input.getText().toString().trim()))
                .show();
    }

    private void rejectRequest(String reason) {
        showLoading(true);
        repository.rejectRequest(requestId, reason, new RepositoryCallback<ManagerReliefDetailState>() {
            @Override
            public void onSuccess(ManagerReliefDetailState data) {
                runOnUiThread(() -> {
                    currentState = data;
                    showLoading(false);
                    bindState(data);
                    showShortToast("Đã từ chối yêu cầu cứu trợ.");
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(message == null ? "Không từ chối được yêu cầu." : message);
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

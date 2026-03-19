package com.floodrescue.mobile.ui.role.manager.relief.create;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.ItemCategoryOption;
import com.floodrescue.mobile.data.model.ui.ManagerReliefDetailState;
import com.floodrescue.mobile.data.repository.ManagerReliefCreateRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.manager.ManagerUi;
import com.floodrescue.mobile.ui.role.manager.relief.detail.ManagerReliefDetailActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ManagerReliefCreateActivity extends BaseActivity {

    private ManagerReliefCreateRepository repository;
    private final List<ItemCategoryOption> itemCategories = new ArrayList<>();
    private final List<ManagerCreateLineAdapter.LineDraft> lineDrafts = new ArrayList<>();

    private ManagerCreateLineAdapter lineAdapter;
    private TextView generatedCodeView;
    private TextView lineHintView;
    private TextView errorView;
    private ProgressBar progressBar;
    private EditText rescueLinkInput;
    private EditText targetAreaInput;
    private EditText addressInput;
    private EditText latitudeInput;
    private EditText longitudeInput;
    private EditText locationDescriptionInput;
    private EditText noteInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_manager_relief_create);

        repository = new ManagerReliefCreateRepository(this);
        bindBackButton(R.id.buttonManagerCreateBack);

        generatedCodeView = findViewById(R.id.textManagerGeneratedCode);
        lineHintView = findViewById(R.id.textManagerCreateLineHint);
        errorView = findViewById(R.id.textManagerCreateError);
        progressBar = findViewById(R.id.progressManagerCreate);
        rescueLinkInput = findViewById(R.id.inputManagerRescueLink);
        targetAreaInput = findViewById(R.id.inputManagerTargetArea);
        addressInput = findViewById(R.id.inputManagerAddress);
        latitudeInput = findViewById(R.id.inputManagerLatitude);
        longitudeInput = findViewById(R.id.inputManagerLongitude);
        locationDescriptionInput = findViewById(R.id.inputManagerLocationDescription);
        noteInput = findViewById(R.id.inputManagerCreateNote);

        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.recyclerManagerCreateLines);
        lineAdapter = new ManagerCreateLineAdapter(position -> {
            if (position >= 0 && position < lineDrafts.size()) {
                lineDrafts.remove(position);
                syncLineItems();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(lineAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        findViewById(R.id.buttonManagerAddLine).setOnClickListener(v -> openAddLineDialog());
        findViewById(R.id.buttonManagerSubmitCreate).setOnClickListener(v -> submitRequest());

        loadBootstrap();
    }

    private void loadBootstrap() {
        repository.generateCode(new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(() -> generatedCodeView.setText(data == null ? "AUTO" : data));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> generatedCodeView.setText("AUTO"));
            }
        });

        repository.loadItemCategories(new RepositoryCallback<List<ItemCategoryOption>>() {
            @Override
            public void onSuccess(List<ItemCategoryOption> data) {
                runOnUiThread(() -> {
                    itemCategories.clear();
                    itemCategories.addAll(data);
                    lineHintView.setText(itemCategories.isEmpty() ? "Danh mục vật tư đang trống." : "Chọn vật tư từ backend và thêm vào phiếu cứu trợ.");
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> lineHintView.setText(message == null ? "Không tải được danh mục vật tư." : message));
            }
        });
    }

    private void openAddLineDialog() {
        if (itemCategories.isEmpty()) {
            showShortToast("Danh mục vật tư chưa sẵn sàng.");
            return;
        }

        int padding = ManagerUi.dp(this, 16);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(padding, padding, padding, padding);

        Spinner spinner = new Spinner(this);
        ArrayAdapter<ItemCategoryOption> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemCategories);
        spinner.setAdapter(adapter);
        container.addView(spinner);

        EditText qtyInput = new EditText(this);
        qtyInput.setHint("Số lượng");
        qtyInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        container.addView(qtyInput);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Thêm hàng cứu trợ")
                .setView(container)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    ItemCategoryOption option = (ItemCategoryOption) spinner.getSelectedItem();
                    String qty = qtyInput.getText() == null ? "" : qtyInput.getText().toString().trim();
                    if (option == null || qty.isEmpty()) {
                        showShortToast("Vui lòng chọn vật tư và số lượng.");
                        return;
                    }
                    lineDrafts.add(new ManagerCreateLineAdapter.LineDraft(option, qty));
                    syncLineItems();
                })
                .show();
    }

    private void syncLineItems() {
        lineAdapter.submit(lineDrafts);
        lineHintView.setVisibility(lineDrafts.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void submitRequest() {
        errorView.setVisibility(View.GONE);
        String targetArea = valueOf(targetAreaInput);
        String address = valueOf(addressInput);
        String locationDescription = valueOf(locationDescriptionInput);
        String note = valueOf(noteInput);

        if (targetArea.isEmpty()) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("Vui lòng nhập khu vực mục tiêu.");
            return;
        }
        if (lineDrafts.isEmpty()) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("Yêu cầu cứu trợ phải có ít nhất một dòng vật tư.");
            return;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("targetArea", targetArea);
        if (!address.isEmpty()) payload.addProperty("addressText", address);
        Double latitude = parseDouble(valueOf(latitudeInput));
        Double longitude = parseDouble(valueOf(longitudeInput));
        if (latitude != null) payload.addProperty("latitude", latitude);
        if (longitude != null) payload.addProperty("longitude", longitude);
        if (!locationDescription.isEmpty()) {
            payload.addProperty("locationDescription", locationDescription);
        } else if (!address.isEmpty()) {
            payload.addProperty("locationDescription", address);
        }
        Long rescueRequestId = parseLong(valueOf(rescueLinkInput));
        if (rescueRequestId != null) payload.addProperty("rescueRequestId", rescueRequestId);
        if (!note.isEmpty()) payload.addProperty("note", note);

        JsonArray lines = new JsonArray();
        for (ManagerCreateLineAdapter.LineDraft draft : lineDrafts) {
            JsonObject line = new JsonObject();
            line.addProperty("itemCategoryId", draft.getOption().getId());
            line.addProperty("qty", draft.getQty());
            if (!TextUtils.isEmpty(draft.getOption().getUnit())) {
                line.addProperty("unit", draft.getOption().getUnit());
            }
            lines.add(line);
        }
        payload.add("lines", lines);

        setSubmitting(true);
        repository.createRequest(payload, new RepositoryCallback<ManagerReliefDetailState>() {
            @Override
            public void onSuccess(ManagerReliefDetailState data) {
                runOnUiThread(() -> {
                    setSubmitting(false);
                    showShortToast("Tạo yêu cầu cứu trợ thành công.");
                    Intent intent = new Intent(ManagerReliefCreateActivity.this, ManagerReliefDetailActivity.class);
                    intent.putExtra(ManagerUi.EXTRA_REQUEST_ID, data.getId());
                    intent.putExtra(ManagerUi.EXTRA_REQUEST_CODE, data.getCode());
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setSubmitting(false);
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(message == null ? "Không tạo được yêu cầu cứu trợ." : message);
                });
            }
        });
    }

    private void setSubmitting(boolean submitting) {
        progressBar.setVisibility(submitting ? View.VISIBLE : View.GONE);
        findViewById(R.id.buttonManagerSubmitCreate).setEnabled(!submitting);
    }

    private String valueOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}

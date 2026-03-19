package com.floodrescue.mobile.ui.role.coordinator.blockedcitizen;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.BlockedCitizenItem;
import com.floodrescue.mobile.data.repository.CoordinatorOperationsRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BlockedCitizenListActivity extends BaseActivity implements BlockedCitizenAdapter.Listener {

    private CoordinatorOperationsRepository repository;
    private BlockedCitizenAdapter adapter;
    private EditText editSearch;
    private TextView textCount;
    private TextView textEmpty;
    private TextView textError;
    private View progressView;
    private final List<BlockedCitizenItem> fullData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_coordinator_blocked_citizens);

        repository = new CoordinatorOperationsRepository(this);
        bindViews();
        bindActions();
        setupList();
        loadBlockedCitizens();
    }

    private void bindViews() {
        bindBackButton(R.id.buttonBackBlockedCitizen);
        editSearch = findViewById(R.id.editBlockedCitizenSearch);
        textCount = findViewById(R.id.textBlockedCitizenCount);
        textEmpty = findViewById(R.id.textBlockedCitizenEmpty);
        textError = findViewById(R.id.textBlockedCitizenError);
        progressView = findViewById(R.id.progressBlockedCitizen);
    }

    private void bindActions() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterLocal(); }
            @Override public void afterTextChanged(Editable s) { }
        });
        findViewById(R.id.navBlockedDashboard).setOnClickListener(v -> AppNavigator.openHome(this));
        findViewById(R.id.navBlockedMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.navBlockedCitizen).setOnClickListener(v -> { });
        findViewById(R.id.navBlockedSettings).setOnClickListener(v -> AppNavigator.openProfile(this));
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerBlockedCitizens);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BlockedCitizenAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void loadBlockedCitizens() {
        setLoading(true);
        repository.getBlockedCitizens(new RepositoryCallback<List<BlockedCitizenItem>>() {
            @Override
            public void onSuccess(List<BlockedCitizenItem> data) {
                runOnUiThread(() -> {
                    setLoading(false);
                    fullData.clear();
                    if (data != null) {
                        fullData.addAll(data);
                    }
                    filterLocal();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    textError.setVisibility(View.VISIBLE);
                    textError.setText(message == null ? getString(R.string.coordinator_blocked_load_error) : message);
                });
            }
        });
    }

    private void filterLocal() {
        String keyword = editSearch.getText() == null ? "" : editSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
        List<BlockedCitizenItem> filtered = new ArrayList<>();
        for (BlockedCitizenItem item : fullData) {
            String haystack = (item.getFullName() + " " + item.getPhone() + " " + item.getEmail()).toLowerCase(Locale.ROOT);
            if (keyword.isEmpty() || haystack.contains(keyword)) {
                filtered.add(item);
            }
        }
        adapter.submit(filtered);
        textCount.setText(getString(R.string.coordinator_blocked_count, filtered.size()));
        textEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setLoading(boolean loading) {
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        textError.setVisibility(View.GONE);
        if (loading) {
            textEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUnblock(BlockedCitizenItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.coordinator_blocked_unblock_title)
                .setMessage(getString(R.string.coordinator_blocked_unblock_message, item.getFullName()))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    repository.unblockCitizen(item.getId(), null, new RepositoryCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            runOnUiThread(() -> {
                                showShortToast(data);
                                loadBlockedCitizens();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            runOnUiThread(() -> showShortToast(message));
                        }
                    });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}

package com.floodrescue.mobile.ui.role.manager.relief.list;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.model.ui.ManagerReliefRequestItem;
import com.floodrescue.mobile.data.model.ui.ManagerReliefRequestListState;
import com.floodrescue.mobile.data.repository.ManagerReliefListRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.manager.ManagerUi;
import com.floodrescue.mobile.ui.role.manager.relief.create.ManagerReliefCreateActivity;
import com.floodrescue.mobile.ui.role.manager.relief.detail.ManagerReliefDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManagerReliefListActivity extends BaseActivity {

    private ManagerReliefListRepository repository;
    private ManagerReliefRequestAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView errorView;
    private TextView emptyView;
    private TextView countView;
    private EditText searchInput;

    private final List<ManagerReliefRequestItem> sourceItems = new ArrayList<>();
    private String currentStatusFilter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_manager_relief_list);

        repository = new ManagerReliefListRepository(this);
        swipeRefreshLayout = findViewById(R.id.swipeManagerRequests);
        progressBar = findViewById(R.id.progressManagerRequestList);
        errorView = findViewById(R.id.textManagerRequestError);
        emptyView = findViewById(R.id.textManagerRequestEmpty);
        countView = findViewById(R.id.textManagerRequestsCount);
        searchInput = findViewById(R.id.inputManagerRequestSearch);

        bindBackButton(R.id.buttonManagerRequestsBack);
        ManagerUi.bindBottomNav(this, R.id.navManagerRequests);
        setupFilters();
        setupRecycler();

        findViewById(R.id.fabManagerCreateRequest).setOnClickListener(v -> startActivity(new Intent(this, ManagerReliefCreateActivity.class)));
        swipeRefreshLayout.setOnRefreshListener(this::reload);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applySearchFilter(); }
            @Override public void afterTextChanged(Editable s) { }
        });

        reload();
    }

    private void setupRecycler() {
        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.recyclerManagerRequests);
        adapter = new ManagerReliefRequestAdapter(item -> {
            Intent intent = new Intent(this, ManagerReliefDetailActivity.class);
            intent.putExtra(ManagerUi.EXTRA_REQUEST_ID, item.getId());
            intent.putExtra(ManagerUi.EXTRA_REQUEST_CODE, item.getCode());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupFilters() {
        View all = findViewById(R.id.filterManagerAll);
        View pending = findViewById(R.id.filterManagerPending);
        View approved = findViewById(R.id.filterManagerApproved);
        View delivered = findViewById(R.id.filterManagerDelivered);

        all.setOnClickListener(v -> setStatusFilter(null, all, pending, approved, delivered));
        pending.setOnClickListener(v -> setStatusFilter("DRAFT", pending, all, approved, delivered));
        approved.setOnClickListener(v -> setStatusFilter("APPROVED", approved, all, pending, delivered));
        delivered.setOnClickListener(v -> setStatusFilter("DONE", delivered, all, pending, approved));
    }

    private void setStatusFilter(String value, View selected, View... unselected) {
        currentStatusFilter = value;
        selectFilterChip((TextView) selected, true);
        for (View view : unselected) {
            selectFilterChip((TextView) view, false);
        }
        reload();
    }

    private void selectFilterChip(TextView chip, boolean selected) {
        chip.setBackgroundResource(selected ? R.drawable.bg_manager_filter_active : R.drawable.bg_manager_filter_inactive);
        chip.setTextColor(getColor(selected ? R.color.white : R.color.text_secondary));
    }

    private void reload() {
        showLoading(true);
        repository.loadRequests(currentStatusFilter, new RepositoryCallback<ManagerReliefRequestListState>() {
            @Override
            public void onSuccess(ManagerReliefRequestListState data) {
                runOnUiThread(() -> {
                    showLoading(false);
                    sourceItems.clear();
                    sourceItems.addAll(data.getItems());
                    applySearchFilter();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(message == null ? "Không tải được danh sách yêu cầu cứu trợ." : message);
                    sourceItems.clear();
                    applySearchFilter();
                });
            }
        });
    }

    private void applySearchFilter() {
        String query = searchInput.getText() == null ? "" : searchInput.getText().toString().trim().toLowerCase(Locale.ROOT);
        List<ManagerReliefRequestItem> filtered = new ArrayList<>();
        for (ManagerReliefRequestItem item : sourceItems) {
            String searchable = (item.getCode() + " " + item.getTargetArea() + " " + item.getAddress() + " " + item.getCreatedByName()).toLowerCase(Locale.ROOT);
            if (query.isEmpty() || searchable.contains(query)) {
                filtered.add(item);
            }
        }
        adapter.submit(filtered);
        countView.setText(filtered.size() + " items");
        emptyView.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (!loading) {
            swipeRefreshLayout.setRefreshing(false);
        }
        errorView.setVisibility(View.GONE);
    }
}

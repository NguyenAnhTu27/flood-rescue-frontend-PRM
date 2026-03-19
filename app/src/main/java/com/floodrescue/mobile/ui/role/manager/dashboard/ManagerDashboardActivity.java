package com.floodrescue.mobile.ui.role.manager.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.data.model.ui.ManagerDashboardState;
import com.floodrescue.mobile.data.repository.ManagerDashboardRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.floodrescue.mobile.ui.role.manager.ManagerUi;
import com.floodrescue.mobile.ui.role.manager.asset.list.ManagerAssetListActivity;
import com.floodrescue.mobile.ui.role.manager.dispatch.ManagerDispatchActivity;
import com.floodrescue.mobile.ui.role.manager.inventory.issue.list.ManagerInventoryIssueListActivity;
import com.floodrescue.mobile.ui.role.manager.inventory.stock.ManagerInventoryStockActivity;
import com.floodrescue.mobile.ui.role.manager.relief.create.ManagerReliefCreateActivity;
import com.floodrescue.mobile.ui.role.manager.relief.list.ManagerReliefListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

public class ManagerDashboardActivity extends BaseActivity {

    private ManagerDashboardRepository repository;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView errorView;
    private ManagerOverviewAdapter overviewAdapter;
    private ManagerInventorySummaryAdapter inventorySummaryAdapter;
    private ManagerInventoryItemAdapter inventoryItemAdapter;
    private ManagerTransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_manager_dashboard);

        repository = new ManagerDashboardRepository(this);
        swipeRefreshLayout = findViewById(R.id.swipeManagerDashboard);
        progressBar = findViewById(R.id.progressManagerDashboard);
        errorView = findViewById(R.id.textManagerDashboardError);

        SessionManager sessionManager = new SessionManager(this);
        String fullName = AppNavigator.displayName(sessionManager);
        ((TextView) findViewById(R.id.textManagerName)).setText(fullName);
        ((TextView) findViewById(R.id.textManagerRole)).setText(AppNavigator.displayRole(sessionManager.getRole()));
        ((TextView) findViewById(R.id.textManagerInitial)).setText(AppNavigator.initials(fullName));

        setupRecyclerViews();
        setupActions();
        ManagerUi.bindBottomNav(this, R.id.navManagerDashboard);

        swipeRefreshLayout.setOnRefreshListener(this::loadDashboard);
        loadDashboard();
    }

    private void setupRecyclerViews() {
        androidx.recyclerview.widget.RecyclerView overviewRecycler = findViewById(R.id.recyclerManagerOverview);
        overviewAdapter = new ManagerOverviewAdapter();
        overviewRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        overviewRecycler.setAdapter(overviewAdapter);
        overviewRecycler.setNestedScrollingEnabled(false);

        androidx.recyclerview.widget.RecyclerView summaryRecycler = findViewById(R.id.recyclerManagerInventorySummary);
        inventorySummaryAdapter = new ManagerInventorySummaryAdapter();
        summaryRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        summaryRecycler.setAdapter(inventorySummaryAdapter);
        summaryRecycler.setNestedScrollingEnabled(false);

        androidx.recyclerview.widget.RecyclerView itemsRecycler = findViewById(R.id.recyclerManagerInventoryItems);
        inventoryItemAdapter = new ManagerInventoryItemAdapter();
        itemsRecycler.setLayoutManager(new LinearLayoutManager(this));
        itemsRecycler.setAdapter(inventoryItemAdapter);
        itemsRecycler.setNestedScrollingEnabled(false);

        androidx.recyclerview.widget.RecyclerView transactionRecycler = findViewById(R.id.recyclerManagerTransactions);
        transactionAdapter = new ManagerTransactionAdapter();
        transactionRecycler.setLayoutManager(new LinearLayoutManager(this));
        transactionRecycler.setAdapter(transactionAdapter);
        transactionRecycler.setNestedScrollingEnabled(false);
    }

    private void setupActions() {
        findViewById(R.id.cardManagerQuickRequests).setOnClickListener(v -> startActivity(new Intent(this, ManagerReliefListActivity.class)));
        findViewById(R.id.cardManagerQuickDispatch).setOnClickListener(v -> startActivity(new Intent(this, ManagerDispatchActivity.class)));
        findViewById(R.id.cardManagerQuickCreate).setOnClickListener(v -> startActivity(new Intent(this, ManagerReliefCreateActivity.class)));
        findViewById(R.id.cardManagerQuickWarehouse).setOnClickListener(v -> startActivity(new Intent(this, ManagerInventoryStockActivity.class)));
        findViewById(R.id.buttonManagerInventoryViewAll).setOnClickListener(v -> startActivity(new Intent(this, ManagerInventoryStockActivity.class)));
        findViewById(R.id.buttonManagerTransactionsHistory).setOnClickListener(v -> startActivity(new Intent(this, ManagerInventoryIssueListActivity.class)));
        findViewById(R.id.textManagerInitial).setOnClickListener(v -> AppNavigator.openProfile(this));
    }

    private void loadDashboard() {
        showLoading(true);
        repository.loadDashboard(new RepositoryCallback<ManagerDashboardState>() {
            @Override
            public void onSuccess(ManagerDashboardState data) {
                runOnUiThread(() -> {
                    showLoading(false);
                    overviewAdapter.submit(data.getOverviewItems());
                    inventorySummaryAdapter.submit(data.getInventorySummary());
                    inventoryItemAdapter.submit(data.getInventoryItems());
                    transactionAdapter.submit(data.getRecentTransactions());
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(message == null ? "Không tải được dashboard cứu trợ." : message);
                });
            }
        });
    }

    private void showLoading(boolean loading) {
        if (!loading) {
            swipeRefreshLayout.setRefreshing(false);
        }
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        errorView.setVisibility(View.GONE);
    }
}

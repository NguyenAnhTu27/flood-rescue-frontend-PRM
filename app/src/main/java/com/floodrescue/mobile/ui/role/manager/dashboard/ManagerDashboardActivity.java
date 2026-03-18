package com.floodrescue.mobile.ui.role.manager.dashboard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.role.manager.asset.list.ManagerAssetListActivity;
import com.floodrescue.mobile.ui.role.manager.inventory.stock.ManagerInventoryStockActivity;
import com.floodrescue.mobile.ui.role.manager.relief.list.ManagerReliefListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;
import com.google.android.material.button.MaterialButton;

public class ManagerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_manager_dashboard);

        SessionManager sessionManager = new SessionManager(this);
        String fullName = AppNavigator.displayName(sessionManager);

        ((TextView) findViewById(R.id.textManagerName)).setText(fullName);
        ((TextView) findViewById(R.id.textManagerRole)).setText(AppNavigator.displayRole(sessionManager.getRole()));
        ((TextView) findViewById(R.id.textManagerInitial)).setText(AppNavigator.initials(fullName));

        findViewById(R.id.cardManagerRequests).setOnClickListener(v -> startActivity(new android.content.Intent(this, ManagerReliefListActivity.class)));
        findViewById(R.id.cardManagerWarehouse).setOnClickListener(v -> startActivity(new android.content.Intent(this, ManagerInventoryStockActivity.class)));
        findViewById(R.id.cardManagerAssets).setOnClickListener(v -> startActivity(new android.content.Intent(this, ManagerAssetListActivity.class)));
        findViewById(R.id.cardManagerMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.cardManagerNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.cardManagerProfile).setOnClickListener(v -> AppNavigator.openProfile(this));

        ((MaterialButton) findViewById(R.id.buttonManagerMap)).setOnClickListener(v -> AppNavigator.openMap(this));
        ((MaterialButton) findViewById(R.id.buttonManagerNotifications)).setOnClickListener(v -> AppNavigator.openNotifications(this));
        ((MaterialButton) findViewById(R.id.buttonManagerProfile)).setOnClickListener(v -> AppNavigator.openProfile(this));
        ((MaterialButton) findViewById(R.id.buttonManagerLogout)).setOnClickListener(v -> AppNavigator.logout(this));
    }
}

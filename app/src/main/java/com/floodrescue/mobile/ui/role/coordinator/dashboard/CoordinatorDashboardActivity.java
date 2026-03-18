package com.floodrescue.mobile.ui.role.coordinator.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.role.coordinator.blockedcitizen.BlockedCitizenListActivity;
import com.floodrescue.mobile.ui.role.coordinator.rescuequeue.CoordinatorRescueQueueActivity;
import com.floodrescue.mobile.ui.role.coordinator.taskgroup.list.CoordinatorTaskGroupListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;
import com.google.android.material.button.MaterialButton;

public class CoordinatorDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_coordinator_dashboard);

        SessionManager sessionManager = new SessionManager(this);
        String fullName = AppNavigator.displayName(sessionManager);

        ((TextView) findViewById(R.id.textCoordinatorName)).setText(fullName);
        ((TextView) findViewById(R.id.textCoordinatorRole)).setText(AppNavigator.displayRole(sessionManager.getRole()));
        ((TextView) findViewById(R.id.textCoordinatorInitial)).setText(AppNavigator.initials(fullName));

        findViewById(R.id.cardCoordinatorQueue).setOnClickListener(v -> startActivity(new Intent(this, CoordinatorRescueQueueActivity.class)));
        findViewById(R.id.cardCoordinatorTeams).setOnClickListener(v -> startActivity(new Intent(this, CoordinatorTaskGroupListActivity.class)));
        findViewById(R.id.cardCoordinatorChat).setOnClickListener(v -> startActivity(new Intent(this, BlockedCitizenListActivity.class)));
        findViewById(R.id.cardCoordinatorMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.cardCoordinatorNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.cardCoordinatorProfile).setOnClickListener(v -> AppNavigator.openProfile(this));

        ((MaterialButton) findViewById(R.id.buttonCoordinatorMap)).setOnClickListener(v -> AppNavigator.openMap(this));
        ((MaterialButton) findViewById(R.id.buttonCoordinatorNotifications)).setOnClickListener(v -> AppNavigator.openNotifications(this));
        ((MaterialButton) findViewById(R.id.buttonCoordinatorProfile)).setOnClickListener(v -> AppNavigator.openProfile(this));
        ((MaterialButton) findViewById(R.id.buttonCoordinatorLogout)).setOnClickListener(v -> AppNavigator.logout(this));
    }
}

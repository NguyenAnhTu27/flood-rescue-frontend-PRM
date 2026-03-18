package com.floodrescue.mobile.ui.role.rescuer.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.role.rescuer.relief.list.RescuerReliefListActivity;
import com.floodrescue.mobile.ui.role.rescuer.task.list.RescuerTaskListActivity;
import com.floodrescue.mobile.ui.role.rescuer.taskgroup.list.RescuerTaskGroupListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;
import com.google.android.material.button.MaterialButton;

public class RescuerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_rescuer_dashboard);

        SessionManager sessionManager = new SessionManager(this);
        String fullName = AppNavigator.displayName(sessionManager);

        ((TextView) findViewById(R.id.textRescuerName)).setText(fullName);
        ((TextView) findViewById(R.id.textRescuerRole)).setText(AppNavigator.displayRole(sessionManager.getRole()));
        ((TextView) findViewById(R.id.textRescuerInitial)).setText(AppNavigator.initials(fullName));

        findViewById(R.id.cardRescuerMission).setOnClickListener(v -> startActivity(new Intent(this, RescuerTaskListActivity.class)));
        findViewById(R.id.cardRescuerUpdate).setOnClickListener(v -> startActivity(new Intent(this, RescuerTaskGroupListActivity.class)));
        findViewById(R.id.cardRescuerChat).setOnClickListener(v -> startActivity(new Intent(this, RescuerReliefListActivity.class)));
        findViewById(R.id.cardRescuerMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.cardRescuerNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.cardRescuerProfile).setOnClickListener(v -> AppNavigator.openProfile(this));

        ((MaterialButton) findViewById(R.id.buttonRescuerMap)).setOnClickListener(v -> AppNavigator.openMap(this));
        ((MaterialButton) findViewById(R.id.buttonRescuerNotifications)).setOnClickListener(v -> AppNavigator.openNotifications(this));
        ((MaterialButton) findViewById(R.id.buttonRescuerProfile)).setOnClickListener(v -> AppNavigator.openProfile(this));
        ((MaterialButton) findViewById(R.id.buttonRescuerLogout)).setOnClickListener(v -> AppNavigator.logout(this));
    }
}

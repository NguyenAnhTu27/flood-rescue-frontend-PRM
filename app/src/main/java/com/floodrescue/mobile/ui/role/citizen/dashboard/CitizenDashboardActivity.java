package com.floodrescue.mobile.ui.role.citizen.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.role.citizen.rescue.create.CreateRescueRequestActivity;
import com.floodrescue.mobile.ui.role.citizen.rescue.detail.CitizenRescueDetailActivity;
import com.floodrescue.mobile.ui.role.citizen.rescue.list.CitizenRescueListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;
import com.google.android.material.button.MaterialButton;

public class CitizenDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_citizen_dashboard);

        SessionManager sessionManager = new SessionManager(this);
        String fullName = AppNavigator.displayName(sessionManager);

        ((TextView) findViewById(R.id.textCitizenName)).setText(fullName);
        ((TextView) findViewById(R.id.textCitizenRole)).setText(AppNavigator.displayRole(sessionManager.getRole()));
        ((TextView) findViewById(R.id.textCitizenInitial)).setText(AppNavigator.initials(fullName));

        findViewById(R.id.cardCitizenRescue).setOnClickListener(v -> startActivity(new Intent(this, CreateRescueRequestActivity.class)));
        findViewById(R.id.cardCitizenRelief).setOnClickListener(v -> startActivity(new Intent(this, CitizenRescueListActivity.class)));
        findViewById(R.id.cardCitizenTracking).setOnClickListener(v -> startActivity(new Intent(this, CitizenRescueDetailActivity.class)));
        findViewById(R.id.cardCitizenMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.cardCitizenNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.cardCitizenProfile).setOnClickListener(v -> AppNavigator.openProfile(this));

        ((MaterialButton) findViewById(R.id.buttonCitizenMap)).setOnClickListener(v -> AppNavigator.openMap(this));
        ((MaterialButton) findViewById(R.id.buttonCitizenNotifications)).setOnClickListener(v -> AppNavigator.openNotifications(this));
        ((MaterialButton) findViewById(R.id.buttonCitizenProfile)).setOnClickListener(v -> AppNavigator.openProfile(this));
        ((MaterialButton) findViewById(R.id.buttonCitizenLogout)).setOnClickListener(v -> AppNavigator.logout(this));
    }
}

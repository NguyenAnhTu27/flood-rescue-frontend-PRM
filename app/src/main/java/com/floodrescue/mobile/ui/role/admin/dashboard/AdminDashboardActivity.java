package com.floodrescue.mobile.ui.role.admin.dashboard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.role.admin.audit.AdminAuditActivity;
import com.floodrescue.mobile.ui.role.admin.team.AdminTeamActivity;
import com.floodrescue.mobile.ui.role.admin.user.list.AdminUserListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;
import com.google.android.material.button.MaterialButton;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_admin_dashboard);

        SessionManager sessionManager = new SessionManager(this);
        String fullName = AppNavigator.displayName(sessionManager);

        ((TextView) findViewById(R.id.textAdminName)).setText(fullName);
        ((TextView) findViewById(R.id.textAdminRole)).setText(AppNavigator.displayRole(sessionManager.getRole()));
        ((TextView) findViewById(R.id.textAdminInitial)).setText(AppNavigator.initials(fullName));

        findViewById(R.id.cardAdminUsers).setOnClickListener(v -> startActivity(new android.content.Intent(this, AdminUserListActivity.class)));
        findViewById(R.id.cardAdminTeams).setOnClickListener(v -> startActivity(new android.content.Intent(this, AdminTeamActivity.class)));
        findViewById(R.id.cardAdminAudit).setOnClickListener(v -> startActivity(new android.content.Intent(this, AdminAuditActivity.class)));
        findViewById(R.id.cardAdminMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.cardAdminNotifications).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.cardAdminProfile).setOnClickListener(v -> AppNavigator.openProfile(this));

        ((MaterialButton) findViewById(R.id.buttonAdminMap)).setOnClickListener(v -> AppNavigator.openMap(this));
        ((MaterialButton) findViewById(R.id.buttonAdminNotifications)).setOnClickListener(v -> AppNavigator.openNotifications(this));
        ((MaterialButton) findViewById(R.id.buttonAdminProfile)).setOnClickListener(v -> AppNavigator.openProfile(this));
        ((MaterialButton) findViewById(R.id.buttonAdminLogout)).setOnClickListener(v -> AppNavigator.logout(this));
    }
}

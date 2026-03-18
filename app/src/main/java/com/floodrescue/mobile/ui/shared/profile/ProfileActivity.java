package com.floodrescue.mobile.ui.shared.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.auth.login.LoginActivity;
import com.floodrescue.mobile.ui.role.admin.audit.AdminAuditActivity;
import com.floodrescue.mobile.ui.role.admin.user.list.AdminUserListActivity;
import com.floodrescue.mobile.ui.role.citizen.feedback.CitizenFeedbackActivity;
import com.floodrescue.mobile.ui.role.citizen.rescue.list.CitizenRescueListActivity;
import com.floodrescue.mobile.ui.role.coordinator.rescuequeue.CoordinatorRescueQueueActivity;
import com.floodrescue.mobile.ui.role.coordinator.taskgroup.list.CoordinatorTaskGroupListActivity;
import com.floodrescue.mobile.ui.role.manager.inventory.stock.ManagerInventoryStockActivity;
import com.floodrescue.mobile.ui.role.manager.relief.list.ManagerReliefListActivity;
import com.floodrescue.mobile.ui.role.rescuer.relief.list.RescuerReliefListActivity;
import com.floodrescue.mobile.ui.role.rescuer.task.list.RescuerTaskListActivity;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_profile);

        SessionManager sessionManager = new SessionManager(this);
        String fullName = AppNavigator.displayName(sessionManager);

        ((TextView) findViewById(R.id.textProfileName)).setText(fullName);
        ((TextView) findViewById(R.id.textProfilePhone)).setText(sessionManager.isLoggedIn() ? "Tài khoản đã đăng nhập" : "0901234567");
        ((TextView) findViewById(R.id.textProfileAvatar)).setText(AppNavigator.initials(fullName));

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.buttonEditProfile).setOnClickListener(v ->
                Toast.makeText(this, "Khối chỉnh sửa hồ sơ sẽ được tách tiếp trong package shared/profile/edit.", Toast.LENGTH_SHORT).show());
        findViewById(R.id.cardHistoryRescue).setOnClickListener(v -> openPrimaryModule(sessionManager));
        findViewById(R.id.cardHistoryRelief).setOnClickListener(v -> openSecondaryModule(sessionManager));
        findViewById(R.id.buttonLogoutProfile).setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        findViewById(R.id.navHome).setOnClickListener(v -> AppNavigator.openHome(this));
        findViewById(R.id.navMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.navNotification).setOnClickListener(v -> AppNavigator.openNotifications(this));
    }

    private void openPrimaryModule(SessionManager sessionManager) {
        switch (AppNavigator.normalizeRole(sessionManager.getRole())) {
            case AppNavigator.ROLE_CITIZEN:
                startActivity(new Intent(this, CitizenRescueListActivity.class));
                break;
            case AppNavigator.ROLE_COORDINATOR:
                startActivity(new Intent(this, CoordinatorRescueQueueActivity.class));
                break;
            case AppNavigator.ROLE_RESCUER:
                startActivity(new Intent(this, RescuerTaskListActivity.class));
                break;
            case AppNavigator.ROLE_MANAGER:
                startActivity(new Intent(this, ManagerReliefListActivity.class));
                break;
            case AppNavigator.ROLE_ADMIN:
                startActivity(new Intent(this, AdminUserListActivity.class));
                break;
            default:
                AppNavigator.openNotifications(this);
                break;
        }
    }

    private void openSecondaryModule(SessionManager sessionManager) {
        switch (AppNavigator.normalizeRole(sessionManager.getRole())) {
            case AppNavigator.ROLE_CITIZEN:
                startActivity(new Intent(this, CitizenFeedbackActivity.class));
                break;
            case AppNavigator.ROLE_COORDINATOR:
                startActivity(new Intent(this, CoordinatorTaskGroupListActivity.class));
                break;
            case AppNavigator.ROLE_RESCUER:
                startActivity(new Intent(this, RescuerReliefListActivity.class));
                break;
            case AppNavigator.ROLE_MANAGER:
                startActivity(new Intent(this, ManagerInventoryStockActivity.class));
                break;
            case AppNavigator.ROLE_ADMIN:
                startActivity(new Intent(this, AdminAuditActivity.class));
                break;
            default:
                AppNavigator.openNotifications(this);
                break;
        }
    }
}

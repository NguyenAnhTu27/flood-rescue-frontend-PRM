package com.floodrescue.mobile.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.auth.login.LoginActivity;
import com.floodrescue.mobile.ui.home.HomeActivity;
import com.floodrescue.mobile.ui.map.SafeMapActivity;
import com.floodrescue.mobile.ui.notification.NotificationActivity;
import com.floodrescue.mobile.ui.request.detail.RequestDetailActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SessionManager sessionManager = new SessionManager(this);
        String fullName = sessionManager.getFullName().trim();
        if (fullName.isEmpty()) {
            fullName = "Nguyễn Văn An";
        }

        ((TextView) findViewById(R.id.textProfileName)).setText(fullName);
        ((TextView) findViewById(R.id.textProfilePhone)).setText(sessionManager.isLoggedIn() ? "Tài khoản đã đăng nhập" : "0901234567");
        ((TextView) findViewById(R.id.textProfileAvatar)).setText(String.valueOf(fullName.charAt(0)).toUpperCase());

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.buttonEditProfile).setOnClickListener(v ->
                Toast.makeText(this, "Màn chỉnh sửa hồ sơ sẽ làm tiếp ngay sau phần UI này.", Toast.LENGTH_SHORT).show());
        findViewById(R.id.cardHistoryRescue).setOnClickListener(v -> startActivity(new Intent(this, RequestDetailActivity.class)));
        findViewById(R.id.cardHistoryRelief).setOnClickListener(v -> startActivity(new Intent(this, RequestDetailActivity.class)));
        findViewById(R.id.buttonLogoutProfile).setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        findViewById(R.id.navHome).setOnClickListener(v -> switchTab(HomeActivity.class));
        findViewById(R.id.navMap).setOnClickListener(v -> switchTab(SafeMapActivity.class));
        findViewById(R.id.navNotification).setOnClickListener(v -> switchTab(NotificationActivity.class));
    }

    private void switchTab(Class<?> target) {
        startActivity(new Intent(this, target));
        finish();
    }
}
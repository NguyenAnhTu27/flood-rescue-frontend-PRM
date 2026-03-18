package com.floodrescue.mobile.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.ui.home.HomeActivity;
import com.floodrescue.mobile.ui.notification.NotificationActivity;
import com.floodrescue.mobile.ui.profile.ProfileActivity;

public class SafeMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_map);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.buttonDirections).setOnClickListener(v ->
                Toast.makeText(this, "Điều hướng bản đồ sẽ nối dịch vụ map ở bước sau.", Toast.LENGTH_SHORT).show());
        findViewById(R.id.buttonCall).setOnClickListener(v ->
                Toast.makeText(this, "Tính năng gọi khẩn sẽ được làm tiếp.", Toast.LENGTH_SHORT).show());
        findViewById(R.id.navHome).setOnClickListener(v -> switchTab(HomeActivity.class));
        findViewById(R.id.navNotification).setOnClickListener(v -> switchTab(NotificationActivity.class));
        findViewById(R.id.navProfile).setOnClickListener(v -> switchTab(ProfileActivity.class));
    }

    private void switchTab(Class<?> target) {
        startActivity(new Intent(this, target));
        finish();
    }
}
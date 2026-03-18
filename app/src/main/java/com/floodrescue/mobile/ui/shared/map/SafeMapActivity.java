package com.floodrescue.mobile.ui.shared.map;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

public class SafeMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_safe_map);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.buttonDirections).setOnClickListener(v ->
                Toast.makeText(this, "Dịch vụ điều hướng bản đồ sẽ được nối ở bước module map.", Toast.LENGTH_SHORT).show());
        findViewById(R.id.buttonCall).setOnClickListener(v ->
                Toast.makeText(this, "Gọi khẩn sẽ được triển khai trong shared/map/emergency.", Toast.LENGTH_SHORT).show());
        findViewById(R.id.navHome).setOnClickListener(v -> AppNavigator.openHome(this));
        findViewById(R.id.navNotification).setOnClickListener(v -> AppNavigator.openNotifications(this));
        findViewById(R.id.navProfile).setOnClickListener(v -> AppNavigator.openProfile(this));
    }
}

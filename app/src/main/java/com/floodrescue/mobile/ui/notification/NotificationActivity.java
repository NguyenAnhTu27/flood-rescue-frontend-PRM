package com.floodrescue.mobile.ui.notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.ui.home.HomeActivity;
import com.floodrescue.mobile.ui.map.SafeMapActivity;
import com.floodrescue.mobile.ui.profile.ProfileActivity;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.navHome).setOnClickListener(v -> switchTab(HomeActivity.class));
        findViewById(R.id.navMap).setOnClickListener(v -> switchTab(SafeMapActivity.class));
        findViewById(R.id.navProfile).setOnClickListener(v -> switchTab(ProfileActivity.class));
    }

    private void switchTab(Class<?> target) {
        startActivity(new Intent(this, target));
        finish();
    }
}
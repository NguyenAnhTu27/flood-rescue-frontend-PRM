package com.floodrescue.mobile.ui.shared.notification;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_notification);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.navHome).setOnClickListener(v -> AppNavigator.openHome(this));
        findViewById(R.id.navMap).setOnClickListener(v -> AppNavigator.openMap(this));
        findViewById(R.id.navProfile).setOnClickListener(v -> AppNavigator.openProfile(this));
    }
}

package com.floodrescue.mobile.ui.home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.shared.navigation.AppNavigator;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(this);
        startActivity(AppNavigator.homeIntent(this, sessionManager.getRole()));
        finish();
    }
}

package com.floodrescue.mobile.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.databinding.ActivitySplashBinding;
import com.floodrescue.mobile.ui.auth.login.LoginActivity;
import com.floodrescue.mobile.ui.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = sessionManager.isLoggedIn()
                    ? new Intent(this, HomeActivity.class)
                    : new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }
}

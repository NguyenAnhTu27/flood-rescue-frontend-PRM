package com.floodrescue.mobile.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.databinding.ActivitySplashBinding;
import com.floodrescue.mobile.ui.onboarding.OnboardingActivity;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable routeRunnable = this::routeNextScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.removeCallbacks(routeRunnable);
        handler.postDelayed(routeRunnable, 2400);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(routeRunnable);
        super.onPause();
    }

    private void routeNextScreen() {
        startActivity(new Intent(this, OnboardingActivity.class));
        finish();
    }
}

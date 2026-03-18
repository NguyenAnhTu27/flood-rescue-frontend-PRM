package com.floodrescue.mobile.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.databinding.ActivityOnboardingBinding;
import com.floodrescue.mobile.ui.auth.setup.InitialSetupActivity;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textSkip.setOnClickListener(view -> openInitialSetup());
        binding.buttonNext.setOnClickListener(view -> openInitialSetup());
    }

    private void openInitialSetup() {
        startActivity(new Intent(this, InitialSetupActivity.class));
    }
}

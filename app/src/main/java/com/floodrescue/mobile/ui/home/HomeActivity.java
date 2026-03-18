package com.floodrescue.mobile.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.databinding.ActivityHomeBinding;
import com.floodrescue.mobile.ui.map.SafeMapActivity;
import com.floodrescue.mobile.ui.notification.NotificationActivity;
import com.floodrescue.mobile.ui.profile.ProfileActivity;
import com.floodrescue.mobile.ui.relief.create.CreateReliefRequestActivity;
import com.floodrescue.mobile.ui.request.detail.RequestDetailActivity;
import com.floodrescue.mobile.ui.rescue.create.CreateRescueRequestActivity;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager sessionManager = new SessionManager(this);
        String fullName = sessionManager.getFullName().trim();
        if (fullName.isEmpty()) {
            fullName = "Nguyễn Văn An";
        }

        binding.textUserName.setText(fullName);
        binding.textAvatar.setText(String.valueOf(fullName.charAt(0)).toUpperCase());

        binding.buttonBell.setOnClickListener(v -> open(NotificationActivity.class));
        binding.cardEmergencyRescue.setOnClickListener(v -> open(CreateRescueRequestActivity.class));
        binding.cardEmergencyRelief.setOnClickListener(v -> open(CreateReliefRequestActivity.class));
        binding.cardAlert.setOnClickListener(v -> open(RequestDetailActivity.class));
        binding.cardQuickMap.setOnClickListener(v -> open(SafeMapActivity.class));
        binding.cardQuickNotification.setOnClickListener(v -> open(NotificationActivity.class));
        binding.cardQuickHistory.setOnClickListener(v -> open(RequestDetailActivity.class));
        binding.cardQuickGuide.setOnClickListener(v ->
                Toast.makeText(this, "Màn cẩm nang sinh tồn sẽ được thiết kế tiếp theo.", Toast.LENGTH_SHORT).show());
        binding.navMap.setOnClickListener(v -> open(SafeMapActivity.class));
        binding.navNotification.setOnClickListener(v -> open(NotificationActivity.class));
        binding.navProfile.setOnClickListener(v -> open(ProfileActivity.class));
    }

    private void open(Class<?> target) {
        startActivity(new Intent(this, target));
    }
}
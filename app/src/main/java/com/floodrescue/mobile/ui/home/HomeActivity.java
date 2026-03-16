package com.floodrescue.mobile.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.util.Resource;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.databinding.ActivityHomeBinding;
import com.floodrescue.mobile.ui.auth.login.LoginActivity;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        SessionManager sessionManager = new SessionManager(this);

        binding.textWelcome.setText(sessionManager.getFullName().isEmpty()
                ? getString(R.string.home_title)
                : getString(R.string.home_welcome, sessionManager.getFullName()));
        binding.textRole.setText(getString(R.string.home_role, sessionManager.getRole()));

        binding.buttonLogout.setOnClickListener(view -> {
            sessionManager.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        binding.buttonCitizen.setOnClickListener(view ->
                Toast.makeText(this, getString(R.string.placeholder_citizen), Toast.LENGTH_SHORT).show());
        binding.buttonCoordinator.setOnClickListener(view ->
                Toast.makeText(this, getString(R.string.placeholder_coordinator), Toast.LENGTH_SHORT).show());
        binding.buttonChat.setOnClickListener(view ->
                Toast.makeText(this, getString(R.string.placeholder_chat), Toast.LENGTH_SHORT).show());

        binding.swipeRefresh.setOnRefreshListener(viewModel::loadProfile);

        observeViewModel();
        viewModel.loadProfile();
    }

    private void observeViewModel() {
        viewModel.getProfileState().observe(this, resource -> {
            if (resource == null) {
                return;
            }

            if (resource.getStatus() == Resource.Status.LOADING) {
                binding.swipeRefresh.setRefreshing(true);
                return;
            }

            binding.swipeRefresh.setRefreshing(false);

            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                binding.textProfileName.setText(resource.getData().getFullName());
                binding.textProfilePhone.setText(resource.getData().getPhone());
                binding.textProfileEmail.setText(resource.getData().getEmail() == null || resource.getData().getEmail().isEmpty()
                        ? getString(R.string.not_available)
                        : resource.getData().getEmail());
                return;
            }

            if (resource.getStatus() == Resource.Status.ERROR) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

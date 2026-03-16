package com.floodrescue.mobile.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.util.Resource;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.databinding.ActivityLoginBinding;
import com.floodrescue.mobile.ui.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.buttonLogin.setOnClickListener(view -> submitLogin());

        viewModel.getLoginState().observe(this, resource -> {
            if (resource == null) {
                return;
            }
            if (resource.getStatus() == Resource.Status.LOADING) {
                binding.buttonLogin.setEnabled(false);
                binding.buttonLogin.setText(R.string.login_loading);
                return;
            }

            binding.buttonLogin.setEnabled(true);
            binding.buttonLogin.setText(R.string.login_button);

            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                new SessionManager(this).saveLoginSession(resource.getData());
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return;
            }

            if (resource.getStatus() == Resource.Status.ERROR) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submitLogin() {
        String identifier = binding.editIdentifier.getText() == null
                ? ""
                : binding.editIdentifier.getText().toString().trim();
        String password = binding.editPassword.getText() == null
                ? ""
                : binding.editPassword.getText().toString().trim();

        if (identifier.isEmpty()) {
            binding.layoutIdentifier.setError(getString(R.string.login_identifier_error));
            return;
        }
        binding.layoutIdentifier.setError(null);

        if (password.isEmpty()) {
            binding.layoutPassword.setError(getString(R.string.login_password_error));
            return;
        }
        binding.layoutPassword.setError(null);

        viewModel.login(identifier, password);
    }
}

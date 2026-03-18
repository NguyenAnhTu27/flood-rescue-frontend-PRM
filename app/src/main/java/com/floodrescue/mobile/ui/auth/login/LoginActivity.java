package com.floodrescue.mobile.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.util.Resource;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.auth.register.RegisterCitizenActivity;
import com.floodrescue.mobile.ui.home.HomeActivity;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private TextInputLayout layoutIdentifier;
    private TextInputLayout layoutPassword;
    private EditText editIdentifier;
    private EditText editPassword;
    private TextView buttonLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        layoutIdentifier = findViewById(R.id.layoutIdentifier);
        layoutPassword = findViewById(R.id.layoutPassword);
        editIdentifier = findViewById(R.id.editIdentifier);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        findViewById(R.id.buttonBack).setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.textForgotPassword).setOnClickListener(view ->
                Toast.makeText(this, "Chức năng hỗ trợ đăng nhập sẽ được bổ sung sau.", Toast.LENGTH_SHORT).show());
        findViewById(R.id.buttonBiometric).setOnClickListener(view ->
                Toast.makeText(this, getString(R.string.login_biometric_coming_soon), Toast.LENGTH_SHORT).show());
        findViewById(R.id.textRegister).setOnClickListener(view ->
                startActivity(new Intent(this, RegisterCitizenActivity.class)));
        buttonLogin.setOnClickListener(view -> submitLogin());

        viewModel.getLoginState().observe(this, resource -> {
            if (resource == null) {
                return;
            }

            if (resource.getStatus() == Resource.Status.LOADING) {
                buttonLogin.setEnabled(false);
                buttonLogin.setText("Đang đăng nhập...");
                return;
            }

            buttonLogin.setEnabled(true);
            buttonLogin.setText("Đăng nhập");

            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                new SessionManager(this).saveLoginSession(resource.getData());
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
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
        String identifier = editIdentifier.getText() == null ? "" : editIdentifier.getText().toString().trim();
        String password = editPassword.getText() == null ? "" : editPassword.getText().toString().trim();

        layoutIdentifier.setError(null);
        layoutPassword.setError(null);

        if (TextUtils.isEmpty(identifier)) {
            layoutIdentifier.setError("Vui lòng nhập email hoặc số điện thoại");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            layoutPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }

        viewModel.login(identifier, password);
    }
}

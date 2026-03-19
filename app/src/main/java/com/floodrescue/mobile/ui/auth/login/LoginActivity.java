package com.floodrescue.mobile.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.util.Resource;
import com.floodrescue.mobile.data.local.SessionManager;
import com.floodrescue.mobile.ui.auth.publicpage.privacy.PrivacyPolicyActivity;
import com.floodrescue.mobile.ui.auth.register.RegisterCitizenActivity;
import com.floodrescue.mobile.ui.auth.publicpage.support.SupportContactActivity;
import com.floodrescue.mobile.ui.auth.publicpage.terms.TermsOfUseActivity;
import com.floodrescue.mobile.ui.home.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private TextInputLayout layoutIdentifier;
    private TextInputLayout layoutPassword;
    private EditText editIdentifier;
    private EditText editPassword;
    private TextView buttonLogin;
    private MaterialButton buttonRegisterCitizen;
    private View layoutLoginError;
    private TextView textLoginError;

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
        buttonRegisterCitizen = findViewById(R.id.buttonRegisterCitizen);
        layoutLoginError = findViewById(R.id.layoutLoginError);
        textLoginError = findViewById(R.id.textLoginError);

        buttonLogin.setOnClickListener(view -> submitLogin());
        buttonRegisterCitizen.setOnClickListener(view ->
                startActivity(new Intent(this, RegisterCitizenActivity.class)));
        findViewById(R.id.textTerms).setOnClickListener(view ->
                startActivity(TermsOfUseActivity.newIntent(this, TermsOfUseActivity.SOURCE_LOGIN)));
        findViewById(R.id.textPrivacy).setOnClickListener(view ->
                startActivity(PrivacyPolicyActivity.newIntent(this, TermsOfUseActivity.SOURCE_LOGIN)));
        findViewById(R.id.textSupport).setOnClickListener(view ->
                startActivity(SupportContactActivity.newIntent(this, TermsOfUseActivity.SOURCE_LOGIN)));

        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutIdentifier.setError(null);
                layoutPassword.setError(null);
                hideLoginError();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        editIdentifier.addTextChangedListener(clearErrorWatcher);
        editPassword.addTextChangedListener(clearErrorWatcher);

        viewModel.getLoginState().observe(this, resource -> {
            if (resource == null) {
                return;
            }

            if (resource.getStatus() == Resource.Status.LOADING) {
                setLoadingState(true);
                return;
            }

            setLoadingState(false);

            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                hideLoginError();
                new SessionManager(this).saveLoginSession(resource.getData());
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return;
            }

            if (resource.getStatus() == Resource.Status.ERROR) {
                showLoginError(resource.getMessage());
            }
        });
    }

    private void submitLogin() {
        String identifier = editIdentifier.getText() == null ? "" : editIdentifier.getText().toString().trim();
        String password = editPassword.getText() == null ? "" : editPassword.getText().toString().trim();

        layoutIdentifier.setError(null);
        layoutPassword.setError(null);
        hideLoginError();

        if (TextUtils.isEmpty(identifier)) {
            layoutIdentifier.setError(getString(R.string.login_identifier_error));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            layoutPassword.setError(getString(R.string.login_password_error));
            return;
        }

        viewModel.login(identifier, password);
    }

    private void setLoadingState(boolean loading) {
        buttonLogin.setEnabled(!loading);
        buttonLogin.setAlpha(loading ? 0.72f : 1f);
        buttonLogin.setText(loading ? getString(R.string.login_loading) : getString(R.string.login_button));
        buttonRegisterCitizen.setEnabled(!loading);
        buttonRegisterCitizen.setAlpha(loading ? 0.72f : 1f);
    }

    private void showLoginError(String message) {
        String fallback = "Tài khoản hoặc mật khẩu không chính xác";
        textLoginError.setText(message == null || message.trim().isEmpty() ? fallback : message.trim());
        layoutLoginError.setVisibility(View.VISIBLE);
    }

    private void hideLoginError() {
        layoutLoginError.setVisibility(View.GONE);
    }
}

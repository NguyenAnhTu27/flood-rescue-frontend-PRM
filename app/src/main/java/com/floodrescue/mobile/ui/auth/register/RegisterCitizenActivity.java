package com.floodrescue.mobile.ui.auth.register;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.ui.auth.publicpage.privacy.PrivacyPolicyActivity;
import com.floodrescue.mobile.ui.auth.publicpage.support.SupportContactActivity;
import com.floodrescue.mobile.ui.auth.publicpage.terms.TermsOfUseActivity;
import com.floodrescue.mobile.data.model.response.ApiMessageResponse;
import com.floodrescue.mobile.data.repository.AuthRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterCitizenActivity extends AppCompatActivity {

    private static final String VIETNAM_PHONE_PATTERN =
            "^(\\+84|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-6|8|9]|9[0-4|6-9])[0-9]{7,8}$";
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,72}$";

    private TextInputLayout layoutFullName;
    private TextInputLayout layoutPhone;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private EditText editFullName;
    private EditText editPhone;
    private EditText editEmail;
    private EditText editPassword;
    private TextView buttonRegisterCitizen;
    private View layoutRegisterError;
    private TextView textRegisterError;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_citizen);

        authRepository = new AuthRepository(getApplicationContext());
        layoutFullName = findViewById(R.id.layoutFullName);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        editFullName = findViewById(R.id.editFullName);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonRegisterCitizen = findViewById(R.id.buttonRegisterCitizen);
        layoutRegisterError = findViewById(R.id.layoutRegisterError);
        textRegisterError = findViewById(R.id.textRegisterError);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.textGoLogin).setOnClickListener(v -> finish());
        findViewById(R.id.textTerms).setOnClickListener(v ->
                startActivity(TermsOfUseActivity.newIntent(this, TermsOfUseActivity.SOURCE_REGISTER)));
        findViewById(R.id.textPrivacy).setOnClickListener(v ->
                startActivity(PrivacyPolicyActivity.newIntent(this, TermsOfUseActivity.SOURCE_REGISTER)));
        findViewById(R.id.textSupport).setOnClickListener(v ->
                startActivity(SupportContactActivity.newIntent(this, TermsOfUseActivity.SOURCE_REGISTER)));
        buttonRegisterCitizen.setOnClickListener(v -> submitRegister());

        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearErrors();
                hideRegisterError();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        editFullName.addTextChangedListener(clearErrorWatcher);
        editPhone.addTextChangedListener(clearErrorWatcher);
        editEmail.addTextChangedListener(clearErrorWatcher);
        editPassword.addTextChangedListener(clearErrorWatcher);
    }

    private void submitRegister() {
        String fullName = textOf(editFullName);
        String phone = normalizePhone(textOf(editPhone));
        String email = textOf(editEmail);
        String password = textOf(editPassword);

        clearErrors();
        hideRegisterError();

        if (TextUtils.isEmpty(fullName)) {
            layoutFullName.setError(getString(R.string.register_fullname_error));
            return;
        }
        if (fullName.length() > 120) {
            layoutFullName.setError("Họ và tên không được vượt quá 120 ký tự");
            return;
        }
        if (TextUtils.isEmpty(phone) || !phone.matches(VIETNAM_PHONE_PATTERN)) {
            layoutPhone.setError(getString(R.string.register_phone_error));
            return;
        }
        if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError(getString(R.string.register_email_error));
            return;
        }
        if (!password.matches(PASSWORD_PATTERN)) {
            layoutPassword.setError(getString(R.string.register_password_error));
            return;
        }

        setLoadingState(true);
        authRepository.registerCitizen(fullName, phone, email, password, new RepositoryCallback<ApiMessageResponse>() {
            @Override
            public void onSuccess(ApiMessageResponse data) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    Toast.makeText(RegisterCitizenActivity.this,
                            data == null || data.getMessage().isEmpty()
                                    ? getString(R.string.register_success)
                                    : data.getMessage(),
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    showRegisterError(message);
                });
            }
        });
    }

    private void setLoadingState(boolean loading) {
        buttonRegisterCitizen.setEnabled(!loading);
        buttonRegisterCitizen.setAlpha(loading ? 0.72f : 1f);
        buttonRegisterCitizen.setText(loading ? getString(R.string.register_loading) : getString(R.string.register_button));
    }

    private void showRegisterError(String message) {
        String fallback = "Không thể tạo tài khoản. Vui lòng kiểm tra lại thông tin.";
        textRegisterError.setText(message == null || message.trim().isEmpty() ? fallback : message.trim());
        layoutRegisterError.setVisibility(View.VISIBLE);
    }

    private void hideRegisterError() {
        layoutRegisterError.setVisibility(View.GONE);
    }

    private void clearErrors() {
        layoutFullName.setError(null);
        layoutPhone.setError(null);
        layoutEmail.setError(null);
        layoutPassword.setError(null);
    }

    private String textOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "";
        }
        return phone.replaceAll("[\\s\\-\\(\\)\\.]", "");
    }
}

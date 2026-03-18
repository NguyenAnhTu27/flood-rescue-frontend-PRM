package com.floodrescue.mobile.ui.auth.register;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.data.model.response.ApiMessageResponse;
import com.floodrescue.mobile.data.repository.AuthRepository;
import com.floodrescue.mobile.data.repository.RepositoryCallback;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterCitizenActivity extends AppCompatActivity {

    private TextInputLayout layoutFullName;
    private TextInputLayout layoutPhone;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutConfirmPassword;
    private EditText editFullName;
    private EditText editPhone;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private CheckBox checkPolicy;
    private TextView buttonRegisterCitizen;
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
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
        editFullName = findViewById(R.id.editFullName);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        checkPolicy = findViewById(R.id.checkPolicy);
        buttonRegisterCitizen = findViewById(R.id.buttonRegisterCitizen);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.textGoLogin).setOnClickListener(v -> finish());
        buttonRegisterCitizen.setOnClickListener(v -> submitRegister());
    }

    private void submitRegister() {
        String fullName = textOf(editFullName);
        String phone = textOf(editPhone);
        String email = textOf(editEmail);
        String password = textOf(editPassword);
        String confirmPassword = textOf(editConfirmPassword);

        clearErrors();

        if (TextUtils.isEmpty(fullName)) {
            layoutFullName.setError("Vui lòng nhập họ và tên");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            layoutPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            layoutPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }
        if (password.length() < 6) {
            layoutPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        if (!password.equals(confirmPassword)) {
            layoutConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }
        if (!checkPolicy.isChecked()) {
            Toast.makeText(this, "Bạn cần đồng ý điều khoản trước khi đăng ký.", Toast.LENGTH_SHORT).show();
            return;
        }

        buttonRegisterCitizen.setEnabled(false);
        buttonRegisterCitizen.setText("Đang đăng ký...");

        authRepository.registerCitizen(fullName, phone, email, password, new RepositoryCallback<ApiMessageResponse>() {
            @Override
            public void onSuccess(ApiMessageResponse data) {
                runOnUiThread(() -> {
                    buttonRegisterCitizen.setEnabled(true);
                    buttonRegisterCitizen.setText("Đăng ký tài khoản");
                    Toast.makeText(RegisterCitizenActivity.this,
                            data == null ? "Đăng ký thành công" : data.getMessage(),
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    buttonRegisterCitizen.setEnabled(true);
                    buttonRegisterCitizen.setText("Đăng ký tài khoản");
                    Toast.makeText(RegisterCitizenActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void clearErrors() {
        layoutFullName.setError(null);
        layoutPhone.setError(null);
        layoutEmail.setError(null);
        layoutPassword.setError(null);
        layoutConfirmPassword.setError(null);
    }

    private String textOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}

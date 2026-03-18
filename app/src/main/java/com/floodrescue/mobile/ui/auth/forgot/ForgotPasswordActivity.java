package com.floodrescue.mobile.ui.auth.forgot;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.textLoginNow).setOnClickListener(v -> finish());
        findViewById(R.id.buttonSendOtp).setOnClickListener(v ->
                Toast.makeText(this, "Đã gửi OTP demo. Bước xác thực sẽ nối tiếp sau.", Toast.LENGTH_SHORT).show());
    }
}
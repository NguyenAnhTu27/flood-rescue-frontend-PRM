package com.floodrescue.mobile.ui.auth.register;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;

public class RegisterCitizenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_citizen);

        CheckBox checkPolicy = findViewById(R.id.checkPolicy);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.textGoLogin).setOnClickListener(v -> finish());
        findViewById(R.id.buttonRegisterCitizen).setOnClickListener(v -> {
            if (!checkPolicy.isChecked()) {
                Toast.makeText(this, "Bạn cần đồng ý điều khoản trước khi đăng ký.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Giao diện đăng ký đã sẵn sàng. Mình sẽ nối API ở bước tiếp theo.", Toast.LENGTH_SHORT).show();
        });
    }
}
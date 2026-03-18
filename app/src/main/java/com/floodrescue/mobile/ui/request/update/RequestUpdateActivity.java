package com.floodrescue.mobile.ui.request.update;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;

public class RequestUpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_update);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.buttonUpdateRequest).setOnClickListener(v ->
                Toast.makeText(this, "Đã lưu thay đổi giao diện demo.", Toast.LENGTH_SHORT).show());
        findViewById(R.id.buttonCancelRequest).setOnClickListener(v ->
                Toast.makeText(this, "Tính năng hủy yêu cầu sẽ được nối API sau.", Toast.LENGTH_SHORT).show());
    }
}
package com.floodrescue.mobile.ui.chat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;

public class DispatchChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_chat);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.buttonCall).setOnClickListener(v ->
                Toast.makeText(this, "Tính năng gọi sẽ được hoàn thiện sau.", Toast.LENGTH_SHORT).show());
        findViewById(R.id.buttonSend).setOnClickListener(v ->
                Toast.makeText(this, "Giao diện chat đã sẵn sàng để nối API tin nhắn.", Toast.LENGTH_SHORT).show());
    }
}
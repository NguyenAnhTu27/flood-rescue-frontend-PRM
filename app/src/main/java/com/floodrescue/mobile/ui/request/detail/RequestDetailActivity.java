package com.floodrescue.mobile.ui.request.detail;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.ui.chat.DispatchChatActivity;
import com.floodrescue.mobile.ui.request.update.RequestUpdateActivity;

public class RequestDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.buttonMenu).setOnClickListener(v ->
                startActivity(new Intent(this, RequestUpdateActivity.class)));
        findViewById(R.id.buttonOpenUpdate).setOnClickListener(v ->
                startActivity(new Intent(this, RequestUpdateActivity.class)));
        findViewById(R.id.buttonOpenChat).setOnClickListener(v ->
                startActivity(new Intent(this, DispatchChatActivity.class)));
    }
}
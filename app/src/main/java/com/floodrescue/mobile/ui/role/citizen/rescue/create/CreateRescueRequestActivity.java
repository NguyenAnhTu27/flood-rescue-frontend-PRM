package com.floodrescue.mobile.ui.role.citizen.rescue.create;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.ui.role.citizen.rescue.detail.CitizenRescueDetailActivity;

public class CreateRescueRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_citizen_create_rescue_request);

        findViewById(R.id.buttonBack).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.buttonSubmitRescue).setOnClickListener(v ->
                startActivity(new Intent(this, CitizenRescueDetailActivity.class)));
    }
}

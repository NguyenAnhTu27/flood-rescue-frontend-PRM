package com.floodrescue.mobile.ui.auth.setup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.databinding.ActivityInitialSetupBinding;
import com.floodrescue.mobile.ui.auth.login.LoginActivity;

public class InitialSetupActivity extends AppCompatActivity {

    private ActivityInitialSetupBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInitialSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupDropdowns();
        binding.buttonStart.setOnClickListener(view -> openLogin());
        binding.buttonBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupDropdowns() {
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.setup_languages, android.R.layout.simple_list_item_1);
        binding.inputLanguage.setAdapter(languageAdapter);
        binding.inputLanguage.setText(getString(R.string.setup_default_language), false);

        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,
                R.array.setup_cities, android.R.layout.simple_list_item_1);
        binding.inputCity.setAdapter(cityAdapter);

        ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(this,
                R.array.setup_districts, android.R.layout.simple_list_item_1);
        binding.inputDistrict.setAdapter(districtAdapter);

        ArrayAdapter<CharSequence> wardAdapter = ArrayAdapter.createFromResource(this,
                R.array.setup_wards, android.R.layout.simple_list_item_1);
        binding.inputWard.setAdapter(wardAdapter);
    }

    private void openLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}

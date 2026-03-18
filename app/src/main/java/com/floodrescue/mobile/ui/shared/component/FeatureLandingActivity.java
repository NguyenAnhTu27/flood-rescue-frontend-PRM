package com.floodrescue.mobile.ui.shared.component;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.R;
import com.floodrescue.mobile.core.base.BaseActivity;
import com.google.android.material.button.MaterialButton;

/**
 * Shared scaffold for role-based feature screens while modules are being migrated
 * from generic placeholders into dedicated packages.
 */
public abstract class FeatureLandingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_landing);

        bindBackButton(R.id.buttonBack);
        ((TextView) findViewById(R.id.textFeatureBadge)).setText(featureBadge());
        ((TextView) findViewById(R.id.textFeatureTitle)).setText(featureTitle());
        ((TextView) findViewById(R.id.textFeaturePath)).setText(featurePath());
        ((TextView) findViewById(R.id.textFeatureSummary)).setText(featureSummary());

        MaterialButton primaryButton = findViewById(R.id.buttonPrimary);
        if (primaryButton != null) {
            String label = primaryActionLabel();
            if (label == null || label.trim().isEmpty()) {
                primaryButton.setVisibility(View.GONE);
            } else {
                primaryButton.setText(label);
                primaryButton.setOnClickListener(v -> onPrimaryAction());
            }
        }

        MaterialButton homeButton = findViewById(R.id.buttonOpenDashboard);
        if (homeButton != null) {
            homeButton.setOnClickListener(v -> startActivity(new Intent(this, dashboardActivityClass())));
        }
    }

    protected abstract String featureBadge();

    protected abstract String featureTitle();

    protected abstract String featurePath();

    protected abstract String featureSummary();

    protected abstract Class<? extends AppCompatActivity> dashboardActivityClass();

    protected String primaryActionLabel() {
        return null;
    }

    protected void onPrimaryAction() {
        showShortToast("Màn hình đang được hoàn thiện theo module mới.");
    }
}

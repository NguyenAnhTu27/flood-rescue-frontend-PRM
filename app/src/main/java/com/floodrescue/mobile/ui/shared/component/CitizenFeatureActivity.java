package com.floodrescue.mobile.ui.shared.component;

import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.ui.role.citizen.dashboard.CitizenDashboardActivity;

public abstract class CitizenFeatureActivity extends FeatureLandingActivity {

    @Override
    protected String featureBadge() {
        return "CITIZEN";
    }

    @Override
    protected Class<? extends AppCompatActivity> dashboardActivityClass() {
        return CitizenDashboardActivity.class;
    }
}

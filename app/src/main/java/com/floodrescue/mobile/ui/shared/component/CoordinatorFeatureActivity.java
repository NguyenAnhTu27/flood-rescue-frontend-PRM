package com.floodrescue.mobile.ui.shared.component;

import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.ui.role.coordinator.dashboard.CoordinatorDashboardActivity;

public abstract class CoordinatorFeatureActivity extends FeatureLandingActivity {

    @Override
    protected String featureBadge() {
        return "COORDINATOR";
    }

    @Override
    protected Class<? extends AppCompatActivity> dashboardActivityClass() {
        return CoordinatorDashboardActivity.class;
    }
}

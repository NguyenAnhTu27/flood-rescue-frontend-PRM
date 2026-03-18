package com.floodrescue.mobile.ui.shared.component;

import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.ui.role.manager.dashboard.ManagerDashboardActivity;

public abstract class ManagerFeatureActivity extends FeatureLandingActivity {

    @Override
    protected String featureBadge() {
        return "MANAGER";
    }

    @Override
    protected Class<? extends AppCompatActivity> dashboardActivityClass() {
        return ManagerDashboardActivity.class;
    }
}

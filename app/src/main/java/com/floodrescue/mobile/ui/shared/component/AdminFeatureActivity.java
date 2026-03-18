package com.floodrescue.mobile.ui.shared.component;

import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.ui.role.admin.dashboard.AdminDashboardActivity;

public abstract class AdminFeatureActivity extends FeatureLandingActivity {

    @Override
    protected String featureBadge() {
        return "ADMIN";
    }

    @Override
    protected Class<? extends AppCompatActivity> dashboardActivityClass() {
        return AdminDashboardActivity.class;
    }
}

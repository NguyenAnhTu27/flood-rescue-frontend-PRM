package com.floodrescue.mobile.ui.shared.component;

import androidx.appcompat.app.AppCompatActivity;

import com.floodrescue.mobile.ui.role.rescuer.dashboard.RescuerDashboardActivity;

public abstract class RescuerFeatureActivity extends FeatureLandingActivity {

    @Override
    protected String featureBadge() {
        return "RESCUER";
    }

    @Override
    protected Class<? extends AppCompatActivity> dashboardActivityClass() {
        return RescuerDashboardActivity.class;
    }
}

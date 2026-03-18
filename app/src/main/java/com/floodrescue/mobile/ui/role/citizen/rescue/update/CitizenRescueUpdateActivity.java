package com.floodrescue.mobile.ui.role.citizen.rescue.update;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.citizen.feedback.CitizenFeedbackActivity;
import com.floodrescue.mobile.ui.shared.component.CitizenFeatureActivity;

public class CitizenRescueUpdateActivity extends CitizenFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Cap nhat yeu cau cuu ho";
    }

    @Override
    protected String featurePath() {
        return "ui/role/citizen/rescue/update";
    }

    @Override
    protected String featureSummary() {
        return "Man cap nhat theo dung package moi cho citizen rescue update, giup tach logic form khoi man chi tiet cu.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Gui phan hoi";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, CitizenFeedbackActivity.class));
    }
}

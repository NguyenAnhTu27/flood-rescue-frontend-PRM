package com.floodrescue.mobile.ui.role.citizen.rescue.detail;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.citizen.rescue.update.CitizenRescueUpdateActivity;
import com.floodrescue.mobile.ui.shared.component.CitizenFeatureActivity;

public class CitizenRescueDetailActivity extends CitizenFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Chi tiet yeu cau cuu ho";
    }

    @Override
    protected String featurePath() {
        return "ui/role/citizen/rescue/detail";
    }

    @Override
    protected String featureSummary() {
        return "Man chi tiet thay the shared/request/detail de theo doi trang thai, timeline va thao tac cua citizen.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Chinh sua yeu cau";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, CitizenRescueUpdateActivity.class));
    }
}

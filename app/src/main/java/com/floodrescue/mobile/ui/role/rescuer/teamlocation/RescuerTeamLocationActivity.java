package com.floodrescue.mobile.ui.role.rescuer.teamlocation;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.rescuer.relief.list.RescuerReliefListActivity;
import com.floodrescue.mobile.ui.shared.component.RescuerFeatureActivity;

public class RescuerTeamLocationActivity extends RescuerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Cap nhat vi tri doi";
    }

    @Override
    protected String featurePath() {
        return "ui/role/rescuer/teamlocation";
    }

    @Override
    protected String featureSummary() {
        return "Man cap nhat vi tri va tai san doi cuu ho, tach rieng theo role de sau nay noi geolocation va asset return.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo cuu tro duoc giao";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, RescuerReliefListActivity.class));
    }
}

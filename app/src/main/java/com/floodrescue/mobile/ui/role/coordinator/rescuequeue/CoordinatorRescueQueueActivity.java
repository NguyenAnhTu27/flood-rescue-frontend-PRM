package com.floodrescue.mobile.ui.role.coordinator.rescuequeue;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.coordinator.rescuedetail.CoordinatorRescueDetailActivity;
import com.floodrescue.mobile.ui.shared.component.CoordinatorFeatureActivity;

public class CoordinatorRescueQueueActivity extends CoordinatorFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Hang cho cuu ho";
    }

    @Override
    protected String featurePath() {
        return "ui/role/coordinator/rescuequeue";
    }

    @Override
    protected String featureSummary() {
        return "Man queue moi cho dieu phoi, thay the cach mo truc tiep vao request detail generic tu dashboard cu.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo yeu cau can xu ly";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, CoordinatorRescueDetailActivity.class));
    }
}

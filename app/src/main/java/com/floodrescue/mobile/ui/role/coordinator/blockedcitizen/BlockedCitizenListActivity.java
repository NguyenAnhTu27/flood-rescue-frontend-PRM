package com.floodrescue.mobile.ui.role.coordinator.blockedcitizen;

import com.floodrescue.mobile.ui.shared.component.CoordinatorFeatureActivity;

public class BlockedCitizenListActivity extends CoordinatorFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Cong dan bi chan";
    }

    @Override
    protected String featurePath() {
        return "ui/role/coordinator/blockedcitizen";
    }

    @Override
    protected String featureSummary() {
        return "Man theo doi danh sach cong dan bi chan duoc dat dung package coordinator/blockedcitizen de de bao tri.";
    }
}

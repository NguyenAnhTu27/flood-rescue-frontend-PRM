package com.floodrescue.mobile.ui.role.rescuer.relief.detail;

import com.floodrescue.mobile.ui.shared.component.RescuerFeatureActivity;

public class RescuerReliefDetailActivity extends RescuerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Chi tiet giao cuu tro";
    }

    @Override
    protected String featurePath() {
        return "ui/role/rescuer/relief/detail";
    }

    @Override
    protected String featureSummary() {
        return "Man detail giao cuu tro cho rescuer, dung lam diem noi cho delivery status va thong tin diem giao.";
    }
}

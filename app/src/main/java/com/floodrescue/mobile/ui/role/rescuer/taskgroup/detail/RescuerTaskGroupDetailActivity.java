package com.floodrescue.mobile.ui.role.rescuer.taskgroup.detail;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.rescuer.teamlocation.RescuerTeamLocationActivity;
import com.floodrescue.mobile.ui.shared.component.RescuerFeatureActivity;

public class RescuerTaskGroupDetailActivity extends RescuerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Chi tiet nhom nhiem vu";
    }

    @Override
    protected String featurePath() {
        return "ui/role/rescuer/taskgroup/detail";
    }

    @Override
    protected String featureSummary() {
        return "Man detail task group cho doi cuu ho, dat dung package de noi danh sach request, assignment va escalation.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Cap nhat vi tri doi";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, RescuerTeamLocationActivity.class));
    }
}

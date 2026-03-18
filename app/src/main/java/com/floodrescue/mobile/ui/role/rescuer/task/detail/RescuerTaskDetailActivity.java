package com.floodrescue.mobile.ui.role.rescuer.task.detail;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.rescuer.taskgroup.list.RescuerTaskGroupListActivity;
import com.floodrescue.mobile.ui.shared.component.RescuerFeatureActivity;

public class RescuerTaskDetailActivity extends RescuerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Chi tiet nhiem vu";
    }

    @Override
    protected String featurePath() {
        return "ui/role/rescuer/task/detail";
    }

    @Override
    protected String featureSummary() {
        return "Man detail nhiem vu cho rescuer, dat dung package moi de sau nay tach action cap nhat trang thai va ghi chu.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo nhom nhiem vu";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, RescuerTaskGroupListActivity.class));
    }
}

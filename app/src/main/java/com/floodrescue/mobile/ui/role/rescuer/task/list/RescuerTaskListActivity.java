package com.floodrescue.mobile.ui.role.rescuer.task.list;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.rescuer.task.detail.RescuerTaskDetailActivity;
import com.floodrescue.mobile.ui.shared.component.RescuerFeatureActivity;

public class RescuerTaskListActivity extends RescuerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Danh sach nhiem vu cuu ho";
    }

    @Override
    protected String featurePath() {
        return "ui/role/rescuer/task/list";
    }

    @Override
    protected String featureSummary() {
        return "Man danh sach nhiem vu cho doi cuu ho, thay the cach mo detail generic tu dashboard cu.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo nhiem vu mau";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, RescuerTaskDetailActivity.class));
    }
}

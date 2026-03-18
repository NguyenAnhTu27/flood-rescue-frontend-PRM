package com.floodrescue.mobile.ui.role.rescuer.taskgroup.list;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.rescuer.taskgroup.detail.RescuerTaskGroupDetailActivity;
import com.floodrescue.mobile.ui.shared.component.RescuerFeatureActivity;

public class RescuerTaskGroupListActivity extends RescuerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Nhom nhiem vu cua doi";
    }

    @Override
    protected String featurePath() {
        return "ui/role/rescuer/taskgroup/list";
    }

    @Override
    protected String featureSummary() {
        return "Man tong hop task group cua doi cuu ho, giup gom nhiem vu va theo doi trang thai theo package rieng.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Xem chi tiet nhom";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, RescuerTaskGroupDetailActivity.class));
    }
}

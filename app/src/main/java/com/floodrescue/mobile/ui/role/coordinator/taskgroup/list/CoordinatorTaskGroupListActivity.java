package com.floodrescue.mobile.ui.role.coordinator.taskgroup.list;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.coordinator.taskgroup.create.CoordinatorTaskGroupCreateActivity;
import com.floodrescue.mobile.ui.shared.component.CoordinatorFeatureActivity;

public class CoordinatorTaskGroupListActivity extends CoordinatorFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Danh sach nhom nhiem vu";
    }

    @Override
    protected String featurePath() {
        return "ui/role/coordinator/taskgroup/list";
    }

    @Override
    protected String featureSummary() {
        return "Man tong hop task group cho coordinator, tach rieng de gom queue, phan cong doi va timeline xu ly.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Tao nhom moi";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, CoordinatorTaskGroupCreateActivity.class));
    }
}

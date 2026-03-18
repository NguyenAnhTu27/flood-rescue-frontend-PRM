package com.floodrescue.mobile.ui.role.coordinator.taskgroup.create;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.coordinator.taskgroup.detail.CoordinatorTaskGroupDetailActivity;
import com.floodrescue.mobile.ui.shared.component.CoordinatorFeatureActivity;

public class CoordinatorTaskGroupCreateActivity extends CoordinatorFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Tao nhom nhiem vu";
    }

    @Override
    protected String featurePath() {
        return "ui/role/coordinator/taskgroup/create";
    }

    @Override
    protected String featureSummary() {
        return "Man tao task group cho coordinator, dung lam diem dat form va workflow phan cong trong cau truc moi.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Xem chi tiet nhom";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, CoordinatorTaskGroupDetailActivity.class));
    }
}

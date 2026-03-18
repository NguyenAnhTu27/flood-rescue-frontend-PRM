package com.floodrescue.mobile.ui.role.coordinator.rescuedetail;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.coordinator.taskgroup.list.CoordinatorTaskGroupListActivity;
import com.floodrescue.mobile.ui.shared.component.CoordinatorFeatureActivity;

public class CoordinatorRescueDetailActivity extends CoordinatorFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Chi tiet yeu cau dieu phoi";
    }

    @Override
    protected String featurePath() {
        return "ui/role/coordinator/rescuedetail";
    }

    @Override
    protected String featureSummary() {
        return "Man chi tiet cuu ho cho coordinator, tach rieng de xu ly verify, note, status va duplicate theo role.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo nhom nhiem vu";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, CoordinatorTaskGroupListActivity.class));
    }
}

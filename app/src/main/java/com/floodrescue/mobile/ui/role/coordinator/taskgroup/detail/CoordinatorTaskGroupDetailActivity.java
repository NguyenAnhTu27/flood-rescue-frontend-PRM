package com.floodrescue.mobile.ui.role.coordinator.taskgroup.detail;

import com.floodrescue.mobile.ui.shared.component.CoordinatorFeatureActivity;

public class CoordinatorTaskGroupDetailActivity extends CoordinatorFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Chi tiet nhom nhiem vu";
    }

    @Override
    protected String featurePath() {
        return "ui/role/coordinator/taskgroup/detail";
    }

    @Override
    protected String featureSummary() {
        return "Man detail task group thay the diem vao generic, de sau nay noi assignment, status va lich su xu ly.";
    }
}

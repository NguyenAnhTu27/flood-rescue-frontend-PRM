package com.floodrescue.mobile.ui.role.admin.team;

import com.floodrescue.mobile.ui.shared.component.AdminFeatureActivity;

public class AdminTeamActivity extends AdminFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Quan ly doi cuu ho";
    }

    @Override
    protected String featurePath() {
        return "ui/role/admin/team";
    }

    @Override
    protected String featureSummary() {
        return "Man quan ly doi cuu ho cua admin, tach rieng de sau nay noi leader, member candidates va workload.";
    }
}

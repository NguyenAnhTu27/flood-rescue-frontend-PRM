package com.floodrescue.mobile.ui.role.admin.permission;

import com.floodrescue.mobile.ui.shared.component.AdminFeatureActivity;

public class AdminPermissionActivity extends AdminFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Phan quyen he thong";
    }

    @Override
    protected String featurePath() {
        return "ui/role/admin/permission";
    }

    @Override
    protected String featureSummary() {
        return "Man phan quyen cho admin, dat dung package rieng de sau nay noi role, permission va matrix UI.";
    }
}

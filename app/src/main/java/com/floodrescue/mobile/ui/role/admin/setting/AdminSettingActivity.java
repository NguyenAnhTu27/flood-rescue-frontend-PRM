package com.floodrescue.mobile.ui.role.admin.setting;

import com.floodrescue.mobile.ui.shared.component.AdminFeatureActivity;

public class AdminSettingActivity extends AdminFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Cau hinh he thong";
    }

    @Override
    protected String featurePath() {
        return "ui/role/admin/setting";
    }

    @Override
    protected String featureSummary() {
        return "Man system settings cho admin, dat dung package de sau nay noi key-value editor va runtime settings.";
    }
}

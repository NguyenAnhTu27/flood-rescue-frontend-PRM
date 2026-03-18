package com.floodrescue.mobile.ui.role.admin.audit;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.admin.setting.AdminSettingActivity;
import com.floodrescue.mobile.ui.shared.component.AdminFeatureActivity;

public class AdminAuditActivity extends AdminFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Nhat ky he thong";
    }

    @Override
    protected String featurePath() {
        return "ui/role/admin/audit";
    }

    @Override
    protected String featureSummary() {
        return "Man audit logs cua admin, duoc tach thanh module rieng de bao tri va noi filter sau nay.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo cau hinh";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, AdminSettingActivity.class));
    }
}

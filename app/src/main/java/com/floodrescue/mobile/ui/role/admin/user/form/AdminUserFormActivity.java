package com.floodrescue.mobile.ui.role.admin.user.form;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.admin.permission.AdminPermissionActivity;
import com.floodrescue.mobile.ui.shared.component.AdminFeatureActivity;

public class AdminUserFormActivity extends AdminFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Tao va chinh sua user";
    }

    @Override
    protected String featurePath() {
        return "ui/role/admin/user/form";
    }

    @Override
    protected String featureSummary() {
        return "Man form cho admin user duoc dat dung package rieng de sau nay noi create, edit va reset password.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo phan quyen";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, AdminPermissionActivity.class));
    }
}

package com.floodrescue.mobile.ui.role.admin.user.list;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.admin.user.form.AdminUserFormActivity;
import com.floodrescue.mobile.ui.shared.component.AdminFeatureActivity;

public class AdminUserListActivity extends AdminFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Nguoi dung he thong";
    }

    @Override
    protected String featurePath() {
        return "ui/role/admin/user/list";
    }

    @Override
    protected String featureSummary() {
        return "Man danh sach nguoi dung cua admin, thay the diem vao tam bang toast tu dashboard cu.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Tao hoac sua user";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, AdminUserFormActivity.class));
    }
}

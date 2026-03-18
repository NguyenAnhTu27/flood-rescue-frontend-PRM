package com.floodrescue.mobile.ui.role.manager.relief.list;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.manager.relief.create.ManagerReliefCreateActivity;
import com.floodrescue.mobile.ui.shared.component.ManagerFeatureActivity;

public class ManagerReliefListActivity extends ManagerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Danh sach yeu cau cuu tro";
    }

    @Override
    protected String featurePath() {
        return "ui/role/manager/relief/list";
    }

    @Override
    protected String featureSummary() {
        return "Man danh sach yeu cau cuu tro cua manager, thay the diem vao tam bang toast o dashboard cu.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Tao yeu cau cuu tro";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, ManagerReliefCreateActivity.class));
    }
}

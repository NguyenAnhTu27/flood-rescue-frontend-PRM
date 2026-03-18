package com.floodrescue.mobile.ui.role.manager.relief.create;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.manager.relief.detail.ManagerReliefDetailActivity;
import com.floodrescue.mobile.ui.shared.component.ManagerFeatureActivity;

public class ManagerReliefCreateActivity extends ManagerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Tao yeu cau cuu tro";
    }

    @Override
    protected String featurePath() {
        return "ui/role/manager/relief/create";
    }

    @Override
    protected String featureSummary() {
        return "Man tao yeu cau cuu tro duoc dat dung package manager/relief/create theo nghiep vu chi manager moi duoc tao.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Xem chi tiet yeu cau";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, ManagerReliefDetailActivity.class));
    }
}

package com.floodrescue.mobile.ui.role.manager.relief.detail;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.manager.dispatch.ManagerDispatchActivity;
import com.floodrescue.mobile.ui.shared.component.ManagerFeatureActivity;

public class ManagerReliefDetailActivity extends ManagerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Chi tiet yeu cau cuu tro";
    }

    @Override
    protected String featurePath() {
        return "ui/role/manager/relief/detail";
    }

    @Override
    protected String featureSummary() {
        return "Man detail cuu tro cho manager, duoc tach rieng de sau nay noi phe duyet, tu choi va dieu phoi.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo dieu phoi cuu tro";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, ManagerDispatchActivity.class));
    }
}

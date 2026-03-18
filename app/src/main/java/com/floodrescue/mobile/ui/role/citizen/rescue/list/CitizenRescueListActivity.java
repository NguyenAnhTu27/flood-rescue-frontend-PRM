package com.floodrescue.mobile.ui.role.citizen.rescue.list;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.citizen.rescue.detail.CitizenRescueDetailActivity;
import com.floodrescue.mobile.ui.shared.component.CitizenFeatureActivity;

public class CitizenRescueListActivity extends CitizenFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Yeu cau cuu ho cua toi";
    }

    @Override
    protected String featurePath() {
        return "ui/role/citizen/rescue/list";
    }

    @Override
    protected String featureSummary() {
        return "Man danh sach duoc tach rieng cho citizen de quan ly cac yeu cau cuu ho va mo nhanh vao chi tiet.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo chi tiet mau";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, CitizenRescueDetailActivity.class));
    }
}

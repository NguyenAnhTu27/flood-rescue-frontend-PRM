package com.floodrescue.mobile.ui.role.rescuer.relief.list;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.rescuer.relief.detail.RescuerReliefDetailActivity;
import com.floodrescue.mobile.ui.shared.component.RescuerFeatureActivity;

public class RescuerReliefListActivity extends RescuerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Cuu tro duoc giao";
    }

    @Override
    protected String featurePath() {
        return "ui/role/rescuer/relief/list";
    }

    @Override
    protected String featureSummary() {
        return "Man danh sach giao cuu tro cho rescuer, phan tach ro voi luong citizen va manager.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo chi tiet giao";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, RescuerReliefDetailActivity.class));
    }
}

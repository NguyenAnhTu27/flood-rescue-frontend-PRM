package com.floodrescue.mobile.ui.role.manager.dispatch;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.manager.inventory.stock.ManagerInventoryStockActivity;
import com.floodrescue.mobile.ui.shared.component.ManagerFeatureActivity;

public class ManagerDispatchActivity extends ManagerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Dieu phoi cuu tro";
    }

    @Override
    protected String featurePath() {
        return "ui/role/manager/dispatch";
    }

    @Override
    protected String featureSummary() {
        return "Man dieu phoi cua manager duoc tach thanh package rieng de quan ly doi, phuong tien va luong giao cuu tro.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo ton kho";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, ManagerInventoryStockActivity.class));
    }
}

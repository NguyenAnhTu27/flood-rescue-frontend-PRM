package com.floodrescue.mobile.ui.role.manager.inventory.stock;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.manager.inventory.receipt.list.ManagerInventoryReceiptListActivity;
import com.floodrescue.mobile.ui.shared.component.ManagerFeatureActivity;

public class ManagerInventoryStockActivity extends ManagerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Tong quan ton kho";
    }

    @Override
    protected String featurePath() {
        return "ui/role/manager/inventory/stock";
    }

    @Override
    protected String featureSummary() {
        return "Man ton kho duoc dat vao package manager/inventory/stock de tach ro module kho khoi dashboard va relief.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo phieu nhap";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, ManagerInventoryReceiptListActivity.class));
    }
}

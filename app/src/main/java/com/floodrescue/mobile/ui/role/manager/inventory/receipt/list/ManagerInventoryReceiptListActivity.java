package com.floodrescue.mobile.ui.role.manager.inventory.receipt.list;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.manager.inventory.issue.list.ManagerInventoryIssueListActivity;
import com.floodrescue.mobile.ui.shared.component.ManagerFeatureActivity;

public class ManagerInventoryReceiptListActivity extends ManagerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Danh sach phieu nhap";
    }

    @Override
    protected String featurePath() {
        return "ui/role/manager/inventory/receipt/list";
    }

    @Override
    protected String featureSummary() {
        return "Man tong hop phieu nhap kho cua manager, duoc tach rieng de sau nay noi create, detail va approve.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo phieu xuat";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, ManagerInventoryIssueListActivity.class));
    }
}

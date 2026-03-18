package com.floodrescue.mobile.ui.role.manager.inventory.issue.list;

import android.content.Intent;

import com.floodrescue.mobile.ui.role.manager.asset.list.ManagerAssetListActivity;
import com.floodrescue.mobile.ui.shared.component.ManagerFeatureActivity;

public class ManagerInventoryIssueListActivity extends ManagerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Danh sach phieu xuat";
    }

    @Override
    protected String featurePath() {
        return "ui/role/manager/inventory/issue/list";
    }

    @Override
    protected String featureSummary() {
        return "Man phieu xuat kho duoc dat dung package manager/inventory/issue/list de de tiep tuc noi quy trinh xuat kho.";
    }

    @Override
    protected String primaryActionLabel() {
        return "Mo phuong tien";
    }

    @Override
    protected void onPrimaryAction() {
        startActivity(new Intent(this, ManagerAssetListActivity.class));
    }
}

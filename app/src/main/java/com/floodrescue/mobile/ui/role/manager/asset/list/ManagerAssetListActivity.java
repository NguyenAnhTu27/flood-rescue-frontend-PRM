package com.floodrescue.mobile.ui.role.manager.asset.list;

import com.floodrescue.mobile.ui.shared.component.ManagerFeatureActivity;

public class ManagerAssetListActivity extends ManagerFeatureActivity {

    @Override
    protected String featureTitle() {
        return "Danh sach phuong tien";
    }

    @Override
    protected String featurePath() {
        return "ui/role/manager/asset/list";
    }

    @Override
    protected String featureSummary() {
        return "Man phuong tien va thiet bi cua manager, dung lam diem dat cho asset list thay vi toast placeholder.";
    }
}

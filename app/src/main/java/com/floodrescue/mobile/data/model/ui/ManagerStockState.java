package com.floodrescue.mobile.data.model.ui;

import java.util.ArrayList;
import java.util.List;

public class ManagerStockState {

    private final List<StockItem> items;

    public ManagerStockState(List<StockItem> items) {
        this.items = items == null ? new ArrayList<>() : items;
    }

    public List<StockItem> getItems() {
        return items;
    }

    public static class StockItem {
        private final int itemCategoryId;
        private final String code;
        private final String name;
        private final String unit;
        private final String donationQty;
        private final String purchaseQty;
        private final String totalQty;

        public StockItem(int itemCategoryId, String code, String name, String unit, String donationQty, String purchaseQty, String totalQty) {
            this.itemCategoryId = itemCategoryId;
            this.code = code;
            this.name = name;
            this.unit = unit;
            this.donationQty = donationQty;
            this.purchaseQty = purchaseQty;
            this.totalQty = totalQty;
        }

        public int getItemCategoryId() { return itemCategoryId; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getUnit() { return unit; }
        public String getDonationQty() { return donationQty; }
        public String getPurchaseQty() { return purchaseQty; }
        public String getTotalQty() { return totalQty; }
    }
}

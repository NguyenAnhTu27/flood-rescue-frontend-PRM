package com.floodrescue.mobile.data.model.ui;

import java.util.ArrayList;
import java.util.List;

public class ManagerInventoryReceiptDetailState {

    private final long id;
    private final String code;
    private final String sourceType;
    private final String status;
    private final String note;
    private final String createdAt;
    private final String updatedAt;
    private final List<LineItem> lines;

    public ManagerInventoryReceiptDetailState(long id, String code, String sourceType, String status, String note, String createdAt, String updatedAt, List<LineItem> lines) {
        this.id = id;
        this.code = code;
        this.sourceType = sourceType;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lines = lines == null ? new ArrayList<>() : lines;
    }

    public long getId() { return id; }
    public String getCode() { return code; }
    public String getSourceType() { return sourceType; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public List<LineItem> getLines() { return lines; }

    public static class LineItem {
        private final int itemCategoryId;
        private final String itemCode;
        private final String itemName;
        private final String qty;
        private final String unit;

        public LineItem(int itemCategoryId, String itemCode, String itemName, String qty, String unit) {
            this.itemCategoryId = itemCategoryId;
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.qty = qty;
            this.unit = unit;
        }

        public int getItemCategoryId() { return itemCategoryId; }
        public String getItemCode() { return itemCode; }
        public String getItemName() { return itemName; }
        public String getQty() { return qty; }
        public String getUnit() { return unit; }
    }
}

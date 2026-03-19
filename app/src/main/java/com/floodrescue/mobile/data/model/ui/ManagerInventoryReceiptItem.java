package com.floodrescue.mobile.data.model.ui;

public class ManagerInventoryReceiptItem {

    private final long id;
    private final String code;
    private final String sourceType;
    private final String status;
    private final String note;
    private final String createdAt;
    private final String updatedAt;
    private final int lineCount;

    public ManagerInventoryReceiptItem(long id, String code, String sourceType, String status, String note, String createdAt, String updatedAt, int lineCount) {
        this.id = id;
        this.code = code;
        this.sourceType = sourceType;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lineCount = lineCount;
    }

    public long getId() { return id; }
    public String getCode() { return code; }
    public String getSourceType() { return sourceType; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public int getLineCount() { return lineCount; }
}

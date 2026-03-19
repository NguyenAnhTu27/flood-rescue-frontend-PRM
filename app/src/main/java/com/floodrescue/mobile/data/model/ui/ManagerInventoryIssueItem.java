package com.floodrescue.mobile.data.model.ui;

public class ManagerInventoryIssueItem {

    private final long id;
    private final String code;
    private final String status;
    private final String reliefRequestCode;
    private final String assignedTeamName;
    private final String assetName;
    private final String note;
    private final String createdAt;
    private final int lineCount;

    public ManagerInventoryIssueItem(long id, String code, String status, String reliefRequestCode, String assignedTeamName, String assetName, String note, String createdAt, int lineCount) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.reliefRequestCode = reliefRequestCode;
        this.assignedTeamName = assignedTeamName;
        this.assetName = assetName;
        this.note = note;
        this.createdAt = createdAt;
        this.lineCount = lineCount;
    }

    public long getId() { return id; }
    public String getCode() { return code; }
    public String getStatus() { return status; }
    public String getReliefRequestCode() { return reliefRequestCode; }
    public String getAssignedTeamName() { return assignedTeamName; }
    public String getAssetName() { return assetName; }
    public String getNote() { return note; }
    public String getCreatedAt() { return createdAt; }
    public int getLineCount() { return lineCount; }
}

package com.floodrescue.mobile.data.model.ui;

public class CoordinatorTaskGroupListItem {
    private final long id;
    private final String code;
    private final String status;
    private final String assignedTeamName;
    private final String createdByName;
    private final String createdAt;
    private final String updatedAt;
    private final String note;

    public CoordinatorTaskGroupListItem(long id, String code, String status, String assignedTeamName, String createdByName, String createdAt, String updatedAt, String note) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.assignedTeamName = assignedTeamName;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getAssignedTeamName() {
        return assignedTeamName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getNote() {
        return note;
    }
}

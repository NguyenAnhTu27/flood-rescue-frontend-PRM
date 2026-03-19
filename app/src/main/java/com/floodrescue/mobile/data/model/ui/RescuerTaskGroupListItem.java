package com.floodrescue.mobile.data.model.ui;

public class RescuerTaskGroupListItem {

    private final long id;
    private final String code;
    private final String status;
    private final String teamName;
    private final String note;
    private final String createdAt;
    private final String updatedAt;
    private final int requestCount;
    private final int activeAssignments;

    public RescuerTaskGroupListItem(
            long id,
            String code,
            String status,
            String teamName,
            String note,
            String createdAt,
            String updatedAt,
            int requestCount,
            int activeAssignments
    ) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.teamName = teamName;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.requestCount = requestCount;
        this.activeAssignments = activeAssignments;
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

    public String getTeamName() {
        return teamName;
    }

    public String getNote() {
        return note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getActiveAssignments() {
        return activeAssignments;
    }
}

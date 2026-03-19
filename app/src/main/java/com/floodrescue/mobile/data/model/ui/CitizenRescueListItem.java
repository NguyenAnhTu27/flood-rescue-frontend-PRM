package com.floodrescue.mobile.data.model.ui;

public class CitizenRescueListItem {

    private final long id;
    private final String code;
    private final String status;
    private final String priority;
    private final String addressText;
    private final int affectedPeopleCount;
    private final boolean waitingForTeam;
    private final boolean locationVerified;
    private final String createdAt;
    private final String updatedAt;

    public CitizenRescueListItem(
            long id,
            String code,
            String status,
            String priority,
            String addressText,
            int affectedPeopleCount,
            boolean waitingForTeam,
            boolean locationVerified,
            String createdAt,
            String updatedAt
    ) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.priority = priority;
        this.addressText = addressText;
        this.affectedPeopleCount = affectedPeopleCount;
        this.waitingForTeam = waitingForTeam;
        this.locationVerified = locationVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getPriority() {
        return priority;
    }

    public String getAddressText() {
        return addressText;
    }

    public int getAffectedPeopleCount() {
        return affectedPeopleCount;
    }

    public boolean isWaitingForTeam() {
        return waitingForTeam;
    }

    public boolean isLocationVerified() {
        return locationVerified;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
